package tech.sumato.utility360.presentation.fragments.base.instruction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.databinding.BaseInstructionActivityBinding

@AndroidEntryPoint
abstract class BaseInstructionFragment : Fragment() {

    private var binding_: BaseInstructionActivityBinding? = null
    private val binding get() = binding_!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = BaseInstructionActivityBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {

            /*titleView.text = getTitle()
            descriptionView.text = getDescription()*/

            instructionHolder.apply {
                //setItemCount(getInstructionItemSize())
                itemBuilder = { parent, index ->
                    buildInstructionItemView(parent, index)
                }
                itemBinding { holder, index ->
                    bindInstructionItem(holder, index)
                }
            }.render()


            continueBtn.setOnClickListener {
                onContinueClicked()
            }

        }


    }

    open fun setTitle(title: String) {
        binding.titleView.text = title
    }

    open fun setDescription(description: String) {
        binding.descriptionView.text = description
    }

    open fun setInstructionItemSize(size: Int) {
        binding.instructionHolder.apply {
            setItemCount(size)
        }.render()
    }

    /* abstract fun getTitle(): String

     abstract fun getDescription(): String

     abstract fun getInstructionItemSize(): Int*/

    abstract fun buildInstructionItemView(parent: ViewGroup, index: Int): View

    abstract fun bindInstructionItem(viewHolder: RecyclerView.ViewHolder, index: Int)

    open fun itemChanged(index: Int) {
        binding.instructionHolder.itemChanged(index)
    }

    abstract fun onContinueClicked()

    open fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }


}