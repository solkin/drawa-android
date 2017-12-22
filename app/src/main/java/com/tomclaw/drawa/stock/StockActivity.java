package com.tomclaw.drawa.stock;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.tomclaw.drawa.R;
import com.tomclaw.drawa.dto.Image;
import com.tomclaw.drawa.dto.Size;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

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

        Single
                .create(loadStockItems())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<StockItem>>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onSuccess(List<StockItem> stockItems) {
                adapter.setItems(stockItems);
            }

            @Override
            public void onError(Throwable e) {
            }
        });
    }

    private SingleOnSubscribe<List<StockItem>> loadStockItems() {
        return new SingleOnSubscribe<List<StockItem>>() {
            @Override
            public void subscribe(SingleEmitter<List<StockItem>> e) throws Exception {
                List<StockItem> items = Arrays.asList(
                        new StockItem(new Image("", new Size(10, 10))),
                        new StockItem(new Image("image2", new Size(10, 10)))
                );
                e.onSuccess(items);
            }
        };
    }
}
