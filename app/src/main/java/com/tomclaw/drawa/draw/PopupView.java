package com.tomclaw.drawa.draw;

import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.FrameLayout;

import com.h6ah4i.android.widget.verticalseekbar.VerticalSeekBar;
import com.tomclaw.drawa.R;

/**
 * Created by solkin on 25.03.17.
 */
//@EViewGroup(R.layout.popup_view)
public class PopupView extends FrameLayout {

//    @ViewById
    VerticalSeekBar seekBar;

    public PopupView(@NonNull Context context) {
        super(context);
    }

    public void setSeekBarValue(int value) {
        seekBar.setProgress(value);
    }
}
