package tech.sumato.utility360.domain.use_case.user

import tech.sumato.utility360.data.local.entity.user.UserEntity
import tech.sumato.utility360.domain.repository.user.UserRepository
import javax.inject.Inject

class GetUserFromSharedPrefUseCase @Inject constructor(
    private val userRepository: UserRepository
) {

    suspend operator fun invoke() : UserEntity {
        return userRepository.getUserFromSharedPref()
    }

}