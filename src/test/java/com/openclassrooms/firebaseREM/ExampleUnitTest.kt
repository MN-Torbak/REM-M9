package com.openclassrooms.firebaseREM

import com.openclassrooms.firebaseREM.Utils.convertEuroToDollar
import com.openclassrooms.firebaseREM.Utils.todayDateFrenchFormat
import org.junit.Assert
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {

    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        Assert.assertEquals(4, (2 + 2).toLong())
    }

    @Test
    @Throws(Exception::class)
    fun convertEuroToDollarTest() {
        val Euro = 1000
        val Dollar = 1060
        Assert.assertEquals(Dollar, convertEuroToDollar(Euro))
    }

    @Test
    @Throws(Exception::class)
    fun dateReformedTest() {
        Assert.assertEquals("22/06/2022", todayDateFrenchFormat)
    }

}