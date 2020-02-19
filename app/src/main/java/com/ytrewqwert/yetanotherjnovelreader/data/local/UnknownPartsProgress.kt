package com.ytrewqwert.yetanotherjnovelreader.data.local

import org.json.JSONArray

class UnknownPartsProgress(partsProgressJson: JSONArray? = null) {

    private val progressList = HashMap<String, Double>()

    init {
        if (partsProgressJson != null) {
            for (i in 0 until partsProgressJson.length()) {
                val part = partsProgressJson.getJSONObject(i)
                val partId = part.getString("partId")
                val completion = part.getDouble("completion")
                progressList[partId] = completion
            }
        }
    }

    fun getProgress(partId: String): Double? =
        if (progressList.containsKey(partId)) progressList[partId] else null
}