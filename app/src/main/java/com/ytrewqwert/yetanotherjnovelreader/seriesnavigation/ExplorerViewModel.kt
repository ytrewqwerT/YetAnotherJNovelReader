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

    private var curSeries: Serie? = null
    private var curVolume: Volume? = null

    private val seriesCollectorJob: JobHolder = JobHolder()
    private val volumesCollectorJob: JobHolder = JobHolder()
    private val partsCollectorJob: JobHolder = JobHolder()

    private val _seriesList = MutableLiveData<List<Serie>>()
    private val _volumesList = MutableLiveData<List<Volume>>()
    private val _partsList = MutableLiveData<List<PartWithProgress>>()
    val seriesList: LiveData<List<Serie>> = _seriesList
    val volumesList: LiveData<List<Volume>> = _volumesList
    val partsList: LiveData<List<PartWithProgress>> = _partsList

    fun fetchSeries() {
        seriesCollectorJob.job = viewModelScope.launch {
            repository.getSeries(this).collect { _seriesList.value = it }
        }
    }

    fun fetchSerieVolumes(series: Serie) {
        curSeries = series
        fetchSerieVolumes()
    }
    fun fetchSerieVolumes() {
        val series = curSeries ?: return
        volumesCollectorJob.job = viewModelScope.launch {
            repository.getSerieVolumes(this, series.id).collect { _volumesList.value = it }
        }
    }

    fun fetchVolumeParts(volume: Volume) {
        curVolume = volume
        fetchVolumeParts()
    }
    fun fetchVolumeParts() {
        val volume = curVolume ?: return
        partsCollectorJob.job = viewModelScope.launch {
            repository.getVolumeParts(this, volume.id).collect { _partsList.value = it }
        }
    }
}