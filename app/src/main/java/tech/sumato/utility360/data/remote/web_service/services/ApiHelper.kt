package tech.sumato.utility360.data.remote.web_service.services

import com.undabot.izzy.models.JsonDocument
import retrofit2.Response
import retrofit2.http.QueryMap
import tech.sumato.utility360.data.remote.model.customer.CustomerResource

interface ApiHelper {


    suspend fun getCustomers(query: Map<String, String>): Response<JsonDocument<List<CustomerResource>>>

}