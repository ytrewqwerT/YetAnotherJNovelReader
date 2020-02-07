package com.example.yetanotherjnovelreader

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

    override fun getListItemContents(): ListItem.ListItemContents =
        ListItem.ListItemContents(title, descriptionShort)
}