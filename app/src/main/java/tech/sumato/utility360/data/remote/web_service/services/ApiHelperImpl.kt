package tech.sumato.utility360.data.remote.web_service.services

import com.undabot.izzy.models.JsonDocument
import okhttp3.RequestBody
import retrofit2.Response
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.model.user.LoginResponse
import tech.sumato.utility360.data.remote.model.user.LogoutResponse
import tech.sumato.utility360.data.remote.model.user.UserResponse
import tech.sumato.utility360.data.remote.model.utils.ResponseResource
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {

    override suspend fun getCustomers(query: Map<String, String>): Response<JsonDocument<List<CustomerResource>>> =
        apiService.getCustomers(query = query)


    override suspend fun login(loginRequestBody: RequestBody): Response<ResponseResource<LoginResponse>> =
        apiService.login(loginRequestBody)

    override suspend fun logout(): Response<LogoutResponse> = apiService.logout()


}