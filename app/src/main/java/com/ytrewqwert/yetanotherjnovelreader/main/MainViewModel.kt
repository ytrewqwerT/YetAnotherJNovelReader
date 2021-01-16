package com.ytrewqwert.yetanotherjnovelreader.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import kotlinx.coroutines.launch

/** Exposes data for the [MainActivity]. */
class MainViewModel(private val repository: Repository) : ViewModel() {
    /** Notifies an observer of the result of a call to [logout]. */
    val logoutEvent = SingleLiveEvent<Boolean>()

    val followFailureEvent = SingleLiveEvent<FollowResult>()
    enum class FollowResult { FOLLOW_FAILURE, UNFOLLOW_FAILURE }

    /** Identifies whether lists should be filtered to only show stuff from followed series. */
    val isFilterFollowing =
        repository.isFilterFollowing.asLiveData(viewModelScope.coroutineContext)

    init {
        // Sync follow and progress data on startup
        viewModelScope.launch { repository.fetchPartsProgress() }
        viewModelScope.launch { repository.fetchFollowedSeries() }
    }

    fun logout() { viewModelScope.launch { logoutEvent.value = repository.logout() } }

    fun isLoggedIn() = repository.isLoggedIn()
    /** Returns the logged-in user's username, or null if not logged in. */
    fun getUsername() = repository.getUsername()

    /** Toggles whether lists should be filtered to only show stuff from followed series. */
    fun toggleFilterFollowing() {
        repository.setIsFilterFollowing(isFilterFollowing.value == false)
    }

    /**
     * Toggles the following status for the given series.
     * If the action fails, the value of [followFailureEvent] is set accordingly.
     */
    fun toggleFollow(serieId: String) {
        viewModelScope.launch {
            if (repository.isFollowed(serieId)) {
                if (!repository.unfollowSeries(serieId)) {
                    followFailureEvent.value = FollowResult.UNFOLLOW_FAILURE
                }
            } else {
                if (!repository.followSeries(serieId)) {
                    followFailureEvent.value = FollowResult.FOLLOW_FAILURE
                }
            }
        }
    }
}