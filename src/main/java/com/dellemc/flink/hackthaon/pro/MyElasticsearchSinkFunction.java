package com.dellemc.flink.hackthaon.pro;

import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.streaming.connectors.elasticsearch.ElasticsearchSinkFunction;
import org.apache.flink.streaming.connectors.elasticsearch.RequestIndexer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentType;
import java.io.Serializable;

public class MyElasticsearchSinkFunction implements ElasticsearchSinkFunction<String>, Serializable {
    private static final long serialVersionUID = 8240899316395147392L;
    private String indexName;
    private String type;

    public MyElasticsearchSinkFunction(final String indexName, final String type) {
        this.indexName = indexName;
        this.type = type;
    }

    public IndexRequest createIndexRequest(String element) {
        return Requests.indexRequest()
                .index(indexName)
                .type(type)
                .source(element, XContentType.JSON);
    }

    @Override
    public void process(String element, RuntimeContext ctx, RequestIndexer indexer) {
        indexer.add(createIndexRequest(element));
    }
}
