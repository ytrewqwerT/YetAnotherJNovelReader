package com.ytrewqwert.yetanotherjnovelreader.common.swipeablelist

import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.launch

abstract class SwipeableListViewModel(private val repository: Repository) : ViewModel() {
    protected val mRefreshing = MutableLiveData(false)
    val refreshing: LiveData<Boolean> = mRefreshing

    /**
     * Retrieves an image.
     *
     * @param[source] The URL where the image can be found.
     * @param[callback] A callback to provide the image back to the requester.
     */
    fun getImage(source: String, callback: (source: String, image: Drawable?) -> Unit) {
        viewModelScope.launch { callback(source, repository.getImage(source)) }
    }

    abstract fun refresh()
}