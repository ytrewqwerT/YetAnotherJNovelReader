package com.ytrewqwert.yetanotherjnovelreader.data.remote

import java.util.*
import kotlin.collections.ArrayList

/**
 * Builder for creating the *stuff* in a ?filter=*stuff* appendage of a url for a server running
 * [Loopback](https://loopback.io/).
 */
class UrlParameterBuilder {
    private val wheres = ArrayList<String>()
    private var limit: String? = null
    private var offset: String? = null

    /** Adds a parameter of the form "limit":value */
    fun addLimit(value: Int) { limit = "\"limit\":$value" }

    /** Adds a parameter of the form "offset":value */
    fun addOffset(value: Int) { offset = "\"offset\":$value" }

    /** Adds a 'where' parameter of the form: "key":value */
    fun addWhere(key: String, value: String) { wheres.add("\"$key\":$value") }

    /** Adds a 'where' parameter of the form: "key":{"inq":["values[0]","values[1]",...]} */
    fun addWhereFieldInList(key: String, values: List<String>) {
        // "key":{"inq":["a","b","c"]}
        val valuesStr = values.joinToString(",", "[", "]") { "\"$it\"" }
        val str = "\"$key\":{\"inq\":$valuesStr}"
        wheres.add(str)
    }

    override fun toString(): String {
        val result = StringJoiner(",", "{", "}")
        if (wheres.isNotEmpty()) result.add(wheresJoined())
        if (limit != null) result.add(limit)
        if (offset != null) result.add(offset)
        return result.toString()
    }

    private fun wheresJoined(): String {
        val result = StringJoiner(",","\"where\":{","}")
        for (item in wheres) { result.add(item) }
        return result.toString()
    }
}