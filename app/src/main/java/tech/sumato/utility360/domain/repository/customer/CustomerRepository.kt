package tech.sumato.utility360.domain.repository.customer

import com.undabot.izzy.models.JsonDocument
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.utils.Resource

interface CustomerRepository {


    suspend fun getCustomers(query: Map<String, String>): Resource<List<CustomerResource>>


    suspend fun getCustomersWithDocument(query: Map<String, String>): Resource<JsonDocument<List<CustomerResource>>>

    suspend fun getCustomer(
        uuid: String,
        query: Map<String, String>
    ): Resource<JsonDocument<CustomerResource>>

}