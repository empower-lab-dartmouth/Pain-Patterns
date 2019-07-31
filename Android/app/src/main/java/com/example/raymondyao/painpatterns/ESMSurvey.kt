package com.example.raymondyao.painpatterns

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class ESMSurvey : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_esmsurvey)
        val webview : WebView = findViewById(R.id.webviewESM)
        val webSettings = webview.getSettings()
        webSettings.setJavaScriptEnabled(true)
        webview.setWebViewClient(WebViewClient())
        webview.loadUrl("https://stanforduniversity.qualtrics.com/jfe/form/SV_0P1d0g3k8oB6UV7")
    }
}
