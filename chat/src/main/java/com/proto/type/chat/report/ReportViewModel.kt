package com.proto.type.chat.report

import com.proto.type.base.SuccessCallback
import com.proto.type.base.base_component.BaseViewModel
import com.proto.type.base.data.model.ReportData
import com.proto.type.base.data.model.ReportReasonType
import com.proto.type.base.repository.user.IUserRepository
import com.proto.type.base.utils.AppLog
import kotlinx.coroutines.launch

class ReportViewModel(private val userRepo: IUserRepository): BaseViewModel() {

    // MARK: - Companion Object
    companion object {
        private val TAG: String = ReportViewModel::class.java.simpleName
    }

    // MARK: - Private Variable
    private val reportData = ReportData(targetId = "")

    // MARK: - Computed Variables
    val shouldEnableSending: Boolean get() = reportData.reasonType != ReportReasonType.other || reportData.reasonContent.isNotEmpty()
    val targetType: String get() = reportData.targetType

    // MARK: - Public Functions
    fun report(callback: SuccessCallback) {
        if (reportData.targetId.isEmpty()) {
            AppLog.d(TAG, "There is no target id to report, ignoring...")
            callback.invoke(false)
            return
        }
        ioScope.launch {
            try {
                val success = userRepo.report(reportData.targetId, reportData.targetType, reportData.reasonType, reportData.reasonContent)
                uiScope.launch { callback.invoke(success) }
            } catch (e: Exception) {
                AppLog.d(TAG, "Report failed with exception: $e")
                uiScope.launch { callback.invoke(false) }
            }
        }
    }

    fun setReportData(targetId: String, targetType: String, reasonType: String) {
        reportData.reasonType = reasonType
        reportData.targetId = targetId
        reportData.targetType = targetType
    }

    fun updateReportContent(newContent: String) {
        reportData.reasonContent = newContent
    }
}