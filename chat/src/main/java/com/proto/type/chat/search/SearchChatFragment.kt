package com.proto.type.chat.search

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.proto.type.base.*
import com.proto.type.base.adapter.UserAdapter
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.*
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.fragment_search_chat.*
import kotlinx.android.synthetic.main.item_search_tab.view.*
import kotlinx.android.synthetic.main.search_toolbar.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchChatFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_search_chat

    private val viewModel: SearchChatViewModel by viewModel()

    private lateinit var userAdapter: UserAdapter
    private lateinit var groupAdapter: SearchChatGroupAdapter
    private lateinit var messageAdapter: SearchMessageAdapter

    override fun initView() {
        super.initView()
        hideViews(searchAllSymbol, searchAllContacts, searchAllGroups, searchAllMessages)
        setTabAdapter()
        setupListAdapter()
        ivCross.invisibleView()
    }

    override fun initEvent() {
        super.initEvent()

        tvCancel.setOnClickListener { navigateBack() }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    ivCross.showView()
                    when {
                        viewModel.seeMoreContacts.value!! -> { viewModel.searchUserList(s.toString()) }
                        viewModel.seeMoreGroups.value!! -> { viewModel.getRoomGroupByKeyword(s.toString()) }
                        viewModel.seeMoreMessages.value!! -> { viewModel.getMessageByKeyword(s.toString()) }
                        else -> {
                            viewModel.searchUserList(s.toString())
                            viewModel.getRoomGroupByKeyword(s.toString())
                            viewModel.getMessageByKeyword(s.toString())
                        }
                    }
                } else {
                    ivCross.invisibleView()
                    when {
                        viewModel.seeMoreContacts.value!! -> { viewModel.getAllUsers() }
                        viewModel.seeMoreGroups.value!! -> { viewModel.getRoomGroupByKeyword("") }
                        viewModel.seeMoreMessages.value!! -> { viewModel.getMessageByKeyword("") }
                        else -> { viewModel.getAllSearch() }
                    }
                }
            }
        })

        ivCross.setOnClickListener {
            etSearch.text.clear()
            when {
                viewModel.seeMoreContacts.value!! -> { viewModel.getAllUsers() }
                viewModel.seeMoreGroups.value!! -> { viewModel.getRoomGroupByKeyword("") }
                viewModel.seeMoreMessages.value!! -> { viewModel.getMessageByKeyword("") }
                else -> { viewModel.getAllSearch() }
            }
        }

        tvAllContactsMore.setOnClickListener { viewModel.setSeeMoreContacts(true) }
        tvAllGroupsMore.setOnClickListener { viewModel.setSeeMoreGroups(true) }
    }

    override fun initLogic() {
        super.initLogic()
        viewModel.getAllSearch()
        viewModel.groupChats.observe(this, groupChatsObserver)
        viewModel.hashMapMessages.observe(this, Observer {
            when (it) {
                is UIState.FINISHED<*> -> {
                    val hashMessages = (it.data as? MutableList<*>)?.filterIsInstance<HashMap<String, Any>>() ?: mutableListOf()
                    if (hashMessages.isNotEmpty()) { searchAllMessages.showView() } else { searchAllMessages.hideView() }

                    when {
                        viewModel.seeMoreMessages.value!! -> {
                            messageAdapter.display(hashMessages)
                        }
                        hashMessages.size > 5 -> {
                            messageAdapter.display(hashMessages.subList(0, 5))
                        }
                        else -> {
                            messageAdapter.display(hashMessages)
                            tvAllMessagesMore.invisibleView()
                        }
                    }
                }
                is UIState.FAILED -> {
//                showInfo(getString(R.string.txt_unexpected_error))
                }
                else -> {}
            }
        })
        viewModel.users.observe(this,  Observer {
            when (it) {
                is UIState.FINISHED<*> -> {
                    val users = (it.data as? MutableList<*>)?.filterIsInstance<UserModel>() ?: mutableListOf()
                    if (users.isNotEmpty()) { searchAllContacts.showView() } else { searchAllContacts.hideView() }

                    when {
                        viewModel.seeMoreContacts.value!! -> {
                            userAdapter.display(users)
                        }
                        users.size > 5 -> {
                            userAdapter.display(users.subList(0, 5))
                        }
                        else -> {
                            userAdapter.display(users)
                            tvAllContactsMore.invisibleView()
                        }
                    }
                }
                is UIState.FAILED -> {
//                showInfo(getString(R.string.txt_unexpected_error))
                }
                else -> {}
            }
        })
        viewModel.seeMoreContacts.observe(this, Observer {
            val contacts = ((viewModel.users.value as? UIState.FINISHED<*>)?.data as? MutableList<*>)?.filterIsInstance<UserModel>()
            if (it) {
                hideViews(tvAllContactsMore, /*searchAllSymbol,*/ searchAllGroups, searchAllMessages)
            } else if (!contacts.isNullOrEmpty()) {
                if (contacts.size > 5) { tvAllContactsMore.showView() } else { tvAllContactsMore.hideView() }
            }
        })
        viewModel.seeMoreGroups.observe(this, Observer {
            val groups = ((viewModel.groupChats.value as? UIState.FINISHED<*>)?.data as? MutableList<*>)?.filterIsInstance<ChatModel>()
            if (it) {
                viewModel.groupChats.removeObservers(this)
                viewModel.groupChats.observe(this, groupChatsObserver)
                hideViews(tvAllGroupsMore, /*searchAllSymbol,*/ searchAllContacts, searchAllMessages)
            } else if (!groups.isNullOrEmpty()) {
                if (groups.size > 5) {
                    tvAllGroupsMore.showView()
                } else {
                    tvAllGroupsMore.hideView()
                }
            }
        })
        viewModel.seeMoreMessages.observe(this,  Observer<Boolean> {
            val messages = ((viewModel.hashMapMessages.value as? UIState.FINISHED<*>)?.data as? MutableList<*>)?.filterIsInstance<HashMap<String, Any>>()
            if (it) {
                hideViews(tvAllMessagesMore, /*searchAllSymbol,*/ searchAllContacts, searchAllGroups)
            } else if (!messages.isNullOrEmpty()) {
                if (messages.size > 5) { tvAllMessagesMore.showView() } else { tvAllMessagesMore.hideView() }
            }
        })
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.chat.removeObservers(this)
        viewModel.groupChats.removeObservers(this)
        viewModel.hashMapMessages.removeObservers(this)
        viewModel.users.removeObservers(this)
        viewModel.seeMoreContacts.removeObservers(this)
        viewModel.seeMoreGroups.removeObservers(this)
        viewModel.seeMoreMessages.removeObservers(this)
    }

    private fun setupListAdapter() {
        searchAllSymbol.hideView() // TODO("Implement search for symbols data")

        userAdapter = UserAdapter { user ->
            viewModel.setPrivateParticipant(user)
            viewModel.getChatRoom(user.id)
            viewModel.chat.observe(this, Observer {
                when (it) {
                    is UIState.FINISHED<*> -> {
                        navigateTo(
                            R.id.action_search_message_to_chatFragment,
                            bundleOf(
                                Constants.KEY_ROOM_ID to (it.data as? ChatModel)?.id,
                                Constants.KEY_PARTICIPANT_ID to viewModel.privateParticipant.value?.id
                            )
                        )
                    }
                    is UIState.FAILED -> {
//                showInfo(getString(R.string.txt_unexpected_error))
                    }
                    else -> {}
                }
            })
        }
        rvAllContacts.adapter = userAdapter

        groupAdapter = SearchChatGroupAdapter(context!!, mutableListOf(),
            disableSwipe = true,
            isBasicView = true
        ) { action, chat, _ ->
            when (action) {
                SearchChatGroupAdapter.Actions.ACTION_SELECT -> {
                    navigateTo(
                        R.id.action_search_message_to_chatFragment,
                        bundleOf(Constants.KEY_ROOM_ID to chat.id)
                    )
                }
            }
        }
        rvAllGroups.adapter = groupAdapter

        messageAdapter = SearchMessageAdapter(context!!) {
            navigateTo(
                R.id.action_search_message_to_chatFragment,
                bundleOf(Constants.KEY_ROOM_ID to it[Constants.KEY_ROOM_ID])
            )
        }
        rvAllMessages.adapter = messageAdapter
    }

    private fun setTabAdapter() {
        rvSearchTab.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = SearchTabAdapter(context!!, arrayListOf(getString(R.string.search_tab_all), /*getString(R.string.search_tab_symbols),*/
                getString(R.string.search_tab_people), getString(R.string.search_tab_groups), getString(R.string.search_tab_messages))) { _, position ->
                    when (position) {
                        0 -> {
                            viewModel.setSeeMoreContacts(false)
                            viewModel.setSeeMoreGroups(false)
                            viewModel.setSeeMoreMessages(false)
                            showViews(/*searchAllSymbol,*/ searchAllContacts, searchAllGroups, searchAllMessages)
                        }
//                        1 -> {
//                            searchAllSymbol.showView()
//                            hideViews(searchAllContacts, searchAllGroups, searchAllMessages)
//                        }
                        1 -> {
                            viewModel.setSeeMoreContacts(true)
                            viewModel.setSeeMoreGroups(false)
                            viewModel.setSeeMoreMessages(false)
                            searchAllContacts.showView()
                            hideViews(/*searchAllSymbol,*/ searchAllGroups, searchAllMessages)
                        }
                        2 -> {
                            viewModel.setSeeMoreContacts(false)
                            viewModel.setSeeMoreGroups(true)
                            viewModel.setSeeMoreMessages(false)
                            searchAllGroups.showView()
                            hideViews(/*searchAllSymbol,*/ searchAllContacts, searchAllMessages)
                        }
                        3 -> {
                            viewModel.setSeeMoreContacts(false)
                            viewModel.setSeeMoreGroups(false)
                            viewModel.setSeeMoreMessages(true)
                            searchAllMessages.showView()
                            hideViews(/*searchAllSymbol,*/ searchAllContacts, searchAllGroups)
                        }
                    }
            }
        }
    }

    private class SearchTabAdapter(
        val context: Context,
        private val tabs : ArrayList<String>,
        private val onItemSelected: (action: String, position: Int) -> Unit) : RecyclerView.Adapter<SearchTabAdapter.ViewHolder>() {
        override fun getItemCount(): Int = tabs.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_search_tab, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = tabs[position]
            holder.itemView.tvSearchTab.text = item
            holder.itemView.tabSearch.setOnClickListener {
                onItemSelected(item, position)
            }
        }

        class ViewHolder (view: View) : RecyclerView.ViewHolder(view)
    }

    private val groupChatsObserver = Observer<UIState> {
        when (it) {
            is UIState.FINISHED<*> -> {
                val groupChats = (it.data as? MutableList<*>)?.filterIsInstance<ChatModel>()?.toMutableList() ?: mutableListOf()
                if (groupChats.isNotEmpty()) { searchAllGroups.showView() } else { searchAllGroups.hideView() }
                when {
                    viewModel.seeMoreGroups.value!! -> {
                        groupAdapter.setData(groupChats)
                    }
                    groupChats.size > 5 -> {
                        groupAdapter.setData(groupChats.subList(0, 5))
                    }
                    else -> {
                        groupAdapter.setData(groupChats)
                        tvAllGroupsMore.invisibleView()
                    }
                }
            }
            is UIState.FAILED -> {
//                showInfo(getString(R.string.txt_unexpected_error))
            }
            else -> {}
        }
    }
}