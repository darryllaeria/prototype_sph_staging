package com.proto.type.chat.chat

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.MessageModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.*
import com.proto.type.base.ui.dialog.FullScreenPhotoDialog
import com.proto.type.base.ui.dialog.SuccessDialog
import com.proto.type.base.utils.AnimateUtils
import com.proto.type.base.utils.AppLog
import com.proto.type.base.utils.RuntimePermissionCallback
import com.proto.type.base.utils.RuntimePermissionUtils
import com.proto.type.chat.R
import com.proto.type.chat.dialog.MessageActionDialog
import com.proto.type.chat.newchat.SpacingItemDecoration
import com.proto.type.chat.settings.ChatSettingsParticipantListingAdapter
import kotlinx.android.synthetic.main.fragment_chat.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class ChatFragment: BaseFragment() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = ChatFragment::class.java.simpleName
    }

    // MARK: - Override Variable
    override var layoutId = R.layout.fragment_chat

    // MARK: - Private Constants
    private val REQUEST_IMAGE_CAPTURE = 102
    private val chatId: String by lazy { arguments?.getString(Constants.KEY_ROOM_ID) ?: "" }
    private val navDestination: String by lazy { arguments?.getString(Constants.KEY_BACK_NAV_DESTINATION) ?: "" }
    private val viewModel: ChatViewModel by viewModel()

    // MARK: - Private Variables
    private lateinit var chatPagingAdapter: ChatPagingAdapter
    private lateinit var galleryAdapter: GalleryAdapter
    private lateinit var mentionListingAdapter: MentionListingAdapter
    private var pictureImagePath = ""
    private lateinit var textSuggestionListingAdapter: TextSuggestionListingAdapter

    // MARK: - Override Functions
    override fun cleanupOnFragmentStop() {
        super.cleanupOnFragmentStop()
        hideImagePicker()
    }

    override fun cleanupSubscriptions() {
        super.cleanupSubscriptions()
        viewModel.cleanupChatMessagesManager()
        viewModel.cleanupTypingManager()
        viewModel.cleanupLatestPriceSubscriptionIfAny()
        viewModel.unsubscribeUsersTypingTopic(chatId)
    }

    override fun initOneTimeLogic() {
        super.initOneTimeLogic()
        bindingActionView()
    }

    override fun initView() {
        super.initView()
        initMentionAdapter()
        initImageAdapter()
        setDoodleBackground()
        tvChatTitle.text = getString(R.string.chatq_room)
        tvChatActive.text = getString(R.string.loading)
    }

    override fun initEvent() {
        super.initEvent()

        clTopBar.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                mlTopBarContainer.transitionToStart()
            }
            false
        }

        // Setup send message text box text change event
        etMessage.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p2 == p3) return
                hideImagePicker()
                viewModel.publishUsersTypingTopic(chatId)

                // Suggestion bar
                p0.toString()?.let {
                    val lastString = it.getLastPhrase()
                    if (lastString.startsWith("@"))
                        viewModel.findMention(it)
                    else
                        viewModel.findSuggestions(it)
                    viewModel.performHighlight(etMessage, context!!)
                }

                // TODO("Uncomment when working on bot store feature")
                // if (!p0.isNullOrEmpty()) {
                //     ivBotOrSend.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_send))
                // } else {
                //     ivBotOrSend.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_bot_store))
                // }
            }
        })

        // Setup send message text box focus change event
        etMessage.setOnFocusChangeListener { view, _ ->
            if (!view.isFocused) {
                etMessage.hideKeyboard()
            } else {
                hideImagePicker()
            }
        }

        // Setup hide image picker when text box is clicked
        etMessage.setOnClickListener {
            hideImagePicker()
        }

        // Setup back action
        ivBack.setOnClickListener {
            handleBackEvent()
        }

        // Setup send message or open bot store action
        ivBotOrSend.setOnClickListener {
            val blockData = viewModel.checkIfCurrentUserBlocked()
            if (blockData.second) {
                showError("Can’t send message due to ${blockData.first?.displayingName() ?: ""}'s privacy settings")
                return@setOnClickListener
            }
            when (flipper.displayedChild) {
                0 -> {
                    if (etMessage.text.toString().trim().isEmpty()) {
                        etMessage.text.clear()
                        etMessage.hideKeyboard()
                        return@setOnClickListener
                    }
                    viewModel.sendTextMessage(etMessage.text.toString(), chatId)
                    etMessage.text.clear()
                }
                1 -> {
                    if (galleryAdapter.selectedItems.isNotEmpty()) {
                        viewModel.sendImagesMessage(galleryAdapter.selectedItems.toMutableList(), chatId)
                        hideImagePicker()
                        etMessage.hideKeyboard()
                        flipper.displayedChild = 0
                    }
                }
            }
        }

        // Setup open chat settings action
        tvChatActive.setOnClickListener {
            navigateTo(
                R.id.action_chat_to_chatSettingsFragment,
                bundleOf(Constants.KEY_ROOM_ID to chatId)
            )
        }

        // Setup open manage bots action
        ivChatBot.invisibleView() // TODO("Remove this line when implement bot feature, temporary hide for now")
        ivChatBot.setOnClickListener {
            showInfo("Chat bot clicked")
        }

        // Setup open chat notification action
        ivChatNotification.invisibleView() // TODO("Remove this line when implement bot feature, temporary hide for now")
        ivChatNotification.setOnClickListener {
            showInfo("Notification clicked")
        }

        // Setup back action
        ivChatUnread.setOnClickListener {
            handleBackEvent()
        }

        // Setup open gallery action
        ivPictureSelect.setOnClickListener {
            if (!context!!.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                RuntimePermissionUtils.requestPermission(requireActivity(),
                    listOf(Manifest.permission.READ_EXTERNAL_STORAGE), object:
                        RuntimePermissionCallback {
                        override fun onGranted() {
                            getImageGalleryAndShowPicker()
                        }

                        override fun onFailed() { }
                    })
            } else {
                if (!rvGallery.isShown) {
                    getImageGalleryAndShowPicker()
                } else {
                    hideImagePicker()
                }
            }
        }

        // Setup open chat settings action
        tvChatTitle.setOnClickListener {
            navigateTo(
                R.id.action_chat_to_chatSettingsFragment,
                bundleOf(Constants.KEY_ROOM_ID to chatId)
            )
        }

        // Setup unblock user action
        tvUnblockUser.setOnClickListener {
            viewModel.getOtherUserInPrivateChat()?.let { user ->
                showLoading()
                viewModel.unblockUser(user.id) {
                    hideLoading()
                    if (it) showInfo(getString(R.string.success)) else showError("Unable to unblock this user at the moment. Please try again later.")
                }
            }
        }

        // Setup back button from gallery after image selected
        ivGalleryBack.setOnClickListener {
            hideImagePicker()
        }

        // Setup hide keyboard or image picker when scroll up on chat list
        rvChatList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy < 0) {
                    etMessage.hideKeyboard()
                    hideImagePicker()
                }
            }
        })
    }

    override fun initLogic() {
        super.initLogic()

        // Setup market data suggestions
        viewModel.allSuggestions.observe(this, Observer { suggestions ->
            AppLog.d(TAG, "Loaded suggestions: $suggestions")
        })
        viewModel.findAssetExchanges()
        viewModel.addLatestPriceSubscriptionIfAny()
        textSuggestionListingAdapter = TextSuggestionListingAdapter(
            context!!,
            mutableListOf()
        ) { action, suggestion, _ ->
            when (action) {
                ChatSettingsParticipantListingAdapter.Actions.ACTION_SELECT -> {
                    etMessage.replaceLast(
                        toReplaceText = etMessage.text.toString().findSuggestionText(),
                        replaceText = suggestion.name + " "
                    )
                    mlTopBarContainer.visibility = View.VISIBLE
                    tvInstrumentId.text = suggestion.id
                    tvInstrumentName.text = suggestion.name
                    tvLivePrice.text = "-"
                    tvLivePercent.text = "%0.00"
                    tvPriceTime.text = getString(R.string.today)
                    mlTopBarContainer.transitionToEnd()
                    viewModel.updateAndObserveSelectedSuggestion(suggestion.id)
                }
            }
        }
        viewModel.displaySuggestions.observe(this, Observer {
            rvSuggestion.adapter = textSuggestionListingAdapter
            rvSuggestion.animate()
                .alpha(if (it.isNotEmpty()) 1f else 0f)
                .translationY(if (it.isNotEmpty()) 4f else 8f)
                .setDuration(100)
                .start()
            textSuggestionListingAdapter.setData(it.toMutableList())
        })
        viewModel.latestPrice.observe(this, Observer {
            tvLivePrice.text = it
        })

        // Setup chat messages layoutManager and pagingAdapter
        val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        linearLayoutManager.stackFromEnd = true
        rvChatList.layoutManager = linearLayoutManager
        chatPagingAdapter = ChatPagingAdapter(UserModel(), listOf(), ChatModel()) { action, user, position, message ->
            when (action) {
                ChatPagingAdapter.ACTION_USER_IMAGE, ChatPagingAdapter.ACTION_USER_CHART, ChatPagingAdapter.ACTION_RESPONDER_IMAGE, ChatPagingAdapter.ACTION_RESPONDER_CHART -> {
                    if (!message?.data?.value.isNullOrEmpty()) {
                        FullScreenPhotoDialog(message?.data?.value_url, false).show(childFragmentManager, "fullscreen_photo")
                    }
                }
                ChatPagingAdapter.ACTION_RESPONDER_PROFILE -> {
                    if (message?.sender?.type == Constants.Message.CHAT_SENDER_USER) {
                        navigateTo(
                            R.id.action_chatFragment_to_userProfileFragment,
                            bundleOf(Constants.KEY_USER_ID to message.sender.id)
                        )
                    }
                }
            }
        }
        chatPagingAdapter.setHasStableIds(true)
        chatPagingAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (chatPagingAdapter.itemCount - 1 >= 0) {
                    linearLayoutManager.isSmoothScrollbarEnabled = false
                    linearLayoutManager.scrollToPosition(chatPagingAdapter.itemCount - 1)
                }
            }
        })
        chatPagingAdapter.messageOnLongPressed.observe(viewLifecycleOwner, Observer {
            viewModel.messageAction.postValue(it)
        })
        rvChatList.apply {
            adapter = chatPagingAdapter
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            (itemAnimator as SimpleItemAnimator).changeDuration = 0
            hasFixedSize()
        }

        // Setup current user data
        viewModel.currentUser.observe(this, Observer { user ->
            chatPagingAdapter.setCurrentUser(user)
            viewModel.getOtherUserInPrivateChat()?.let { privateUser ->
                tvUnblockUser.visibility = if (user.blockee_ids.contains(privateUser.id)) View.VISIBLE else View.GONE
            }
        })
        viewModel.loadCurrentUserFromRealm()
        viewModel.getBlockList()

        // Setup chat participants
        viewModel.participants.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                chatPagingAdapter.setParticipants(it)
                viewModel.getOtherUserInPrivateChat()?.let { user ->
                    tvChatTitle.text = user.displayingName()
                    tvChatActive.text = user.lastOnlineTime(context!!)
                    tvUnblockUser.visibility = if (user.blockee_ids.contains(user.id)) View.VISIBLE else View.GONE
                }

                // Getting messages from server
                viewModel.getInitialMessages(chatId)
                viewModel.messages.observe(this, Observer { messages ->
                    chatPagingAdapter.submitList(messages)
                })
            }
        })

        // Setup chat data
        viewModel.getChatDetailFromServer(chatId)
        viewModel.chat.observe(this, Observer {
            when (it) {
                is UIState.FINISHED<*> -> {
                    (it.data as? ChatModel)?.let { chat ->
                        chatPagingAdapter.setChat(chat)
                        if (chat.category != Constants.CHAT_CATEGORY_PRIVATE) {
                            tvChatTitle.text = chat.name
                            tvChatActive.text = String.format(getString(R.string.chat_members), chat.participant_ids?.size ?: 0)
                        }
                        viewModel.loadParticipants(chat.participant_ids ?: listOf())
                    }
                }
            }
        })

        // Reset mark read flag
        viewModel.isMarkRead = false

        // Setup MQTT Subscriptions
        viewModel.subscribeUsersTypingTopic(chatId)
        viewModel.setupChatMessagesManager()

        // Setup Users Typing
        viewModel.setupTypingManager()
        viewModel.isTypingMessage.observe(this, Observer {
            if (it.isNotEmpty()) {
                tvTypingNames.text = it
                clIsTyping.animate()
                    .translationY(context!!.dpToPx(0f).toFloat())
                    .setDuration(200)
                    .start()
                clIsTyping.visibility = View.VISIBLE
            } else {
                clIsTyping.animate()
                    .translationY(context!!.dpToPx(32f).toFloat())
                    .setDuration(200)
                    .withEndAction {
                        clIsTyping.visibility = View.GONE
                        tvTypingNames.text = it
                    }
                    .start()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == Activity.RESULT_OK) {
                val imgFile = File(pictureImagePath)
                if (imgFile.exists()) {
                    mutableListOf(imgFile.absolutePath).let { viewModel.sendImagesMessage(it, chatId) }
                }
            } else {
                hideImagePicker()
            }
        }
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.allSuggestions.removeObservers(this)
        viewModel.chat.removeObservers(this)
        viewModel.currentUser.removeObservers(this)
        viewModel.displayMentions.removeObservers(this)
        viewModel.displaySuggestions.removeObservers(this)
        viewModel.isTypingMessage.removeObservers(this)
        viewModel.latestPrice.removeObservers(this)
        viewModel.messages.removeObservers(this)
        viewModel.participants.removeObservers(this)
    }

    // MARK: - Private Functions
    private fun bindingActionView() {
        viewModel.messageAction.observe(this, Observer { message ->
            message?.let {
                initMessageActionDialog(message)
                    .show(childFragmentManager, "message_action")
            } ?: run { // message == null
                toggleRepliedMessage(false)
            }
        })
    }

    private fun getImageGalleryAndShowPicker() {
        viewModel.galleryImages.observe(this, Observer {
            galleryAdapter.setData(it)
            showImagePicker()
        })
        viewModel.getAllImages(requireActivity())
    }

    private fun handleBackEvent() {
        if (viewModel.isMarkRead) return // Stop spamming back button

        // Mark read chat before leaving
        viewModel.isMarkRead = true
        viewModel.markReadChat(chatId) {
            viewModel.isMarkRead = false
            if (navDestination != "")
                navigateTo(Uri.parse(navDestination))
            else
                navigateBack()
        }
    }

    private fun hideImagePicker() {
        if (flipper.displayedChild == 1) { flipper.displayedChild = 0 }
        ivPictureSelect.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_take_picture))
        AnimateUtils.collapse(rvGallery)
        rvGallery.visibility = View.GONE
    }

    private fun initImageAdapter() {
        galleryAdapter = GalleryAdapter(context!!) {
            if (context!!.isGranted(Manifest.permission.CAMERA) && context!!.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                startCamera()
            } else {
                RuntimePermissionUtils.requestPermission(requireActivity(), listOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE), object: RuntimePermissionCallback {
                    override fun onGranted() {
                        hideImagePicker()
                        startCamera()
                    }

                    override fun onFailed() {
                        hideImagePicker()
                    }
                })
            }
        }
        rvGallery.addItemDecoration(SpacingItemDecoration(3, 8, false))
        rvGallery.adapter = galleryAdapter
    }

    private fun initMessageActionDialog(message: MessageModel): MessageActionDialog =
        MessageActionDialog(message) { action ->
            when (action) {
                MessageActionDialog.ACTION_COPY -> {
                    // Copy to Clipboard
                    val clipboard =
                        context!!.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val textToCopy = viewModel.messageAction.value?.data?.value ?: ""
                    val clip = ClipData.newPlainText("RANDOM UUID", textToCopy)
                    clipboard.primaryClip = clip

                    // Close ActionView and show check animate
                    SuccessDialog().show(childFragmentManager, "success_animate")
                }
                MessageActionDialog.ACTION_REPLY -> {
                    etMessage.showKeyboard()
                    viewModel.messageReplied = viewModel.messageAction.value
                    RepliedMessageFragment.newInstance(message)?.let { repliedFragment ->
                        repliedFragment.didPressedClose = {
                            toggleRepliedMessage(false)
                        }
                        requireActivity().supportFragmentManager
                            .beginTransaction()
                            .add(
                                R.id.clRepliedFragment,
                                repliedFragment,
                                "replied_fragment"
                            )
                            .commit()
                    }
                    toggleRepliedMessage(true)
                }
                MessageActionDialog.ACTION_FORWARD -> {
                    navigateTo(
                        R.id.action_chat_to_forward_message,
                        bundleOf(Constants.KEY_MESSAGE_ACTION_ID to message.id)
                    )
                }
            }
        }

    private fun initMentionAdapter() {
        mentionListingAdapter = MentionListingAdapter(context!!)
        mentionListingAdapter.userSelectedObservable.observe(this, Observer {
            val textAndRange = etMessage.text.toString().findMentionTextAndRange() ?: return@Observer
            val replacingStr = "@" + it.display_name + " "
            val replacedText = etMessage.text.replaceRange(textAndRange.second, replacingStr)
            etMessage.setText(replacedText)
            etMessage.setSelection(etMessage.length())
        })
        viewModel.displayMentions.observe(this, Observer {
            rvSuggestion.adapter = mentionListingAdapter
            rvSuggestion.animate()
                .alpha(if (it.isNotEmpty()) 1f else 0f)
                .translationY(if (it.isNotEmpty()) 4f else 8f)
                .setDuration(100)
                .start()
            mentionListingAdapter.setData(it)
        })
    }

    private fun setDoodleBackground() {
        val shouldShow = viewModel.getShowDoodleBackgroundStatus(chatId)
        imageViewDoodleBackground.visibility = if (shouldShow) View.VISIBLE else View.INVISIBLE
        rvChatList.setBackgroundColor(if (shouldShow) Color.argb(0,255, 255, 255) else Color.argb(100,227, 233, 240))
    }

    private fun toggleRepliedMessage(isShow: Boolean) {
        if (isShow) clRepliedFragment.visibility = View.VISIBLE
        clRepliedFragment.animate()
            .translationY(if (isShow) 0f else clRepliedFragment.height.toFloat())
            .setDuration(100)
            .withEndAction {
                if (!isShow) clRepliedFragment.visibility = View.GONE
            }
            .start()
    }

    private fun showImagePicker() {
        ivPictureSelect.setImageDrawable(ContextCompat.getDrawable(context!!, R.drawable.ic_take_picture_selected))
        rvGallery.visibility = View.VISIBLE
        AnimateUtils.expand(rvGallery, height = getScreenHeight() / 3)
        etMessage.hideKeyboard()
        galleryAdapter.observableItems.observe(this, Observer {
            if (it.isEmpty()) {
                flipper.displayedChild = 0
            } else {
                flipper.displayedChild = 1
            }
        })
    }

    private fun startCamera() {
        StrictMode.setVmPolicy(VmPolicy.Builder().build())
        pictureImagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath.toString() + "/${SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())}.jpg"
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            activity?.packageManager?.let {
                takePictureIntent.resolveActivity(it)?.also {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(pictureImagePath)))
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
    }
}