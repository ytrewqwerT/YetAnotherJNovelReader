package com.ytrewqwert.yetanotherjnovelreader.data.remote

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Appends filters to a URL for a server running [Loopback](https://loopback.io/).
 *
 * @property[baseUrl] The original URL without any filters attached.
 */
class ParameterizedURLBuilder(
    private val baseUrl: String
) {
    private val fieldInListFilters = ArrayList<Pair<String, List<String>>>()
    private val whereFilters = HashMap<String, String>()
    private val baseFilters = ArrayList<Pair<String, String>>()
    private val includes = ArrayList<String>()

    /** Filter that checks if [field]'s value is [value]. */
    fun addWhereFilter(field: String, value: String): ParameterizedURLBuilder {
        whereFilters[field] = value
        return this
    }
    /** Filter that checks if [field]'s value is in [values]. */
    fun addFieldInListFilter(field: String, values: List<String>?): ParameterizedURLBuilder {
        if (values == null) return this
        fieldInListFilters.add(Pair(field, values))
        return this
    }
    /** Includes relevant instances of the specified object type in the result. */
    fun addInclude(value: String): ParameterizedURLBuilder {
        includes.add(value)
        return this
    }
    /** Add a filter in the root level. i.e. ?filters={"[type]":"[value]", ...} */
    fun addBaseFilter(type: String, value: String): ParameterizedURLBuilder {
        baseFilters.add(Pair(type, value))
        return this
    }

    /** Returns the URL with the filters attached. Can only be called once per instance. */
    fun build() = toString()

    override fun toString(): String {
        val filteredUrl = StringJoiner(",", "${baseUrl}?filter={", "}")
        val filterString = generateBaseFiltersString()
        if (filterString.isNotBlank()) filteredUrl.add(filterString)
        if (includes.isNotEmpty()) filteredUrl.add(generateIncludeString())

        return filteredUrl.toString()
    }

    private fun generateBaseFiltersString(): String {
        generateWhereFilterString()
        if (baseFilters.isEmpty()) return ""
        val filterString = StringJoiner(",")
        for (f in baseFilters) {
            if (f.second[0] == '{' || f.second[0] == '[') {
                filterString.add("\"${f.first}\":${f.second}")
            } else {
                filterString.add("\"${f.first}\":\"${f.second}\"")
            }
        }
        return filterString.toString()
    }
    private fun generateWhereFilterString() {
        generateFieldInListString()
        if (whereFilters.keys.isEmpty()) return
        val filterString = StringJoiner(",", "{", "}")
        for (key in whereFilters.keys) {
            if (whereFilters[key]!![0] == '{' || whereFilters[key]!![0] == '[') {
                filterString.add("\"${key}\":${whereFilters[key]}")
            } else {
                filterString.add("\"${key}\":\"${whereFilters[key]}\"")
            }
        }
        baseFilters.add(Pair("where", filterString.toString()))
    }

    private fun generateIncludeString(): String {
        val includeString = StringJoiner(",", "\"include\":[", "]")
        for (includeVal in includes) includeString.add("\"${includeVal}\"")
        return includeString.toString()
    }

    private fun generateFieldInListString() {
        fieldInListFilters.forEach { filter ->
            val matchArray = filter.second.joinToString(",","{\"inq\":[","]}") {
                "\"$it\""
            }
            whereFilters[filter.first] = matchArray
        }
    }
}