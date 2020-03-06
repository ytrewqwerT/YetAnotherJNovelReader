package com.ytrewqwert.yetanotherjnovelreader.seriesnavigation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ytrewqwert.yetanotherjnovelreader.data.Part
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.Series
import com.ytrewqwert.yetanotherjnovelreader.data.Volume
import kotlinx.coroutines.launch

class ExplorerViewModel(private val repository: Repository) : ViewModel() {

    private var curSeries: Series? = null
    private var curVolume: Volume? = null

    private val _seriesList = MutableLiveData<List<Series>>()
    private val _volumesList = MutableLiveData<List<Volume>>()
    private val _partsList = MutableLiveData<List<Part>>()
    val seriesList: LiveData<List<Series>> = _seriesList
    val volumesList: LiveData<List<Volume>> = _volumesList
    val partsList: LiveData<List<Part>> = _partsList

    fun fetchSeries() {
        viewModelScope.launch { _seriesList.value = repository.getSeries() }
    }

    fun fetchSerieVolumes(series: Series) {
        curSeries = series
        fetchSerieVolumes()
    }
    fun fetchSerieVolumes() {
        val series = curSeries ?: return
        viewModelScope.launch { _volumesList.value = repository.getSerieVolumes(series.id) }
    }

    fun fetchVolumeParts(volume: Volume) {
        curVolume = volume
        fetchVolumeParts()
    }
    fun fetchVolumeParts() {
        val volume = curVolume ?: return
        viewModelScope.launch { _partsList.value = repository.getVolumeParts(volume) }
    }
}