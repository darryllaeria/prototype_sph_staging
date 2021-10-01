package com.proto.type.chat.report

import android.text.InputFilter
import com.proto.type.base.Constants
import com.proto.type.base.extension.afterTextChanged
import com.proto.type.base.base_component.BaseFragment
import com.proto.type.base.extension.hideKeyboard
import com.proto.type.base.extension.showKeyboard
import com.proto.type.chat.R
import com.proto.type.chat.dialog.ReportReceivedDialog
import kotlinx.android.synthetic.main.fragment_report.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ReportFragment: BaseFragment() {

    override var layoutId = R.layout.fragment_report

    private val viewModel: ReportViewModel by viewModel()

    override fun initEvent() {
        super.initEvent()

        btnSend.setOnClickListener {
            etReportContent.hideKeyboard()
            showLoading()
            viewModel.report {
                hideLoading()
                if (it) {
                    ReportReceivedDialog { action ->
                        if (action == ReportReceivedDialog.ACTION_OK) navigateBack()
                    }.show(childFragmentManager, "report_action")
                } else {
                    showInfo(if (it) getString(R.string.success) else "There is an issue reporting this ${viewModel.targetType}. Please try again later.")
                }
            }
        }
        etReportContent.afterTextChanged {
            viewModel.updateReportContent(it)
            tvMessageCount.text = "${it.length}/${Constants.Report.MAX_REPORT_CONTENT_CHARS_COUNT}"
            btnSend.isEnabled = viewModel.shouldEnableSending
        }
        toolbar.apply {
            addOnBackClickListener {
                etReportContent.hideKeyboard()
                navigateBack()
            }
        }
    }

    override fun initLogic() {
        super.initLogic()
        etReportContent.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(Constants.Report.MAX_REPORT_CONTENT_CHARS_COUNT))
    }

    override fun initView() {
        super.initView()

        // Setup data for displaying
        viewModel.setReportData(arguments?.getString(Constants.Report.KEY_REPORT_TARGET_ID) ?: "", arguments?.getString(Constants.Report.KEY_REPORT_TARGET) ?: "", arguments?.getString(Constants.Report.KEY_REPORT_REASON) ?: "")

        // Display view with setup data
        etReportContent.showKeyboard()
        tvMessageCount.text = "${etReportContent.text.length}/${Constants.Report.MAX_REPORT_CONTENT_CHARS_COUNT}"
        btnSend.isEnabled = viewModel.shouldEnableSending
    }
}