package com.cyuan.bimibimi.core.extension

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

fun ViewModel.launch(block: suspend () -> Unit, error: suspend (Throwable) -> Unit) = viewModelScope.launch {
    try {
        block()
    } catch (e: Throwable) {
        error(e)
    }
}