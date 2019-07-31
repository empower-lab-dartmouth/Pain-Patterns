package com.example.raymondyao.painpatterns

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient

class DailyDiarySurvey : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daily_diary_survey)
        val webview : WebView = findViewById(R.id.webviewDaily)
        val webSettings = webview.getSettings()
        webSettings.setJavaScriptEnabled(true)
        webview.setWebViewClient(WebViewClient())
        webview.loadUrl("https://stanforduniversity.qualtrics.com/jfe/form/SV_etIfLk7J9jTgIIJ")
    }
}
