package tech.sumato.utility360.domain.use_case.instruction

import tech.sumato.utility360.domain.repository.instructions.InstructionsRepository
import javax.inject.Inject

class NeedToStoreInstructionsUseCase @Inject constructor(
    private val instructionsRepository: InstructionsRepository
) {

    suspend operator fun invoke(): Boolean {
        return instructionsRepository.checkIfHasInstructions()
    }

}