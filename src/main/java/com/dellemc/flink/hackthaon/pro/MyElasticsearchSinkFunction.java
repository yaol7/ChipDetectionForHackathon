package com.dellemc.flink.hackthaon.pro;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;

public class MyElasticsearchSinkFunction implements ElasticsearchSinkFunction<String>, Serializable {
    private static final long serialVersionUID = 8240899316395147392L;
    private static final Logger log = LoggerFactory.getLogger(MyElasticsearchSinkFunction.class);
    private String indexName;
    private String type;

    public MyElasticsearchSinkFunction(final String indexName, final String type) {
        this.indexName = indexName;
        this.type = type;
    }

    public IndexRequest createIndexRequest(String element) {
        boolean isIndexExist = false;
        IndicesExistsRequest indicesExistsRequest = Requests.indicesExistsRequest(indexName);
        for (String indexName : indicesExistsRequest.indices()) {
            if (indexName.equals(this.indexName)) {
                isIndexExist = true;
                break;
            }
        }
        if (!isIndexExist) {
            log.info("create the index: {}", indexName);
            Requests.createIndexRequest(indexName)
                    .mapping(type, indexMapping(), XContentType.JSON);
        }
        return Requests.indexRequest()
                .index(indexName)
                .type(type)
                .source(element, XContentType.JSON);
    }

    @Override
    public void process(String element, RuntimeContext ctx, RequestIndexer indexer) {
        indexer.add(createIndexRequest(element));
    }

    public static String indexMapping() {
        return "{\n" +
                "    \"properties\": {\n" +
                "      \"timestamp\": {\n" +
                "        \"type\": \"date\"\n" +
                "      },\n" +
                "      \"defects\": {\n" +
                "        \"properties\": {\n" +
                "          \"br\": {\n" +
                "            \"type\": \"long\"\n" +
                "          },\n" +
                "          \"tl\": {\n" +
                "            \"type\": \"long\"\n" +
                "          },\n" +
                "          \"type\": {\n" +
                "            \"type\": \"long\"\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"defectsLen\": {\n" +
                "        \"type\": \"long\"\n" +
                "      },\n" +
                "      \"encoding_level\": {\n" +
                "        \"type\": \"long\"\n" +
                "      },\n" +
                "      \"encoding_type\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"fields\": {\n" +
                "          \"keyword\": {\n" +
                "            \"type\": \"keyword\",\n" +
                "            \"ignore_above\": 256\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"factory\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"fields\": {\n" +
                "          \"keyword\": {\n" +
                "            \"type\": \"keyword\",\n" +
                "            \"ignore_above\": 256\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"frame_number\": {\n" +
                "        \"type\": \"long\"\n" +
                "      },\n" +
                "      \"height\": {\n" +
                "        \"type\": \"long\"\n" +
                "      },\n" +
                "      \"img_handle\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"fields\": {\n" +
                "          \"keyword\": {\n" +
                "            \"type\": \"keyword\",\n" +
                "            \"ignore_above\": 256\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"location\": {\n" +
                "        \"type\": \"geo_point\"\n" +
                "      },\n" +
                "      \"production_line\": {\n" +
                "        \"type\": \"long\"\n" +
                "      },\n" +
                "      \"timestamp\": {\n" +
                "        \"type\": \"date\"\n" +
                "      },\n" +
                "      \"topic\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"fields\": {\n" +
                "          \"keyword\": {\n" +
                "            \"type\": \"keyword\",\n" +
                "            \"ignore_above\": 256\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"version\": {\n" +
                "        \"type\": \"text\",\n" +
                "        \"fields\": {\n" +
                "          \"keyword\": {\n" +
                "            \"type\": \"keyword\",\n" +
                "            \"ignore_above\": 256\n" +
                "          }\n" +
                "        }\n" +
                "      },\n" +
                "      \"width\": {\n" +
                "        \"type\": \"long\"\n" +
                "      }\n" +
                "    }\n" +
                "  }";
    }
}
