package tech.sumato.utility360.presentation.fragments.meter.reading.instruction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import tech.sumato.utility360.presentation.activity.meter.reading.MeterReadingActivityViewModel
import tech.sumato.utility360.presentation.fragments.base.instruction.BaseInstructionFragment
import tech.sumato.utility360.presentation.fragments.customer.find.FindCustomerFragmentV2
import tech.sumato.utility360.presentation.fragments.instruction.instruction_view_dialog.InstructionViewDialogFragment

@AndroidEntryPoint
class MeterReadingInstructionFragment : BaseInstructionFragment() {


    lateinit var instructionItemsModel: InstructionItemsModel

    private val viewModel by activityViewModels<MeterReadingActivityViewModel>()


    /* override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)

         instructionItemsModel = arguments?.getParcelable<InstructionItemsModel>("data")
             ?: throw Exception("Instruction data not available ")

     }*/

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

    /*override fun getTitle(): String {
        return instructionItemsModel.instructionModel.title
    }

    override fun getDescription(): String {
        return instructionItemsModel.instructionModel.description
    }

    override fun getInstructionItemSize(): Int {
        return instructionItemsModel.items.size
    }*/

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

    private fun handleIfAllAccepted() {
        val accepted = instructionItemsModel.items.all { it.accepted }
        if (!accepted) return
        //all accepted, navigate to meter reading flow
        viewModel.navigate(
            fragment = FindCustomerFragmentV2::class.java
        )
    }

    override fun onContinueClicked() {
        val accepted = instructionItemsModel.items.all { it.accepted }
        if (!accepted) {
            showSnackbar(message = "Read and accept all instructions/steps before continue !")
            return
        }
        //all accepted, navigate to meter reading flow
        viewModel.navigate(
            fragment = FindCustomerFragmentV2::class.java
        )
    }


}