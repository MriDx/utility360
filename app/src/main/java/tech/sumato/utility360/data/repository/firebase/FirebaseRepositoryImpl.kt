package tech.sumato.utility360.data.repository.firebase

import android.content.SharedPreferences
import androidx.core.net.toUri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import tech.sumato.utility360.domain.repository.firebase.FirebaseRepository
import tech.sumato.utility360.utils.*
import java.io.File
import java.util.*
import javax.inject.Inject


class FirebaseRepositoryImpl @Inject constructor(
    private val firebaseStorage: FirebaseStorage,
    private val sharedPreferences: SharedPreferences,
) : FirebaseRepository {


    override suspend fun uploadImage(imagePath: String): String? {

        return withContext(Dispatchers.IO) {

            try {

                val userId = sharedPreferences.getString(USER_ID, "") ?: ""

                if (userId.isEmpty()) {
                    //
                    throw Exception("User not found !")
                }

                val file = File(imagePath)

                if (!file.exists()) {
                    throw Exception("Selected image file not exist !")
                }

                val imageRef =
                    firebaseStorage.getReference("${SITE_IMAGE_DIR}${userId}_${Date().time}.${file.extension}")

                val uploadResponse = imageRef.putFile(file.toUri()).await()

                if (uploadResponse.error != null) {
                    //upload failed
                    throw Exception("upload failed")
                }

                val uploadedImagePath =
                    "$FIREBASE_STORAGE$BUCKET${uploadResponse.metadata?.path?.replace(
                        "/",
                        "%2F"
                    )}$MEDIA"

                uploadedImagePath


            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        }

    }


    /**
     * uploads image to firebase storage at provided upload type path
     *
     * @param   imagePath       path of the image to be upload
     * @param   uploadType      upload type
     * @return                  Returns file path after successful upload otherwise null
     */
    override suspend fun uploadImage(imagePath: String, uploadType: String): String? {
        return withContext(Dispatchers.IO) {

            try {
                val userId = sharedPreferences.getString(USER_ID, "") ?: ""

                if (userId.isEmpty()) {
                    //
                    throw Exception("User not found !")
                }

                val file = File(imagePath)

                if (!file.exists()) {
                    throw Exception("Selected image file not exist !")
                }


                val imageRef =
                    firebaseStorage.getReference("${uploadType}${userId}_${Date().time}.${file.extension}")

                val uploadResponse = imageRef.putFile(file.toUri()).await()

                if (uploadResponse.error != null) {
                    //upload failed
                    throw Exception("upload failed")
                }

                val uploadedImagePath =
                    "$FIREBASE_STORAGE$BUCKET${
                        uploadResponse.metadata?.path?.replace(
                            "/",
                            "%2F"
                        )
                    }$MEDIA"


                uploadedImagePath

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }


        }
    }


}