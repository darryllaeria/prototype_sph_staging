package com.proto.type.chat.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import com.proto.type.chat.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_choose_admin.view.*

class ChooseAdminDialog(private val callback: (Int) -> Unit): BottomSheetDialogFragment() {

    companion object {
        const val ACTION_LEAVE_CHAT = 1
        const val ACTION_CHOOSE_ADMIN = 2
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(context!!, R.style.SheetDialog)
        val view = LayoutInflater.from(context!!).inflate(R.layout.dialog_choose_admin,null)
        view.tvCancel.setOnClickListener {
            dismiss()
        }

        view.tvChooseAdmin.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_CHOOSE_ADMIN)
        }

        view.tvLeaveChat.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_LEAVE_CHAT)
        }

        dialog.setContentView(view)
        return dialog
    }

}