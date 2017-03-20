package com.tomclaw.drawa;

import android.graphics.PorterDuff;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
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

    private Animation toAlpha;
    private Animation fromAlpha;

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

        AlphaAnimation animation;

        animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setDuration(200);
        animation.setStartOffset(0);
        animation.setFillAfter(true);
        toAlpha = animation;

        animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setDuration(200);
        animation.setStartOffset(0);
        animation.setFillAfter(true);
        fromAlpha = animation;
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
    }

}
