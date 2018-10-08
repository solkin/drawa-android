package com.tomclaw.drawa.info

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tomclaw.drawa.R
import com.tomclaw.drawa.info.di.InfoModule
import com.tomclaw.drawa.main.getComponent
import javax.inject.Inject

class InfoActivity : AppCompatActivity(), InfoPresenter.InfoRouter {

    @Inject
    lateinit var presenter: InfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        application.getComponent()
                .infoComponent(InfoModule(context = this))
                .inject(activity = this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.info)

        val view = InfoViewImpl(window.decorView)

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

    override fun openRate() {
        openUriSafe(
                uri = MARKET_URI_RATE + packageName,
                fallback = WEB_URI_RATE + packageName
        )
    }

    override fun openProjects() {
        openUriSafe(
                uri = MARKET_URI_PROJECTS + VENDOR_ID,
                fallback = WEB_URI_PROJECTS + VENDOR_ID
        )
    }

    override fun leaveScreen() {
        finish()
    }

    private fun openUriSafe(uri: String, fallback: String) {
        try {
            startActivity(Intent(ACTION_VIEW, Uri.parse(uri)))
        } catch (ignored: android.content.ActivityNotFoundException) {
            startActivity(Intent(ACTION_VIEW, Uri.parse(fallback)))
        }
    }

}

fun createInfoActivityIntent(context: Context): Intent =
        Intent(context, InfoActivity::class.java)

private const val VENDOR_ID = "TomClaw"
private const val MARKET_URI_RATE = "market://details?id="
private const val MARKET_URI_PROJECTS = "market://search?q="
private const val WEB_URI_RATE = "https://play.google.com/store/apps/details?id="
private const val WEB_URI_PROJECTS = "https://play.google.com/store/apps/search?q="