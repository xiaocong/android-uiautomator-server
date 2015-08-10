/*
 * The MIT License (MIT)
 * Copyright (c) 2015 xiaocong@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE
 * OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.uiautomator.stub;

import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.UiDevice;

import com.googlecode.jsonrpc4j.JsonRpcServer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

public class AutomatorHttpServer extends NanoHTTPD {

    public AutomatorHttpServer(int port) {
        super(port);
    }

    private Map<String, JsonRpcServer> router = new HashMap<String, JsonRpcServer>();

    public void route(String uri, JsonRpcServer rpc) {
        router.put(uri, rpc);
    }

    @Override
    public Response serve(String uri, Method method,
                          Map<String, String> headers, Map<String, String> params,
                          Map<String, String> files) {
        Log.d(String.format("URI: %s, Method: %s, Header: %s, params, %s, files: %s", uri, method, headers, params, files));

        if ("/stop".equals(uri)) {
            stop();
            return new Response("Server stopped!!!");
        } else if ("/0/screenshot".equals(uri) || "/screenshot/0".equals(uri)) {
            float scale = 1.0f;
            if (params.containsKey("scale")) {
                try {
                    scale = Float.parseFloat(params.get("scale"));
                } catch (NumberFormatException e) {
                }
            }
            int quality = 100;
            if (params.containsKey("quality")) {
                try {
                    quality = Integer.parseInt(params.get("quality"));
                } catch (NumberFormatException e) {
                }
            }
            File f = new File(InstrumentationRegistry.getTargetContext().getFilesDir(), "screenshot.png");
            UiDevice.getInstance(InstrumentationRegistry.getInstrumentation()).takeScreenshot(f, scale, quality);
            try {
                return new Response(Response.Status.OK, "image/png", new FileInputStream(f));
            } catch (FileNotFoundException e) {
                Log.e(e.getMessage());
                return new Response(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Internal Server Error!!!");
            }
        } else if (router.containsKey(uri)) {
            JsonRpcServer jsonRpcServer = router.get(uri);
            ByteArrayInputStream is = null;
            if (params.get("NanoHttpd.QUERY_STRING") != null)
                is = new ByteArrayInputStream(params.get("NanoHttpd.QUERY_STRING").getBytes());
            else if (files.get("postData") != null)
                is = new ByteArrayInputStream(files.get("postData").getBytes());
            else
                return new Response(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Invalid http post data!");
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            try {
                jsonRpcServer.handle(is, os);
                return new Response(Response.Status.OK, "application/json", new ByteArrayInputStream(os.toByteArray()));
            } catch (IOException e) {
                return new Response(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Internal Server Error!!!");
            }
        } else
            return new Response(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found!!!");
    }

}
