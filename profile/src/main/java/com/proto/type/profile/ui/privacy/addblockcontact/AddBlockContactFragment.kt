package com.proto.type.profile.ui.privacy.addblockcontact

import androidx.lifecycle.Observer
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.utils.CommonUtils
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.fragment_add_block_contact.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddBlockContactFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_add_block_contact

    private lateinit var chatQUserListingAdapter: ChatQUserListingAdapter
    private val viewModel: AddBlockContactViewModel by viewModel()

    override fun initLogic() {
        super.initLogic()

        setupAdapter()
        observeState()

        viewModel.getAllChatQUsersInContact()
    }


    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.isBlocked.removeObservers(this)
        viewModel.userList.removeObservers(this)
    }

    private fun setupAdapter() {
        chatQUserListingAdapter = ChatQUserListingAdapter(
            context!!,
            mutableListOf()
        ) { action, user, _ ->
            when (action) {
                ChatQUserListingAdapter.Actions.SELECT -> {
                    viewModel.selectedList.add(user)
                    viewModel.blockUser(user.id)
                }
            }
        }

        rvContacts.adapter = chatQUserListingAdapter

        rvContacts.setOnScrollChangeListener { _, _, _, _, _ ->
            CommonUtils.hideKeyboard(requireContext())
        }
    }

    private fun observeState() {
        viewModel.isBlocked.observe(this, Observer {
            when (it) {
                is UIState.LOADING -> {
                    showLoading()
                }
                is UIState.FINISHED<*> -> {
                    hideLoading()
                    if (it.data as? Boolean == true) {
                        navigateBack()
                    } else {
                        showInfo(getString(it.messageId))
                    }
                }
                else -> {
                    hideLoading()
                }
            }
        })

        viewModel.userList.observe(this, Observer { users ->
            chatQUserListingAdapter.setData(users.sortedByDescending { user -> user.display_name }.toMutableList())
        })
    }

    private fun blockSelectedUser(userId: String) {
        viewModel.blockUser(userId)
    }
}