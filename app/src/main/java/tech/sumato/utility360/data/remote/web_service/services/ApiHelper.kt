package tech.sumato.utility360.data.remote.web_service.services

import com.undabot.izzy.models.JsonDocument
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.QueryMap
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.model.user.LoginResponse
import tech.sumato.utility360.data.remote.model.user.LogoutResponse
import tech.sumato.utility360.data.remote.model.user.UserResponse
import tech.sumato.utility360.data.remote.model.utils.ResponseResource

interface ApiHelper {


    suspend fun getCustomers(query: Map<String, String>): Response<JsonDocument<List<CustomerResource>>>

    suspend fun login(loginRequestBody: RequestBody): Response<ResponseResource<LoginResponse>>

    suspend fun logout(): Response<LogoutResponse>


}