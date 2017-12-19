package com.tomclaw.drawa.stock;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.tomclaw.drawa.R;

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

    @ViewById
    RecyclerView recycler;

    StockAdapter adapter;

    @AfterViews
    void init() {
        setSupportActionBar(toolbar);
        setTitle(R.string.stock);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(
                this, 2, GridLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(layoutManager);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recycler.setItemAnimator(itemAnimator);
        recycler.setHasFixedSize(true);

        adapter = new StockAdapter(this);

        recycler.setAdapter(adapter);
    }
}
