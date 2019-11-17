package com.tomclaw.drawa.draw

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tomclaw.drawa.R
import com.tomclaw.drawa.draw.di.DrawModule
import com.tomclaw.drawa.main.getComponent
import com.tomclaw.drawa.play.createPlayActivityIntent
import com.tomclaw.drawa.share.createShareActivityIntent
import com.tomclaw.drawa.util.MetricsProvider
import javax.inject.Inject

class DrawActivity : AppCompatActivity(), DrawPresenter.DrawRouter {

    @Inject
    lateinit var presenter: DrawPresenter

    @Inject
    lateinit var metricsProvider: MetricsProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        val recordId = intent.getRecordId()
        val presenterState = savedInstanceState?.getBundle(KEY_PRESENTER_STATE)
        val drawHostHolder = DrawHostHolder()
        application.getComponent()
                .drawComponent(
                        DrawModule(
                                resources = resources,
                                recordId = recordId,
                                drawHostHolder = drawHostHolder,
                                presenterState = presenterState
                        )
                )
                .inject(activity = this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.draw)

        val view = DrawViewImpl(window.decorView, drawHostHolder, metricsProvider)

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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBundle(KEY_PRESENTER_STATE, presenter.saveState())
    }

    override fun onBackPressed() {
        presenter.onBackPressed()
    }

    override fun showShareScreen() {
        val intent = createShareActivityIntent(
                context = this,
                recordId = intent.getRecordId()
        )
        startActivity(intent)
    }

    override fun showPlayScreen() {
        val intent = createPlayActivityIntent(
                context = this,
                recordId = intent.getRecordId()
        )
        startActivity(intent)
    }

    override fun leaveScreen() {
        setResult(RESULT_OK)
        finish()
    }

    private fun Intent.getRecordId() = this.getIntExtra(EXTRA_RECORD_ID, RECORD_ID_INVALID).apply {
        if (this == RECORD_ID_INVALID) {
            throw IllegalArgumentException("record id must be specified")
        }
    }

}

fun createDrawActivityIntent(
        context: Context,
        recordId: Int
): Intent = Intent(context, DrawActivity::class.java)
        .putExtra(EXTRA_RECORD_ID, recordId)

private const val KEY_PRESENTER_STATE = "presenter_state"

private const val EXTRA_RECORD_ID = "record_id"

private const val RECORD_ID_INVALID = -1