package com.tomclaw.drawa;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

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
    DrawView drawView;

    @ViewById
    ImageView toolPencil;

    @ViewById
    ImageView toolBrush;

    @ViewById
    ImageView toolMarker;

    @ViewById
    ImageView toolBroom;

    @ViewById
    ImageView toolFill;

    @ViewById
    ImageView toolEraser;

    @ViewById
    RecyclerView paletteRecycler;

    private PaletteAdapter adapter;
    private ImageView[] toolViews;

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
                drawView.setToolColor(color);
            }
        });
        paletteRecycler.setAdapter(adapter);

        toolViews = new ImageView[6];
        toolViews[0] = toolPencil;
        toolViews[1] = toolBrush;
        toolViews[2] = toolMarker;
        toolViews[3] = toolBroom;
        toolViews[4] = toolFill;
        toolViews[5] = toolEraser;

        for (ImageView toolView : toolViews) {
            toolView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    setSelectedTool((ImageView) view);
                }
            });
        }

        setSelectedTool(toolPencil);
    }

    private void setSelectedTool(ImageView selected) {
        int colorSelected = getResources().getColor(R.color.color_primary);
        int colorUnselected = getResources().getColor(R.color.color_tint);

        int color;
        for (ImageView imageView : toolViews) {
            color = (imageView == selected) ? colorSelected : colorUnselected;
            imageView.setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        }

        switch (selected.getId()) {
            case R.id.tool_pencil:
                drawView.selectPencil();
                break;
            case R.id.tool_brush:
                drawView.selectBrush();
                break;
            case R.id.tool_marker:
                drawView.selectMarker();
                break;
            case R.id.tool_broom:
                drawView.selectFluffy();
                break;
            case R.id.tool_fill:
                drawView.selectFill();
                break;
            case R.id.tool_eraser:
                drawView.selectEraser();
                break;
        }
    }

    @OptionsItem
    boolean menuUndo() {
        drawView.undo();
        return true;
    }

    @OptionsItem
    boolean menuSave() {
        saveDrawStack();
        return true;
    }

    @OptionsItem
    boolean menuClean() {
        drawView.reset();
        return true;
    }

    void saveDrawStack() {
        drawView.saveHistory();
        Uri uri = drawView.exportGif();

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setType("image/gif");

        grantUriPermission(this, uri, intent);

        startActivity(Intent.createChooser(intent, getString(R.string.send_to)));

    }

    private static void grantUriPermission(Context context, Uri uri, Intent intent) {
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

}
