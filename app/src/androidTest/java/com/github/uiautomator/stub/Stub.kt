package com.github.uiautomator.stub

import android.support.test.InstrumentationRegistry
import android.support.test.filters.SdkSuppress
import android.support.test.runner.AndroidJUnit4
import android.support.test.uiautomator.UiDevice
import android.test.FlakyTest
import android.test.suitebuilder.annotation.LargeTest

import com.fasterxml.jackson.databind.ObjectMapper
import com.googlecode.jsonrpc4j.JsonRpcServer

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * A working example of a ui automator test.

 * @author SNI
 */
RunWith(javaClass<AndroidJUnit4>())
SdkSuppress(minSdkVersion = 18)
public class Stub {
    private var server: AutomatorHttpServer? = null

    Before
    throws(javaClass<Exception>())
    public fun setUp() {
        server = AutomatorHttpServer(PORT)
        server!!.route("/jsonrpc/0", JsonRpcServer(ObjectMapper(), AutomatorServiceImpl(), javaClass<AutomatorService>()))
        server!!.start()
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).wakeUp()
    }

    After
    throws(javaClass<Exception>())
    public fun tearDown() {
        server!!.stop()
        server = null
    }

    Test
    LargeTest
    FlakyTest(tolerance = TEST_TOLERANCE)
    throws(javaClass<InterruptedException>())
    public fun testUIAutomatorStub() {
        while (server!!.isAlive())
            Thread.sleep(100)
    }

    companion object {
        private val TEST_TOLERANCE = 3
        private val PORT = 9008
    }
}