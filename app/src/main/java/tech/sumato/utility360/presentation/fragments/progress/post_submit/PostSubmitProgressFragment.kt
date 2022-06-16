package tech.sumato.utility360.presentation.fragments.progress.post_submit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.databinding.PostSubmitProgressBinding
import tech.sumato.utility360.presentation.utils.ParentActivity

@AndroidEntryPoint
open class PostSubmitProgressFragment : Fragment(), ParentActivity {

    private var binding_: PostSubmitProgressBinding? = null
    private val binding get() = binding_!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = PostSubmitProgressBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.progressCancelBtn.setOnClickListener {
            requireActivity().onBackPressed()
        }

    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }

    override fun <T> getParentActivity(): T {
        return requireActivity() as T
    }


    open fun showProgressbar() {
        binding.processingProgressbar.isVisible = true
        hidePrimaryBtn()
        hideSecondaryBtn()
    }


    open fun hideProgressbar() {
        binding.processingProgressbar.isVisible = false
        hidePrimaryBtn()
        hideSecondaryBtn()
    }

    open fun setStatusInfo(infoText: String) {
        binding.processingRequestHeading.text = infoText
    }

    open fun showPrimaryBtn(label: String, onClick: View.OnClickListener) {
        binding.primaryBtn.apply {
            text = label
            setOnClickListener(onClick)
        }.isVisible = true
    }

    open fun showSecondaryBtn(label: String, onClick: View.OnClickListener) {
        binding.secondaryBtn.apply {
            text = label
            setOnClickListener(onClick)
        }.isVisible = true
    }

    open fun hidePrimaryBtn() {
        binding.primaryBtn.isVisible = false
    }

    private fun hideSecondaryBtn() {
        binding.secondaryBtn.isVisible = false
    }

    open fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

}