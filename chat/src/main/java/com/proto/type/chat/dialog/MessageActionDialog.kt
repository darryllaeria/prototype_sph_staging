package com.proto.type.chat.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import com.proto.type.base.data.model.MessageModel
import com.proto.type.chat.R
import com.proto.type.chat.chat.RepliedMessageFragment
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_message_action.view.*
import kotlinx.android.synthetic.main.dialog_message_action.view.tvRepliedMessage
import kotlinx.android.synthetic.main.dialog_message_action.view.tvRepliedSender

class MessageActionDialog(
    private val message: MessageModel,
    private val callback: (Int) -> Unit
) : BottomSheetDialogFragment() {

    companion object {
        const val ACTION_COPY = 1
        const val ACTION_REPLY = 2
        const val ACTION_FORWARD = 3
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(context!!, R.style.SheetDialog)
        val view = LayoutInflater.from(context!!).inflate(R.layout.dialog_message_action, null)
        view.tvRepliedSender.text = message.sender.id
        view.tvRepliedMessage.text = message.data.value
        view.ibCloseDialog.setOnClickListener {
            this.dismiss()
        }
        view.btnCopy.setOnClickListener {
            callback.invoke(ACTION_COPY)
            this.dismiss()
        }
        view.btnReply.setOnClickListener {
            callback.invoke(ACTION_REPLY)
            this.dismiss()
        }
        view.btnForward.setOnClickListener {
            callback.invoke(ACTION_FORWARD)
            this.dismiss()
        }
        dialog.setContentView(view)
        return dialog
    }
}
