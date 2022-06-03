package tech.sumato.utility360.presentation.activity.home

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.sumato.utility360.R
import tech.sumato.utility360.data.local.entity.user.UserEntity
import tech.sumato.utility360.presentation.activity.base.container_bottm_navigation_activity.ContainerBottomNavigationActivity
import tech.sumato.utility360.presentation.activity.login.LoginActivity
import tech.sumato.utility360.presentation.fragments.home.HomeFragmentDesign2
import tech.sumato.utility360.presentation.fragments.profile.ProfileFragment
import tech.sumato.utility360.presentation.fragments.notification.NotificationsFragment
import tech.sumato.utility360.presentation.fragments.tasks.TasksFragment
import tech.sumato.utility360.utils.startActivity

@AndroidEntryPoint
class HomeActivity : ContainerBottomNavigationActivity() {

    private val homePagesAdapter by lazy {
        HomePagesAdapter(
            supportFragmentManager = supportFragmentManager,
            lifecycle = lifecycle
        )
    }


    private val viewModel by viewModels<HomeActivityViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.logoutResponse.collectLatest { loggedOut ->
                        handleLogoutResponse(loggedOut)
                    }
                }
            }
        }

    }

    private fun handleLogoutResponse(loggedOut: Boolean) {
        if (loggedOut) {
            startActivity(LoginActivity::class.java)
            finish()
            return
        }
        showSnackbar("Could not log out now. Please try after sometime !")
    }



    override fun getContentAdapter(): FragmentStateAdapter {
        return homePagesAdapter.apply {
            //addFragment(fragment = HomeFragment())
            addFragment(fragment = HomeFragmentDesign2())
            addFragment(fragment = TasksFragment())
            addFragment(fragment = NotificationsFragment())
            addFragment(fragment = ProfileFragment())
        }
    }


    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
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
        }
        /*when (menuItem.itemId) {
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
        }*/
        return true
    }

    override fun inflateBottomMenu(): Int {
        return R.menu.bottom_navigation_menu
    }


}