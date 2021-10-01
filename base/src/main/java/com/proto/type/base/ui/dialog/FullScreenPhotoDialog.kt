package com.proto.type.base.ui.dialog

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.proto.type.base.R
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.loadAvatar
import com.proto.type.base.extension.setImage
import kotlinx.android.synthetic.main.dialog_photo_fullscreen.view.*

class FullScreenPhotoDialog(private val content: Any?, private val shouldCircle: Boolean): DialogFragment() {

    // MARK: - Override Function
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context!!, R.style.FullScreenDialog)
        dialog.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        val view = LayoutInflater.from(context!!).inflate(R.layout.dialog_photo_fullscreen, null)
        view.ivClose.setOnClickListener { dismiss() }
        when (content) {
            is ChatModel -> view.pvShowingImage.loadAvatar(content, shouldCircle)
            is String -> Glide.with(context!!).load(content).fitCenter().into(view.pvShowingImage)
            is Uri? -> Glide.with(context!!).load(content).fitCenter().into(view.pvShowingImage)
            is UserModel -> view.pvShowingImage.loadAvatar(content, shouldCircle)
        }
        view.pvShowingImage.setImage(R.drawable.ic_default_image)
        dialog.setContentView(view)
        return dialog
    }

}