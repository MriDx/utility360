package tech.sumato.utility360.data.repository.tasks

import com.undabot.izzy.models.JsonDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.utils.Resource
import tech.sumato.utility360.data.remote.web_service.services.ApiHelper
import tech.sumato.utility360.domain.repository.tasks.TasksRepository
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper
) : TasksRepository {


    override suspend fun getPendingSiteVerifications(query: Map<String, String>): Resource<JsonDocument<List<CustomerResource>>> =
        withContext(Dispatchers.IO) {

            try {

                val response = apiHelper.pendingSiteVerifications(query)

                if (!response.isSuccessful) {
                    //
                    throw Exception("Api error")
                }

                Resource.success(data = response.body()!!)

            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(message = e.message)
            }

        }

}

