package com.proto.type.profile.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.loadAvatar
import com.proto.type.profile.R
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.dialog_delete_account.view.*

class DeleteConfirmDialog(
    private val user: UserModel,
    private val callback: () -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), R.style.SheetDialog)

        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_delete_account, null)

        view.ivAvatar.loadAvatar(user)
        view.tvDelete.setOnClickListener {
            callback.invoke()
            dismiss()
        }

        view.tvCancel.setOnClickListener {
            dismiss()
        }

        dialog.setContentView(view)

        return dialog
    }

}