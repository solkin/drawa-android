package com.tomclaw.drawa;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EActivity(R.layout.main)
@OptionsMenu(R.menu.main_menu)
public class MainActivity extends AppCompatActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    SketchView sketchView;

    @ViewById
    RecyclerView paletteRecycler;

    private PaletteAdapter adapter;

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        paletteRecycler.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        paletteRecycler.setItemAnimator(itemAnimator);

        int[] colors = getResources().getIntArray(R.array.palette);
        List<Integer> palette = new ArrayList<>();
        for (int c : colors) {
            palette.add(c);
        }
        adapter = new PaletteAdapter(this, palette);
        adapter.setListener(new PaletteAdapter.PaletteClickListener() {
            @Override
            public void onColorClicked(int color) {
                sketchView.color(color);
            }
        });
        paletteRecycler.setAdapter(adapter);

//        final File file = new File(getFilesDir(), "file.dat");
//        if (file.exists()) {
//            file.delete();
//        }
//        new Thread() {
//            @Override
//            public void run() {
//                BitmapStack bitmapStack = new BitmapStack(file);
//
////                FileStack<StringStackItem> fileStack = new FileStack<StringStackItem>(file) {
////                    @Override
////                    public StringStackItem createItem() {
////                        return new StringStackItem("");
////                    }
////                };
//                try {
//                    fileStack.push(new StringStackItem("one"));
//                    fileStack.push(new StringStackItem("two"));
//                    fileStack.push(new StringStackItem("three"));
//                    fileStack.push(new StringStackItem("four"));
//
//                    Log.d("~!~", fileStack.pop().getString());
//                    Log.d("~!~", fileStack.pop().getString());
//                    Log.d("~!~", fileStack.pop().getString());
//                    Log.d("~!~", fileStack.pop().getString());
//
//                    fileStack.push(new StringStackItem("five"));
//                    fileStack.push(new StringStackItem("six"));
//                    fileStack.push(new StringStackItem("seven"));
//
//                    Log.d("~!~", fileStack.pop().getString());
//                    Log.d("~!~", fileStack.pop().getString());
//                    Log.d("~!~", fileStack.pop().getString());
//                } catch (Stack.StackException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            class StringStackItem implements StackItem {
//
//                private String string;
//
//                StringStackItem(String string) {
//                    this.string = string;
//                }
//
//                public String getString() {
//                    return string;
//                }
//
//                @Override
//                public void write(OutputStream output) throws IOException {
//                    DataOutputStream dos = new DataOutputStream(output);
//                    dos.writeUTF(string);
//                }
//
//                @Override
//                public void read(InputStream input) throws IOException {
//                    DataInputStream dis = new DataInputStream(input);
//                    string = dis.readUTF();
//                }
//            }
//        }.start();
    }

    @OptionsItem
    boolean menuUndo() {
        sketchView.undo();
        return true;
    }

    @OptionsItem
    boolean menuSave() {
        return true;
    }

    @OptionsItem
    boolean menuClean() {
        sketchView.reset();
        return true;
    }

}
