package tech.sumato.utility360.data.remote.web_service.services

import com.undabot.izzy.models.JsonDocument
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.utils.IzzyInterface

interface ApiService {


    @IzzyInterface
    @GET("customers")
    suspend fun getCustomers(@QueryMap query: Map<String, String>): Response<JsonDocument<List<CustomerResource>>>

}