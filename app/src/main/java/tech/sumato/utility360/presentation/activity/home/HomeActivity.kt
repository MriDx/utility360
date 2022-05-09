package tech.sumato.utility360.presentation.activity.home

import android.os.Bundle
import android.view.MenuItem
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.presentation.activity.base.container_bottm_navigation_activity.ContainerBottomNavigationActivity
import tech.sumato.utility360.presentation.activity.base.fragment_holder.FragmentHolderActivity
import tech.sumato.utility360.presentation.fragments.home.HomeFragment
import tech.sumato.utility360.presentation.fragments.home.HomeFragmentDesign2
import tech.sumato.utility360.presentation.fragments.profile.ProfileFragment
import tech.sumato.utility360.presentation.fragments.tasks.NotificationsFragment
import tech.sumato.utility360.presentation.fragments.tasks.TasksFragment

@AndroidEntryPoint
class HomeActivity : ContainerBottomNavigationActivity() {

    private val homePagesAdapter by lazy {
        HomePagesAdapter(
            supportFragmentManager = supportFragmentManager,
            lifecycle = lifecycle
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


    override fun getContentAdapter(): FragmentStateAdapter {
        return homePagesAdapter.apply {
            addFragment(fragment = HomeFragment())
            addFragment(fragment = HomeFragmentDesign2())
            addFragment(fragment = TasksFragment())
            addFragment(fragment = NotificationsFragment())
            addFragment(fragment = ProfileFragment())
        }
    }


    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        /*when (menuItem.itemId) {
            R.id.bottomNavTasks -> {
                changePage(1)
            }
            R.id.bottomNavNotifications -> {
                changePage(2)
            }
            R.id.bottomNavProfile -> {
                changePage(3)
            }
            else -> {
                changePage(0)
            }
        }*/
        when (menuItem.itemId) {
            R.id.bottomNavHome2 -> {
                changePage(1)
            }
            R.id.bottomNavTasks -> {
                changePage(2)
            }
            R.id.bottomNavNotifications -> {
                changePage(3)
            }
            R.id.bottomNavProfile -> {
                changePage(4)
            }
            else -> {
                changePage(0)
            }
        }
        return true
    }

    override fun inflateBottomMenu(): Int {
        return R.menu.bottom_navigation_menu
    }


}