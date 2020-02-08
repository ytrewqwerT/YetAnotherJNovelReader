package com.example.yetanotherjnovelreader.data

import org.json.JSONArray
import org.json.JSONObject

class LocalRepository private constructor() {
    companion object {
        private var INSTANCE: LocalRepository? = null
        fun getInstance(): LocalRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: LocalRepository().also {
                    INSTANCE = it
                }
            }
    }

    private val _series = ArrayList<Series>()
    private val _volumes = ArrayList<Volume>()
    private val _parts = ArrayList<Part>()
    val series: List<Series> get() = _series
    val volumes: List<Volume> get() = _volumes
    val parts: List<Part> get() = _parts

    fun addSeriesInfo(seriesData: JSONArray) {
        for (i in 0 until seriesData.length()) addSerieInfo(seriesData.getJSONObject(i))
    }
    fun addVolumesInfo(volumesData: JSONArray) {
        for (i in 0 until volumesData.length()) addVolumeInfo(volumesData.getJSONObject(i))
    }
    fun addPartsInfo(partsData: JSONArray) {
        for (i in 0 until partsData.length()) addPartInfo(partsData.getJSONObject(i))
    }

    fun addSerieInfo(serieData: JSONObject) {
        if (!containsSerie(serieData.getString("id"))) _series.add(Series(serieData))
        if (serieData.has("volumes")) addVolumesInfo(serieData.getJSONArray("volumes"))
        if (serieData.has("parts")) addPartsInfo(serieData.getJSONArray("parts"))
    }
    fun addVolumeInfo(volumeData: JSONObject) {
        if (!containsVolume(volumeData.getString("id"))) _volumes.add(Volume(volumeData))
    }
    fun addPartInfo(partData: JSONObject) {
        if (!containsPart(partData.getString("id"))) _parts.add(Part(partData))
    }

    private fun containsSerie(id: String): Boolean {
        for (serie in series) if (serie.id == id) return true
        return false
    }
    private fun containsVolume(id: String): Boolean {
        for (volume in volumes) if (volume.id == id) return true
        return false
    }
    private fun containsPart(id: String): Boolean {
        for (part in parts) if (part.id == id) return true
        return false
    }

}