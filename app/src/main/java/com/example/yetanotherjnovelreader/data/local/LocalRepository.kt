package com.example.yetanotherjnovelreader.data.local

import android.content.SharedPreferences
import com.example.yetanotherjnovelreader.data.Part
import com.example.yetanotherjnovelreader.data.Series
import com.example.yetanotherjnovelreader.data.Volume
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
        set(value) { setSharedPrefString(USER_ID_KEY, value) }
    var authToken: String?
        get() = sharedPreferences.getString(AUTH_TOKEN_KEY, null)
        set(value) { setSharedPrefString(AUTH_TOKEN_KEY, value) }
    var authDate: String?
        get() = sharedPreferences.getString(AUTH_DATE_KEY, null)
        set(value) { setSharedPrefString(AUTH_DATE_KEY, value) }
    var username: String?
        get() = sharedPreferences.getString(USERNAME_KEY, null)
        set(value) { setSharedPrefString(USERNAME_KEY, value) }

    private val _series = ArrayList<Series>()
    private val _volumes = ArrayList<Volume>()
    private val _parts = ArrayList<Part>()

    var partsProgress: PartsProgress? = null

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
        if (!containsPart(partData.getString("id"))) _parts.add(
            Part(
                partData
            )
        )
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

    private fun setSharedPrefString(key: String, value: String?) {
        with (sharedPreferences.edit()) {
            putString(key, value)
            commit()
        }
    }
}