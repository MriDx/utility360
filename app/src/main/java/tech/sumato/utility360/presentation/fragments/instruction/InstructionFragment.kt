package tech.sumato.utility360.presentation.fragments.instruction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.sumato.utility360.R
import tech.sumato.utility360.data.local.model.instructions.InstructionItemModel
import tech.sumato.utility360.data.local.model.instructions.InstructionItemsModel
import tech.sumato.utility360.databinding.InstructionItemViewBinding
import tech.sumato.utility360.presentation.activity.instructions.InstructionActivityViewModel
import tech.sumato.utility360.presentation.fragments.base.instruction.BaseInstructionFragment
import tech.sumato.utility360.presentation.fragments.instruction.instruction_view_dialog.InstructionViewDialogFragment

@AndroidEntryPoint
class InstructionFragment : BaseInstructionFragment() {


    /*class Builder {
        private var args = Bundle()

        fun setInstructionItemsModel(instructionItemsModel: InstructionItemsModel): Builder {
            args.putParcelable("data", instructionItemsModel)
            return this
        }

        fun build(): InstructionFragment {
            val fragment = InstructionFragment()
            fragment.arguments = args
            return fragment
        }

    }*/

    private lateinit var instructionItemsModel: InstructionItemsModel

    private val viewModel by activityViewModels<InstructionActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /*instructionItemsModel = arguments?.getParcelable<InstructionItemsModel>("data")
            ?: throw IllegalArgumentException("Invalid instructions data ")*/


        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.instructions.collectLatest { instructions ->
                        handleInstruction(instructions)
                    }
                }
            }
        }

        hideContinueBtn()

        //setInstructionItemSize(instructionItemsModel.items.size)


    }

    private fun handleInstruction(instructions: InstructionItemsModel) {
        instructionItemsModel = instructions
        setInstructionItemSize(instructions.items.size)
        setTitle(instructions.instructionModel.title)
        setDescription(instructions.instructionModel.description)
    }


    override fun buildInstructionItemView(parent: ViewGroup, index: Int): View {
        return DataBindingUtil.inflate<InstructionItemViewBinding>(
            LayoutInflater.from(parent.context),
            R.layout.instruction_item_view,
            parent,
            false
        ).root
    }

    override fun bindInstructionItem(viewHolder: RecyclerView.ViewHolder, index: Int) {
        val item = instructionItemsModel.items[index]
        DataBindingUtil.bind<InstructionItemViewBinding>(viewHolder.itemView)?.apply {
            //checkbox.isChecked = item.accepted
            checkbox.isVisible = false
            titleView.text = item.title
            descriptionView.text = item.description
            root.setOnClickListener {
                //
                showInstructionViewDialog(instructionItem = item, index = index)
            }
        }
    }

    override fun onContinueClicked() {

    }

    private fun showInstructionViewDialog(instructionItem: InstructionItemModel, index: Int) {
        InstructionViewDialogFragment.Builder()
            .setInstructionItem(instructionItem)
            .showAcceptBtn(show = false)
            .build()
            .show(childFragmentManager, "Instruction view")
    }


}