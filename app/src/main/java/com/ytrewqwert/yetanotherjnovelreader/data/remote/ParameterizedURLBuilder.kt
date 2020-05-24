package com.ytrewqwert.yetanotherjnovelreader.data.remote

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ParameterizedURLBuilder(
    private val baseUrl: String
) {

    private var seriesFilters: List<String>? = null
    private val whereFilters = HashMap<String, String>()
    private val baseFilters = ArrayList<Pair<String, String>>()
    private val includes = ArrayList<String>()

    fun addFilter(key: String, value: String): ParameterizedURLBuilder {
        whereFilters[key] = value
        return this
    }
    fun setSeriesFilters(seriesIds: List<String>?): ParameterizedURLBuilder {
        seriesFilters = seriesIds
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
            filterString.add("\"${f.first}\":\"${f.second}\"")
        }
        return filterString.toString()
    }
    private fun generateWhereFilterString() {
        generateSeriesFilterString()
        if (whereFilters.keys.isEmpty()) return
        val filterString = StringJoiner(",", "\"where\":{", "}")
        for (key in whereFilters.keys) {
            if (whereFilters[key]!![0] == '{' || whereFilters[key]!![0] == '[') {
                filterString.add("\"${key}\":${whereFilters[key]}")
            } else {
                filterString.add("\"${key}\":\"${whereFilters[key]}\"")
            }
        }
        baseFilters.add(Pair("where", filterString.toString()))
    }
    private fun generateSeriesFilterString() {
        val result = seriesFilters?.map { "{\"id\":\"$it\"}" } ?: return
        whereFilters["or"] = result.joinToString(",", "[", "]")
    }

    private fun generateIncludeString(): String {
        val includeString = StringJoiner(",", "\"include\":[", "]")
        for (includeVal in includes) includeString.add("\"${includeVal}\"")
        return includeString.toString()
    }
}