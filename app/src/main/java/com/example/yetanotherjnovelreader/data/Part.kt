package com.example.yetanotherjnovelreader.data

import com.example.yetanotherjnovelreader.common.ListItem
import com.example.yetanotherjnovelreader.data.remote.RemoteRepository
import org.json.JSONObject

class Part(source: JSONObject) : JSONObject(source.toString()), ListItem {
    val id = getString("id")
    val volumeId = getString("volumeId")
    val serieId = getString("serieId")
    val title = getString("title")
    val titleslug = getString("titleslug")
    val partNum = getInt("partNumber")
    val description = getString("description")
    val descriptionShort = getString("descriptionShort")
    val tags = getString("tags")
    val created = getString("created")
    val coverFullUrl: String
    val coverThumbUrl: String
    var progress = 0.0

    init {
        val attachArray = source.getJSONArray("attachments")
        var cover: String? = null
        var thumb: String? = null
        for (i in 0 until attachArray.length()) {
            val attachUrl = attachArray.getJSONObject(i).getString("fullpath")
            if (attachUrl.contains("cover")) cover = attachUrl
            if (attachUrl.contains("thumb")) thumb = attachUrl
        }
        coverFullUrl = cover ?: ""
        coverThumbUrl = thumb ?: ""
    }

    override fun getListItemContents(): ListItem.ListItemContents = ListItem.ListItemContents(
        title, null,
        "${RemoteRepository.IMG_ADDR}/$coverFullUrl",
        progress
    )
}
