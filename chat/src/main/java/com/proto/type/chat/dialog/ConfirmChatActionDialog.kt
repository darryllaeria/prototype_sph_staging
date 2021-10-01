package com.proto.type.chat.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import com.proto.type.base.Constants
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.loadAvatar
import com.proto.type.chat.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_confirm_chat_action.view.*

class ConfirmChatActionDialog(
    private val chat: ChatModel,
    private val user: UserModel? = null,
    private val message: String,
    private val actionButtonTitle: String,
    private val callback: () -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(context!!, R.style.SheetDialog)
        val view = LayoutInflater.from(context!!).inflate(R.layout.dialog_confirm_chat_action,null)
        if (chat.category == Constants.CHAT_CATEGORY_PRIVATE) {
            user?.let { view.ivAvatar.loadAvatar(it) }
        } else {
            view.ivAvatar.loadAvatar(chat)
        }
        view.tvDestructiveAction.setOnClickListener {
            callback.invoke()
            dismiss()
        }
        view.tvDestructiveAction.text = actionButtonTitle
        view.tvMessage.text = message
        view.tvCancel.setOnClickListener {
            dismiss()
        }
        dialog.setContentView(view)
        return dialog
    }

}