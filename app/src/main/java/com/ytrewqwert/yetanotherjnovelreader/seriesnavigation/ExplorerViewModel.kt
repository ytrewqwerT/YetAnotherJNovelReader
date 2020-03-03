package com.ytrewqwert.yetanotherjnovelreader.seriesnavigation

import androidx.lifecycle.ViewModel
import com.ytrewqwert.yetanotherjnovelreader.data.Part
import com.ytrewqwert.yetanotherjnovelreader.data.Repository
import com.ytrewqwert.yetanotherjnovelreader.data.Series
import com.ytrewqwert.yetanotherjnovelreader.data.Volume

class ExplorerViewModel(private val repository: Repository) : ViewModel() {

    private var curSeries: Series? = null
    private var curVolume: Volume? = null

    suspend fun getSeries() = repository.getSeries()

    suspend fun getSerieVolumes(series: Series): List<Volume> {
        curSeries = series
        return getSerieVolumes()
    }
    suspend fun getSerieVolumes(): List<Volume> {
        val series = curSeries ?: return emptyList()
        return repository.getSerieVolumes(series.id)
    }

    suspend fun getVolumeParts(volume: Volume): List<Part> {
        curVolume = volume
        return getVolumeParts()
    }
    suspend fun getVolumeParts(): List<Part> {
        val volume = curVolume ?: return emptyList()
        return  repository.getVolumeParts(volume)
    }
}