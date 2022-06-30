package tech.sumato.utility360.presentation.fragments.instruction.instruction_view_dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.data.local.entity.instructions.InstructionItemEntity
import tech.sumato.utility360.data.local.model.instructions.InstructionItemModel
import tech.sumato.utility360.databinding.InstructionViewDialogBinding
import javax.inject.Inject

@AndroidEntryPoint
class InstructionViewDialogFragment : BottomSheetDialogFragment() {


    class Builder {
        private var acceptanceListener: ((accepted: Boolean) -> Unit)? = null

        private var args = bundleOf()

        fun setInstructionItem(item: InstructionItemModel): Builder {
            args.putParcelable("data", item.toEntity())
            return this
        }

        fun showAcceptBtn(show: Boolean = true): Builder {
            args.putBoolean("accept_btn", show)
            return this
        }

        fun setAcceptListener(listener: (accepted: Boolean) -> Unit): Builder {
            acceptanceListener = listener
            return this
        }

        fun build(): InstructionViewDialogFragment {
            val fragment = InstructionViewDialogFragment()
            fragment.arguments = args
            acceptanceListener?.let { fragment.setListener(it) }
            return fragment
        }

    }

    private var binding_: InstructionViewDialogBinding? = null
    private val binding get() = binding_!!

    private var acceptanceListener: ((accepted: Boolean) -> Unit)? = null

    private lateinit var instructionItemEntity: InstructionItemEntity

    fun setListener(listener: (accepted: Boolean) -> Unit) {
        this.acceptanceListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = InstructionViewDialogBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        instructionItemEntity = arguments?.getParcelable<InstructionItemEntity>("data")
            ?: throw Exception("instruction item not found !")

        binding.titleView.text = instructionItemEntity.title
        binding.descriptionView.text = instructionItemEntity.description

        val acceptBtn = arguments?.getBoolean("accept_btn")

        if (!acceptBtn!!) {
            binding.acceptBtn.isVisible = acceptBtn
        } else {
            binding.acceptBtn.isVisible = !instructionItemEntity.accepted
        }

        binding.acceptBtn.setOnClickListener {
            acceptanceListener?.invoke(true)
            this.dismiss()
        }

    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()

    }


}