package tech.sumato.utility360.data.utils

import android.os.Bundle
import androidx.fragment.app.Fragment

data class FragmentNavigation(
    val fragment: Fragment,
    val args: Bundle? = null
)
