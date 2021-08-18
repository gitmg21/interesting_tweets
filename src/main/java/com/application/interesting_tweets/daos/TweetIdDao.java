package com.application.interesting_tweets.daos;


/*
 * DAO Class Implementation to interact with the Redis using @ POJO
 * */

import com.application.interesting_tweets.models.Tweet;
import redis.clients.jedis.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TweetIdDao {

    private final JedisPool jedisPool;

    private static final long globalMaxLength = 10000;
    private final String feedKey;
    private final String sortedSetKey;

    public TweetIdDao(JedisPool jedisPool){
        this.jedisPool = jedisPool;
        feedKey = RedisSchema.getTweetFeedKey();
        sortedSetKey = RedisSchema.getSortedSetKey();
    }
    /**
     * Inserts the Tweet's text and Id in the Redis Stream.
     * Also, inserts Tweet's Id in a sorted set with timestamp as key
     * Note, both the data structures are trimmed to globalMaxLength
     * zremrangeByRank will be O(1 + log(n)) and also n < globalMaxLength
     * TODO: To discuss if creating a Lua script with checking ZCARD> globalMaxLength and then only doing ZREMRANGEBYRANK is better
     * According to my understanding if ZCARD < globalMaxLength performance is 0(log(n)) where n<globalMaxLength
     *
     * @param @Tweet to insert
     */
    public void insertInterestingTweet(Tweet tweet) {

        try (Jedis jedis = jedisPool.getResource()){
            Long ts = ZonedDateTime.now().toInstant().toEpochMilli();

            Pipeline p = jedis.pipelined();

            p.xadd(feedKey, StreamEntryID.NEW_ENTRY, tweet.toMap(), globalMaxLength, true);
            p.zadd(sortedSetKey, ts, tweet.getId());
            p.zremrangeByRank(sortedSetKey, 0, -globalMaxLength);

            p.sync();
        }
    }

    /**
     * Method to return the most recent Interesting Tweets (Stream Ids are based on time in Redis)
     * TODO: If limit is large or even as a good performance practice in Redis for Production, convert to SCAN
     *
     * @return List of the most recent interesting tweets
     */
    public List<Tweet> getMostRecentInterestingTweets(int limit){
        List<Tweet> tweets = new ArrayList<>(limit);
        try (Jedis jedis = jedisPool.getResource()) {
            List<StreamEntry> entries = jedis.xrevrange(feedKey, null,
                    null, limit);
            for (StreamEntry entry : entries) {
                tweets.add(new Tweet(entry.getFields()));
            }
            return tweets;
        }
    }

    /**
     * Method that checks if the parent is an interesting tweet
     * TODO: Seems like a design issue, To Discuss!
     *
     * @return List of the most recent interesting tweets
     */
    public boolean checkIfInterestingParent(Tweet tweet) {
        //Get the referenced_tweets in a List of POJOs, then traverse and look for "type": "replied_to" and search the
        //corresponding "id" in the zset, if yes return true else false


        //Create a Lua script


        return false;
    }


}
