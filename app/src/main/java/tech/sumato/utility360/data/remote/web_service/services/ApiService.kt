package tech.sumato.utility360.data.remote.web_service.services

import com.undabot.izzy.models.JsonDocument
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.model.user.LoginResponse
import tech.sumato.utility360.data.remote.model.user.LogoutResponse
import tech.sumato.utility360.data.remote.model.user.UserResponse
import tech.sumato.utility360.data.remote.model.utils.ResponseResource
import tech.sumato.utility360.data.remote.model.utils.SimpleResponse
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


    /**
     * Customers List For Site Verifications
     */
    @MyIzzyInterface
    @GET("siteverifications")
    suspend fun pendingSiteVerifications(@QueryMap query: Map<String, String>): Response<JsonDocument<List<CustomerResource>>>

    @GsonInterface
    @POST("customers/{uuid}/site-verification")
    suspend fun submitSiteVerification(
        @Path("uuid", encoded = true) uuid: String,
        @Body siteVerificationRequestBody: RequestBody
    ): Response<SimpleResponse>

    @MyIzzyInterface
    @GET("meterinstallations")
    suspend fun pendingMeterInstallations(@QueryMap query: Map<String, String>): Response<JsonDocument<List<CustomerResource>>>



    @GsonInterface
    @POST("customers/{uuid}/meter-installation")
    suspend fun submitMeterInstallation(
        @Path("uuid", encoded = true) uuid: String,
        @Body requestBody: RequestBody
    ): Response<SimpleResponse>

}