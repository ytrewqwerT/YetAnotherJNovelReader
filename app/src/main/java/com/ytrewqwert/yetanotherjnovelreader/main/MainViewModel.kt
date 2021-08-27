package com.ytrewqwert.yetanotherjnovelreader.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.SingleLiveEvent
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import kotlinx.coroutines.launch

/** Exposes data for the [MainActivity]. */
class MainViewModel(private val repository: Repository) : ViewModel() {
    /** Notifies an observer of the result of a call to [logout]. */
    val logoutEvent = SingleLiveEvent<Boolean>()

    val followFailureEvent = SingleLiveEvent<FollowResult>()
    enum class FollowResult { FOLLOW_FAILURE, UNFOLLOW_FAILURE }

    val changePageEvent = SingleLiveEvent<PageContent>()
    sealed class PageContent {
        class SeriePage(val serieId: String) : PageContent()
        class VolumePage(val volumeId: String) : PageContent()
    }

    /** Identifies whether lists should be filtered to only show stuff from followed series. */
    val isFilterFollowing =
        repository.isFilterFollowing.asLiveData(viewModelScope.coroutineContext)

    /** The object for which context menu selections should apply to */
    var contextMenuTarget: Any? = null

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

    fun processContextMenuSelection(itemId: Int): Boolean {
        val target = contextMenuTarget ?: return false
        return when (target) {
            is PartFull -> processPartContextSelection(itemId, target)
            is VolumeFull -> processVolumeContextSelection(itemId, target)
            else -> false
        }
    }

    private fun processPartContextSelection(itemId: Int, part: PartFull): Boolean {
        return when (itemId) {
            CONTEXT_TO_SERIES -> {
                changePageEvent.value = PageContent.SeriePage(part.part.serieId)
                true
            }
            CONTEXT_TO_VOLUME -> {
                changePageEvent.value = PageContent.VolumePage(part.part.volumeId)
                true
            }
            CONTEXT_TOGGLE_READ -> {
                viewModelScope.launch { togglePartProgress(part) }
                true
            }
            else -> false
        }
    }

    private fun processVolumeContextSelection(itemId: Int, volume: VolumeFull): Boolean {
        return when (itemId) {
            CONTEXT_TO_SERIES -> {
                changePageEvent.value = PageContent.SeriePage(volume.volume.serieId)
                true
            }
            else -> false
        }
    }

    private suspend fun togglePartProgress(part: PartFull) {
        val newProgress = if (part.progress?.progress == 1.0) 0.0 else 1.0
        repository.setPartProgress(part.part.id, newProgress)
    }

    companion object {
        const val CONTEXT_TO_SERIES = 0
        const val CONTEXT_TO_VOLUME = 1
        const val CONTEXT_TOGGLE_READ = 2
    }
}