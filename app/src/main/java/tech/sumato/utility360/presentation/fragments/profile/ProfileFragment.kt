package tech.sumato.utility360.presentation.fragments.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.sumato.etrack_agri.ui.utils.PlaceHolderDrawableHelper
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.data.utils.ProfileActionData
import tech.sumato.utility360.databinding.ProfileActionItemViewBinding
import tech.sumato.utility360.databinding.ProfileFragmentBinding
import tech.sumato.utility360.databinding.ProfileInfoItemViewBinding
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {


    private var binding_: ProfileFragmentBinding? = null
    private val binding get() = binding_!!


    @Inject
    lateinit var profileActions: List<ProfileActionData>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding_ = DataBindingUtil.inflate<ProfileFragmentBinding?>(
            inflater,
            R.layout.profile_fragment,
            container,
            false
        ).apply {
            setLifecycleOwner { viewLifecycleOwner.lifecycle }
        }
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.apply {

            heading.text = "Profile"


            userNameView.text = "Tony Stark"
            userInfoView.text = "Inventor"
            Glide.with(this@ProfileFragment)
                .asBitmap()
                .load("https://avatarfiles.alphacoders.com/839/83919.jpg")
                .placeholder(PlaceHolderDrawableHelper.getAvatar(requireContext(), "Tony Stark", 0))
                .into(avatarView)


            val profileInfo = mapOf(
                "Email" to "owner@starkindustries.com",
                "Phone" to "7597365975",
                "Joined on" to "Jan 1 1989"
            )

            profileInfo.forEach { item ->
                val itemView = DataBindingUtil.inflate<ProfileInfoItemViewBinding>(
                    LayoutInflater.from(requireContext()),
                    R.layout.profile_info_item_view,
                    profileInfoView,
                    false
                ).apply {
                    headingView.text = item.key
                    valueView.text = item.value
                }.root
                profileInfoView.addView(itemView)
            }
        }


        binding.accountItemHolder.apply {

            setItemCount(profileActions.size)
            itemBuilder = { parent, index ->
                DataBindingUtil.inflate<ProfileActionItemViewBinding>(
                    LayoutInflater.from(parent.context),
                    R.layout.profile_action_item_view,
                    parent,
                    false
                ).root
            }
            itemBinding { holder, index ->
                val actionData = profileActions[index]
                DataBindingUtil.bind<ProfileActionItemViewBinding>(holder.itemView)!!.apply {
                    data = actionData
                }.executePendingBindings()
            }

        }.render()


    }


    override fun onDestroyView() {
        binding_ = null
        super.onDestroyView()
    }


}