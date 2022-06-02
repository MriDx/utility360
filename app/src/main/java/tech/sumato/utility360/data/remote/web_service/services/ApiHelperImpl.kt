package tech.sumato.utility360.data.remote.web_service.services

import com.undabot.izzy.models.JsonDocument
import retrofit2.Response
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService) : ApiHelper {

    override suspend fun getCustomers(query: Map<String, String>): Response<JsonDocument<List<CustomerResource>>> =
        apiService.getCustomers(query = query)
}