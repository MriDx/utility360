package tech.sumato.utility360.presentation.activity.instructions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import tech.sumato.utility360.data.local.model.instructions.InstructionItemsModel
import tech.sumato.utility360.domain.use_case.instruction.GetInstructionsUseCase
import javax.inject.Inject

@HiltViewModel
class InstructionActivityViewModel @Inject constructor(
    private val getInstructionsUseCase: GetInstructionsUseCase,
) : ViewModel() {


    private var instructions_ = Channel<InstructionItemsModel>()
    val instructions = instructions_.receiveAsFlow()


    fun getInstructions(type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val instructionsData = getInstructionsUseCase.byType(type = type)
            instructions_.send(instructionsData)
        }
    }


}