package com.proto.type.chat.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import com.proto.type.chat.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_participant_action.view.*

class ParticipantActionDialog(private val isAdmin: Boolean, private val isCurrentUser: Boolean, private val isCurrentUserAdmin: Boolean, private val callback: (Int) -> Unit): BottomSheetDialogFragment() {

    companion object {
        const val ACTION_OPEN_PROFILE = 1
        const val ACTION_SEND_PRIVATE_MESSAGE = 2
        const val ACTION_MAKE_GROUP_ADMIN = 3
        const val ACTION_REMOVE_ADMIN = 4
        const val ACTION_REMOVE_FROM_GROUP = 5
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(context!!, R.style.SheetDialog)
        val view = LayoutInflater.from(context!!).inflate(R.layout.dialog_participant_action, null)
        view.tvCancel.setOnClickListener {
            dismiss()
        }

        view.tvOpenProfile.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_OPEN_PROFILE)
        }

        view.tvSendPrivateMessage.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_SEND_PRIVATE_MESSAGE)
        }

        view.tvMakeGroupAdmin.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_MAKE_GROUP_ADMIN)
        }

        view.tvRemoveAdmin.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_REMOVE_ADMIN)
        }

        view.tvRemoveFromGroup.setOnClickListener {
            dismiss()
            callback.invoke(ACTION_REMOVE_FROM_GROUP)
        }

        if (isCurrentUser) {
            view.viewSeparator1.visibility = View.GONE
            view.viewSeparator2.visibility = View.GONE
            view.viewSeparator3.visibility = View.GONE
            view.tvMakeGroupAdmin.visibility = View.GONE
            view.tvSendPrivateMessage.visibility = View.GONE
            view.tvRemoveFromGroup.visibility = View.GONE
        } else {
            if (isCurrentUserAdmin) {
                if (isAdmin) {
                    view.tvMakeGroupAdmin.visibility = View.GONE
                    view.tvRemoveAdmin.visibility = View.VISIBLE
                }
            } else {
                view.viewSeparator2.visibility = View.GONE
                view.viewSeparator3.visibility = View.GONE
                view.tvMakeGroupAdmin.visibility = View.GONE
                view.tvRemoveFromGroup.visibility = View.GONE
            }
        }

        dialog.setContentView(view)
        return dialog
    }

}