package tech.sumato.utility360.domain.use_case.user

import tech.sumato.utility360.data.remote.model.user.LoginRequest
import tech.sumato.utility360.data.remote.model.user.LoginResponse
import tech.sumato.utility360.data.remote.model.user.UserResponse
import tech.sumato.utility360.data.remote.utils.Resource
import tech.sumato.utility360.domain.repository.user.UserRepository
import javax.inject.Inject

class LoginUserUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke(loginRequest: LoginRequest): Resource<LoginResponse> {
        return userRepository.login(loginRequest)
    }

}