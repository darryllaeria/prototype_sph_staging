package com.proto.type.chat.inbox

import android.Manifest
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.showView
import com.proto.type.base.utils.CommonUtils
import com.proto.type.base.utils.RuntimePermissionCallback
import com.proto.type.base.utils.RuntimePermissionUtils
import com.proto.type.chat.R
import com.proto.type.chat.dialog.ChooseAdminDialog
import com.proto.type.chat.dialog.ConfirmChatActionDialog
import kotlinx.android.synthetic.main.fragment_inbox.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class InboxFragment: BaseFragment() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = InboxFragment::class.java.simpleName
    }

    // MARK: - Override Variable
    override var layoutId = R.layout.fragment_inbox

    // MARK: - Private Constants
    private val openRoomId: String by lazy { arguments?.getString(Constants.KEY_OPEN_ROOM_ID) ?: "" }
    private val viewModel: InboxViewModel by viewModel()

    // MARK: - Private Variables
    private lateinit var inboxListingAdapter: InboxListingAdapter

    // MARK: - Override Functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // TODO("Uncomment this line when we will fully understand and implement the Android service logic")
        // initMQTT()
    }

    override fun initView() {
        super.initView()
    }

    override fun initEvent() {
        super.initEvent()

        clSearch.setOnClickListener {
            startSearchInbox()
        }

        rvInbox.setOnScrollChangeListener { _, _, _, _, _ ->
            CommonUtils.hideKeyboard(requireContext())
        }

        toolbar.addOnToolbarButtonCallback {
            startNewChat()
        }
    }

    override fun initLogic() {
        super.initLogic()
        // Setup inboxListingAdapter
        inboxListingAdapter = InboxListingAdapter(context!!,
            disableSwipe = false,
            isBasicView = false
        ) { action, chat, position ->
            when (action) {
                InboxListingAdapter.Actions.ACTION_SELECT -> {
                    navigateTo(
                        R.id.action_inbox_to_chat,
                        bundleOf(Constants.KEY_ROOM_ID to chat.id)
                    )
                }
                InboxListingAdapter.Actions.ACTION_PIN -> {
                    viewModel.updateRoomPin(chat.is_pinned, chat.id)
                }
                InboxListingAdapter.Actions.ACTION_MUTE -> {
                    viewModel.updateRoomMute(chat.mute_notification, chat.id)
                }
                InboxListingAdapter.Actions.ACTION_LEAVE -> {
                    showConfirmLeaveChatDialog(chat, viewModel.getOtherUserInPrivateChat(chat), position)
                }
            }
        }
        rvInbox.adapter = inboxListingAdapter

        // Setup chat list
        viewModel.getChatList()
        viewModel.chats.observe(this, Observer { page ->
            updateView(page.size)
            inboxListingAdapter.submitList(page)
            page.firstOrNull { it.id == openRoomId }?.let { chat ->
                arguments?.remove(Constants.KEY_OPEN_ROOM_ID)
                navigateTo(
                    R.id.action_inbox_to_chat,
                    bundleOf(Constants.KEY_ROOM_ID to chat.id)
                )
            }
        })
        viewModel.loadingChatsState.observe(this, Observer {
//        when (it) {
//            is UIState.LOADING -> {
//                showLoading()
//            }
//            is UIState.FINISHED<*> -> {
//                val data = it.data as List<ChatModel>
//                updateView(data.size)
//            }
//            else -> {
//                hideLoading()
//            }
//        }
        })
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.loadingChatsState.removeObservers(this)
        viewModel.chats.removeObservers(this)
    }

    // MARK: - Private Functions
    private fun updateView(dataSize: Int) {
        if (dataSize == 0) {
            showLoading(true)
            rvInbox.hideView()
        } else {
            hideLoading()
            rvInbox.showView()
        }
    }

    private fun showConfirmLeaveChatDialog(chat: ChatModel, user: UserModel?, index: Int) {
        ConfirmChatActionDialog(
            chat,
            user,
            String.format(getString(R.string.are_you_sure_to_leave_chat), if (chat.category == Constants.CHAT_CATEGORY_PRIVATE) user?.display_name ?: "" else chat.name),
            getString(R.string.leave_chat)
        ) {
            showLoading()
            viewModel.getChatDetails(chat.id) { detailedChat ->
                hideLoading()
                detailedChat?.let { validDetailedChat ->
                    if (viewModel.checkIsLastAdmin(validDetailedChat)) {
                        ChooseAdminDialog { action ->
                            when (action) {
                                ChooseAdminDialog.ACTION_CHOOSE_ADMIN -> {
                                    showLoading()
                                    viewModel.getParticipants(
                                        validDetailedChat.participant_ids ?: listOf()
                                    ) {
                                        hideLoading()
                                        if (it) {
                                            navigateTo(
                                                R.id.action_inboxFragment_to_participantsFragment,
                                                bundleOf(
                                                    Constants.KEY_ROOM_ID to detailedChat.id
                                                )
                                            )
                                        } else {
                                            showInfo("Cannot open participants list at the moment. Please try again later.")
                                        }
                                    }
                                }
                                ChooseAdminDialog.ACTION_LEAVE_CHAT -> {
                                    viewModel.leaveChat(chat.id)
                                }
                            }
                        }.show(childFragmentManager, "choose_admin")
                    } else {
                        viewModel.leaveChat(chat.id)
                    }
                } ?: run {
                    viewModel.leaveChat(chat.id)
                }
            }
        }.show(childFragmentManager,"leave")
    }

    private fun startNewChat() {
        if (RuntimePermissionUtils.checkPermission(context!!, Manifest.permission.READ_CONTACTS)) {
            navigateTo(R.id.action_inbox_to_new_message)
        } else {
            RuntimePermissionUtils.requestPermission(requireActivity(), listOf(Manifest.permission.READ_CONTACTS), object: RuntimePermissionCallback {
                override fun onGranted() {
                    navigateTo(R.id.action_inbox_to_new_message)
                }

                override fun onFailed() {
                    showInfo(getString(R.string.txt_user_denied_access_contact))
                }

            })
        }
    }

    private fun startSearchInbox() {
        if (RuntimePermissionUtils.checkPermission(context!!, Manifest.permission.READ_CONTACTS)) {
            navigateTo(R.id.action_inbox_to_search_message)
        } else {
            RuntimePermissionUtils.requestPermission(requireActivity(), listOf(Manifest.permission.READ_CONTACTS), object: RuntimePermissionCallback {
                override fun onGranted() {
                    navigateTo(R.id.action_inbox_to_search_message)
                }

                override fun onFailed() {
                    showInfo(getString(R.string.txt_user_denied_access_contact))
                }
            })
        }
    }
}