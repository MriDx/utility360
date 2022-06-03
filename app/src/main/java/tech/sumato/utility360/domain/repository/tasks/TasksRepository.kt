package tech.sumato.utility360.domain.repository.tasks

import com.undabot.izzy.models.JsonDocument
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.utils.Resource
import javax.inject.Inject

interface TasksRepository {


    suspend fun getPendingSiteVerifications(query: Map<String, String>): Resource<JsonDocument<List<CustomerResource>>>


}