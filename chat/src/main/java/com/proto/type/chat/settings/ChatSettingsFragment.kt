package com.proto.type.chat.settings

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.proto.type.base.*
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.*
import com.proto.type.base.ui.dialog.FullScreenPhotoDialog
import com.proto.type.chat.dialog.ManageGroupChatDialog
import com.proto.type.chat.dialog.ParticipantActionDialog
import com.proto.type.chat.dialog.ChooseAdminDialog
import com.proto.type.base.ui.dialog.ImagePickerDialog
import com.proto.type.base.utils.RuntimePermissionCallback
import com.proto.type.base.utils.RuntimePermissionUtils
import com.proto.type.chat.R
import com.proto.type.chat.dialog.ConfirmChatActionDialog
import com.proto.type.chat.newchat.SpacingItemDecoration
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import kotlinx.android.synthetic.main.fragment_chat_settings.*
import kotlinx.android.synthetic.main.fragment_chat_settings.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class ChatSettingsFragment: BaseFragment() {

    // Override Variable
    override var layoutId = R.layout.fragment_chat_settings

    // Private Constants
    private val maxShowingParticipantsCount = 3
    private val roomId: String by lazy { arguments?.getString(Constants.KEY_ROOM_ID) ?: "" }
    private val viewModel: ChatSettingsViewModel by viewModel()

    // Private Variables
    private var avatarUri: Uri? = null
    private lateinit var chatSettingsMediaListingAdapter: ChatSettingsMediaListingAdapter
    private lateinit var chatSettingsParticipantListingAdapter: ChatSettingsParticipantListingAdapter

    // MARK: - Override Functions
    override fun initView() {
        super.initView()
        setChatName(getString(R.string.chatq_room))
        setUIShowDoodleBackground(viewModel.getShowDoodleBackgroundStatus(roomId))
        textViewParticipants.text = String.format(getString(R.string.participants_title), 0)
        updateChatUI(false, getString(R.string.chatq_room))
        ivProfilePhoto.setImage(R.drawable.ic_group)
    }

    override fun initEvent() {
        super.initEvent()

        clClearChatHistoryItem.setOnClickListener {
            viewModel.getCurrentChat()?.let {
                showConfirmClearChatDialog(it, viewModel.getOtherUserInPrivateChat())
            }
        }

        clCreateChatAction.setOnClickListener {
            val participantIds = viewModel.getParticipantsIds()
            if (viewModel.getChatCategory() == Constants.CHAT_CATEGORY_PRIVATE) {
                if (participantIds.isEmpty()) return@setOnClickListener
                navigateTo(R.id.action_chatSettingsFragment_to_createGroupFragment,
                    bundleOf(
                        Constants.KEY_CHAT_PARTICIPANTS_IDS to participantIds,
                        Constants.KEY_IS_ADD_FRIEND to true
                    )
                )
            } else {
                navigateTo(R.id.action_chatSettingsFragment_to_addParticipantFragment,
                    bundleOf(
                        Constants.KEY_CHAT_PARTICIPANTS_IDS to participantIds,
                        Constants.KEY_ROOM_ID to roomId
                    )
                )
            }
        }

        clLeaveChatItem.setOnClickListener {
            viewModel.getCurrentChat()?.let {
                showConfirmLeaveChatDialog(it, viewModel.getOtherUserInPrivateChat())
            }
        }

        clSearchMessageItem.setOnClickListener {
            // TODO("Handle tap to search for local messages in a chat")
             showInfo("Search messages pressed")
        }

        clToogleDoodleBackgroundItem.setOnClickListener {
            toggleDoodleBackground()
        }

        clToogleMuteStatusItem.setOnClickListener {
            viewModel.setMuteNotification(!(viewModel.getCurrentChat()?.mute_notification ?: true), roomId)
        }

        etUserName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { } // Don't need to do anything

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { } // Don't need to do anything

            override fun afterTextChanged(s: Editable) {
                ivClearEditName.visibility = if (s.isEmpty()) View.INVISIBLE else View.VISIBLE
            }
        })

        ivClearEditName.setOnClickListener {
            etUserName.setText("")
        }

        ivProfilePhoto.setOnClickListener {
            viewModel.getOtherUserInPrivateChat()?.let { user ->
                FullScreenPhotoDialog(user, false).show(childFragmentManager, "fullscreen_photo")
            } ?: run {
                FullScreenPhotoDialog(viewModel.getCurrentChat(), false).show(childFragmentManager, "fullscreen_photo")
            }
        }

        textViewManageChat.setOnClickListener {
            when (viewModel.getChatCategory()) {
                Constants.CHAT_CATEGORY_PRIVATE -> {
                    viewModel.getOtherUserInPrivateChat()?.let {
                        navigateTo(
                            R.id.action_chatSettingsFragment_to_userProfileFragment,
                            bundleOf(Constants.KEY_USER_ID to it.id)
                        )
                    }
                }
                else -> {
                    if (!viewModel.isCurrentUserAdmin()) {
                        showInfo("You don't have permission to edit this group")
                    } else {
                        ManageGroupChatDialog { action ->
                            when (action) {
                                ManageGroupChatDialog.ACTION_EDIT_GROUP_NAME -> {
                                    toggleEditGroupNameMode(true)
                                }
                                ManageGroupChatDialog.ACTION_CHANGE_GROUP_AVATAR -> {
                                    ImagePickerDialog(shouldShowDeleteOption = false) {
                                        when (it) {
                                            ImagePickerDialog.ACTION_TAKE_PICTURE -> {
                                                avatarUri = null
                                                RuntimePermissionUtils.requestPermission(requireActivity(),
                                                    listOf(
                                                        Manifest.permission.CAMERA,
                                                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                        Manifest.permission.READ_EXTERNAL_STORAGE
                                                    ),
                                                    object : RuntimePermissionCallback {
                                                        override fun onGranted() { runCamera() }
                                                        override fun onFailed() { } // Don't do anything
                                                    })
                                            }
                                            ImagePickerDialog.ACTION_PICK_PHOTO -> {
                                                avatarUri = null
                                                RuntimePermissionUtils.requestPermission(requireActivity(), listOf(
                                                    Manifest.permission.CAMERA,
                                                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE),
                                                    object: RuntimePermissionCallback {
                                                        override fun onGranted() {
                                                            Matisse.from(this@ChatSettingsFragment)
                                                                .choose(MimeType.ofImage())
                                                                .countable(true)
                                                                .maxSelectable(1)
                                                                .theme(R.style.Matisse_Zhihu)
                                                                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                                                                .thumbnailScale(0.85f)
                                                                .imageEngine(GlideEngine())
                                                                .showPreview(true)
                                                                .autoHideToolbarOnSingleTap(true)
                                                                .forResult(Constants.Request.REQUEST_CODE_CHOOSE)
                                                        }
                                                        override fun onFailed() { } // Don't do anything
                                                    })
                                            }
                                        }
                                    }.show(childFragmentManager, "picker")
                                }
                            }
                        }.show(childFragmentManager, "manage_group_chat")
                    }
                }
            }
        }

        textViewViewAll1.setOnClickListener {
            showSharedMediaScreen()
        }

        textViewViewAll2.setOnClickListener {
            navigateTo(
                R.id.action_chatSettingsFragment_to_participantsFragment,
                bundleOf(Constants.KEY_ROOM_ID to roomId)
            )
        }

        toolbar.apply {
            addOnBackClickListener {
                if (toolbar.isEditMode) {
                    setChatName(viewModel.getChatName())
                    toggleEditGroupNameMode(false)
                } else {
                    navigateBack()
                }
            }

            addOnSaveClickListener {
                this.showBackView()
                viewModel.updateRoomName(roomId, etUserName.text.toString())
            }
        }
    }

    override fun initLogic() {
        super.initLogic()

        // Setup chat
        viewModel.chat.observe(this, Observer {
            when (it) {
                is UIState.LOADING -> {
                    if (it.messageId != viewModel.CHAT_REALM_MESSAGE_ID) { showLoading() }
                }
                is UIState.FINISHED<*> -> {
                    if (it.messageId != viewModel.CHAT_REALM_MESSAGE_ID) { hideLoading() }
                    (it.data as? ChatModel)?.let { chatRoom ->
                        setChatName(if (chatRoom.category != Constants.CHAT_CATEGORY_PRIVATE) chatRoom.name ?: "" else viewModel.getOtherUserInPrivateChat()?.display_name ?: "")
                        toggleEditGroupNameMode(false)
                        updateChatUI(chatRoom.category == Constants.CHAT_CATEGORY_PRIVATE, chatRoom.name ?: "")
                        setChatNotificationStatus(chatRoom.mute_notification)
                        if (chatRoom.category != Constants.CHAT_CATEGORY_PRIVATE) {
                            ivProfilePhoto.loadAvatar(chatRoom)
                        }
                        if (it.messageId == viewModel.CHAT_REALM_MESSAGE_ID) { viewModel.loadParticipants(chatRoom.participant_ids ?: listOf(), chatRoom.admin_ids) }
                    }
                }
                is UIState.FAILED -> {
                    if (it.messageId != viewModel.CHAT_REALM_MESSAGE_ID) { hideLoading() }
                    showInfo("There is an issue with the app at the moment. Please try again later.")
                }
                else -> {}
            }
        })
        viewModel.getChatFromRealm(roomId)

        // Setup private chat
        viewModel.sendPrivateChat.observe(this, Observer {
            when (it) {
                is UIState.FINISHED<*> -> {
                    navigateTo(
                        R.id.action_chatSettingsFragment_to_inbox,
                        bundleOf(Constants.KEY_OPEN_ROOM_ID to (it.data as? ChatModel)?.id)
                    )
                }
                is UIState.FAILED -> {
                    showInfo(getString(R.string.txt_unexpected_error))
                }
                else -> {}
            }
        })

        // Setup medias
        viewModel.messageMediaItems.observe(this, Observer {
            clEmptySharedMedia.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
            textViewViewAll1.visibility = if (it.isEmpty()) View.GONE else View.VISIBLE
            chatSettingsMediaListingAdapter.setData(it)
        })
        viewModel.getInitialMessageMediaItems(roomId, 5) // Only need to show top 5 media messages per requirement
        chatSettingsMediaListingAdapter = ChatSettingsMediaListingAdapter(
            context!!,
            mutableListOf()
        ) { action, _, _ ->
            when (action) {
                ChatSettingsMediaListingAdapter.Actions.ACTION_SELECT -> {
                    showSharedMediaScreen()
                }
            }
        }
        rvSharedMedia.addItemDecoration(SpacingItemDecoration(5, context!!.dpToPx(6.8.toFloat()), false))
        rvSharedMedia.adapter = chatSettingsMediaListingAdapter

        // Setup participants
        viewModel.participants.observe(this, Observer {
            textViewParticipants.text = String.format(getString(R.string.participants_title), it.size)
            chatSettingsParticipantListingAdapter.setData(if (it.size > maxShowingParticipantsCount) it.subList(0, maxShowingParticipantsCount) else it)
            textViewViewAll2.visibility = if (it.size > maxShowingParticipantsCount) View.VISIBLE else View.GONE
            viewModel.getOtherUserInPrivateChat()?.let { user ->
                ivProfilePhoto.loadAvatar(user)
                textViewCreateGroup.text = String.format(getString(R.string.new_group_with), user.display_name)
                textViewUserName.text = user.display_name
            }
        })
        chatSettingsParticipantListingAdapter = ChatSettingsParticipantListingAdapter(
            context!!,
            mutableListOf()
        ) { action, user, _ ->
            when (action) {
                ChatSettingsParticipantListingAdapter.Actions.ACTION_SELECT -> {
                    ParticipantActionDialog(viewModel.isAdmin(user), viewModel.isCurrentUser(user) , viewModel.isCurrentUserAdmin()) {
                        when (it) {
                            ParticipantActionDialog.ACTION_OPEN_PROFILE -> {
                                navigateTo(
                                    R.id.action_chatSettingsFragment_to_userProfileFragment,
                                    bundleOf(Constants.KEY_USER_ID to user.id)
                                )
                            }
                            ParticipantActionDialog.ACTION_SEND_PRIVATE_MESSAGE -> {
                                viewModel.getChatRoom(user.id)
                            }
                            ParticipantActionDialog.ACTION_MAKE_GROUP_ADMIN -> {
                                showLoading()
                                viewModel.promoteDemoteUser(roomId, user.id) {
                                    activity?.runOnUiThread {
                                        hideLoading()
                                        if (it) {
                                            viewModel.addNewAdmin(user).apply { showInfo(getString(R.string.success)) }
                                        } else {
                                            showInfo("Promote admin failed. Please try again later!")
                                        }
                                    }
                                }
                            }
                            ParticipantActionDialog.ACTION_REMOVE_ADMIN -> {
                                showLoading()
                                viewModel.promoteDemoteUser(roomId, user.id) {
                                    activity?.runOnUiThread {
                                        if (it) {
                                            viewModel.removeCurrentAdmin(user).apply {
                                                hideLoading()
                                                showInfo(getString(R.string.success))
                                            }
                                        } else {
                                            showInfo("Promote admin failed. Please try again later!")
                                        }
                                    }
                                }
                            }
                            ParticipantActionDialog.ACTION_REMOVE_FROM_GROUP -> {
                                if (viewModel.isCurrentUserAdmin()) {
                                    showLoading()
                                    viewModel.removeChatMembers(roomId, listOf(user.id)) { success ->
                                        hideLoading()
                                        showInfo(if (success) getString(R.string.success) else "Remove participant failed. Please try again later!")
                                    }
                                } else {
                                    showInfo("You don't have permission to perform this action.")
                                }
                            }
                        }
                    }.show(childFragmentManager, "participant_actions")
                }
            }
        }
        recyclerViewParticipants.adapter = chatSettingsParticipantListingAdapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.Request.REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK && data != null) {
            avatarUri = Matisse.obtainResult(data)[0]
        }
        if (avatarUri != null) {
            viewModel.updateRoomAvatar(avatarUri!!, roomId)
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.chat.removeObservers(this)
        viewModel.messageMediaItems.removeObservers(this)
        viewModel.participants.removeObservers(this)
        viewModel.sendPrivateChat.removeObservers(this)
    }

    // MARK: - Private Functions
    private fun runCamera() {
        val path = File(requireActivity().filesDir, "images")
        val image = File(File(requireActivity().filesDir, "images"), "${System.currentTimeMillis()}.jpg")
        path.mkdir()
        avatarUri = FileProvider.getUriForFile(context!!, "com.proto.type", image)
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, avatarUri)
            activity?.packageManager?.let {
                takePictureIntent.resolveActivity(it)?.also {
                    startActivityForResult(takePictureIntent, Constants.Request.REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }

    private fun setChatName(roomName: String) {
        etUserName.setText(roomName)
        textViewUserName.text = roomName
    }

    private fun setChatNotificationStatus(isMuted: Boolean) {
        imageViewNotification.setImage(if (isMuted) R.drawable.ic_notification_mute else R.drawable.ic_notification_on)
        textViewMuteNotification.text = getString(if (isMuted) R.string.unmute_notification else R.string.mute_notification)
    }

    private fun setUIShowDoodleBackground(isShown: Boolean) {
        imageViewBackground.setImage(if (isShown) R.drawable.ic_chat_background_show else R.drawable.ic_chat_background_hide)
        textViewHideBackground.text = getString(if (isShown) R.string.hide_background else R.string.show_background)
    }

    private fun showConfirmClearChatDialog(chatRoom: ChatModel, user: UserModel?) {
        ConfirmChatActionDialog(
            chatRoom,
            user,
            String.format(
                getString(R.string.are_you_sure_to_clear_chat),
                viewModel.getChatName()
            ),
            getString(R.string.clear_chat_history)
        ) {
            showLoading()
            viewModel.clearChatHistory(roomId) {
                activity?.runOnUiThread {
                    hideLoading()
                    if (it) {
                        viewModel.deleteChatMessages(roomId)
                    }
                    showInfo(if (it) getString(R.string.success) else "Clear chat history failed. Please try again later!")
                }
            }
        }.show(childFragmentManager,"clear")
    }

    private fun showConfirmLeaveChatDialog(chatRoom: ChatModel, user: UserModel?) {
        ConfirmChatActionDialog(
            chatRoom,
            user,
            String.format(
                getString(R.string.are_you_sure_to_leave_chat),
                viewModel.getChatName()
            ),
            getString(R.string.leave_chat)
        ) {
            if (viewModel.checkIsLastAdmin()) {
                ChooseAdminDialog { action ->
                    when (action) {
                        ChooseAdminDialog.ACTION_CHOOSE_ADMIN -> {
                            navigateTo(
                                R.id.action_chatSettingsFragment_to_participantsFragment,
                                bundleOf(Constants.KEY_ROOM_ID to roomId)
                            )
                        }
                        ChooseAdminDialog.ACTION_LEAVE_CHAT -> {
                            triggerLeaveChat()
                        }
                    }
                }.show(childFragmentManager, "choose_admin")
            } else {
                triggerLeaveChat()
            }
        }.show(childFragmentManager,"leave")
    }

    private fun showSharedMediaScreen() {
        navigateTo(
            R.id.action_chatSettingsFragment_to_sharedMediaFragment,
            bundleOf(Constants.KEY_ROOM_ID to roomId)
        )
    }

    private fun toggleDoodleBackground() {
        viewModel.toggleShowDoodleBackground(roomId)
        setUIShowDoodleBackground(viewModel.getShowDoodleBackgroundStatus(roomId))
    }

    private fun toggleEditGroupNameMode(enabled: Boolean) {
        etUserName.visibility = if (enabled) View.VISIBLE else View.INVISIBLE
        ivClearEditName.visibility = if (enabled) View.VISIBLE else View.INVISIBLE
        textViewManageChat.visibility = if (enabled) View.INVISIBLE else View.VISIBLE
        textViewUserName.visibility = if (enabled) View.INVISIBLE else View.VISIBLE
        toolbar.enterEditMode(enabled)
        if (enabled) {
            etUserName.showKeyboard()
        } else {
            toolbar.showBackView()
            etUserName.hideKeyboard()
        }
    }

    private fun triggerLeaveChat() {
        showLoading()
        viewModel.leaveChat(roomId) {
            activity?.runOnUiThread {
                hideLoading()
                if (it) {
                    viewModel.deleteChat(roomId).apply {
                        navigateTo(R.id.action_chatSettingsFragment_to_inbox)
                        showInfo(getString(R.string.success))
                    }
                    viewModel.deleteChatMessages(roomId)
                } else {
                    showInfo("Leave chat failed. Please try again later!")
                }
            }
        }
    }

    private fun updateChatUI(isPrivateChat: Boolean, roomName: String) {
        textViewManageChat.text = getString(if (isPrivateChat) R.string.view_profile else R.string.edit_group)
        textViewCreateGroup.text = if (isPrivateChat) String.format(getString(R.string.new_group_with), roomName) else getString(R.string.add_friend_to_group)
    }
}