package com.cyuan.bimibimi.ui.base

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.SkinAppCompatDelegateImpl
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB: ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: VB

    protected val isBindingInitialized
        get() = ::binding.isInitialized


    override fun getDelegate(): AppCompatDelegate {
        return SkinAppCompatDelegateImpl.get(this, this)
    }
}