package com.proto.type.base.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.proto.type.base.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_image_picker.view.*

class ImagePickerDialog(private val shouldShowDeleteOption: Boolean = true, private val callback: (Int) -> Unit): BottomSheetDialogFragment() {

    companion object {
        const val ACTION_TAKE_PICTURE = 1
        const val ACTION_PICK_PHOTO = 2
        const val ACTION_REMOVE_AVATAR = 3
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(context!!, R.style.SheetDialog)
        val view = LayoutInflater.from(context!!).inflate(R.layout.dialog_image_picker,null)
        view.tvTakePhoto.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_TAKE_PICTURE)
        }
        view.tvSelectPhoto.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_PICK_PHOTO)
        }
        view.tvRemovePhoto.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_REMOVE_AVATAR)
        }
        view.tvCancel.setOnClickListener {
            dismiss()
        }

        view.viewSeparator2.visibility = if (shouldShowDeleteOption) View.VISIBLE else View.GONE
        view.tvRemovePhoto.visibility = if (shouldShowDeleteOption) View.VISIBLE else View.GONE

        dialog.setContentView(view)
        return dialog
    }

}