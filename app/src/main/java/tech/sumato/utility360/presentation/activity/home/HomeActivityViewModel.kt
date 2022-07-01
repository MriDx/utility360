package tech.sumato.utility360.presentation.activity.home

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import tech.sumato.utility360.data.local.entity.user.UserEntity
import tech.sumato.utility360.data.local.model.instructions.InstructionItemsModel
import tech.sumato.utility360.data.local.model.user.UserModel
import tech.sumato.utility360.domain.use_case.instruction.GetInstructionsUseCase
import tech.sumato.utility360.domain.use_case.user.GetUserFromSharedPrefUseCase
import tech.sumato.utility360.domain.use_case.user.LogoutUserUseCase
import tech.sumato.utility360.presentation.utils.Navigation
import javax.inject.Inject

@HiltViewModel
class HomeActivityViewModel @Inject constructor(
    private val getUserFromSharedPrefUseCase: GetUserFromSharedPrefUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val getInstructionsUseCase: GetInstructionsUseCase,
) : ViewModel(), Navigation {


    private var userData_ = MutableSharedFlow<UserEntity>()
    val userData: SharedFlow<UserEntity> = userData_

    private var logoutResponse_ = MutableSharedFlow<Boolean>()
    val logoutResponse: SharedFlow<Boolean> = logoutResponse_

    private var instructions_ = Channel<List<InstructionItemsModel>>()
    val instructions = instructions_.receiveAsFlow()


    init {
        getUser()
        getInstructions()
    }

    private fun getUser() {
        viewModelScope.launch {
            val userEntity = getUserFromSharedPrefUseCase()
            userData_.emit(userEntity)
        }
    }


    private fun getInstructions() {
        viewModelScope.launch(Dispatchers.IO) {
            val instructionList = getInstructionsUseCase.getAll()
            instructions_.send(instructionList)
        }
    }

    fun logout() {
        viewModelScope.launch {
            val response = logoutUserUseCase()

            logoutResponse_.emit(response)

        }
    }


}