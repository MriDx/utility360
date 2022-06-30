package tech.sumato.utility360.domain.use_case.instruction

import tech.sumato.utility360.data.local.model.instructions.InstructionItemsModel
import tech.sumato.utility360.domain.repository.instructions.InstructionsRepository
import javax.inject.Inject

class GetInstructionsUseCase @Inject constructor(
    private val instructionsRepository: InstructionsRepository
) {


    suspend fun byType(type: String): InstructionItemsModel {
        return instructionsRepository.getByType(type = type)
    }

    suspend fun getAll(): List<InstructionItemsModel> {
        return instructionsRepository.getInstructions()
    }

}