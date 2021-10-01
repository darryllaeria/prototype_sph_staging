package com.proto.type.chat.forward

import android.text.Editable
import android.text.TextWatcher
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.data.model.MessageModel
import com.proto.type.chat.R
import org.koin.androidx.viewmodel.ext.android.viewModel
import androidx.lifecycle.Observer
import com.proto.type.chat.chat.ChatViewModel
import kotlinx.android.synthetic.main.fragment_forward_chat.*
import kotlinx.android.synthetic.main.fragment_shared_media.tabLayout
import kotlinx.android.synthetic.main.fragment_shared_media.toolbar
import kotlinx.android.synthetic.main.fragment_shared_media.viewPager

class ForwardChatFragment: BaseFragment() {

    // MARK: - Override Variable
    override var layoutId = R.layout.fragment_forward_chat

    // MARK: - ViewModel
    private val chatViewModel: ChatViewModel by viewModel()
    private val forwardViewModel: ForwardChatViewModel by viewModel()

    // MARK: - Private Variables
    private var messageAction: MessageModel? = null
    private var messageObserver = Observer<MessageModel?> {
        it?.let { message ->
            messageAction = message
            forwardViewModel.getChatsFromRealm()
        }
    }
    private lateinit var pagerAdapter: ForwardChatPagerAdapter
    private lateinit var groupChatAdapter: ForwardChatAdapter
    private lateinit var singleChatAdapter: ForwardChatAdapter

    // MARK: - Override Functions
    override fun initView() {
        super.initView()
        tabLayout.setupWithViewPager(viewPager)
        viewPager.pageMargin = 16
    }

    override fun initEvent() {
        super.initEvent()
        toolbar.apply {
            addOnBackClickListener {
                navigateBack()
            }
        }
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                p0?.let {
                    forwardViewModel.filterSearchText(it.toString())
                }
            }
        })
    }

    override fun initLogic() {
        super.initLogic()
        initForwardChatAdapter()
        initPageAdapter()
        forwardViewModel.message.observe(this, messageObserver)
        arguments?.getString(Constants.KEY_MESSAGE_ACTION_ID)
            ?.let { forwardViewModel.getMessageFromRealm(it) }
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        forwardViewModel.singleChats.removeObservers(this)
        forwardViewModel.groupChats.removeObservers(this)
    }

    // MARK: - Private Functions
    private fun initPageAdapter() {
        pagerAdapter = ForwardChatPagerAdapter(
            singleChatAdapter, groupChatAdapter,
            arrayOf(getString(R.string.friends), getString(R.string.group))
        )
        viewPager.adapter = pagerAdapter
    }

    private fun initForwardChatAdapter() {
        // Group Chat
        groupChatAdapter = ForwardChatAdapter(context!!) {
            sendToChat(it)
        }
        forwardViewModel.groupChats.observe(this, Observer {
            groupChatAdapter.setData(it)
        })

        // Single Chat
        singleChatAdapter = ForwardChatAdapter(context!!) {
            sendToChat(it)
        }
        forwardViewModel.singleChats.observe(this, Observer {
            singleChatAdapter.setData(it)
        })
    }

    private fun sendToChat(chatId: String) {
        messageAction?.let {
            chatViewModel.messageReplied = it // Forward is quote message
            chatViewModel.sendTextMessage("", chatId)
        }
    }
}