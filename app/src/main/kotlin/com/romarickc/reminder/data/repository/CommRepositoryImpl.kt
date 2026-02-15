package com.romarickc.reminder.data.repository

import com.romarickc.reminder.commons.ExportIntakesRequest
import com.romarickc.reminder.commons.ExportIntakesResponse
import com.romarickc.reminder.commons.ExportIntakesStream
import com.romarickc.reminder.commons.Resource
import com.romarickc.reminder.commons.ServerPing
import com.romarickc.reminder.data.remote.Comm
import com.romarickc.reminder.domain.repository.CommRepository
import retrofit2.Response
import retrofit2.http.Body

class CommRepositoryImpl(
    private val comm: Comm,
) : CommRepository {
    override suspend fun getIntakesServer(): Resource<ExportIntakesStream> {
        return try {
            val response = comm.getIntakesData()
            return Resource.Success(
                retrieve(response),
                message = "errorBody: ${response.errorBody()?.string()}",
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "Err, can't get intakes from the server")
        }
    }

    override suspend fun serverPing(): Resource<ServerPing> {
        return try {
            val response = comm.serverPing()
            return Resource.Success(
                retrieve(response),
                message = "errorBody: ${response.errorBody()?.string()}",
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "Err, can't ping server")
        }
    }

    override suspend fun putIntakesServer(
        @Body req: ExportIntakesRequest,
    ): Resource<ExportIntakesResponse> {
        return try {
            val response = comm.putIntakesData(req)
            return Resource.Success(
                retrieve(response),
                message = "errorBody: ${response.errorBody()?.string()}",
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(
                e.message
                    ?: "Err, can't export data for the server -> ${e.message}",
            )
        }
    }

    fun <T> retrieve(response: Response<T>): T? =
        if (response.isSuccessful) {
            response.body()
        } else {
            null
        }
}
