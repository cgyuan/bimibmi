package com.cyuan.bimibimi.ui.download.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.cyuan.bimibimi.core.App
import com.cyuan.bimibimi.db.repository.RepositoryProvider

class DownloadViewModelFactory: ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val repository = RepositoryProvider.providerDownloadTaskRepository()
        return DownloadViewModel(App.getContext() as Application, repository) as T
    }
}