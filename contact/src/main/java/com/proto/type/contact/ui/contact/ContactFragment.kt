package com.proto.type.contact.ui.contact

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.ContactsContract
import android.provider.Settings
import android.view.View
import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.proto.type.base.Constants
import com.proto.type.base.adapter.ContactAdapter
import com.proto.type.base.adapter.UserAdapter
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.base_component.UIState
import com.proto.type.base.data.model.ChatModel
import com.proto.type.base.data.model.ContactModel
import com.proto.type.base.data.model.UserModel
import com.proto.type.base.extension.hasContents
import com.proto.type.base.extension.hideView
import com.proto.type.base.extension.showView
import com.proto.type.base.extension.showViews
import com.proto.type.base.utils.RuntimePermissionCallback
import com.proto.type.base.utils.RuntimePermissionUtils
import com.proto.type.base.utils.Utils
import com.proto.type.base.utils.ViewUtils.hideViews
import com.proto.type.contact.R
import kotlinx.android.synthetic.main.fragment_contacts.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ContactFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_contacts
    private lateinit var contactAdapter: ContactAdapter
    private lateinit var chatQAdapter: UserAdapter
    private val viewModel: ContactViewModel by viewModel()

    override fun initEvent() {
        super.initEvent()

        toolbar.addOnToolbarButtonCallback {
            val addContactIntent = Intent(ContactsContract.Intents.Insert.ACTION).apply {
                type = ContactsContract.RawContacts.CONTENT_TYPE
            }

            startActivity(addContactIntent)
        }

        overlay.setOnClickListener {
            navigateTo(R.id.action_search_contact)
        }

        itmvScanQR.addOnItemClickCallback {
            navigateTo(R.id.action_scan_qr_code)
        }

        itmvChatID.addOnItemClickCallback {
            navigateTo(
                R.id.action_search_contact,
                FragmentNavigatorExtras(clSearch to getString(R.string.trans_search))
            )
        }
    }

    override fun initView() {
        super.initView()
        setupAdapter()

        if (loaderChatQ.visibility == View.VISIBLE) {
            tvChatQPermission.hideView()
        }

        if (loaderContact.visibility == View.VISIBLE) {
            tvContactPermission.hideView()
        }

        tvChatQPermission.setOnClickListener {
            appSettingsContactsPermission()
        }

        tvContactPermission.setOnClickListener {
            appSettingsContactsPermission()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (!RuntimePermissionUtils.checkPermission(context, Manifest.permission.READ_CONTACTS)) {
            requireContactPermission()
        } else {
            viewModel.getAllContacts()
        }
    }

    override fun initLogic() {
        super.initLogic()
        if (!RuntimePermissionUtils.checkPermission(requireContext(), Manifest.permission.READ_CONTACTS)) {
            requireContactPermission()
        } else {
            viewModel.getAllContacts()
        }
        viewModel.chat.observe(this, Observer {
            when (it) {
                is UIState.FINISHED<*> -> {
                    navigateTo(Uri.parse("${Constants.Uri.URI_NEW_CHAT}/${(it.data as? ChatModel)?.id}/${Constants.Uri.URI_CONTACTS}"))
                }
            }
        })
        viewModel.chatQContacts.observe(this, Observer {
            when (it) {
                is UIState.LOADING -> {
                    hideViews(rvFriendOnChatQ)
                    showViews(tvChatQUser, loaderChatQ)
                }
                is UIState.FINISHED<*> -> {
                    val chatQUsers = (it.data as? List<*>)?.filterIsInstance<UserModel>() ?: listOf()
                    loaderChatQ.hideView()
                    if (chatQUsers.hasContents()) {
                        tvChatQPermission.hideView()
                        showViews(tvChatQUser, rvFriendOnChatQ)
                        tvChatQUser.text = String.format(getString(R.string.user_chatq_number), chatQUsers.size)
                        chatQAdapter.display(chatQUsers)
                    } else {
                        tvChatQUser.hideView()
                    }
                }
                is UIState.FAILED -> {
                    hideViews(loaderChatQ, tvChatQUser, tvChatQPermission)
                }
            }
        })
        viewModel.localContacts.observe(this, Observer {
            when (it) {
                is UIState.LOADING -> {
                    hideViews(rvContacts)
                    showViews(tvContacts, loaderContact)
                }
                is UIState.FINISHED<*> -> {
                    val localContacts = (it.data as? List<*>)?.filterIsInstance<ContactModel>() ?: listOf()
                    loaderContact.hideView()
                    if (localContacts.hasContents()) {
                        tvContactPermission.hideView()
                        showViews(tvContacts, rvContacts)
                        tvContacts.text = String.format(getString(R.string.contacts_number), localContacts.size)
                        contactAdapter.display(localContacts)
                    } else {
                        tvContacts.hideView()
                    }
                }
                is UIState.FAILED -> {
                    hideViews(loaderContact, tvContacts, tvContactPermission)
                }
                else -> {
                }
            }
        })
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.chat.removeObservers(this)
        viewModel.chatQContacts.removeObservers(this)
        viewModel.localContacts.removeObservers(this)
    }

    private fun setupAdapter() {
        contactAdapter = ContactAdapter {
            Utils.showInviteDialog(requireActivity())
        }
        rvContacts.adapter = contactAdapter
        chatQAdapter = UserAdapter {
            viewModel.enterChatRoom(it.id)
            viewModel.setFriendName(it.displayingName())
        }
        rvFriendOnChatQ.adapter = chatQAdapter
        itmvInvite.addOnItemClickCallback {
            Utils.showInviteDialog(requireActivity())
        }
    }

    private fun requireContactPermission() {
        RuntimePermissionUtils.requestPermission(requireActivity(),
            listOf(Manifest.permission.READ_CONTACTS),
            object : RuntimePermissionCallback {
                override fun onGranted() {
                    viewModel.syncContacts()
                }

                override fun onFailed() {
                    if (viewModel.getPermissionMessageShow()) {
                        showInfo(getString(R.string.txt_user_denied_access_contact))
                        viewModel.setPermissionMessageShow(false)
                    } else {
                        tvChatQPermission.showView()
                        loaderChatQ.hideView()
                        tvContactPermission.showView()
                        loaderContact.hideView()
                    }
                }
            }
        )
    }

    private fun appSettingsContactsPermission() {
        requireContext().startActivity(Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", requireActivity().packageName, null)
        })
    }
}