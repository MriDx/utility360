package tech.sumato.utility360.presentation.activity.login

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableStringBuilder
import androidx.core.os.bundleOf
import androidx.core.text.bold
import androidx.core.text.buildSpannedString
import androidx.core.text.color
import androidx.databinding.DataBindingUtil
import dagger.hilt.android.AndroidEntryPoint
import tech.sumato.utility360.R
import tech.sumato.utility360.databinding.LoginActivityBinding
import tech.sumato.utility360.presentation.activity.base.BaseActivity
import tech.sumato.utility360.presentation.activity.home.HomeActivity
import tech.sumato.utility360.utils.startActivity

@AndroidEntryPoint
class LoginActivity : BaseActivity() {


    private lateinit var binding: LoginActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            DataBindingUtil.setContentView<LoginActivityBinding>(this, R.layout.login_activity)
                .apply {
                    setLifecycleOwner { lifecycle }
                }

        binding.welcomeText.text = buildSpannedString {
            append("Welcome \n")
            bold { append(getString(R.string.app_name)) }
        }


        binding.privacyPolicyLink.text = buildSpannedString {
            append("Learn more about our ")
            color(Color.BLUE) { bold { append("privacy policy") } }
        }

        binding.loginBtn.setOnClickListener {
            startActivity(HomeActivity::class.java)
        }

    }

}