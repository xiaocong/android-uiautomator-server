package com.github.uiautomator.stub;

import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.test.FlakyTest;
import android.test.suitebuilder.annotation.LargeTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * A working example of a ui automator test.
 * 
 * @author SNI
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class Stub {

	private static final int TEST_TOLERANCE = 3;
	private static final int PORT = 9008;
	private AutomatorHttpServer server = null;

	@Before
	public void setUp() throws Exception {
		server = new AutomatorHttpServer(PORT);
		server.route("/jsonrpc/0", new JsonRpcServer(new ObjectMapper(),
				new AutomatorServiceImpl(), AutomatorService.class));
		server.start();
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
		server = null;
	}

	@Test
	@LargeTest
	@FlakyTest(tolerance = TEST_TOLERANCE)
	public void testUIAutomatorStub() throws InterruptedException {
		while (server.isAlive())
			Thread.sleep(100);
	}
}