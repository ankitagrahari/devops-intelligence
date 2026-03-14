package org.backendbrilliance.devopsintelligence.models;

public class PrometheusResponse {
    private String status;           // "success" or "error"
    private PrometheusData data;     // Contains results
    private String error;            // Error message if failed

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PrometheusData getData() {
        return data;
    }

    public void setData(PrometheusData data) {
        this.data = data;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}