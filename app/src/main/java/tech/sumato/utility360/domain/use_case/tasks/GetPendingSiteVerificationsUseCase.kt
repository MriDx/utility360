package tech.sumato.utility360.domain.use_case.tasks

import com.undabot.izzy.models.JsonDocument
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.utils.Resource
import tech.sumato.utility360.domain.repository.tasks.TasksRepository
import javax.inject.Inject

class GetPendingSiteVerificationsUseCase @Inject constructor(
    private val tasksRepository: TasksRepository
) {


    suspend operator fun invoke(query: Map<String, String>): Resource<JsonDocument<List<CustomerResource>>> {
        return tasksRepository.getPendingSiteVerifications(query)
    }


}