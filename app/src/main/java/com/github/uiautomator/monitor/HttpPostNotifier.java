package com.github.uiautomator.monitor;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by hzsunshx on 2017/11/15.
 */

public class HttpPostNotifier {
    private String reportUrl;
    private OkHttpClient client;

    public HttpPostNotifier(String reportUrl) {
        // reportUrl eg: http://127.0.0.1:7912
        this.reportUrl = reportUrl;
        this.client = new OkHttpClient();
    }

    public void Notify(String baseUrl, String content) {
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), content);
        Notify(baseUrl, body);
    }

    public void Notify(String baseUrl, RequestBody body) {
        // baseUrl should have / prefix
        Request request = new Request.Builder()
                .url(reportUrl + baseUrl)
                .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                // Do nothing
            }
        });
    }
}
