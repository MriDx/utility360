package tech.sumato.utility360.presentation.activity.splash

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.ByteString.Companion.encode
import org.json.JSONObject
import tech.sumato.utility360.R
import tech.sumato.utility360.data.remote.model.user.ChangePasswordRequest
import tech.sumato.utility360.data.remote.model.user.SettingsRequest
import tech.sumato.utility360.databinding.SplashActivityBinding
import tech.sumato.utility360.presentation.activity.base.BaseActivity
import tech.sumato.utility360.presentation.activity.login.LoginActivity
import tech.sumato.utility360.utils.SPLASH_DURATION
import tech.sumato.utility360.utils.startActivity


@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : BaseActivity() {


    private lateinit var binding: SplashActivityBinding

    private val viewModel by viewModels<SplashActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.splash_activity)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.preLaunchInfo.collectLatest { completed ->
                        if (completed) {
                            //
                            continueSplashWork()
                        }
                    }
                }
            }
        }

        /*lifecycleScope.launch(Dispatchers.Default) {
            delay(SPLASH_DURATION)
            withContext(Dispatchers.Main) {
                startActivity(LoginActivity::class.java)
                finish()
            }
        }*/


    }

    private fun continueSplashWork() {
        lifecycleScope.launch {
            delay(SPLASH_DURATION)
            withContext(Dispatchers.Main) {
                startActivity(LoginActivity::class.java)
                finish()
            }
        }
    }


}
