package tech.sumato.utility360.presentation.activity.splash

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.sumato.utility360.R
import tech.sumato.utility360.databinding.SplashActivityBinding
import tech.sumato.utility360.presentation.activity.base.BaseActivity
import tech.sumato.utility360.presentation.activity.login.LoginActivity
import tech.sumato.utility360.utils.SPLASH_DURATION
import tech.sumato.utility360.utils.startActivity


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {


    private lateinit var binding: SplashActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.splash_activity)


        lifecycleScope.launch {
            delay(SPLASH_DURATION)
            withContext(Dispatchers.Main) {
                startActivity(LoginActivity::class.java)
                finish()
            }
        }


    }


}