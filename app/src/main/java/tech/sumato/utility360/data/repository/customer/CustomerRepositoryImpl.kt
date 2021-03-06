package tech.sumato.utility360.data.repository.customer

import com.undabot.izzy.models.JsonDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.utils.Resource
import tech.sumato.utility360.data.remote.web_service.services.ApiHelper
import tech.sumato.utility360.domain.repository.customer.CustomerRepository
import javax.inject.Inject

/**
 * Repository to handle all customer related things
 */
class CustomerRepositoryImpl @Inject constructor(private val apiHelper: ApiHelper) :
    CustomerRepository {

    /**
     * gets customers by provided queries
     */
    override suspend fun getCustomers(query: Map<String, String>): Resource<List<CustomerResource>> =
        withContext(Dispatchers.IO) {
            try {

                val response = apiHelper.getCustomers(query = query)

                if (!response.isSuccessful) {
                    //
                    throw Exception("api error")
                }

                Resource.success(data = response.body()!!.data)

            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(message = e.message)
            }
        }

    /**
     * gets customers with provided query and return whole json document
     */
    override suspend fun getCustomersWithDocument(query: Map<String, String>): Resource<JsonDocument<List<CustomerResource>>> =
        withContext(Dispatchers.IO) {
            try {

                val response = apiHelper.getCustomers(query)

                if (!response.isSuccessful) {
                    //
                    throw Exception ("api error")
                }

                Resource.success(data = response.body()!!)

            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(message = e.message)
            }
        }


    override suspend fun getCustomer(
        uuid: String,
        query: Map<String, String>
    ): Resource<JsonDocument<CustomerResource>>  = withContext(Dispatchers.IO) {
        try {

            val response = apiHelper.getCustomer(uuid, query)

            if (!response.isSuccessful) {
                //
                throw Exception("Api Error !")
            }

            Resource.success(data = response.body()!!)

        } catch (e: Exception) {
            e.printStackTrace()
            Resource.error(message = e.message)
        }
    }

}