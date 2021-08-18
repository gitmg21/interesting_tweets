package com.application.interesting_tweets.connections;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

public class MongoConnection {

    private final String uri = "mongodb+srv://m001-student:m001-mongodb-basics@sandbox.yuhit.mongodb.net/myFirstDatabase?retryWrites=true&w=majority";

    public final MongoClient mongoClient;

    public final String REGEX_DATABASE = "sample_regex";

    public final String REGEX_COLLECTION = "regex";

    public MongoConnection(){

        ConnectionString connectionString = new ConnectionString(uri);
        MongoClientSettings clientSettings =
                MongoClientSettings.builder()
                        .applyConnectionString(connectionString)
                        .build();

        mongoClient = MongoClients.create(clientSettings);

    }
}
