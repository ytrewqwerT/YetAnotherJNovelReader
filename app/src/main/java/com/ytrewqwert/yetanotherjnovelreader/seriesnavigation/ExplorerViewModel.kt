package com.ytrewqwert.yetanotherjnovelreader.seriesnavigation

import androidx.lifecycle.ViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.Part
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.Series
import com.ytrewqwert.yetanotherjnovelreader.data.Volume

class ExplorerViewModel(private val repository: Repository) : ViewModel() {

    private var curSeries: Series? = null
    private var curVolume: Volume? = null

    fun getSeries(callback: (List<Series>) -> Unit) {
        repository.getSeries(callback)
    }

    fun getSerieVolumes(series: Series, callback: (List<Volume>) -> Unit) {
        curSeries = series
        getSerieVolumes(callback)
    }
    fun getSerieVolumes(callback: (List<Volume>) -> Unit) {
        val series = curSeries
        if (series != null) repository.getSerieVolumes(series, callback)
        else callback(emptyList())
    }

    fun getVolumeParts(volume: Volume, callback: (List<Part>) -> Unit) {
        curVolume = volume
        getVolumeParts(callback)
    }
    fun getVolumeParts(callback: (List<Part>) -> Unit) {
        val volume = curVolume
        if (volume != null) repository.getVolumeParts(volume, callback)
        else callback(emptyList())
    }
}