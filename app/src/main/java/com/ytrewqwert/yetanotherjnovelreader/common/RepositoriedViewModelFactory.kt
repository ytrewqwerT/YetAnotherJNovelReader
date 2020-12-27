package com.ytrewqwert.yetanotherjnovelreader.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ytrewqwert.yetanotherjnovelreader.data.Repository

class RepositoriedViewModelFactory(private val repository: Repository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        try {
            val constructor = modelClass.getConstructor(Repository::class.java)
            return constructor.newInstance(repository)
        } catch (e: NoSuchMethodException) {
            throw IllegalArgumentException(
                "Class ${modelClass.canonicalName} has no constructor of type constructor(Repository)."
            )
        }
    }
}