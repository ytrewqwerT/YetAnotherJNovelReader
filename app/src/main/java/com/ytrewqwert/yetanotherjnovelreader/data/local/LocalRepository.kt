package com.ytrewqwert.yetanotherjnovelreader.data.local

import com.ytrewqwert.yetanotherjnovelreader.data.Part
import com.ytrewqwert.yetanotherjnovelreader.data.Series
import com.ytrewqwert.yetanotherjnovelreader.data.Volume
import org.json.JSONArray
import org.json.JSONObject

class LocalRepository private constructor() {
    companion object {
        @Volatile
        private var INSTANCE: LocalRepository? = null
        fun getInstance(): LocalRepository =
            INSTANCE
                ?: synchronized(this) {
                INSTANCE
                    ?: LocalRepository().also {
                    INSTANCE = it
                }
            }
    }

    private val dataStore = DataStore()

    private var unknownPartsProgress: UnknownPartsProgress? = null

    fun getSeries(): List<Series> = dataStore.series
    fun getVolumes(serieId: String): List<Volume> {
        val result = ArrayList<Volume>()
        for (volume in dataStore.volumes) {
            if (volume.serieId == serieId) result.add(volume)
        }
        return result
    }
    fun getParts(volumeId: String): List<Part> {
        val result = ArrayList<Part>()
        for (part in dataStore.parts) {
            if (part.volumeId == volumeId) result.add(part)
        }
        return result
    }

    fun getPart(partId: String): Part? {
        for (part in dataStore.parts) {
            if (part.id == partId) return part
        }
        return null
    }

    fun getParts(partIds: List<String>): List<Part> {
        val resultParts = ArrayList<Part>()
        for (partId in partIds) {
            val part = getPart(partId)
            if (part != null) resultParts.add(part)
        }
        return resultParts
    }

    fun addData(dataJson: JSONObject) { addData(DataStore.fromJson(dataJson)) }
    fun addData(dataJson: JSONArray) { addData(DataStore.fromJson(dataJson)) }
    fun addData(newData: DataStore) {
        for (part in newData.parts) {
            val partProgress = unknownPartsProgress?.getProgress(part.id)
            if (partProgress != null) part.progress = partProgress
        }
        dataStore.mergeData(newData)
    }

    fun setPartsProgress(progress: UnknownPartsProgress) {
        unknownPartsProgress = progress
        for (part in dataStore.parts) {
            val partProgress = progress.getProgress(part.id)
            if (partProgress != null) part.progress = partProgress
        }
    }
}