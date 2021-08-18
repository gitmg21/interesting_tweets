package com.application.interesting_tweets.daos;

import com.application.interesting_tweets.KeyHelper;

public class RedisSchema {

    // tweets:feed
    // Redis type: stream
    static String getTweetFeedKey() {
        return KeyHelper.getKey("tweets:feed");
    }

    // tweets:sortedSet
    // Redis type: sortedSet
    static String getSortedSetKey() {
        return KeyHelper.getKey("tweets:sortedSet");
    }


}
