package com.ir.index.es;


import com.ir.config.retailer.amazon.FieldNames;
import com.ir.crawl.parse.field.Field;
import com.ir.crawl.parse.parser.Parser;
import com.ir.index.json.ItemTypeTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;

import java.util.Map;

public class Indexer {

    protected static final Logger logger = LogManager.getLogger(Indexer.class.getName());

    public static final String INDEX_TYPE_ITEM = "item";

    public static final String INDEX_TYPE_EXCL_ITEM = "exitem";

    private String clusterId = "elasticsearch";

    private Client client = null;

    public Indexer(String clusterId){
        this.clusterId = clusterId;
        Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", this.clusterId).build();
        client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
    }

    public void addDoc(Parser parser, Map<Field, Object> data) {
        if(parser.getDataType().equals(INDEX_TYPE_ITEM))
            addItemTypeDoc(parser, data);

    }


    public final void addItemTypeDoc(Parser parser, Map<Field, Object> data) {
        String id = null;
        try{
            id = (String)data.get(parser.getField(FieldNames.ID));
            XContentBuilder xb = ItemTypeTransformer.serialize(parser, data);
            logger.debug("Indexing Document: " + id + "  -  " + xb.prettyPrint().string());
            client.prepareIndex(parser.getRetailer(), INDEX_TYPE_ITEM, id)
                .setSource(xb).execute().actionGet();
        } catch(Exception e){
            logger.error("Indexing Item Doc failed for Id: " + id);
            e.printStackTrace();
        }
    }

    public void addExItemTypeDoc(Parser parser, Map<Field, Object> data) {
        String id = null;
        try{
            id = (String)data.get(parser.getField(FieldNames.ID));
            XContentBuilder xb = ItemTypeTransformer.serializeExclItem(parser, data);
            logger.debug("Indexing Document: " + id + "  -  " + xb.prettyPrint().string());
            client.prepareIndex(parser.getRetailer(), INDEX_TYPE_EXCL_ITEM, id)
                    .setSource(xb).execute().actionGet();
        } catch(Exception e){
            logger.error("Indexing Item Doc failed for Id: " + id);
            e.printStackTrace();
        }
    }

    public void shutdown(){
        client.close();
    }


}
