package tech.sumato.utility360.data.repository.tasks

import com.google.gson.Gson
import com.undabot.izzy.models.JsonDocument
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import tech.sumato.utility360.data.remote.model.customer.CustomerResource
import tech.sumato.utility360.data.remote.model.utils.ErrorResponse
import tech.sumato.utility360.data.remote.model.utils.SimpleResponse
import tech.sumato.utility360.data.remote.utils.Resource
import tech.sumato.utility360.data.remote.web_service.services.ApiHelper
import tech.sumato.utility360.domain.repository.tasks.TasksRepository
import tech.sumato.utility360.utils.parseException
import javax.inject.Inject

class TasksRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper,
    private val gson: Gson
) : TasksRepository {


    /**
     * fetches pending site verifications tasks for the requested user
     *
     * @param   query       Map<String, String> for additional query, page numbers etc
     *
     * @return              Resource of type JsonDocument containing List of CustomerResource
     */
    override suspend fun getPendingSiteVerifications(query: Map<String, String>): Resource<JsonDocument<List<CustomerResource>>> =
        withContext(Dispatchers.IO) {

            try {

                val response = apiHelper.pendingSiteVerifications(query)

                if (!response.isSuccessful) {
                    //
                    throw Exception("Something went wrong !")
                }

                Resource.success(data = response.body()!!)

            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(message = e.message)
            }

        }


    /**
     * submits site verification details to server
     *
     * @param       customerUuid        uuid of the customer, whose site is being verified
     * @param       jsonParams          JSONObject of all the details of site verification
     *
     * @return                          Resource of SimpleResponse
     *
     */

    override suspend fun submitSiteVerification(
        customerUuid: String,
        jsonParams: JSONObject
    ): Resource<SimpleResponse> = withContext(Dispatchers.IO) {
        try {

            val requestBody =
                jsonParams.toString().toRequestBody("application/json".toMediaTypeOrNull())

            val response = apiHelper.submitSiteVerification(
                uuid = customerUuid,
                siteVerificationRequestBody = requestBody
            )

            if (!response.isSuccessful) {
                //
                throw Exception("api error")
            }

            Resource.success(data = response.body()!!)

        } catch (e: Exception) {
            e.printStackTrace()
            Resource.error(message = e.message)
        }
    }


    /**
     * submit meter installation details to server
     *
     * @param       customerUuid        uuid of the customer whose meter is being installed
     * @param       jsonParams          JSONObject of all the details of meter installation
     *
     * @return                          Resource of SimpleResponse
     */
    override suspend fun submitMeterInstallation(
        customerUuid: String,
        jsonParams: JSONObject
    ): Resource<SimpleResponse> =
        withContext(Dispatchers.IO) {
            try {

                val requestBody =
                    jsonParams.toString().toRequestBody("application/json".toMediaTypeOrNull())


                val response = apiHelper.submitMeterInstallation(
                    uuid = customerUuid,
                    requestBody = requestBody
                )

                if (!response.isSuccessful) {
                    //
                    throw Exception("api error")
                }

                Resource.success(data = response.body()!!)


            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(message = e.message)
            }
        }


    /**
     * fetches pending meter installation tasks for the requested user
     *
     * @param       query       Query in Map<String, String>
     *
     * @return                  Response of JsonDocument containing List of CustomerResource
     */
    override suspend fun getPendingMeterInstallations(query: Map<String, String>):
            Resource<JsonDocument<List<CustomerResource>>> =
        withContext(Dispatchers.IO) {
            try {

                val response = apiHelper.pendingMeterInstallations(query)

                if (!response.isSuccessful) {
                    //
                    throw Exception("Something went wrong !")
                }

                Resource.success(data = response.body()!!)

            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(message = e.message)
            }
        }


    /**
     * submit meter reaading details to server
     *
     * @param       customerUuid        uuid of the customer whose meter is being read
     * @param       params          JSONObject of all the details of meter reading
     *
     * @return                          Resource of SimpleResponse
     */
    override suspend fun submitMeterReading(
        customerUuid: String,
        params: JSONObject
    ): Resource<SimpleResponse> = withContext(Dispatchers.IO) {
        try {

            val requestBody =
                params.toString().toRequestBody("application/json".toMediaTypeOrNull())
            val response = apiHelper.submitMeterReading(customerUuid, requestBody)

            if (!response.isSuccessful) {
                //
                if (response.code() >= 500) {
                    throw Exception("Server error !")
                }
                throw Exception("Something went wrong !")
            }

            Resource.success(data = response.body()!!)

        } catch (e: Exception) {
            e.printStackTrace()
            Resource.error(message = e.message)
        }
    }

    override suspend fun meterQrAssociation(
        customerUuid: String,
        params: JSONObject
    ): Resource<SimpleResponse> = withContext(Dispatchers.IO) {
        try {

            val requestBody = params.toString().toRequestBody("application/json".toMediaType())

            val response = apiHelper.qrMeterAssociation(customerUuid, requestBody)

            if (!response.isSuccessful) {
                if (response.code() >= 500) {
                    throw Exception("Server Error !")
                }
                if (response.code() == 401) {
                    throw Exception("Please re-login and try again !")
                }
                val errorResponse = gson.fromJson(
                    response.errorBody()?.charStream(),
                    ErrorResponse::class.java
                )
                throw Exception(errorResponse.message)
            }
            Resource.success(data = response.body()!!)


        } catch (e: Exception) {
            e.printStackTrace()
            Resource.error(message = parseException(e))
        }
    }

}

