package tech.sumato.utility360.presentation.fragments.settings.account.password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.databinding.ChangePasswordFragmentBinding

@AndroidEntryPoint
class ChangePasswordFragment : Fragment() {

    private var binding_ : ChangePasswordFragmentBinding? = null
    private val binding get() = binding_!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = ChangePasswordFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }

}