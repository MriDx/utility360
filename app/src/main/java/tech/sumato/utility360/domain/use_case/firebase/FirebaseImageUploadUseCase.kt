package tech.sumato.utility360.domain.use_case.firebase

import tech.sumato.utility360.domain.repository.firebase.FirebaseRepository
import javax.inject.Inject

class FirebaseImageUploadUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {

    suspend operator fun invoke(imagePath: String) : String? {
        return firebaseRepository.uploadImage(imagePath)
    }


}