package tech.sumato.utility360.presentation.utils

import android.os.Bundle
import javax.inject.Scope

abstract interface Navigation {


    fun navigate(fragment: Class<*>) {}

    fun navigate(fragment: Class<*>, args: Bundle) {}

}