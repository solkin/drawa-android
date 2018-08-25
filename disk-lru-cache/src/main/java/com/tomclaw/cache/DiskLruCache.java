package com.tomclaw.cache;

import java.io.*;
import java.util.*;

public class DiskLruCache {

    private static final int JOURNAL_FORMAT_VERSION = 1;
    private static final boolean LOGGING = true;

    private final File cacheDir;
    private final Journal journal;
    private final long cacheSize;

    private DiskLruCache(File cacheDir, Journal journal, long cacheSize) {
        this.cacheDir = cacheDir;
        this.journal = journal;
        this.cacheSize = cacheSize;
    }

    public static DiskLruCache create(File cacheDir, long cacheSize) throws IOException {
        if (!cacheDir.exists()) {
            if (!cacheDir.mkdirs()) {
                throw new IOException("Unable to create specified cache directory");
            }
        }
        File file = new File(cacheDir, "journal.bin");
        Journal journal = Journal.readJournal(file);
        return new DiskLruCache(cacheDir, journal, cacheSize);
    }

    public File put(String key, File file) throws IOException {
        String name = file.getName();
        long time = System.currentTimeMillis();
        long fileSize = file.length();
        LruRecord record = new LruRecord(key, name, time, fileSize);
        File cacheFile = new File(cacheDir, name);
        if ((cacheFile.exists() && cacheFile.delete()) | file.renameTo(cacheFile)) {
            journal.put(record, cacheSize, cacheDir);
            journal.writeJournal();
            return cacheFile;
        } else {
            throw new IOException(String.format("Unable to move file %s to the cache directory", name));
        }
    }

    public File get(String key) {
        LruRecord record = journal.get(key);
        if (record != null) {
            journal.writeJournal();
            return new File(cacheDir, record.getName());
        } else {
            log("[-] No requested file with key %s in cache", key);
            return null;
        }
    }

    public static void log(String format, Object... args) {
        if (LOGGING) {
            System.out.println(String.format(format, args));
        }
    }

    private static class Journal {

        private final File file;
        private final Map<String, LruRecord> map = new HashMap<>();
        private long totalSize = 0;

        public Journal(File file) {
            this.file = file;
        }

        public void put(LruRecord record, long cacheSize, File cacheDir) throws IOException {
            long fileSize = record.getSize();
            prepare(fileSize, cacheSize, cacheDir);
            put(record);
        }

        private void put(LruRecord record) {
            map.put(record.key, record);
            totalSize += record.getSize();
            log("[+] Put %s (%d bytes) and cache size became %d bytes", record.getName(), record.getSize(), totalSize);
        }

        public LruRecord get(String key) {
            LruRecord record = map.get(key);
            if (record != null) {
                updateTime(record);
                log("[^] Update time of %s (%d bytes)", record.getName(), record.getSize());
            }
            return record;
        }

        private void updateTime(LruRecord record) {
            long time = System.currentTimeMillis();
            map.put(record.key, new LruRecord(record, time));
        }

        private void prepare(long fileSize, long cacheSize, File cacheDir) throws IOException {
            log("[?] Check total %d bytes with new file %d bytes is more than cache size %d bytes", totalSize, fileSize, totalSize + fileSize, cacheSize);
            if (totalSize + fileSize > cacheSize) {
                List<LruRecord> records = new ArrayList<>(map.values());
                records.sort(new LruRecordComparator());
                for (int c = records.size() - 1; c > 0; c--) {
                    LruRecord record = records.remove(c);
                    long nextTotalSize = totalSize - record.size;
                    log("[x] Delete %s [%d ms] %d bytes and free cache to %d bytes", record.name, record.time, record.size, nextTotalSize);
                    File file = new File(cacheDir, record.name);
                    if (file.exists()) {
                        if (!file.delete()) {
                            throw new IOException(String.format("Unable to delete file %s from cache", file.getName()));
                        }
                    }
                    map.remove(record.key);
                    totalSize = nextTotalSize;

                    if (totalSize + fileSize <= cacheSize) {
                        break;
                    }
                }
            }
        }

        public long getTotalSize() {
            return totalSize;
        }

        private void setTotalSize(long totalSize) {
            this.totalSize = totalSize;
        }

        public void writeJournal() {
            DataOutputStream stream = null;
            try {
                stream = new DataOutputStream(new FileOutputStream(file));
                stream.writeShort(JOURNAL_FORMAT_VERSION);
                stream.writeInt(map.size());
                for (LruRecord record : map.values()) {
                    stream.writeUTF(record.getKey());
                    stream.writeUTF(record.getName());
                    stream.writeLong(record.getTime());
                    stream.writeLong(record.getSize());
                }
            } catch (IOException ex) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException ignored) {
                    }
                }
            }
        }

        public static Journal readJournal(File file) {
            log("[.] Start journal reading", file.getName());
            Journal journal = new Journal(file);
            DataInputStream stream = null;
            try {
                stream = new DataInputStream(new FileInputStream(file));
                int version = stream.readShort();
                if (version != JOURNAL_FORMAT_VERSION) {
                    throw new IllegalArgumentException("Invalid journal format version");
                }
                int count = stream.readInt();
                long totalSize = 0;
                for (int c = 0; c < count; c++) {
                    String key = stream.readUTF();
                    String name = stream.readUTF();
                    long time = stream.readLong();
                    long size = stream.readLong();
                    totalSize += size;
                    LruRecord record = new LruRecord(key, name, time, size);
                    journal.put(record);
                }
                journal.setTotalSize(totalSize);
                log("[.] Journal read. Files count is %d and total size is %d", count, totalSize);
            } catch (IOException ex) {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (IOException ignored) {
                    }
                }
            }
            return journal;
        }

    }

    private static class LruRecord {

        private String key;
        private String name;
        private long time;
        private long size;

        public LruRecord(LruRecord record, long time) {
            this(record.key, record.name, time, record.size);
        }

        public LruRecord(String key, String name, long time, long size) {
            this.key = key;
            this.name = name;
            this.time = time;
            this.size = size;
        }

        public String getKey() {
            return key;
        }

        public String getName() {
            return name;
        }

        public long getTime() {
            return time;
        }

        public long getSize() {
            return size;
        }
    }

    private static class LruRecordComparator implements Comparator<LruRecord> {

        @Override
        public int compare(LruRecord o1, LruRecord o2) {
            return Long.compare(o2.time, o1.time);
        }
    }
}
