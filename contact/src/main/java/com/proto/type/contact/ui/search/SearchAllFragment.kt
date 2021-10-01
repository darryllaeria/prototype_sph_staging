package com.proto.type.contact.ui.search

import androidx.lifecycle.Observer
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.contact.R
import com.proto.type.contact.adapter.MarketAdapter
import com.proto.type.contact.adapter.PeopleAdapter
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchAllFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_search

    private val allViewModel: SearchAllViewModel by viewModel()
    private val peopleAdapter = PeopleAdapter()
    private val marketAdapter = MarketAdapter()

    override fun initView() {
        super.initView()
        rvPeople.adapter = peopleAdapter
        rvSymbols.adapter = marketAdapter
    }

    override fun initEvent() {
        super.initEvent()

        tvCancel.setOnClickListener {
            navigateTo(R.id.back_to_contacts, FragmentNavigatorExtras(clSearch to getString(R.string.trans_search)))
        }
    }

    override fun initLogic() {
        super.initLogic()
        allViewModel.people.observe(this, Observer {
            peopleAdapter.display(it)
        })
        allViewModel.symbols.observe(this, Observer {
            marketAdapter.display(it)
        })
        allViewModel.loadSuggestData()
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        allViewModel.people.removeObservers(this)
        allViewModel.symbols.removeObservers(this)
    }
}