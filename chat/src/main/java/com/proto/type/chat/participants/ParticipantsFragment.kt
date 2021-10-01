package com.proto.type.chat.participants

import AdminsListingAdapter
import ParticipantsListingAdapter
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.chat.dialog.ParticipantActionDialog
import com.proto.type.chat.R
import com.proto.type.chat.settings.ChatSettingsParticipantListingAdapter
import kotlinx.android.synthetic.main.fragment_participants.*
import kotlinx.android.synthetic.main.fragment_participants.toolbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class ParticipantsFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_participants

    private val viewModel: ParticipantsViewModel by viewModel()

    private lateinit var adminsListingAdapter: AdminsListingAdapter
    private lateinit var pagerAdapter: ParticipantsPagerAdapter
    private lateinit var participantsListingAdapter: ParticipantsListingAdapter

    // MARK: - Override Functions
    override fun initView() {
        super.initView()
        tabLayout.setupWithViewPager(viewPager)
    }

    override fun initEvent() {
        super.initEvent()

        toolbar.apply {
            addOnBackClickListener {
                navigateBack()
            }
        }
    }

    override fun initLogic() {
        super.initLogic()
        viewModel.chat.observe(this, chatObserver)
        arguments?.getString(Constants.KEY_ROOM_ID)?.let { viewModel.getChatFromRealm(it) }

        viewModel.adminsList.observe(this, adminsListObserver)
        adminsListingAdapter = AdminsListingAdapter(
            context!!,
            mutableListOf()
        ) { action, user, _ ->
            when (action) {
                ChatSettingsParticipantListingAdapter.Actions.ACTION_SELECT -> {
                    showParticipantActionsDialog(user)
                }
            }
        }
        viewModel.participantsList.observe(this, participantsListObserver)
        participantsListingAdapter = ParticipantsListingAdapter(
            context!!,
            mutableListOf()
        ) { action, user, _ ->
            when (action) {
                ChatSettingsParticipantListingAdapter.Actions.ACTION_SELECT -> {
                    showParticipantActionsDialog(user)
                }
            }
        }
        pagerAdapter = ParticipantsPagerAdapter(adminsListingAdapter, participantsListingAdapter, arrayOf(getString(R.string.all), getString(R.string.admin)))
        viewPager.adapter = pagerAdapter
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.adminsList.removeObserver(adminsListObserver)
        viewModel.chat.removeObserver(chatObserver)
        viewModel.participantsList.removeObserver(participantsListObserver)
    }

    // MARK: - Private Observers
    private var adminsListObserver = Observer<MutableList<UserModel>> {
        adminsListingAdapter.setData(it)
    }

    private var chatObserver = Observer<ChatModel?> {
        it?.let { chat ->
            viewModel.getParticipantsAndAdminsFromRealm(chat.participant_ids?.toTypedArray() ?: arrayOf(), chat.admin_ids)
        }
    }

    private var participantsListObserver = Observer<MutableList<UserModel>> {
        participantsListingAdapter.setData(it, viewModel.chat.value?.admin_ids ?: listOf())
    }

    // MARK: - Private Functions
    private fun showParticipantActionsDialog(participant: UserModel) {
        ParticipantActionDialog(viewModel.isAdmin(participant), viewModel.isCurrentUser(participant) , viewModel.isCurrentUserAdmin()) { action ->
            when (action) {
                ParticipantActionDialog.ACTION_OPEN_PROFILE -> {
                    navigateTo(
                        R.id.action_participantsFragment_to_userProfileFragment,
                        bundleOf(Constants.KEY_USER_ID to participant.id)
                    )
                }
                ParticipantActionDialog.ACTION_SEND_PRIVATE_MESSAGE -> {
                    // TODO("Handle tap to send private message")
                     showInfo("Send private message pressed")
                }
                ParticipantActionDialog.ACTION_MAKE_GROUP_ADMIN -> {
                    showLoading()
                    viewModel.promoteDemoteUser(arguments?.getString(Constants.KEY_ROOM_ID) ?: "",participant.id) {
                        activity?.runOnUiThread {
                            hideLoading()
                            if (it) {
                                viewModel.addNewAdmin(participant).apply {
                                    showInfo(getString(R.string.success))
                                }
                            } else {
                                showInfo("Promote admin failed. Please try again later!")
                            }
                        }
                    }
                }
                ParticipantActionDialog.ACTION_REMOVE_ADMIN -> {
                    showLoading()
                    viewModel.promoteDemoteUser(arguments?.getString(Constants.KEY_ROOM_ID) ?: "",participant.id) {
                        activity?.runOnUiThread {
                            if (it) {
                                viewModel.removeCurrentAdmin(participant).apply {
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
                        viewModel.removeChatMembers(arguments?.getString(Constants.KEY_ROOM_ID) ?: "", listOf(participant.id)) {
                            hideLoading()
                            showInfo(if (it) getString(R.string.success) else "Remove participant failed. Please try again later!")
                        }
                    } else {
                        showInfo("You don't have permission to perform this action.")
                    }
                }
            }
        }.show(childFragmentManager, "participant_actions")
    }
}