package com.proto.type.chat.newchat

import android.text.Editable
import android.text.TextWatcher
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.proto.type.base.Constants
import com.proto.type.base.adapter.UserAdapter
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.showViews
import com.proto.type.base.utils.ViewUtils
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.fragment_new_chat.*
import kotlinx.android.synthetic.main.layout_new_chat_options.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewChatFragment: BaseFragment() {

    // Override Variable
    override var layoutId = R.layout.fragment_new_chat

    // Private Constants
    private val viewModel: NewChatViewModel by viewModel()

    private lateinit var userAdapter: UserAdapter

    override fun initView() {
        super.initView()
        setupAdapter()
    }

    override fun initEvent() {
        super.initEvent()

        viewBtnGroupChat.setOnClickListener {
            navigateTo(R.id.action_new_message_to_addParticipantFragment,
                bundleOf(
                    Constants.KEY_IS_SELF_CHAT to false
                )
            )
        }

        viewBtnSelfChat.setOnClickListener {
            navigateTo(R.id.action_new_message_to_createGroupFragment,
                bundleOf(
                    Constants.KEY_IS_SELF_CHAT to true
                )
            )
        }

        toolbar.apply {
            addOnRightTextClickListener {
                navigateBack()
            }
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchUserList(p0.toString())
            }
        })
    }

    override fun initLogic() {
        super.initLogic()
        viewModel.getAllContacts()
        viewModel.users.observe(this, Observer {
            when (it) {
                is UIState.LOADING -> {
                    showLoading(true)
                    ViewUtils.hideViews(rvFriendOnChatQ)
                    showViews(tvChatQUser, loaderChatQ)
                }
                is UIState.FINISHED<*> -> {
                    hideLoading()
                    loaderChatQ.hideView()
                    val users = it.data as? MutableList<UserModel> ?: mutableListOf()
                    if (!users.isNullOrEmpty()) {
                        showViews(tvChatQUser, rvFriendOnChatQ)
                        tvChatQUser.text = String.format(getString(R.string.user_chatq_number), users.size)
                        userAdapter.display(users)
                    } else {
                        tvChatQUser.hideView()
                    }
                }
                is UIState.FAILED -> {
                    hideLoading()
                    loaderChatQ.hideView()
                    showInfo(getString(R.string.txt_unexpected_error))
                }
                else -> {}
            }
        })
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.chat.removeObservers(this)
        viewModel.users.removeObservers(this)
    }

    private fun setupAdapter() {
        userAdapter = UserAdapter { user ->
            viewModel.setPrivateParticipant(user)
            viewModel.getChat(user.id)
            viewModel.chat.observe(this, Observer {
                when (it) {
                    is UIState.FINISHED<*> -> {
                        navigateTo(
                            R.id.action_new_message_to_chat,
                            bundleOf(
                                Constants.KEY_ROOM_ID to (it.data as? ChatModel)?.id,
                                Constants.KEY_PARTICIPANT_ID to viewModel.privateParticipant.value?.id
                            )
                        )
                    }
                    is UIState.FAILED -> {
                        showInfo(getString(R.string.txt_unexpected_error))
                    }
                    else -> {}
                }
            })
        }
        rvFriendOnChatQ.adapter = userAdapter
    }
}