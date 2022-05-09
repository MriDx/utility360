package tech.sumato.utility360.presentation.fragments.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.databinding.ArticleItemCardBinding
import tech.sumato.utility360.databinding.HomeFragmentBinding

@AndroidEntryPoint
class HomeFragment : Fragment() {


    private var binding_: HomeFragmentBinding? = null
    private val binding get() = binding_!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = DataBindingUtil.inflate<HomeFragmentBinding?>(
            inflater,
            R.layout.home_fragment,
            container,
            false
        ).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


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