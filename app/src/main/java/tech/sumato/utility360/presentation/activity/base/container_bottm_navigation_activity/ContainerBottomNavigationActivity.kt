package tech.sumato.utility360.presentation.activity.base.container_bottm_navigation_activity

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.MenuRes
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.databinding.ContainerBottomNavigationActivityBinding
import tech.sumato.utility360.presentation.activity.base.BaseActivity

@AndroidEntryPoint
abstract class ContainerBottomNavigationActivity : BaseActivity() {

    private lateinit var binding: ContainerBottomNavigationActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<ContainerBottomNavigationActivityBinding?>(
            this,
            R.layout.container_bottom_navigation_activity
        ).apply {
            setLifecycleOwner { lifecycle }
        }

        binding.bottomNavigation.apply {
            inflateMenu(inflateBottomMenu())
            setOnItemSelectedListener { onNavigationItemSelected(it) }
        }

        binding.contentHolder.apply {
            adapter = getContentAdapter()
            isUserInputEnabled = false
            offscreenPageLimit = 5
        }


    }


    open fun changePage(index: Int) {
        //binding.contentHolder.currentItem = index
        binding.contentHolder.setCurrentItem(index, false)
    }

    abstract fun getContentAdapter(): FragmentStateAdapter

    @MenuRes
    abstract fun inflateBottomMenu(): Int


    open fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        return false
    }


    fun setActionBar(actionBarTitle: String) {
        supportActionBar?.apply {
            title = actionBarTitle
            setDisplayHomeAsUpEnabled(true)
        }
    }

    fun setActionBarTitle(actionBarTitle: String) {
        supportActionBar?.apply {
            title = actionBarTitle
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home)
            onBackPressed()
        return super.onOptionsItemSelected(item)
    }

}