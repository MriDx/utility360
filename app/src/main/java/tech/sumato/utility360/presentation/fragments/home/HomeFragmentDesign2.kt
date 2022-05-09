package tech.sumato.utility360.presentation.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.data.utils.HomeFragmentActionData
import tech.sumato.utility360.databinding.ArticleItemCardBinding
import tech.sumato.utility360.databinding.HomeActionItemViewBinding
import tech.sumato.utility360.databinding.HomeFragmentDesign2Binding
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragmentDesign2 : Fragment() {

    private var binding_: HomeFragmentDesign2Binding? = null
    private val binding get() = binding_!!

    @Inject
    lateinit var homeFragmentActions: List<HomeFragmentActionData>


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
                }?.executePendingBindings()
            }

            layoutManager = GridLayoutManager(requireContext(), 2)

        }.render()


        binding.articleHolder.apply {

            setItemCount(5)

            itemBuilder = { parent, index ->
                DataBindingUtil.inflate<ArticleItemCardBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.article_item_card,
                    parent,
                    false
                ).root
            }

            itemBinding { holder, index ->
                DataBindingUtil.bind<ArticleItemCardBinding>(holder.itemView)?.apply {

                }
            }

            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)

        }.render()


    }

    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }

}