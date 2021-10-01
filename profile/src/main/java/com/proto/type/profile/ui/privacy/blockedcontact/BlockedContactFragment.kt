package com.proto.type.profile.ui.privacy.blockedcontact

import androidx.lifecycle.Observer
import com.proto.type.base.adapter.UserAdapter
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.fragment_view_blocked_contact.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class BlockedContactFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_view_blocked_contact
    private val viewModel: BlockContactViewModel by viewModel()
    private lateinit var adapter: UserAdapter

    override fun initView() {
        super.initView()
        setupAdapter()
    }

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }

        tvAddNew.setOnClickListener {
            navigateTo(R.id.action_blocked_contact_to_add_new)
        }
    }

    override fun initLogic() {
        super.initLogic()
        viewModel.blockUsersData.observe(this, Observer {
            adapter.display(it)
        })
        viewModel.loadBlockedContacts()
    }

    private fun setupAdapter() {
        adapter = UserAdapter()

        rvBlockedContact.adapter = adapter
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.blockUsersData.removeObservers(this)
    }
}