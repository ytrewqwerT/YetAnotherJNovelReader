package com.ytrewqwert.yetanotherjnovelreader.seriesnavigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.JobHolder
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.PartWithProgress
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.Serie
import com.ytrewqwert.yetanotherjnovelreader.data.local.database.Volume
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ExplorerViewModel(private val repository: Repository) : ViewModel() {

    var curSerie: Serie? = null
    var curVolume: Volume? = null

    private val seriesCollectorJob: JobHolder = JobHolder()
    private val volumesCollectorJob: JobHolder = JobHolder()
    private val partsCollectorJob: JobHolder = JobHolder()

    private val _seriesList = MutableLiveData<List<Serie>>()
    private val _volumesList = MutableLiveData<List<Volume>>()
    private val _partsList = MutableLiveData<List<PartWithProgress>>()
    val seriesList: LiveData<List<Serie>> = _seriesList
    val volumesList: LiveData<List<Volume>> = _volumesList
    val partsList: LiveData<List<PartWithProgress>> = _partsList

    fun fetchSeries(onComplete: (success: Boolean) -> Unit = {}) {
        seriesCollectorJob.job = viewModelScope.launch {
            repository.getSeries(this, onComplete).collect {
                _seriesList.value = it
            }
        }
    }
    fun fetchSerieVolumes(onComplete: (success: Boolean) -> Unit = {}) {
        val series = curSerie ?: return
        volumesCollectorJob.job = viewModelScope.launch {
            repository.getSerieVolumes(this, series.id, onComplete).collect {
                _volumesList.value = it
            }
        }
    }
    fun fetchVolumeParts(onComplete: (success: Boolean) -> Unit = {}) {
        val volume = curVolume ?: return
        partsCollectorJob.job = viewModelScope.launch {
            repository.getVolumeParts(this, volume.id, onComplete).collect {
                _partsList.value = it
            }
        }
    }
}