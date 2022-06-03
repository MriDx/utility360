package tech.sumato.utility360.domain.repository.user

import tech.sumato.utility360.data.local.entity.user.UserEntity
import tech.sumato.utility360.data.remote.model.user.LoginRequest
import tech.sumato.utility360.data.remote.model.user.LoginResponse
import tech.sumato.utility360.data.remote.model.user.UserResponse
import tech.sumato.utility360.data.remote.utils.Resource

interface UserRepository {

    suspend fun login(loginRequest: LoginRequest): Resource<LoginResponse>

    suspend fun saveUser(loginResponse: LoginResponse)

    suspend fun getUserFromSharedPref(): UserEntity
    suspend fun checkUserLogin(): Boolean
    suspend fun logout(): Boolean

    suspend fun clearUserData()

}