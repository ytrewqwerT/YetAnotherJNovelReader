package com.ytrewqwert.yetanotherjnovelreader.data.remote

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ParameterizedURLBuilder(
    private val baseUrl: String
) {

    private val filters = HashMap<String, String>()
    private val includes = ArrayList<String>()
    private val baseFilters = ArrayList<Pair<String, String>>()

    fun addFilter(key: String, value: String): ParameterizedURLBuilder {
        filters[key] = value
        return this
    }
    fun addInclude(value: String): ParameterizedURLBuilder {
        includes.add(value)
        return this
    }
    fun addBaseFilter(type: String, value: String): ParameterizedURLBuilder {
        baseFilters.add(Pair(type, value))
        return this
    }

    fun build() = toString()

    override fun toString(): String {
        val filteredUrl = StringJoiner(",", "${baseUrl}?filter={", "}")
        if (filters.isNotEmpty()) filteredUrl.add(generateFilterString())
        if (includes.isNotEmpty()) filteredUrl.add(generateIncludeString())
        if (baseFilters.isNotEmpty()) filteredUrl.add(generateBaseFiltersString())

        return filteredUrl.toString()
    }

    private fun generateFilterString(): String {
        val filterString = StringJoiner(",", "\"where\":{", "}")
        for (key in filters.keys) {
            if (filters[key]!![0] == '{') {
                filterString.add("\"${key}\":${filters[key]}")
            } else {
                filterString.add("\"${key}\":\"${filters[key]}\"")
            }
        }
        return filterString.toString()
    }
    private fun generateIncludeString(): String {
        val includeString = StringJoiner(",", "\"include\":[", "]")
        for (includeVal in includes) includeString.add("\"${includeVal}\"")
        return includeString.toString()
    }
    private fun generateBaseFiltersString(): String {
        val filterString = StringJoiner(",")
        for (f in baseFilters) {
            filterString.add("\"${f.first}\":\"${f.second}\"")
        }
        return filterString.toString()
    }
}