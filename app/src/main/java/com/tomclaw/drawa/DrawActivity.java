package com.tomclaw.drawa;

import android.annotation.SuppressLint;
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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

@SuppressLint("Registered")
@EActivity(R.layout.main)
@OptionsMenu(R.menu.main_menu)
public class DrawActivity extends AppCompatActivity {

    @ViewById
    Toolbar toolbar;

    @ViewById
    DrawView drawView;

    @ViewById
    ViewGroup toolsContainer;

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

    private int popupWidth = 180;
    private int popupHeight = 450;
    private PopupWindow popup;
    private PopupView popupView;
    private int popupX;
    private int popupY;

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
            toolView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View view, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        setSelectedTool((ImageView) view);
                        showPopup(view);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        dismissPopup();
                    } else {
                        if (popup != null) {
                            int value = (int) (100 * (popupY + popupHeight - event.getRawY()) / popupHeight);
                            if (value < 0) {
                                value = 0;
                            } else if (value > 100) {
                                value = 100;
                            }
                            popupView.setSeekBarValue(value);
                            drawView.setToolRadius((int) ((value + 20) / drawView.getScaleFactor()));
                        }
                    }
                    return false;
                }
            });
        }

        setSelectedTool(toolPencil);
    }

    void showPopup(View view) {
        popupWidth = getResources().getDimensionPixelSize(R.dimen.popup_width);
        popupHeight = getResources().getDimensionPixelSize(R.dimen.popup_height);
        popupView = PopupView_.build(this);
        popup = new PopupWindow(popupView, popupWidth, popupHeight, true);
        popup.setTouchInterceptor(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return false;
            }
        });
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        popupX = location[0] + view.getWidth() / 2 - popupWidth / 2;
        popupY = location[1] + view.getHeight() / 2 - popupHeight / 2;
        popup.showAtLocation(view, Gravity.START | Gravity.TOP, popupX, popupY);
    }

    @UiThread(delay = 100)
    void dismissPopup() {
        if (popup != null) {
            popupView.setVisibility(GONE);
            popup.dismiss();
        }
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
