package tech.sumato.utility360.data.local.entity.user

import androidx.annotation.Keep
import tech.sumato.utility360.utils.*

@Keep
data class UserEntity(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val geoArea: String = "",
    val joinedOn: String = "",
    val photo: String = "",
) {

    companion object {
        fun fromMap(map: Map<String, *>): UserEntity {
            return UserEntity(
                id = map[USER_ID].toString(),
                name = map[NAME].toString(),
                email = map[EMAIL].toString(),
                role = map[ROLE].toString(),
                geoArea = map[GEO_AREA].toString(),
                joinedOn = map[JOINED_FORMATTED].toString(),
                photo = map[PHOTO].toString()
            )
        }
    }

    fun getDetailsMap(): Map<String, String> {
        return mapOf(
            "Email" to email,
            "Geographical area" to geoArea,
            "Joined on" to joinedOn
        )
    }

}