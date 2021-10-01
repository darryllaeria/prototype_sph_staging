package com.proto.type.base.utils

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.core.app.ActivityCompat
import com.proto.type.base.R
import kotlinx.android.synthetic.main.dialog_image_selector.*

class DialogUtils {

    companion object {
        fun showImageSelectorDialog(
            activity: Activity,
            isRemoveButtonVisible: Boolean = true,
            onActionCall: (imagePickerAction: Int) -> Unit
        ) {

            val bottomDialog = Dialog(activity, R.style.BottomPickerDialog)
            bottomDialog.setContentView(R.layout.dialog_image_selector)
            bottomDialog.window?.apply {
                setBackgroundDrawableResource(android.R.color.transparent)
                setLayout(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                setGravity(Gravity.BOTTOM)
            }

            bottomDialog.tvOkCancel.setOnClickListener {
                bottomDialog.dismiss()
            }

            bottomDialog.tvRemovePhoto.setOnClickListener {
                onActionCall(ImagePickerAction.ACTION_REMOVE_PHOTO)
                bottomDialog.dismiss()
            }
            if (!isRemoveButtonVisible) {
                bottomDialog.tvRemovePhoto.visibility = View.GONE
                bottomDialog.viewSelectPhotoDivider.visibility = View.GONE
            }

            bottomDialog.tvSelectPhoto.setOnClickListener {
                val SELECT_PHOTO_PERMISSIONS = arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                if (!Utils.hasPermissions(activity.applicationContext, *SELECT_PHOTO_PERMISSIONS)) {
                    ActivityCompat.requestPermissions(activity, SELECT_PHOTO_PERMISSIONS, 101)
                } else {
                    onActionCall(ImagePickerAction.ACTION_SELECT_PHOTO)
                    bottomDialog.dismiss()
                }
            }

            bottomDialog.tvTakePhoto.setOnClickListener {
                val TAKE_PHOTO_PERMISSIONS = arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )

                if (!Utils.hasPermissions(activity.applicationContext, *TAKE_PHOTO_PERMISSIONS)) {
                    ActivityCompat.requestPermissions(activity, TAKE_PHOTO_PERMISSIONS, 101)
                } else {
                    onActionCall(ImagePickerAction.ACTION_TAKE_PHOTO)
                    bottomDialog.dismiss()
                }
            }

            bottomDialog.show()
        }
    }
}

object ImagePickerAction {
    const val ACTION_TAKE_PHOTO = 0
    const val ACTION_SELECT_PHOTO = 1
    const val ACTION_REMOVE_PHOTO = 3
    const val ACTION_CHAT_REPLY = 4
    const val ACTION_CHAT_COPY = 5
    const val ACTION_CHAT_FORWARD = 6
    const val ACTION_CHAT_REPORT = 7
}