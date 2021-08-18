package com.application.interesting_tweets.services;

import com.application.interesting_tweets.connections.KafkaConnection;
import com.application.interesting_tweets.daos.TweetIdDao;
import com.application.interesting_tweets.daos.TwitterDao;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaConsumers {

    private final KafkaConnection kafkaConnection; //Access to Kafka Connection to create consumers
    private final int num_consumers; //1 thread is spwan per consumer

    private final TweetIdDao redisDB; //Access to RedisDB Interface
    private final TwitterDao elastisearchDB; //Access to ElasticsearchDB Interface

    private final RegexMatchService regexMatchService; //Access to Sevice for Regex Matching

    public ExecutorService executorService;//Executor Service, currently it is responsibility of caller to shutdown


    public KafkaConsumers (KafkaConnection kafkaConnection, TweetIdDao redisDB, TwitterDao elastisearchDB, RegexMatchService regexMatchService){
        this.kafkaConnection = kafkaConnection;
        this.num_consumers = kafkaConnection.NUM_PARTITIONS*2;
        this.redisDB = redisDB;
        this.elastisearchDB = elastisearchDB;
        this.regexMatchService = regexMatchService;

        //Note that we cannot spawn consumer threads here, since the object is not fully constructed yet
    }

    /**
     * Runs the consumers for processing the incoming KafkaStream tweets
     * TODO: In case application requires more than 2 consumer groups, change the signature and method to handle an array of Strings
     *
     * @param interestingFilterGroupName Name of the consumer Group that filters interesting tweets
     * @param parentInterestingFilterGroupName Name of the consumer Group that filters if tweet is a reply to an interesting tweet
     */
    public void runConsumers(String interestingFilterGroupName, String parentInterestingFilterGroupName){

        executorService = Executors.newFixedThreadPool(num_consumers);
        String consumerGroupName = interestingFilterGroupName;
        int consumerId = 0;

        while(consumerId < num_consumers){
            String id = String.valueOf(consumerId);
            if (consumerId < num_consumers/2)
                executorService.execute(() -> startInterestingFilterConsumer(id, interestingFilterGroupName));
            else
                executorService.execute(() -> startParentInterestingFilterConsumer(id, parentInterestingFilterGroupName));

            consumerId++;
        }
    }

    /**
     * Consumer Method to check incoming tweets and write to elasticsearch and redis if interesting
     *
     * @param consumerId Id of the consumer
     * @param groupName Name of the consumer Group
     */

    private void startInterestingFilterConsumer(String consumerId, String groupName){

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(kafkaConnection.getConsumerProps(groupName));
        consumer.subscribe(Collections.singletonList(kafkaConnection.TOPIC_NAME));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : records) {
                //Play with record.value() here to check if interesting tweet and then dump in ElasticsearchDB and RedisDB
                // TODO: extract Tweet from string
                System.out.printf("consumer id:%s, partition id= %s, key = %s, value = %s"
                                + ", offset = %s%n",
                        consumerId, record.partition(),record.key(), record.value(), record.offset());
            }
            consumer.commitSync();
        }

    }

    /**
     * Consumer Method to check incoming tweets and write to elasticsearch only if parent is interesting
     *
     * @param consumerId Id of the consumer
     * @param groupName Name of the consumer Group
     */

    private void startParentInterestingFilterConsumer(String consumerId, String groupName){

        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(kafkaConnection.getConsumerProps(groupName));
        consumer.subscribe(Collections.singletonList(kafkaConnection.TOPIC_NAME));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : records) {
                //Play with record.value() here to check if interesting tweet and then dump in ElasticsearchDB and RedisDB
                System.out.printf("consumer id:%s, partition id= %s, key = %s, value = %s"
                                + ", offset = %s%n",
                        consumerId, record.partition(),record.key(), record.value(), record.offset());
            }
            consumer.commitSync();
        }

    }

}
