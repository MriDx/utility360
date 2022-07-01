package tech.sumato.utility360.presentation.activity.splash

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import tech.sumato.utility360.domain.use_case.instruction.CreateOrUpdateInstructionsUseCase
import tech.sumato.utility360.domain.use_case.instruction.NeedToStoreInstructionsUseCase
import javax.inject.Inject

@HiltViewModel
class SplashActivityViewModel @Inject constructor(
    private val createOrUpdateInstructionsUseCase: CreateOrUpdateInstructionsUseCase,
    private val needToStoreInstructionsUseCase: NeedToStoreInstructionsUseCase,
) : ViewModel() {


    private var preLaunchInfo_ = Channel<Boolean>()
    val preLaunchInfo = preLaunchInfo_.receiveAsFlow()


    init {
        checkIfNeedToStoreInstructions()
    }

    private fun checkIfNeedToStoreInstructions() {
        viewModelScope.launch(Dispatchers.IO) {
            val exists = needToStoreInstructionsUseCase()

            Log.d("mridx", "checkIfNeedToStoreInstructions: $exists")

            if (exists) {
                preLaunchInfo_.send(true)
                return@launch
            }
            createOrUpdateInstructionsUseCase()
            preLaunchInfo_.send(true)

        }
    }


}