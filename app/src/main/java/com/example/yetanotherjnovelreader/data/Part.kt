package com.example.yetanotherjnovelreader.data

import com.example.yetanotherjnovelreader.common.ListItem
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
    var progress = 0.0

    override fun getListItemContents(): ListItem.ListItemContents = ListItem.ListItemContents(
        title,
        descriptionShort
    )
}
