package tech.sumato.utility360.presentation.activity.home

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import tech.sumato.utility360.presentation.fragments.home.HomeFragment

class HomePagesAdapter(
    private val supportFragmentManager: FragmentManager,
    private val lifecycle: Lifecycle
) : FragmentStateAdapter(supportFragmentManager, lifecycle) {

    private var pages_ = mutableListOf<Fragment>()
    private val pages get() = pages_


    fun addFragment(fragment: Fragment) {
        pages_.add(fragment)
    }

    override fun getItemCount(): Int {
        return pages.size
    }

    override fun createFragment(position: Int): Fragment {
        return pages[position]
    }

}