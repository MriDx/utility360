package tech.sumato.utility360.presentation.activity.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.presentation.activity.base.fragment_holder.FragmentHolderActivity
import tech.sumato.utility360.presentation.fragments.settings.SettingsFragment
import tech.sumato.utility360.presentation.fragments.settings.account.password.ChangePasswordFragment

@AndroidEntryPoint
class SettingsActivity : FragmentHolderActivity() {


    private lateinit var from: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        from = intent?.getStringExtra("from") ?: "change_password"


        addAppropriateFragment(from = from)

        addDefaultFragment()

    }

    private fun addAppropriateFragment(from: String) {
        val fragment = generateFragment(from = from)
    }

    private fun generateFragment(from: String): Fragment {
        return when (from) {
            "change_password" -> {
                setActionBarTitle("Change password")
                ChangePasswordFragment()
            }
            else -> {
                SettingsFragment()
            }
        }
    }


    private fun addDefaultFragment() {
        val fragment = ChangePasswordFragment()
        addFragment(
            fragment = fragment,
            addToBackStack = false,
            replace = true
        )
    }


}