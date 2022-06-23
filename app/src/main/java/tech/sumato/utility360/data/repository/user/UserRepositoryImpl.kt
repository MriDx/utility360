package tech.sumato.utility360.data.repository.user

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import tech.sumato.utility360.data.local.entity.user.UserEntity
import tech.sumato.utility360.data.remote.model.user.LoginRequest
import tech.sumato.utility360.data.remote.model.user.LoginResponse
import tech.sumato.utility360.data.remote.model.user.UserResponse
import tech.sumato.utility360.data.remote.model.utils.ErrorResponse
import tech.sumato.utility360.data.remote.model.utils.SimpleResponse
import tech.sumato.utility360.data.remote.utils.Resource
import tech.sumato.utility360.data.remote.web_service.services.ApiHelper
import tech.sumato.utility360.domain.repository.user.UserRepository
import tech.sumato.utility360.utils.*
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiHelper: ApiHelper,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
) : UserRepository {


    override suspend fun login(loginRequest: LoginRequest): Resource<LoginResponse> =
        withContext(Dispatchers.IO) {
            try {
                val requestBody = loginRequest.toJson().toString()
                    .toRequestBody(contentType = "application/json".toMediaTypeOrNull())

                val response = apiHelper.login(loginRequestBody = requestBody)

                if (!response.isSuccessful) {
                    //
                    throw Exception("Something went wrong !")
                }

                saveUser(loginResponse = response.body()!!.data!!)

                Resource.success(data = response.body()!!.data)

            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(message = e.message)
            }
        }

    override suspend fun saveUser(loginResponse: LoginResponse) = withContext(Dispatchers.IO) {

        sharedPreferences.edit {
            putBoolean(LOGGED_IN, true)
            putString(USER_ID, loginResponse.user!!.id)
            putString(USER_TYPE, loginResponse.user.type)
            putString(TOKEN, loginResponse.getAccessToken())
            putString(NAME, loginResponse.user.attributes!!.name)
            putString(EMAIL, loginResponse.user.attributes.email)
            putString(ROLE, loginResponse.user.attributes.role)
            putString(GEO_AREA, loginResponse.user.attributes.geographical_area)
            putString(JOINED_FORMATTED, loginResponse.user.attributes.created?.formatted)
            putString(JOINED_DATE, loginResponse.user.attributes.created?.date)
            putString(PHOTO, loginResponse.user.attributes.photo)
            //putString(PERMISSIONS, loginResponse.user.attributes.permissions)
        }

    }

    override suspend fun getUserFromSharedPref(): UserEntity {
        return withContext(Dispatchers.Default) { UserEntity.fromMap(sharedPreferences.all) }
    }


    override suspend fun checkUserLogin(): Boolean {
        return withContext(Dispatchers.Default) {
            sharedPreferences.getBoolean(LOGGED_IN, false)
        }
    }

    override suspend fun logout(): Boolean =
        withContext(Dispatchers.IO) {
            try {

                val response = apiHelper.logout()

                if (!response.isSuccessful) {
                    if (response.code() == 401) {
                        clearUserData()
                        return@withContext true
                    }
                    throw Exception("Api error")
                }

                clearUserData()

                true

            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    override suspend fun clearUserData() {
        withContext(Dispatchers.Default) {
            sharedPreferences.edit {
                clear()
            }
        }
    }


    override suspend fun changePassword(params: JSONObject): Resource<SimpleResponse> =
        withContext(Dispatchers.IO) {

            try {

                val requestBody = params.toString().toRequestBody("application/json".toMediaType())

                val response = apiHelper.changePassword(requestBody = requestBody)

                if (!response.isSuccessful) {
                    //
                    if (response.code() == 401) {
                        //un authorised
                        throw Exception("Your sessions has expired. Please logout and login again to continue !")
                    }
                    val errorResponse =
                        gson.fromJson(response.errorBody()?.charStream(), ErrorResponse::class.java)
                    throw Exception(errorResponse.message)
                }

                Resource.success(
                    data = response.body()!!
                )

            } catch (e: Exception) {
                e.printStackTrace()
                Resource.error(message = parseException(e))
            }

        }

}