package org.backendbrilliance.devopsintelligence.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PrometheusData {

    @SerializedName("resultType")
    private String resultType;  // "vector", "matrix", "scalar", "string"

    private List<MetricResult> result;

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public List<MetricResult> getResult() {
        return result;
    }

    public void setResult(List<MetricResult> result) {
        this.result = result;
    }
}
