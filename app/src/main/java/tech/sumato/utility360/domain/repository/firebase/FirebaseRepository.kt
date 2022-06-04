package tech.sumato.utility360.domain.repository.firebase

interface FirebaseRepository {



    suspend fun uploadImage(imagePath: String) : String?

    suspend fun uploadImage(imagePath: String, uploadType: String): String?

}