package tech.sumato.utility360.presentation.activity.login

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import tech.sumato.utility360.data.remote.model.user.LoginRequest
import tech.sumato.utility360.data.remote.model.user.LoginResponse
import tech.sumato.utility360.data.remote.utils.Resource
import tech.sumato.utility360.domain.use_case.user.CheckLoggedInStatusUseCase
import tech.sumato.utility360.domain.use_case.user.LoginUserUseCase
import javax.inject.Inject

@HiltViewModel
class LoginActivityViewModel @Inject constructor(
    private val loginUserUseCase: LoginUserUseCase,
    private val checkUserUseCase: CheckLoggedInStatusUseCase,
) : ViewModel() {

    val loginRequest = ObservableField(LoginRequest())

    val isLoading = ObservableField<Boolean>()

    private var loginResponse_ = MutableSharedFlow<Resource<LoginResponse>>()
    val loginResponse: SharedFlow<Resource<LoginResponse>> = loginResponse_

    private var isLoggedIn_ = MutableSharedFlow<Boolean>()
    val isLoggedIn: SharedFlow<Boolean> = isLoggedIn_

    init {
        checkUser()
        fetchMessagingToken()
    }

    private fun fetchMessagingToken() {
        Firebase.messaging.token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loginRequest.get()?.device_name = task.result
            }
        }
    }


    fun login() {
        viewModelScope.launch(Dispatchers.IO) {

            val tmpLoginRequest = loginRequest.get()!!

            if (!tmpLoginRequest.validate()) {
                //email or password is empty
                return@launch
            }

            isLoading.set(true)
            isLoading.notifyChange()

            val response = loginUserUseCase(tmpLoginRequest)

            loginResponse_.emit(response)

            isLoading.set(false)
            isLoading.notifyChange()

        }
    }


    private fun checkUser() {
        viewModelScope.launch {
            val loggedIn = checkUserUseCase()
            isLoggedIn_.emit(loggedIn)
        }
    }


}