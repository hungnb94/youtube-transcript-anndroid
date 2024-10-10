package com.leoh.transcript;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;

import io.github.thoroldvix.api.TranscriptRetrievalException;
import io.github.thoroldvix.api.YoutubeClient;
import io.github.thoroldvix.api.YtApiV3Endpoint;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class OkHttpYoutubeClient implements YoutubeClient {

    private final OkHttpClient client;

    public OkHttpYoutubeClient(OkHttpClient client) {
        this.client = client;
    }

    public OkHttpYoutubeClient() {
        this.client = new OkHttpClient();
    }

    @Override
    public String get(String url, Map<String, String> headers) throws TranscriptRetrievalException {
        Request request = new Request.Builder()
                .headers(Headers.of(headers))
                .url(url)
                .build();

        return sendGetRequest(request);
    }

    public String get(YtApiV3Endpoint endpoint, Map<String, String> params) throws TranscriptRetrievalException {
        Request request = new Request.Builder()
                .url(endpoint.url(params))
                .build();

        return sendGetRequest(request);
    }

    @NotNull
    private String sendGetRequest(Request request) throws TranscriptRetrievalException {
        try (okhttp3.Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                ResponseBody body = response.body();
                if (body == null) {
                    throw new TranscriptRetrievalException("Response body is null");
                }
                return body.string();
            }
        } catch (IOException e) {
            throw new TranscriptRetrievalException("Failed to retrieve data from YouTube", e);
        }
        throw new TranscriptRetrievalException("Failed to retrieve data from YouTube");
    }

}
