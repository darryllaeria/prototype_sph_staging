package com.proto.type.chat.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import com.proto.type.chat.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_manage_group_chat.view.*

class ManageGroupChatDialog(private val callback: (Int) -> Unit): BottomSheetDialogFragment() {

    companion object {
        const val ACTION_EDIT_GROUP_NAME = 1
        const val ACTION_CHANGE_GROUP_AVATAR = 2
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(context!!, R.style.SheetDialog)
        val view = LayoutInflater.from(context!!).inflate(R.layout.dialog_manage_group_chat,null)
        view.tvCancel.setOnClickListener {
            dismiss()
        }

        view.tvEditGroupName.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_EDIT_GROUP_NAME)
        }

        view.tvChangeGroupAvatar.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_CHANGE_GROUP_AVATAR)
        }

        dialog.setContentView(view)
        return dialog
    }

}