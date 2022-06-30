package tech.sumato.utility360.presentation.fragments.customer.verification.instruction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
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
import tech.sumato.utility360.presentation.activity.customer.verification.CustomerVerificationActivityViewModel
import tech.sumato.utility360.presentation.fragments.base.instruction.BaseInstructionFragment
import tech.sumato.utility360.presentation.fragments.customer.find.FindCustomerFragmentV2
import tech.sumato.utility360.presentation.fragments.instruction.instruction_view_dialog.InstructionViewDialogFragment
import tech.sumato.utility360.presentation.fragments.tasks.pending_verification_tasks.PendingSiteVerificationTasksFragment

@AndroidEntryPoint
class SiteVerificationInstructionFragment : BaseInstructionFragment() {


    lateinit var instructionItemsModel: InstructionItemsModel

    private val viewModel by activityViewModels<CustomerVerificationActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.instructions.collectLatest { instruction ->
                        instructionItemsModel = instruction
                        renderInstructions()
                    }
                }
            }
        }

        viewModel.getInstructions()

    }

    private fun renderInstructions() {
        setTitle(title = instructionItemsModel.instructionModel.title)
        setDescription(description = instructionItemsModel.instructionModel.description)
        setInstructionItemSize(size = instructionItemsModel.items.size)
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
            checkbox.isChecked = item.accepted
            titleView.text = item.title
            descriptionView.text = item.description
            root.setOnClickListener {
                //
                showInstructionViewDialog(instructionItem = item, index = index)
            }
        }
    }


    private fun showInstructionViewDialog(instructionItem: InstructionItemModel, index: Int) {
        InstructionViewDialogFragment.Builder()
            .setInstructionItem(instructionItem)
            .setAcceptListener {
                instructionAccepted(instructionItem, it, index)
            }.build()
            .show(childFragmentManager, "Instruction view")
    }


    private fun instructionAccepted(
        instructionItem: InstructionItemModel,
        accepted: Boolean,
        index: Int
    ) {
        if (!accepted) return
        //update to db
        //re render list
        val item = instructionItemsModel.items.find { it.id == instructionItem.id }
        item?.accepted = true
        itemChanged(index)
        //check if all items accepted and redirect to meter reading flow if positive
        //handleIfAllAccepted()
        item?.let { viewModel.updateItemAccepted(item = it) }


    }


    override fun onContinueClicked() {
        val accepted = instructionItemsModel.items.all { it.accepted }
        if (!accepted) {
            showSnackbar(message = "Read and accept all instructions/steps before continue !")
            return
        }
        //all accepted, navigate to site verification flow
        viewModel.navigate(
            fragment = PendingSiteVerificationTasksFragment::class.java
        )
    }


}