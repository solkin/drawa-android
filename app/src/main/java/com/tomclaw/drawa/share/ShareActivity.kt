package com.tomclaw.drawa.share

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tomclaw.drawa.R
import com.tomclaw.drawa.main.getComponent
import com.tomclaw.drawa.share.di.ShareModule
import javax.inject.Inject

class ShareActivity : AppCompatActivity(), SharePresenter.ShareRouter {

    @Inject
    lateinit var presenter: SharePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        val presenterState = savedInstanceState?.getBundle(KEY_PRESENTER_STATE)
        application.getComponent()
                .shareComponent(ShareModule(this, presenterState))
                .inject(activity = this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.share)

        val view = ShareViewImpl(window.decorView)

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

    override fun leaveScreen() {
        finish()
    }

}

fun createShareActivityIntent(context: Context,
                              recordId: Int): Intent =
        Intent(context, ShareActivity::class.java)
                .putExtra(EXTRA_RECORD_ID, recordId)

private const val KEY_PRESENTER_STATE = "presenter_state"

private const val EXTRA_RECORD_ID = "record_id"