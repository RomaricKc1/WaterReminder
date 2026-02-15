package com.romarickc.reminder.domain.repository

import com.romarickc.reminder.commons.ExportIntakesRequest
import com.romarickc.reminder.commons.ExportIntakesResponse
import com.romarickc.reminder.commons.ExportIntakesStream
import com.romarickc.reminder.commons.Resource
import com.romarickc.reminder.commons.ServerPing
import retrofit2.http.Body

interface CommRepository {
    suspend fun getIntakesServer(): Resource<ExportIntakesStream>

    suspend fun serverPing(): Resource<ServerPing>

    suspend fun putIntakesServer(
        @Body req: ExportIntakesRequest,
    ): Resource<ExportIntakesResponse>
}
