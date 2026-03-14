package org.backendbrilliance.devopsintelligence.clients;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;

import java.io.IOException;

/**
 * Reference: https://github.com/elastic/elasticsearch-java/tree/main/examples
 */
public interface LogsClient {

    default ElasticsearchClient elasticRestClient(String serverUrl, String apiKey) throws IOException {

        // Create the low-level client
        RestClient restClient = RestClient
                .builder(HttpHost.create(serverUrl))
                .setDefaultHeaders(new Header[]{
                        new BasicHeader("Authorization", "ApiKey " + apiKey)
                })
                .build();

        // The transport layer of the Elasticsearch client requires a json object mapper to
        // define how to serialize/deserialize java objects. The mapper can be customized by adding
        // modules, for example since the Article and Comment object both have Instant fields, the
        // JavaTimeModule is added to provide support for java 8 Time classes, which the mapper itself does
        // not support.
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();

        // Create the transport with the Jackson mapper
        ElasticsearchTransport transport = new RestClientTransport(
                restClient, new JacksonJsonpMapper(mapper));

        // Create the API client
        ElasticsearchClient client = new ElasticsearchClient(transport);
        return client;
    }

    /**
     * Plain simple
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/docs-index_.html">index</a>
     * creation with an
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-exists.html">
     * exists</a> check
     */
    private void createSimpleIndex(ElasticsearchClient esClient, String index) throws IOException {
        BooleanResponse indexRes = esClient.indices().exists(ex -> ex.index(index));
        if (!indexRes.value()) {
            esClient.indices().create(c -> c
                    .index(index));
        }
    }

    /**
     * If no explicit mapping is defined, elasticsearch will dynamically map types when converting data to
     * the json
     * format. Adding explicit mapping to the date fields assures that no precision will be lost. More
     * information about
     * <a href="https://www.elastic.co/guide/en/elasticsearch/reference/current/dynamic-field-mapping.html">dynamic
     * field mapping</a>, more on <a
     * href="https://www.elastic.co/guide/en/elasticsearch/reference/current/mapping-date-format
     * .html">mapping date
     * format </a>
     */
    private void createIndexWithDateMapping(ElasticsearchClient esClient, String index) throws IOException {
        BooleanResponse indexRes = esClient.indices().exists(ex -> ex.index(index));
        if (!indexRes.value()) {
            esClient.indices()
                    .create(c -> c.index(index)
                            .mappings(m -> m
                                    .properties("createdAt", p -> p.date(d -> d.format("strict_date_optional_time")))
                                    .properties("updatedAt", p -> p.date(d -> d.format("strict_date_optional_time")))));

        }
    }
}
