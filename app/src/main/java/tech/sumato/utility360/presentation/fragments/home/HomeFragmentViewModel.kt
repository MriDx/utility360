package tech.sumato.utility360.presentation.fragments.home

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
import tech.sumato.utility360.domain.use_case.instruction.GetInstructionsUseCase
import tech.sumato.utility360.domain.use_case.user.GetUserFromSharedPrefUseCase
import javax.inject.Inject

@HiltViewModel
class HomeFragmentViewModel @Inject constructor(
    private val getUserFromSharedPrefUseCase: GetUserFromSharedPrefUseCase,
    private val getInstructionsUseCase: GetInstructionsUseCase,
) : ViewModel() {


    private var userData_ = MutableSharedFlow<UserEntity>()
    val userData: SharedFlow<UserEntity> = userData_

    private var instructions_ = Channel<List<InstructionItemsModel>>()
    val instructions = instructions_.receiveAsFlow()


    init {
        getUser()
        //getInstructions()
    }

    private fun getInstructions() {
        viewModelScope.launch(Dispatchers.IO) {
            val instructionList = getInstructionsUseCase.getAll()
            instructions_.send(instructionList)
        }
    }

    private fun getUser() {
        viewModelScope.launch {
            val userEntity = getUserFromSharedPrefUseCase()
            userData_.emit(userEntity)
        }
    }
}