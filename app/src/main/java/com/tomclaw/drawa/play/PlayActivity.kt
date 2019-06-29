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
        val recordId = intent.getRecordId()
        application.getComponent()
                .playComponent(PlayModule(recordId))
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

    private fun Intent.getRecordId() = getIntExtra(EXTRA_RECORD_ID, RECORD_ID_INVALID).apply {
        if (this == RECORD_ID_INVALID) {
            throw IllegalArgumentException("record id must be specified")
        }
    }

}

fun createPlayActivityIntent(
        context: Context,
        recordId: Int
): Intent = Intent(context, PlayActivity::class.java)
        .putExtra(EXTRA_RECORD_ID, recordId)

private const val EXTRA_RECORD_ID = "record_id"

private const val RECORD_ID_INVALID = -1
