package com.proto.type.profile.ui.language

import androidx.lifecycle.Observer
import com.proto.type.base.Constants
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.profile.R
import com.proto.type.profile.adapter.LanguageAdapter
import com.proto.type.profile.adapter.LanguageMenuItem
import kotlinx.android.synthetic.main.fragment_language.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class LanguageFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_language
    private val viewModel: LanguageViewModel by viewModel()
    private val adapter = LanguageAdapter()

    override fun initView() {
        super.initView()

        rvMenus.adapter = adapter

        adapter.display(mutableListOf(
            LanguageMenuItem(context!!.getString(R.string.auto_default),
                Constants.Language.LANG_CODE_DEFAULT),
            LanguageMenuItem(context!!.getString(R.string.txt_english),
                Constants.Language.LANG_CODE_ENGLISH),
            LanguageMenuItem(context!!.getString(R.string.txt_thai),
                Constants.Language.LANG_CODE_JAPANESE),
            LanguageMenuItem(context!!.getString(R.string.txt_japanese),
                Constants.Language.LANG_CODE_THAI),
            LanguageMenuItem(context!!.getString(R.string.txt_Vietnamese),
                Constants.Language.LANG_CODE_VIETNAMESE)))
    }

    override fun initEvent() {
        super.initEvent()

        ivBack.setOnClickListener {
            navigateBack()
        }

        btnSave.setOnClickListener {
            showLoading()
            viewModel.saveConfigLanguage(adapter.getLanguageCode())
            changeLocale()
            hideLoading()
        }
    }

    override fun initLogic() {
        super.initLogic()

        viewModel.loadLanguageConfig()
        viewModel.uiModel.observe(this, Observer {
            adapter.setSelectedCode(it)
        })
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.uiModel.removeObservers(this)
    }

    private fun changeLocale() {
        requireActivity().recreate()
    }
}