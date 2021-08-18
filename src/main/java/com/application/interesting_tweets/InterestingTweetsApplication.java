package com.application.interesting_tweets;


import com.application.interesting_tweets.connections.ElasticsearchConnection;
import com.application.interesting_tweets.connections.KafkaConnection;
import com.application.interesting_tweets.connections.MongoConnection;
import com.application.interesting_tweets.connections.RedisConnection;
import com.application.interesting_tweets.daos.Regexdao;
import com.application.interesting_tweets.daos.TweetIdDao;
import com.application.interesting_tweets.daos.TwitterDao;
import com.application.interesting_tweets.models.Regex;
import com.application.interesting_tweets.services.RegexMatchService;

import java.util.HashMap;
import java.util.Map;

public class InterestingTweetsApplication {

    public static void main(String[] args) {

        //Set Up Connections
        MongoConnection mongoConnection = new MongoConnection();
        RedisConnection redisConnection = new RedisConnection();
        KafkaConnection kafkaConnection = new KafkaConnection();
        ElasticsearchConnection elasticsearchConnection = new ElasticsearchConnection();


        //Initialize DAOs for access to consumers
        TweetIdDao tweetIdDao = new TweetIdDao(redisConnection.jedisPool);
        TwitterDao twitterDao = new TwitterDao();

//        //If you want to add regex in Mongodb use he below block
//        Regexdao regexdao = new Regexdao(mongoConnection.mongoClient, mongoConnection.REGEX_DATABASE, mongoConnection.REGEX_COLLECTION);
//        Map<String,String> regex = new HashMap<>();
//        regex.put("id", "Contains Afghanistan");
//        regex.put("expression", "^.*Afghanistan.*$");
//        regexdao.addRegex(new Regex(regex));

        //Start the services
        RegexMatchService regexMatchService = new RegexMatchService(mongoConnection.mongoClient, mongoConnection.REGEX_DATABASE, mongoConnection.REGEX_COLLECTION);

    }

}
