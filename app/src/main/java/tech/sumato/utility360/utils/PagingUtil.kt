package tech.sumato.utility360.utils

import androidx.paging.LoadState

fun LoadState.isLoading() = this is LoadState.Loading

fun LoadState.isError() = this is LoadState.Error

