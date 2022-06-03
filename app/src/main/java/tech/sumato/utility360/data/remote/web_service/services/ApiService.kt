package tech.sumato.utility360.data.remote.web_service.services

import com.undabot.izzy.models.JsonDocument
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.QueryMap
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.model.user.LoginResponse
import tech.sumato.utility360.data.remote.model.user.LogoutResponse
import tech.sumato.utility360.data.remote.model.user.UserResponse
import tech.sumato.utility360.data.remote.model.utils.ResponseResource
import tech.sumato.utility360.utils.GsonInterface
import tech.sumato.utility360.utils.IzzyInterface
import tech.sumato.utility360.utils.MyIzzyInterface

interface ApiService {


    @MyIzzyInterface
    @GET("customers")
    suspend fun getCustomers(@QueryMap query: Map<String, String>): Response<JsonDocument<List<CustomerResource>>>


    @GsonInterface
    @POST("login")
    suspend fun login(@Body loginRequestBody: RequestBody): Response<ResponseResource<LoginResponse>>

    @GsonInterface
    @POST("logout")
    suspend fun logout(): Response<LogoutResponse>

}