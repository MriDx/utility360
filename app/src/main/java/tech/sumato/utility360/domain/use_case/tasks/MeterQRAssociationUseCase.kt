package tech.sumato.utility360.domain.use_case.tasks

import org.json.JSONObject
import tech.sumato.utility360.data.remote.model.utils.SimpleResponse
import tech.sumato.utility360.data.remote.utils.Resource
import tech.sumato.utility360.domain.repository.tasks.TasksRepository
import javax.inject.Inject

class MeterQRAssociationUseCase @Inject constructor(
    private val tasksRepository: TasksRepository
) {

    suspend operator fun invoke(
        customerUuid: String,
        params: JSONObject
    ): Resource<SimpleResponse> {
        return tasksRepository.meterQrAssociation(customerUuid, params)
    }

}