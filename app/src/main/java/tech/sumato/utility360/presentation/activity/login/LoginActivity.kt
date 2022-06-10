package tech.sumato.utility360.presentation.activity.login

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.util.Log
import androidx.activity.viewModels
import androidx.core.os.bundleOf
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import tech.sumato.utility360.R
import tech.sumato.utility360.data.remote.model.user.LoginResponse
import tech.sumato.utility360.data.remote.model.user.UserResponse
import tech.sumato.utility360.data.remote.utils.Resource
import tech.sumato.utility360.databinding.LoginActivityBinding
import tech.sumato.utility360.presentation.activity.base.BaseActivity
import tech.sumato.utility360.presentation.activity.home.HomeActivity
import tech.sumato.utility360.utils.startActivity
import tech.sumato.utility360.utils.toMeterReadingDate
import java.util.*

@AndroidEntryPoint
class LoginActivity : BaseActivity() {


    private lateinit var binding: LoginActivityBinding

    private val viewModel by viewModels<LoginActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView<LoginActivityBinding>(this, R.layout.login_activity)
                .apply {
                    setLifecycleOwner { lifecycle }
                    viewModel = this@LoginActivity.viewModel
                }

        binding.welcomeText.text = buildSpannedString {
            append(getString(R.string.welcome))
            append("\n")
            bold { append(getString(R.string.app_name)) }
        }


        binding.privacyPolicyLink.text = buildSpannedString {
            append(getString(R.string.learnMoreAbout))
            append(" ")
            color(Color.BLUE) { bold { append(getString(R.string.privacyPolicy)) } }
        }

        val d = Date().toMeterReadingDate()

        Log.d("mridx", "onCreate: $d")


        lifecycleScope.launch {
            launch {
                viewModel.loginResponse.collectLatest { loginResponse ->
                    if (loginResponse.isFailed()) {
                        //show failed
                        showSnackbar(message = loginResponse.message ?: "Something went wrong !")
                        return@collectLatest
                    }
                    //success
                    handleLoginSuccess(loginResponse)
                }
            }
            launch {
                viewModel.isLoggedIn.collectLatest { loggedIn ->
                    if (loggedIn) navigateToHome()
                }
            }
        }


    }

    private fun navigateToHome() {
        startActivity(HomeActivity::class.java)
        finish()
    }

    private fun handleLoginSuccess(loginResponse: Resource<LoginResponse>) {
        startActivity(HomeActivity::class.java)
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

}