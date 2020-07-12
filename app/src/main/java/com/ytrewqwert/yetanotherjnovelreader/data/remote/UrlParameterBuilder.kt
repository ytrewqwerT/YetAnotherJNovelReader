package com.ytrewqwert.yetanotherjnovelreader.data.remote

import java.util.*
import kotlin.collections.ArrayList

/**
 * Builder for creating the *stuff* in a ?filter=*stuff* appendage of a url for a server running
 * [Loopback](https://loopback.io/).
 */
class UrlParameterBuilder {
    private val wheres = ArrayList<String>()
    private val includes = ArrayList<String>()
    private var limit: String? = null
    private var offset: String? = null
    private var order: String? = null

    /** Adds a parameter of the form "limit":value */
    fun addLimit(value: Int) { limit = "\"limit\":$value" }

    /** Adds a parameter of the form "offset":value */
    fun addOffset(value: Int) { offset = "\"offset\":$value" }

    /** Adds a parameter of the form "order":"value" */
    fun addOrder(value: String) { order = "\"order\":\"$value\"" }

    /** Adds a 'where' parameter of the form: "key":value */
    fun addWhere(key: String, value: String) { wheres.add("\"$key\":$value") }

    /** Adds a 'where' parameter of the form: "key":{"inq":["values[0]","values[1]",...]} */
    fun addWhereFieldInList(key: String, values: List<String>) {
        // "key":{"inq":["a","b","c"]}
        val valuesStr = values.joinToString(",", "[", "]") { "\"$it\"" }
        val str = "\"$key\":{\"inq\":$valuesStr}"
        wheres.add(str)
    }

    /** Adds a directive to include relevant instances of the given object type in the result. */
    fun addInclude(type: String) { includes.add(type) }

    override fun toString(): String {
        val result = StringJoiner(",", "{", "}")
        if (wheres.isNotEmpty()) result.add(wheresJoined())
        if (includes.isNotEmpty()) result.add(includesJoined())
        if (limit != null) result.add(limit)
        if (offset != null) result.add(offset)
        if (order != null) result.add(order)
        return result.toString()
    }

    /** Joins the 'where' parameters and returns the final 'where' statement. */
    private fun wheresJoined(): String {
        val result = StringJoiner(",","\"where\":{","}")
        for (item in wheres) { result.add(item) }
        return result.toString()
    }

    /** Joins the 'include' parameters and returns the final 'include' statement. */
    private fun includesJoined(): String {
        val result = StringJoiner(",", "\"include\":[", "]")
        for (include in includes) result.add("\"${include}\"")
        return result.toString()
    }
}