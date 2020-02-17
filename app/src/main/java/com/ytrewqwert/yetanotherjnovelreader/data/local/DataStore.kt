package com.ytrewqwert.yetanotherjnovelreader.data.local

import com.ytrewqwert.yetanotherjnovelreader.data.Part
import com.ytrewqwert.yetanotherjnovelreader.data.Series
import com.ytrewqwert.yetanotherjnovelreader.data.Volume
import org.json.JSONArray
import org.json.JSONObject

class DataStore {
    val series = ArrayList<Series>()
    val volumes = ArrayList<Volume>()
    val parts = ArrayList<Part>()

    fun mergeData(data: DataStore) {
        mergeSeries(data.series)
        mergeVolumes(data.volumes)
        mergeParts(data.parts)
    }

    private fun mergeSeries(seriesData: List<Series>) {
        for (serie in seriesData) {
            if (!series.contains(serie)) series.add(serie)
        }
    }
    private fun mergeVolumes(volumesData: List<Volume>) {
        for (volume in volumesData) {
            if (!volumes.contains(volume)) volumes.add(volume)
        }
    }
    private fun mergeParts(partsData: List<Part>) {
        for (part in partsData) {
            if (!parts.contains(part)) parts.add(part)
        }
    }

    companion object {
        fun fromJson(json: JSONObject): DataStore {
            val store = DataStore()
            // Identify as series, volume, or part, and act accordingly
            when {
                json.has("volumeId") -> {
                    store.parts.add(Part(json))
                }

                json.has("serieId") -> {
                    store.volumes.add(Volume(json))
                }

                else -> {
                    store.series.add(Series(json))
                    // Series may also contain volumes and parts
                    if (json.has("volumes")) {
                        val volumesJson = json.getJSONArray("volumes")
                        store.mergeData(fromJson(volumesJson))
                    }
                    if (json.has("parts")) {
                        val volumesJson = json.getJSONArray("parts")
                        store.mergeData(fromJson(volumesJson))
                    }
                }
            }
            return store
        }

        fun fromJson(json: JSONArray): DataStore {
            val store = DataStore()
            for (i in 0 until json.length()) {
                store.mergeData(fromJson(json.getJSONObject(i)))
            }
            return store
        }
    }
}
