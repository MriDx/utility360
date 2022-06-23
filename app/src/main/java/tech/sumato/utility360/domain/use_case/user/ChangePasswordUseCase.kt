package tech.sumato.utility360.domain.use_case.user

import org.json.JSONObject
import tech.sumato.utility360.data.remote.model.utils.SimpleResponse
import tech.sumato.utility360.data.remote.utils.Resource
import tech.sumato.utility360.domain.repository.user.UserRepository
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke(params: JSONObject): Resource<SimpleResponse> {
        return userRepository.changePassword(params)
    }

}