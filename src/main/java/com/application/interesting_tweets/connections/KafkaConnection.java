package com.application.interesting_tweets.connections;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;

import java.util.Collections;
import java.util.Properties;

public class KafkaConnection {

    public final String BROKERS = "localhost:9092";

    public final String TOPIC_NAME = "tweetsTopic";

    public final int NUM_PARTITIONS = 3;

    public KafkaConnection() {
        try {
            createTopic();
        }
        catch (Exception e){
            e.printStackTrace();
            System.out.println("Redis Topic Exception");
        }
    }

    private void createTopic() throws Exception {
        Properties config = new Properties();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, BROKERS);
        AdminClient admin = AdminClient.create(config);

        //checking if topic already exists
        boolean alreadyExists = admin.listTopics().names().get().stream()
                .anyMatch(existingTopicName -> existingTopicName.equals(TOPIC_NAME));
        if (alreadyExists) {
            System.out.printf("topic already exits: %s%n", TOPIC_NAME);
        } else {
            NewTopic newTopic = new NewTopic(TOPIC_NAME, NUM_PARTITIONS, (short) 1);
            admin.createTopics(Collections.singleton(newTopic)).all().get();
        }
        admin.close();
    }

    public Properties getProducerProps() {
        Properties props = new Properties();
        props.put("bootstrap.servers", BROKERS);
        props.put("acks", "all");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return props;
    }

    public Properties getConsumerProps(String consumerGroup) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", BROKERS);
        props.setProperty("group.id", consumerGroup);
        props.setProperty("enable.auto.commit", "false");
        props.setProperty("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.setProperty("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("auto.offset.reset", "earliest");
        return props;
    }

}
