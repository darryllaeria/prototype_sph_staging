package com.proto.type.chat.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.proto.type.base.data.model.MessageModel
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.fragment_replied_message.view.*

class RepliedMessageFragment(
    private val message: MessageModel
): Fragment() {

    var didPressedClose: (() -> Unit)? = null

    companion object {
        fun newInstance(message: MessageModel): RepliedMessageFragment {
            return RepliedMessageFragment(message)
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_replied_message, container, false)
        view.tvRepliedSender.text = message.sender.id
        view.tvRepliedMessage.text = message.data.value
        view.ibClose.setOnClickListener {
            didPressedClose?.invoke()
        }
        return view
    }
}