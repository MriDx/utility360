package tech.sumato.utility360.presentation.activity.instructions

import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.data.local.model.instructions.InstructionItemsModel
import tech.sumato.utility360.presentation.activity.base.BaseActivity
import tech.sumato.utility360.presentation.activity.base.fragment_holder.FragmentHolderActivity
import tech.sumato.utility360.presentation.fragments.instruction.InstructionFragment

@AndroidEntryPoint
class InstructionActivity : FragmentHolderActivity() {


    private lateinit var type: String

    private val viewModel by viewModels<InstructionActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        type = intent?.getStringExtra("type")
            ?: throw IllegalArgumentException("Invalid instructions type passed !")



        viewModel.getInstructions(type = type)

        handleFragment()

    }

    private fun handleFragment() {
        val fragment = InstructionFragment()

        addFragment(
            fragment = fragment,
            addToBackStack = false,
            replace = true
        )

    }


}