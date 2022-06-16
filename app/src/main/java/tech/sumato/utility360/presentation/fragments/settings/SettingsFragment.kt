package tech.sumato.utility360.presentation.fragments.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.data.utils.SettingsItemData
import tech.sumato.utility360.databinding.ProfileActionItemViewBinding
import tech.sumato.utility360.databinding.SettingsFragmentBinding
import tech.sumato.utility360.databinding.SettingsItemViewBinding
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment() {


    private var binding_: SettingsFragmentBinding? = null
    private val binding get() = binding_!!


    @Inject
    lateinit var settingsItems: List<SettingsItemData>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = SettingsFragmentBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.itemsHolder.apply {
            setItemCount(settingsItems.size)
            itemBuilder = { parent, index ->
                DataBindingUtil.inflate<SettingsItemViewBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.settings_item_view,
                    parent,
                    false
                ).root
            }
            itemBinding { holder, index ->
                val settingsData = settingsItems[index]
                DataBindingUtil.bind<SettingsItemViewBinding>(holder.itemView)?.apply {
                    data = settingsData
                }?.executePendingBindings()
            }
        }.render()


    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }


}