package com.example.movieapp

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import android.view.View
import android.view.WindowInsets
import android.webkit.WebChromeClient
import android.os.Message
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse

class PlayerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        // Hide system UI for fullscreen video
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            or View.SYSTEM_UI_FLAG_FULLSCREEN
            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        )
        val movieId = intent.getStringExtra("movie_id")
        val tvId = intent.getIntExtra("tv_id", -1)
        val seasonNumber = intent.getIntExtra("season_number", -1)
        val episodeNumber = intent.getIntExtra("episode_number", -1)
        setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    movieId != null -> {
                        VideoWebViewMovie(movieId)
                    }
                    tvId != -1 && seasonNumber != -1 && episodeNumber != -1 -> {
                        VideoWebViewTv(tvId, seasonNumber, episodeNumber)
                    }
                }
            }
        }
    }
}

@androidx.compose.runtime.Composable
fun VideoWebViewMovie(movieId: String) {
    val url = "https://vidsrc.icu/embed/movie/$movieId"
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                        val reqUrl = request?.url.toString()
                        if (reqUrl.contains("ads") || reqUrl.contains("doubleclick") || reqUrl.contains("googlesyndication") || reqUrl.contains("adservice")) {
                            // Block this request
                            return WebResourceResponse("text/plain", "utf-8", null)
                        }
                        return super.shouldInterceptRequest(view, request)
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onCreateWindow(
                        view: WebView?,
                        isDialog: Boolean,
                        isUserGesture: Boolean,
                        resultMsg: Message?
                    ): Boolean {
                        // Block all popups
                        return false
                    }
                }
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                loadUrl(url)
            }
        },
        modifier = Modifier
            .fillMaxSize()
    )
}

@androidx.compose.runtime.Composable
fun VideoWebViewTv(tvId: Int, seasonNumber: Int, episodeNumber: Int) {
    val url = "https://vidsrc.icu/embed/tv/$tvId/$seasonNumber/$episodeNumber"
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun shouldInterceptRequest(view: WebView?, request: WebResourceRequest?): WebResourceResponse? {
                        val reqUrl = request?.url.toString()
                        if (reqUrl.contains("ads") || reqUrl.contains("doubleclick") || reqUrl.contains("googlesyndication") || reqUrl.contains("adservice")) {
                            // Block this request
                            return WebResourceResponse("text/plain", "utf-8", null)
                        }
                        return super.shouldInterceptRequest(view, request)
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onCreateWindow(
                        view: WebView?,
                        isDialog: Boolean,
                        isUserGesture: Boolean,
                        resultMsg: Message?
                    ): Boolean {
                        // Block all popups
                        return false
                    }
                }
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mediaPlaybackRequiresUserGesture = false
                settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
                loadUrl(url)
            }
        },
        modifier = Modifier
            .fillMaxSize()
    )
} 