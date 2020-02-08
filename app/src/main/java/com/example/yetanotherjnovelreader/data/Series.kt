package com.example.yetanotherjnovelreader.data

import com.example.yetanotherjnovelreader.ListItem
import org.json.JSONObject

class Series(source: JSONObject) : JSONObject(source.toString()), ListItem {
    val id = getString("id")
    val title = getString("title")
    val titleslug = getString("titleslug")
    val description = getString("description")
    val descriptionShort = getString("descriptionShort")
    val tags = getString("tags")
    val created = getString("created")
    val overrideExpiration = getBoolean("override_expiration")
    val coverFullURL: String
    val coverThumbURL: String

    init {
        val attachArray = source.getJSONArray("attachments")
        var cover: String? = null
        var thumb: String? = null
        for (i in 0 until attachArray.length()) {
            val attachUrl = attachArray.getJSONObject(i).getString("fullpath")
            if (attachUrl.contains("cover")) cover = attachUrl
            if (attachUrl.contains("thumb")) thumb = attachUrl
        }
        coverFullURL = cover ?: ""
        coverThumbURL = thumb ?: ""
    }

    override fun getListItemContents(): ListItem.ListItemContents = ListItem.ListItemContents(
        title,
        descriptionShort,
        "${RemoteRepository.IMG_ADDR}/$coverThumbURL"
    )
}