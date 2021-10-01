package com.proto.type.base.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import com.proto.type.base.R

/**
 * @Details a global progress dialog for app
 */

class AppProgressDialog(context: Context) : Dialog(context) {

    init {

        window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.MATCH_PARENT
        )

        val view = LayoutInflater.from(context).inflate(R.layout.layout_progress_dialog, null, false)
        setContentView(view)

        this.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
}