package com.tomclaw.drawa.info

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tomclaw.drawa.R
import com.tomclaw.drawa.info.di.InfoModule
import com.tomclaw.drawa.main.getComponent
import javax.inject.Inject

class InfoActivity : AppCompatActivity(), InfoPresenter.InfoRouter {

    @Inject
    lateinit var presenter: InfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        application.getComponent()
                .infoComponent(InfoModule())
                .inject(activity = this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.share)

        val view = InfoViewImpl(window.decorView)

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

    override fun leaveScreen() {
        finish()
    }

}

fun createInfoActivityIntent(context: Context): Intent =
        Intent(context, InfoActivity::class.java)