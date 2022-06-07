package tech.sumato.utility360.domain.repository.tasks

import com.undabot.izzy.models.JsonDocument
import org.json.JSONObject
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.model.utils.SimpleResponse
import tech.sumato.utility360.data.remote.utils.Resource
import javax.inject.Inject

interface TasksRepository {


    suspend fun getPendingSiteVerifications(query: Map<String, String>): Resource<JsonDocument<List<CustomerResource>>>

    suspend fun submitSiteVerification(
        customerUuid: String,
        jsonParams: JSONObject
    ): Resource<SimpleResponse>

    suspend fun submitMeterInstallation(
        customerUuid: String,
        jsonParams: JSONObject
    ): Resource<SimpleResponse>

    suspend fun getPendingMeterInstallations(query: Map<String, String>): Resource<JsonDocument<List<CustomerResource>>>

    suspend fun submitMeterReading(customerUuid: String, params: JSONObject): Resource<SimpleResponse>

}