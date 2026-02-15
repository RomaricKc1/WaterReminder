package com.romarickc.reminder.data.remote

import com.romarickc.reminder.commons.ExportIntakesRequest
import com.romarickc.reminder.commons.ExportIntakesResponse
import com.romarickc.reminder.commons.ExportIntakesStream
import com.romarickc.reminder.commons.ServerPing
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface Comm {
    @GET("get_intakes")
    suspend fun getIntakesData(): Response<ExportIntakesStream>

    @GET("ping")
    suspend fun serverPing(): Response<ServerPing>

    @POST("put_intakes")
    suspend fun putIntakesData(
        @Body req: ExportIntakesRequest,
    ): Response<ExportIntakesResponse>
}
