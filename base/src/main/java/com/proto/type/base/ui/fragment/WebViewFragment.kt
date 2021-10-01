package com.proto.type.base.ui.fragment

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.webkit.URLUtil
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageButton
import com.proto.type.base.*
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.extension.setImage
import com.proto.type.base.extension.showWithAnimation
import kotlinx.android.synthetic.main.fragment_chatq_webview.*
import java.net.URL

class WebViewFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_chatq_webview

    // MARK: - Private Variables
    private var myClipboard: ClipboardManager? = null
    private var sharedUrl: String? = null

    // MARK: - Override Functions
    override fun initView() {
        super.initView()

        // Enables javascript in WebView for displaying video contents.
        wvMain.settings.javaScriptEnabled = true

        // Setup the clipboard
        myClipboard = context!!.getSystemService(CLIPBOARD_SERVICE) as? ClipboardManager
    }

    override fun initEvent() {
        super.initEvent()

        ibGoBack.setOnClickListener {
            if (wvMain.canGoBack()) wvMain.goBack()
        }

        ibGoNext.setOnClickListener {
            if (wvMain.canGoForward()) wvMain.goForward()
        }

        ibOpenExternalWebView.setOnClickListener {
            sharedUrl?.let {
                val openURLIntent = Intent(Intent.ACTION_VIEW)
                openURLIntent.data = Uri.parse(it)
                startActivity(openURLIntent)
            }
        }

        popupShareLink.setOnCopyLinkClickListener {
            sharedUrl?.let {
                myClipboard?.primaryClip = ClipData.newPlainText("text", it)
                showInfo(getString(R.string.text_copied))
            }
        }

        popupShareLink.setOnShareToChatClickListener {
            // TODO("Implement share to chat screen to make this feature work")
            showInfo("Share To Chat clicked")
        }

        ibRefresh.setOnClickListener {
            wvMain.reload()
        }

        tvDone.setOnClickListener {
            cleanupWebView()
            navigateBack()
        }

        tvShare.setOnClickListener {
            popupShareLink.showWithAnimation(Constants.App.VIEW_ANIMATION_DURATION)
        }

        wvMain.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                // It calls when progress changed
                pbLoadingIndicator.progress = newProgress
                super.onProgressChanged(view, newProgress)

                // If progress completes, progressbar gets hidden
                if (newProgress == 100) { pbLoadingIndicator.visibility = View.GONE }
            }

            override fun onReceivedIcon(view: WebView?, icon: Bitmap?) {
                super.onReceivedIcon(view, icon)
                // Do something after receiving the icon of the WebView
            }
        }

        wvMain.webViewClient = object: WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                // It calls when web page gets started and shows ProgressBar.
                pbLoadingIndicator.visibility = View.VISIBLE
                toggleButtonState(ibGoBack, wvMain.canGoBack(), if (wvMain.canGoBack()) R.drawable.ic_back_arrow_blue else R.drawable.ic_back_arrow_gray)
                toggleButtonState(ibGoNext, wvMain.canGoForward(), if (wvMain.canGoForward()) R.drawable.ic_next_arrow_blue else R.drawable.ic_next_arrow_gray)
                super.onPageStarted(view, url, favicon)
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                // If web page gets finished
                sharedUrl = url
                super.onPageFinished(view, url)
            }
        }
    }

    override fun initLogic() {
        super.initLogic()
        arguments?.getString(Constants.KEY_LINK_URL)?.let {
            sharedUrl = it
            tvTitle.text = if (URLUtil.isValidUrl(it)) URL(it).host else it
            wvMain.loadUrl(it)
        }
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        myClipboard = null
    }

    // MARK: - Private Functions
    private fun cleanupWebView() {
        // Make sure you remove the WebView from its parent view before doing anything.
        wvMain.removeAllViews()
        wvMain.clearHistory()

        // NOTE: clears RAM cache, if you pass true, it will also clear the disk cache.
        // Probably not a great idea to pass true if you have other WebViews still alive.
        wvMain.clearCache(true)

        // Loading a blank page is optional, but will ensure that the WebView isn't doing anything when you destroy it.
        wvMain.loadUrl("about:blank")
        wvMain.onPause()

        // NOTE: This can occasionally cause a segfault below API 17 (4.2)
        wvMain.destroy()
    }

    private fun toggleButtonState(button: ImageButton, enable: Boolean, imageId: Int) {
        button.isClickable = enable
        button.setImage(imageId)
    }
}