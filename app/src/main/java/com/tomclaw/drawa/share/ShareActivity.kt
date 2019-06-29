package com.tomclaw.drawa.share

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.MATCH_DEFAULT_ONLY
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.core.content.FileProvider
import com.tomclaw.drawa.R
import com.tomclaw.drawa.main.getComponent
import com.tomclaw.drawa.share.di.ShareModule
import com.tomclaw.drawa.util.DataProvider
import java.io.File
import javax.inject.Inject


class ShareActivity : AppCompatActivity(), SharePresenter.ShareRouter {

    @Inject
    lateinit var presenter: SharePresenter

    @Inject
    lateinit var dataProvider: DataProvider<ShareItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        val recordId = intent.getRecordId()
        val presenterState = savedInstanceState?.getBundle(KEY_PRESENTER_STATE)
        application.getComponent()
                .shareComponent(ShareModule(recordId, presenterState))
                .inject(activity = this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.share)

        val adapter = ShareAdapter(layoutInflater, dataProvider)
        val view = ShareViewImpl(window.decorView, adapter)

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

    override fun shareFile(file: File, mime: String) {
        val uri = FileProvider.getUriForFile(this, packageName, file)
        ShareCompat.IntentBuilder.from(this)
                .setStream(uri)
                .setType(mime)
                .intent
                .setAction(Intent.ACTION_SEND)
                .setDataAndType(uri, mime)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                .run {
                    val list = packageManager.queryIntentActivities(this, MATCH_DEFAULT_ONLY)
                    for (resolveInfo in list) {
                        val packageName = resolveInfo.activityInfo.packageName
                        grantUriPermission(packageName, uri, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    if (resolveActivity(packageManager) != null) {
                        startActivity(this)
                    }
                }
    }

    private fun Intent.getRecordId() = getIntExtra(EXTRA_RECORD_ID, RECORD_ID_INVALID).apply {
        if (this == RECORD_ID_INVALID) {
            throw IllegalArgumentException("record id must be specified")
        }
    }

}

fun createShareActivityIntent(
        context: Context,
        recordId: Int
): Intent = Intent(context, ShareActivity::class.java)
        .putExtra(EXTRA_RECORD_ID, recordId)

private const val KEY_PRESENTER_STATE = "presenter_state"

private const val EXTRA_RECORD_ID = "record_id"

private const val RECORD_ID_INVALID = -1