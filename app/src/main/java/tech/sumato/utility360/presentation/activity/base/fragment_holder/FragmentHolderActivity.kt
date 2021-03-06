package tech.sumato.utility360.presentation.activity.base.fragment_holder

import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.IdRes
import androidx.annotation.MenuRes
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.databinding.FragmentHolderActivityBinding
import tech.sumato.utility360.presentation.activity.base.BaseActivity

@AndroidEntryPoint
open class FragmentHolderActivity : BaseActivity() {

    private lateinit var binding: FragmentHolderActivityBinding

    var onFragmentChanged: ((currentFragment: Fragment?) -> Unit)? = null
        set(value) = run {
            field = value
        }

    var currentVisibleFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView<FragmentHolderActivityBinding?>(
            this,
            R.layout.fragment_holder_activity
        ).apply {
            setLifecycleOwner { lifecycle }
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val currentFragment = supportFragmentManager.findFragmentById(binding.fragmentHolder.id)
            currentVisibleFragment = currentFragment
            onFragmentChanged?.invoke(currentFragment)
            currentFragment?.let { onFragmentChanged(fragment = it) }
        }

        /*binding.bottomNavigation.inflateMenu(inflateBottomMenu())

        binding.bottomNavigation.setOnItemSelectedListener { onNavigationItemSelected(it) }*/
    }


    open fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        return false
    }


    /**
     * onFragmentChanged is callback function to be called when fragment changes in the fragment transaction
     * @param fragment Current fragment in the container
     */
    open fun onFragmentChanged(fragment: Fragment) {

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

    fun addFragment(fragment: Fragment, addToBackStack: Boolean, replace: Boolean) {
        supportFragmentManager.commit {
            if (replace)
                replace(binding.fragmentHolder.id, fragment, fragment.javaClass.simpleName)
            else
                add(binding.fragmentHolder.id, fragment, fragment.javaClass.simpleName)
            if (addToBackStack)
                addToBackStack(fragment.javaClass.simpleName)
        }
    }


    fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            binding.root
        ).show(WindowInsetsCompat.Type.systemBars())
    }


}