package org.backendbrilliance.devopsintelligence.clients;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.backendbrilliance.devopsintelligence.models.MetricResult;
import org.backendbrilliance.devopsintelligence.models.PrometheusResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class PrometheusClient {
    private final OkHttpClient httpClient = new OkHttpClient();
    private final Gson gson = new Gson();
    private final String prometheusUrl;

    public PrometheusClient(
            @Value("${prometheus.url:http://prometheus:9090}") String prometheusUrl) {
        this.prometheusUrl = prometheusUrl;
    }

    /**
     * Execute an instant query
     * @param promQL PromQL query string
     * @return List of metric results
     */
    public List<MetricResult> query(String promQL) throws IOException {
        String url = prometheusUrl + "/api/v1/query?query=" +
                URLEncoder.encode(promQL, StandardCharsets.UTF_8);

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP error: " + response.code());
            }

            String body = response.body().string();
            PrometheusResponse prometheusResponse = gson.fromJson(
                    body,
                    PrometheusResponse.class
            );

            if (!"success".equals(prometheusResponse.getStatus())) {
                throw new IOException("Query error: " + prometheusResponse.getError());
            }

            return prometheusResponse.getData().getResult();
        }
    }

    /**
     * Execute a range query (for graphs)
     */
    public List<MetricResult> queryRange(
            String promQL,
            long startTime,
            long endTime,
            String step) throws IOException {

        String url = prometheusUrl + "/api/v1/query_range?" +
                "query=" + URLEncoder.encode(promQL, StandardCharsets.UTF_8) +
                "&start=" + startTime +
                "&end=" + endTime +
                "&step=" + step;

        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("HTTP error: " + response.code());
            }

            String body = response.body().string();
            PrometheusResponse prometheusResponse = gson.fromJson(
                    body,
                    PrometheusResponse.class
            );

            if (!"success".equals(prometheusResponse.getStatus())) {
                throw new IOException("Query error: " + prometheusResponse.getError());
            }

            return prometheusResponse.getData().getResult();
        }
    }
}
