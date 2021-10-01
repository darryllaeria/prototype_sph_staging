package com.proto.type.profile.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import com.proto.type.profile.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_logout.view.*

class LogoutDialog(private val callback: () -> Unit): BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(context!!, R.style.SheetDialog)

        val view = LayoutInflater.from(context!!).inflate(R.layout.dialog_logout, null)
        view.tvLogout.setOnClickListener {
            dialog.dismiss()
            callback.invoke()
        }

        view.tvCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.setContentView(view)
        return dialog
    }
}