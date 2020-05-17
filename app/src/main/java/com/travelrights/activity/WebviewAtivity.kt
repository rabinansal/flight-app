package com.travelrights.activity

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import androidx.appcompat.app.AppCompatActivity
import com.travelrights.R
import kotlinx.android.synthetic.main.web_activity.*


class WebviewAtivity: AppCompatActivity() {


    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.web_activity)


        linr_menu.setOnClickListener {

            super.onBackPressed()
        }
        val title=intent.getStringExtra("title")
        val url=intent.getStringExtra("url")
        web_txt.text=title
        webview.webChromeClient = WebChromeClient()
        webview.webViewClient = WebViewClient()
        webview.settings.allowFileAccessFromFileURLs = true
        webview.settings.allowUniversalAccessFromFileURLs = true
        webview.settings.allowContentAccess = true
        webview.settings.domStorageEnabled = true
        webview.settings.setJavaScriptEnabled(false) // enable javascript
        webview.settings.builtInZoomControls = true
        webview.settings.setSupportZoom(true)
        webview.settings.loadWithOverviewMode = true
        webview.settings.useWideViewPort = true
        webview.settings.builtInZoomControls = true
        webview.settings.displayZoomControls = false
        webview.loadUrl(url)
        webview.webViewClient = WebViewController()
    }

    inner class WebViewController : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {

            println("<<<<<<<<<<<<<<<<<<<<<<<<>>>>>>>>>>>>>>>>>>>>>$url")

            view.loadUrl(url)

            return true
        }
        override fun onLoadResource(view: WebView, url: String) {

            webview.visibility = View.VISIBLE
            webview.loadUrl("javascript:(function() { " +
                    "var foot = document.getElementsByClassName('footer')[0].style.display='none'; " +
                    "})()")

        }

        override fun onPageFinished(view: WebView, url: String) {
            println("on finish")
            webview.loadUrl("javascript:(function() { " +
                    "var foot = document.getElementsByClassName('footer')[0].style.display='none'; " +
                    "})()")
            progressWheel.visibility = View.GONE
            progressbar.visibility = View.GONE

        }

    }

    override fun onBackPressed()
    {
        if (webview.canGoBack())
        {
            webview.goBack()
        } else
        {
            super.onBackPressed()
        }
    }
}

