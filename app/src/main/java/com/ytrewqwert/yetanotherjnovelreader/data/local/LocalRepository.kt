package com.ytrewqwert.yetanotherjnovelreader.data.local

import android.content.SharedPreferences
import com.ytrewqwert.yetanotherjnovelreader.data.Part
import com.ytrewqwert.yetanotherjnovelreader.data.Series
import com.ytrewqwert.yetanotherjnovelreader.data.Volume
import com.ytrewqwert.yetanotherjnovelreader.setString
import org.json.JSONArray
import org.json.JSONObject

class LocalRepository private constructor(private val sharedPreferences: SharedPreferences) {
    companion object {
        private const val USER_ID_KEY = "USER_ID"
        private const val AUTH_TOKEN_KEY = "AUTHENTICATION_TOKEN"
        private const val AUTH_DATE_KEY = "AUTHENTICATION_DATE"
        private const val USERNAME_KEY = "USERNAME"
        @Volatile
        private var INSTANCE: LocalRepository? = null
        fun getInstance(sharedPreferences: SharedPreferences): LocalRepository =
            INSTANCE
                ?: synchronized(this) {
                INSTANCE
                    ?: LocalRepository(
                        sharedPreferences
                    ).also {
                    INSTANCE = it
                }
            }
    }

    var userId: String?
        get() = sharedPreferences.getString(USER_ID_KEY, null)
        set(value) = sharedPreferences.setString(USER_ID_KEY, value)
    var authToken: String?
        get() = sharedPreferences.getString(AUTH_TOKEN_KEY, null)
        set(value) = sharedPreferences.setString(AUTH_TOKEN_KEY, value)
    var authDate: String?
        get() = sharedPreferences.getString(AUTH_DATE_KEY, null)
        set(value) = sharedPreferences.setString(AUTH_DATE_KEY, value)
    var username: String?
        get() = sharedPreferences.getString(USERNAME_KEY, null)
        set(value) = sharedPreferences.setString(USERNAME_KEY, value)

    private val _series = ArrayList<Series>()
    private val _volumes = ArrayList<Volume>()
    private val _parts = ArrayList<Part>()

    private var unknownPartsProgress: UnknownPartsProgress? = null

    fun getSeries(): List<Series> = _series
    fun getVolumes(serieId: String): List<Volume> {
        val result = ArrayList<Volume>()
        for (volume in _volumes) {
            if (volume.serieId == serieId) result.add(volume)
        }
        return result
    }
    fun getParts(volumeId: String): List<Part> {
        val result = ArrayList<Part>()
        for (part in _parts) {
            if (part.volumeId == volumeId) result.add(part)
        }
        return result
    }

    fun getPart(partId: String): Part? {
        for (part in _parts) {
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
        if (!containsSerie(serieData.getString("id"))) _series.add(
            Series(
                serieData
            )
        )
        if (serieData.has("volumes")) addVolumesInfo(serieData.getJSONArray("volumes"))
        if (serieData.has("parts")) addPartsInfo(serieData.getJSONArray("parts"))
    }
    fun addVolumeInfo(volumeData: JSONObject) {
        if (!containsVolume(volumeData.getString("id"))) _volumes.add(
            Volume(
                volumeData
            )
        )
    }
    fun addPartInfo(partData: JSONObject) {
        if (!containsPart(partData.getString("id"))) {
            val part = Part(partData)
            val partProgress = unknownPartsProgress?.getProgress(part.id)
            if (partProgress != null) part.progress = partProgress
            _parts.add(part)
        }
    }

    fun setPartsProgress(progress: UnknownPartsProgress) {
        unknownPartsProgress = progress
        for (part in _parts) {
            val partProgress = progress.getProgress(part.id)
            if (partProgress != null) part.progress = partProgress
        }
    }

    private fun containsSerie(id: String): Boolean {
        for (serie in _series) if (serie.id == id) return true
        return false
    }
    private fun containsVolume(id: String): Boolean {
        for (volume in _volumes) if (volume.id == id) return true
        return false
    }
    private fun containsPart(id: String): Boolean {
        for (part in _parts) if (part.id == id) return true
        return false
    }
}