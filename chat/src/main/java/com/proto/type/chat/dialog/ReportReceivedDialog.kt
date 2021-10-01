package com.proto.type.chat.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import com.proto.type.chat.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_report_received.view.*

class ReportReceivedDialog(private val callback: (Int) -> Unit): BottomSheetDialogFragment() {

    companion object {
        const val ACTION_OK = 1
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(context!!, R.style.SheetDialog)
        val view = LayoutInflater.from(context!!).inflate(R.layout.dialog_report_received, null)

        view.tvOk.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_OK)
        }

        dialog.setContentView(view)
        return dialog
    }

}