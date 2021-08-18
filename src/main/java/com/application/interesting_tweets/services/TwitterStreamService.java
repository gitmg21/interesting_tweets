package com.application.interesting_tweets.services;

import com.application.interesting_tweets.connections.KafkaConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * Service to ingest Tweets using Twitter SampleStream API
 * */
public class TwitterStreamService implements Runnable{

    // To set your environment variables in your terminal run the following line:
    // export 'BEARER_TOKEN'='<your_bearer_token>'

    private final String bearertoken;

    private final KafkaProducer tweetProducer;

    private final int numPartitions; //Can be of use if we want a particular partitioning strategy

    private final String topicName;


    public TwitterStreamService(KafkaConnection kafkaConnection){

        this.bearertoken = System.getenv("BEARER_TOKEN");
        this.tweetProducer = new KafkaProducer(kafkaConnection.getProducerProps());
        this.numPartitions = kafkaConnection.NUM_PARTITIONS;
        this.topicName = kafkaConnection.TOPIC_NAME;

        if (null != bearertoken) {
            connectStream();
        } else {
            System.out.println("There was a problem getting you bearer token. Please make sure you set the BEARER_TOKEN environment variable");
        }

    }

    @Override
    public void run(){
        connectStream();
    }

    /*
     * This method calls the sample stream endpoint and streams Tweets from it and publishes through @KafkaProducer
     * */
    private void connectStream(){

        try {
            HttpClient httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.STANDARD).build())
                    .build();

            URIBuilder uriBuilder = new URIBuilder("https://api.twitter.com/2/tweets/sample/stream?tweet.fields=conversation_id,referenced_tweets");

            HttpGet httpGet = new HttpGet(uriBuilder.build());
            httpGet.setHeader("Authorization", String.format("Bearer %s", this.bearertoken));

            HttpResponse response = httpClient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            if (null != entity) {
                BufferedReader reader = new BufferedReader(new InputStreamReader((entity.getContent())));
                String line = reader.readLine();
                //TODO: Ask if Kafka Produce to Different partitions with no key with default producing strategy possible
                while (line != null) {
                    this.tweetProducer.send(new ProducerRecord(this.topicName, line));
                    line = reader.readLine();
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Twitter Connection Error");
        }
    }
}