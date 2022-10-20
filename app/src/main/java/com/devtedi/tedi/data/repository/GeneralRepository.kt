package com.devtedi.tedi.data.repository

import com.devtedi.tedi.data.remote.ApiResponse
import com.devtedi.tedi.data.remote.general.BugReportResponse
import com.devtedi.tedi.data.source.GeneralDataSource
import com.devtedi.tedi.data.remote.general.report.ReportBugBody
import com.devtedi.tedi.data.remote.general.request.FileRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeneralRepository @Inject constructor(private val dataSource: GeneralDataSource) {

    suspend fun addNewReportBug(reportBugBody: ReportBugBody, file: FileRequest): Flow<ApiResponse<BugReportResponse>> {
        return dataSource.addNewReportBug(reportBugBody, file).flowOn(Dispatchers.IO)
    }

}