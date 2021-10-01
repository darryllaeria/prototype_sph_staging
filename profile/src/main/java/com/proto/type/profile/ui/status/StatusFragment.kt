package com.proto.type.profile.ui.status

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.Observer
import com.proto.type.base.Constants
import com.proto.type.base.extension.hideKeyboard
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.extension.setupCursorEvents
import com.proto.type.profile.R
import kotlinx.android.synthetic.main.fragment_status.*
import org.koin.androidx.viewmodel.ext.android.viewModel

/**
 * @details Update status fragment
 */
class StatusFragment: BaseFragment() {

    // MARK: - Companion Constant
    companion object {
        const val KEY_STATUS = "status"
    }

    // MARK: - Override Variable
    override var layoutId = R.layout.fragment_status

    // MARK: - Private Constant
    private val viewModel: StatusViewModel by viewModel()

    // MARK: - Override Functions
    override fun initView() {
        super.initView()
        etStatus.setText(arguments?.getString(KEY_STATUS))
    }

    override fun initEvent() {
        super.initEvent()

        etStatus.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                tvCount.text = "${s.toString().length} / ${Constants.MAX_COUNT}"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }
        })
        etStatus.setupCursorEvents()

        btDone.setOnClickListener {
            etStatus.hideKeyboard()
            showLoading()
            viewModel.updateStatus(etStatus.text.toString())
        }

        ivBack.setOnClickListener {
            etStatus.hideKeyboard()
            navigateBack()
        }
    }

    override fun initLogic() {
        super.initLogic()
        viewModel.isUpdated.observe(this, Observer {
            hideLoading()
            if (it) navigateBack() else showError("Update failed!")
        })
    }

    override fun unsubscribeObservers() {
        super.unsubscribeObservers()
        viewModel.isUpdated.removeObservers(this)
    }
}