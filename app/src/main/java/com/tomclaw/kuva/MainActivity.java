package com.tomclaw.kuva;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;

public class MainActivity extends AppCompatActivity {

    private SketchView sketchView;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        sketchView = (SketchView) findViewById(R.id.sketch_view);
        GridView gridView = (GridView) findViewById(R.id.palette_view);
        gridView.setAdapter(new PaletteAdapter(this));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Integer color = (Integer) parent.getAdapter().getItem(position);
                sketchView.color(color.intValue());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_undo: {
                sketchView.undo();
                return true;
            }
            case R.id.menu_save: {
                return true;
            }
            case R.id.menu_clean: {
                sketchView.reset();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public static class PaletteAdapter extends BaseAdapter {

        int[] colors;
        private Context context;

        public PaletteAdapter(Context context) {
            this.context = context;
            colors = context.getResources().getIntArray(R.array.palette);
        }

        @Override
        public int getCount() {
            return colors.length;
        }

        @Override
        public Integer getItem(int position) {
            return colors[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PaletteImageView view = (PaletteImageView) convertView;
            if(view == null) {
                view = new PaletteImageView(context);
                view.setImageResource(R.drawable.palette_item);
            }
            view.setColorFilter(getItem(position));
            return view;
        }
    }
}
