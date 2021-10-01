package com.proto.type.chat.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import com.proto.type.chat.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_report_action.view.*

class ReportActionDialog(private val callback: (Int) -> Unit): BottomSheetDialogFragment() {

    companion object {
        const val ACTION_SPAM_ADVERTISING = 1
        const val ACTION_SEXUAL_HARASSMENT = 2
        const val ACTION_OTHER_HARASSMENT = 3
        const val ACTION_OTHER = 4
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(context!!, R.style.SheetDialog)
        val view = LayoutInflater.from(context!!).inflate(R.layout.dialog_report_action, null)
        view.tvCancel.setOnClickListener {
            dismiss()
        }

        view.tvReportOption1.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_SPAM_ADVERTISING)
        }

        view.tvReportOption2.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_SEXUAL_HARASSMENT)
        }

        view.tvReportOption3.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_OTHER_HARASSMENT)
        }

        view.tvReportOption4.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_OTHER)
        }

        dialog.setContentView(view)
        return dialog
    }

}