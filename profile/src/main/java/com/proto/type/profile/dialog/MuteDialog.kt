package com.proto.type.profile.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.profile.R
import com.proto.type.profile.adapter.MuteAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MuteDialog: BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(context!!, R.style.SheetDialog)

        val view = LayoutInflater.from(context!!).inflate(R.layout.dialog_mute_sound, null)

        val adapter = MuteAdapter(
            listOf(
                "Turn off Mute",
                "For 15 minutes",
                "For 1 hour",
                "For 8 hours",
                "For 24 hours",
                "Until 8:00 AM"
            )
        )
        view.findViewById<RecyclerView>(R.id.rvTimes).adapter = adapter

        dialog.setContentView(view)
        return dialog
    }

}