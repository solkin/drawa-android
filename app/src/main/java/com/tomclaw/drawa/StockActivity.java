package com.tomclaw.drawa;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * Created by solkin on 18/12/2017.
 */
@SuppressLint("Registered")
@EActivity(R.layout.stock)
public class StockActivity extends AppCompatActivity {

    @ViewById
    Toolbar toolbar;

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
        setTitle(R.string.stock);
    }
}
