package com.proto.type.base.ui.dialog

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.proto.type.base.R
import java.util.*
import kotlin.concurrent.schedule

class SuccessDialog() : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(context!!, R.style.CenterFadeInOutDialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        val view = LayoutInflater.from(context!!).inflate(R.layout.dialog_success, null)
        dialog.setContentView(view)
        return dialog
    }

    override fun onStart() {
        super.onStart()
        Timer("DismissDialog", false).schedule(800) {
            dismiss()
        }
    }
}