package com.ir.index.es;


import com.ir.crawl.parse.field.Field;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

import java.util.Map;

public class Indexer {

    private static final Client client = new TransportClient()
            .addTransportAddress(new InetSocketTransportAddress("localhost", 9300));


    public static final void addDoc(String id, Map<Field, Object> productMap) {
        try{
            IndexResponse indexResponse = client.prepareIndex("item", "amazons", id)
                .setSource(productMap)
                .execute()
                .actionGet();
            System.out.println(indexResponse.toString());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void shutdown(){
        client.close();
    }


}
