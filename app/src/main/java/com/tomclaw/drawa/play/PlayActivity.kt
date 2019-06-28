package com.tomclaw.drawa.play

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tomclaw.drawa.R
import com.tomclaw.drawa.main.getComponent
import com.tomclaw.drawa.play.di.PlayModule
import javax.inject.Inject

class PlayActivity : AppCompatActivity(), PlayPresenter.PlayRouter {

    @Inject
    lateinit var presenter: PlayPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        application.getComponent()
                .playComponent(PlayModule(context = this))
                .inject(activity = this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.play)

        val view = PlayViewImpl(window.decorView)

        presenter.attachView(view)
    }

    override fun onStart() {
        super.onStart()
        presenter.attachRouter(router = this)
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

fun createPlayActivityIntent(context: Context): Intent =
        Intent(context, PlayActivity::class.java)
