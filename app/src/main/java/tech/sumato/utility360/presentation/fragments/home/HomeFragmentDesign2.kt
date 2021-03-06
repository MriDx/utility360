package tech.sumato.utility360.presentation.fragments.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sumato.etrack_agri.ui.utils.PlaceHolderDrawableHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.sumato.utility360.R
import tech.sumato.utility360.data.local.entity.user.UserEntity
import tech.sumato.utility360.data.local.model.instructions.InstructionItemsModel
import tech.sumato.utility360.data.utils.HomeFragmentActionData
import tech.sumato.utility360.databinding.ArticleItemCardBinding
import tech.sumato.utility360.databinding.HomeActionItemViewBinding
import tech.sumato.utility360.databinding.HomeFragmentDesign2Binding
import tech.sumato.utility360.presentation.activity.customer.verification.CustomerVerificationActivity
import tech.sumato.utility360.presentation.activity.home.HomeActivityViewModel
import tech.sumato.utility360.presentation.activity.instructions.InstructionActivity
import tech.sumato.utility360.presentation.activity.meter.installation.MeterInstallationActivity
import tech.sumato.utility360.presentation.activity.meter.reading.MeterReadingActivity
import tech.sumato.utility360.presentation.fragments.meter.reading.instruction.MeterReadingInstructionFragment
import tech.sumato.utility360.utils.startActivity
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragmentDesign2 : Fragment() {

    private var binding_: HomeFragmentDesign2Binding? = null
    private val binding get() = binding_!!

    @Inject
    lateinit var homeFragmentActions: List<HomeFragmentActionData>

    private val viewModel by activityViewModels<HomeActivityViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = DataBindingUtil.inflate<HomeFragmentDesign2Binding?>(
            inflater,
            R.layout.home_fragment_design_2,
            container,
            false
        ).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.userData.collectLatest { userData ->
                        populateUserData(userData)
                    }
                }
                launch {
                    viewModel.instructions.collectLatest { instructions ->
                        populateArticles(instructions)
                    }
                }
            }
        }


        binding.homeActionHolder.apply {
            setItemCount(homeFragmentActions.size)

            itemBuilder = { parent, index ->
                DataBindingUtil.inflate<HomeActionItemViewBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.home_action_item_view,
                    parent,
                    false
                ).root
            }

            itemBinding { holder, index ->
                val actionData = homeFragmentActions[index]
                DataBindingUtil.bind<HomeActionItemViewBinding>(holder.itemView)?.apply {
                    data = actionData
                    onClick = { data ->
                        handleHomeActionClick(actionData = data as? HomeFragmentActionData)
                    }
                }?.executePendingBindings()
            }

            layoutManager = GridLayoutManager(requireContext(), 2)

        }.render()



    }

    private fun populateArticles(instructions: List<InstructionItemsModel>) {
        binding.articleHolder.apply {
            setItemCount(instructions.size)
            itemBuilder = { parent, index ->
                DataBindingUtil.inflate<ArticleItemCardBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.article_item_card,
                    parent,
                    false
                ).root
            }

            itemBinding { holder, index ->
                val instruction = instructions[index]
                DataBindingUtil.bind<ArticleItemCardBinding>(holder.itemView)?.apply {
                    titleView.text = instruction.instructionModel.title
                    root.setOnClickListener {
                        startActivity(
                            Intent(
                                requireContext(),
                                InstructionActivity::class.java
                            ).apply {
                                putExtras(
                                    bundleOf(
                                        "type" to instruction.instructionModel.type
                                    )
                                )
                            })
                    }
                }
            }

            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        }.render()
    }

    private fun populateUserData(userData: UserEntity) {
        binding.apply {
            userNameView.text = userData.name
            designationView.text = userData.role

            Glide.with(requireContext())
                .asBitmap()
                .load(userData.photo)
                .placeholder(
                    PlaceHolderDrawableHelper.getAvatar(
                        requireContext(),
                        userData.name,
                        0
                    )
                )
                .into(avatar)

        }
    }

    private fun handleHomeActionClick(actionData: HomeFragmentActionData?) {
        actionData ?: return
        when (actionData.actionIdentifier) {
            "meter_reading" -> {
                startActivity(MeterReadingActivity::class.java)
            }
            "meter_installation" -> {
                startActivity(MeterInstallationActivity::class.java)
            }
            "site_verification" -> {
                startActivity(CustomerVerificationActivity::class.java)
            }
        }
    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }

}