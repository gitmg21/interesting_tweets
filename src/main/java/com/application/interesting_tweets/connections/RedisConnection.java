package com.application.interesting_tweets.connections;

import  redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisConnection {

    private final String host = "localhost";

    private final int port = 6379;

    public JedisPool jedisPool;

    public RedisConnection(){
        jedisPool = (new JedisPool(new JedisPoolConfig(), host, port));
    }

}
