package tech.sumato.utility360.presentation.fragments.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import tech.sumato.utility360.data.local.entity.user.UserEntity
import tech.sumato.utility360.domain.use_case.user.GetUserFromSharedPrefUseCase
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val getUserFromSharedPrefUseCase: GetUserFromSharedPrefUseCase
) : ViewModel() {


    private var userData_ = MutableSharedFlow<UserEntity>()
    val userData: SharedFlow<UserEntity> = userData_


    init {
        getUser()
    }

    private fun getUser() {
        viewModelScope.launch {
            val userEntity = getUserFromSharedPrefUseCase()
            userData_.emit(userEntity)
        }
    }
}