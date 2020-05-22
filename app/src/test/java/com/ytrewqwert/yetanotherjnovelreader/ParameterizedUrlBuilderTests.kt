package com.ytrewqwert.yetanotherjnovelreader

import com.ytrewqwert.yetanotherjnovelreader.data.remote.ParameterizedURLBuilder
import org.junit.Assert.assertEquals
import org.junit.Test

class ParameterizedUrlBuilderTests {
    @Test
    fun test_ParameterizedUrlBuilder() {

        var expectedUrl = "?filter=" +
                "{\"where\":{\"launchDate\":{\"gt\":\"now\"}},\"order\":\"launchDate+DESC\"}"
        var actualUrl = ParameterizedURLBuilder("")
            .addFilter("launchDate", "{\"gt\":\"now\"}")
            .addBaseFilter("order", "launchDate+DESC")
            .build()
        assertEquals(expectedUrl, actualUrl)

        expectedUrl = "?filter=" +
                "{\"where\":{\"id\":\"serieId\"},\"include\":[\"volumes\",\"parts\"]}"
        actualUrl = ParameterizedURLBuilder("")
            .addFilter("id", "serieId")
            .addInclude("volumes")
            .addInclude("parts")
            .build()
        assertEquals(expectedUrl, actualUrl)
    }
}
