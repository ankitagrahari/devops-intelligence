package org.backendbrilliance.devopsintelligence.models;

import java.util.List;
import java.util.Map;

public class MetricResult {

    /**
     * The metric labels (e.g., pod_name, namespace, etc.)
     */
    private Map<String, String> metric;

    /**
     * For instant queries: [timestamp, value]
     * For range queries: [[timestamp1, value1], [timestamp2, value2], ...]
     */
    private Object value;  // Can be List or String[]

    public Map<String, String> getMetric() {
        return metric;
    }

    public void setMetric(Map<String, String> metric) {
        this.metric = metric;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Get the numeric value as a String (as Prometheus returns it)
     */
    public String getValueAsString() {
        if (value instanceof List) {
            // Range query - get first value
            List<?> list = (List<?>) value;
            if (list.size() > 1) {
                return list.get(1).toString();
            }
        } else if (value instanceof String[]) {
            // Instant query
            String[] parts = (String[]) value;
            return parts[1];
        }
        return null;
    }

    /**
     * Get the numeric value as Double
     */
    public Double getValueAsDouble() {
        String val = getValueAsString();
        if (val == null) return null;
        try {
            return Double.parseDouble(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Get a specific label value
     */
    public String getLabel(String labelName) {
        if (metric == null) return null;
        return metric.get(labelName);
    }

    @Override
    public String toString() {
        return "MetricResult{" +
                "metric=" + metric +
                ", value=" + value +
                '}';
    }

}
