package tech.sumato.utility360.domain.use_case.user

import tech.sumato.utility360.domain.repository.user.UserRepository
import javax.inject.Inject

class CheckLoggedInStatusUseCase @Inject constructor(private val userRepository: UserRepository) {

    suspend operator fun invoke(): Boolean {
        return userRepository.checkUserLogin()
    }

}