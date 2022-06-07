package tech.sumato.utility360.presentation.activity.home

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import tech.sumato.utility360.data.local.entity.user.UserEntity
import tech.sumato.utility360.data.local.model.user.UserModel
import tech.sumato.utility360.domain.use_case.user.GetUserFromSharedPrefUseCase
import tech.sumato.utility360.domain.use_case.user.LogoutUserUseCase
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val getUserFromSharedPrefUseCase: GetUserFromSharedPrefUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
) : ViewModel() {


    private var userData_ = MutableSharedFlow<UserEntity>()
    val userData: SharedFlow<UserEntity> = userData_

    private var logoutResponse_ = MutableSharedFlow<Boolean>()
    val logoutResponse: SharedFlow<Boolean> = logoutResponse_


    init {
        getUser()
    }

    private fun getUser() {
        viewModelScope.launch {
            val userEntity = getUserFromSharedPrefUseCase()
            userData_.emit(userEntity)
        }
    }


    fun logout() {
        viewModelScope.launch {
            val response = logoutUserUseCase()

            logoutResponse_.emit(response)

        }
    }


}