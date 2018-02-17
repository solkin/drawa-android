package com.tomclaw.drawa.draw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tomclaw.drawa.R
import com.tomclaw.drawa.dto.Record
import com.tomclaw.drawa.main.getComponent
import com.tomclaw.drawa.stock.di.DrawModule
import javax.inject.Inject

class DrawActivity : AppCompatActivity(), DrawPresenter.DrawRouter {

    @Inject
    lateinit var presenter: DrawPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        val record = intent.getParcelableExtra<Record>(EXTRA_RECORD)
                ?: throw IllegalArgumentException("name must be specified")
        val presenterState = savedInstanceState?.getBundle(KEY_PRESENTER_STATE)
        val bitmapHolder = BitmapHolder()
        application.getComponent()
                .drawComponent(
                        DrawModule(
                                context = this,
                                record = record,
                                bitmapHolder = bitmapHolder,
                                presenterState = presenterState)
                )
                .inject(activity = this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.draw)

        val view = DrawViewImpl(window.decorView, bitmapHolder)

        presenter.attachView(view)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachRouter(this)
    }

    override fun onStop() {
        presenter.detachRouter()
        super.onStop()
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putBundle(KEY_PRESENTER_STATE, presenter.saveState())
    }

    override fun showStockScreen() {
    }

    override fun leaveScreen() {
        finish()
    }

}

fun createDrawActivityIntent(context: Context,
                             record: Record): Intent =
        Intent(context, DrawActivity::class.java)
                .putExtra(EXTRA_RECORD, record)

private const val KEY_PRESENTER_STATE = "presenter_state"

private const val EXTRA_RECORD = "record"