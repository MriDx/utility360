package tech.sumato.utility360.domain.use_case.instruction

import tech.sumato.utility360.data.local.model.instructions.InstructionItemModel
import tech.sumato.utility360.domain.repository.instructions.InstructionsRepository
import javax.inject.Inject

class UpdateInstructionItemUseCase @Inject constructor(
    private val instructionsRepository: InstructionsRepository
) {

    suspend fun updateItem(item: InstructionItemModel) {
        instructionsRepository.updateInstructionItem(item = item)
    }

    suspend fun updateAllUnSelect() {
        instructionsRepository.updateAllUnselect()
    }

}