package com.ytrewqwert.yetanotherjnovelreader.seriesnavigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.JobHolder
import com.ytrewqwert.yetanotherjnovelreader.common.ListItemViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.part.PartFull
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.serie.Serie
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.Volume
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.volume.VolumeFull
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlinx.coroutines.suspendCancellableCoroutine

class ExplorerViewModel(private val repository: Repository) : ViewModel() {

    var curSerie: Serie? = null
    var curVolume: Volume? = null

    private val volumesCollectorJob: JobHolder = JobHolder()
    private val partsCollectorJob: JobHolder = JobHolder()

    private val _volumesList = MutableLiveData<List<VolumeFull>>()
    private val _partsList = MutableLiveData<List<PartFull>>()
    val volumesList: LiveData<List<VolumeFull>> = _volumesList
    val partsList: LiveData<List<PartFull>> = _partsList

    fun getSeriesSource(): ListItemViewModel.ListItemSource {
        return ListItemViewModel.ListItemSource(repository.getSeries(viewModelScope)) { scope, amount, offset ->
            suspendCancellableCoroutine { cont ->
                repository.getSeries(scope, amount, offset) { cont.resume(it) }
            }
        }
    }
    fun fetchSerieVolumes(onComplete: (success: Boolean) -> Unit = {}) {
        val serie = curSerie
        if (serie == null) {
            onComplete(true)
            return
        }
        volumesCollectorJob.job = viewModelScope.launch {
            repository.getSerieVolumes(this, serie.id, onComplete).collect {
                _volumesList.value = it
            }
        }
    }
    fun fetchVolumeParts(onComplete: (success: Boolean) -> Unit = {}) {
        val volume = curVolume
        if (volume == null) {
            onComplete(true)
            return
        }
        partsCollectorJob.job = viewModelScope.launch {
            repository.getVolumeParts(this, volume.id, onComplete).collect {
                _partsList.value = it
            }
        }
    }
}