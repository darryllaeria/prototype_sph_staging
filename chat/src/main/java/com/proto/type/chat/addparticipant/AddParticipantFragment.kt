package com.proto.type.chat.addparticipant

import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.showView
import com.proto.type.chat.R
import kotlinx.android.synthetic.main.fragment_add_participant.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class AddParticipantFragment: BaseFragment() {

    // MARK: - Override Variable
    override var layoutId = R.layout.fragment_add_participant

    // MARK: - Private Constants
    private val roomId: String by lazy { arguments?.getString(Constants.KEY_ROOM_ID) ?: "" }
    private val viewModel: AddParticipantViewModel by viewModel()

    // MARK: - Private Variables
    private lateinit var participantListingAdapter: AddParticipantListingAdapter
    private lateinit var selectedListingAdapter: AddSelectedListingAdapter

    // MARK: - Override Functions
    override fun initView() {
        super.initView()
        btAdd.text = if (roomId.isEmpty()) getString(R.string.next).toUpperCase(Locale.ROOT) else getString(R.string.invite).toUpperCase(
            Locale.ROOT)
        if (roomId.isEmpty()) btAdd.showView() else btAdd.hideView()
        tvHeading.text = if (roomId.isEmpty()) getString(R.string.add_participants) else getString(R.string.add_friends)
        setSelectedList()
        setParticipantsList()
    }

    override fun initEvent() {
        super.initEvent()

        btAdd.setOnClickListener {
            if (roomId.isEmpty()) {
                navigateTo(
                    R.id.action_addParticipantFragment_to_createGroupFragment,
                    bundleOf(
                        Constants.KEY_IS_SELF_CHAT to false,
                        Constants.KEY_CHAT_PARTICIPANTS_IDS to viewModel.getSelectedListIds()
                    )
                )
            } else {
                showLoading()
                viewModel.addChatMembers(roomId, viewModel.getSelectedListIds()) {
                    hideLoading()
                    if (it) {
                        navigateTo(
                            R.id.action_addParticipantFragment_to_chatFragment,
                            bundleOf(Constants.KEY_ROOM_ID to roomId)
                        )
                    } else {
                        showInfo("Remove participant failed. Please try again later!")
                    }
                }
            }
        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchUserList(p0.toString())
            }
        })

        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) etSearch.clearFocus()
            false
        }

        toolbar.apply {
            addOnBackClickListener {
                navigateBack()
            }
        }
    }

    override fun initLogic() {
        super.initLogic()
        observeUsersList()
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.users.removeObservers(this)
        viewModel.selectedUsers.removeObservers(this)
    }

    // MARK: - Private Functions
    private fun observeUsersList() {
        viewModel.getAllUsers(arguments?.getStringArrayList(Constants.KEY_CHAT_PARTICIPANTS_IDS) ?: arrayListOf())
        viewModel.users.observe(this, Observer {
            when (it) {
                is UIState.LOADING -> {
                    showLoading()
                }
                is UIState.FINISHED<*> -> {
                    hideLoading()
                    val users = (it.data as? MutableList<*>)?.filterIsInstance<UserModel>()?.toMutableList() ?: mutableListOf()
                    if (users.isNotEmpty()) participantListingAdapter.setData(users)
                }
                is UIState.FAILED -> {
                    hideLoading()
                    showInfo(getString(R.string.txt_unexpected_error))
                }
                else -> {}
            }
        })
        viewModel.selectedUsers.observe(this, Observer {
            when (it) {
                is UIState.LOADING -> {
                    showLoading()
                }
                is UIState.FINISHED<*> -> {
                    hideLoading()
                    val selectedUsers = (it.data as? MutableList<*>)?.filterIsInstance<UserModel>()?.toMutableList() ?: mutableListOf()
                    if (selectedUsers.isNotEmpty()) {
                        selectedListingAdapter.setData(selectedUsers)
                        if (roomId.isNotEmpty()) btAdd.showView()
                    } else {
                        selectedListingAdapter.clearData()
                        if (roomId.isNotEmpty()) btAdd.hideView()
                    }
                }
                is UIState.FAILED -> {
                    hideLoading()
                    showInfo(getString(R.string.txt_unexpected_error))
                }
                else -> {
                }
            }
        })
    }

    private fun setParticipantsList() {
        participantListingAdapter = AddParticipantListingAdapter(context!!, mutableListOf()) { action, user, _ ->
            when (action) {
                AddParticipantListingAdapter.Actions.ACTION_SELECT -> {
                    viewModel.setSelectedUser(user)
                }
            }
        }
        rvParticipants.adapter = participantListingAdapter
    }

    private fun setSelectedList() {
        selectedListingAdapter = AddSelectedListingAdapter(context!!, mutableListOf()) { action, user, _ ->
            when (action) {
                AddSelectedListingAdapter.Actions.ACTION_REMOVE -> {
                    viewModel.resetUserCheckbox(user)
                }
            }
        }
        rvSelectedContact.adapter = selectedListingAdapter
    }
}