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
 * Use JUnit test to start the uiautomator jsonrpc server.
 * @author xiaocong@gmail.com
 */
RunWith(javaClass<AndroidJUnit4>())
SdkSuppress(minSdkVersion = 18)
public class Stub {
    val PORT = 9008
    val server = AutomatorHttpServer(PORT)

    Before
    throws(javaClass<Exception>())
    public fun setUp() {
        server.route("/jsonrpc/0", JsonRpcServer(ObjectMapper(), AutomatorServiceImpl(), javaClass<AutomatorService>()))
        server.start()
        UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).wakeUp()
    }

    After
    throws(javaClass<Exception>())
    public fun tearDown() {
        server.stop()
    }

    Test
    LargeTest
    FlakyTest(tolerance = 3)
    throws(javaClass<InterruptedException>())
    public fun testUIAutomatorStub() {
        while (server.isAlive())
            Thread.sleep(100)
    }

}