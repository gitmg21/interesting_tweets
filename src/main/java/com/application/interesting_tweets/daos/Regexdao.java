package com.application.interesting_tweets.daos;

import com.application.interesting_tweets.models.Regex;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

/*
 * DAO Class Implementation to interact with the MongoDB Regex database using @Regex POJO
 * */
public class Regexdao {

    private final String REGEX_DATABASE;
    private final String REGEX_COLLECTION;

    private MongoDatabase db;
    private MongoClient mongoClient;
    private MongoCollection<Regex> regexCollection;
    private CodecRegistry pojoCodecRegistry;

    public Regexdao(MongoClient mongoClient, String databaseName, String collectionName) {
        this.mongoClient = mongoClient;
        REGEX_DATABASE = databaseName;
        REGEX_COLLECTION = collectionName;
        this.db = this.mongoClient.getDatabase(REGEX_DATABASE);

        this.pojoCodecRegistry =
                fromRegistries(
                        MongoClientSettings.getDefaultCodecRegistry(),
                        fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        this.regexCollection =
                db.getCollection(REGEX_COLLECTION, Regex.class).withCodecRegistry(pojoCodecRegistry);
    }

    /**
     * Returns the Regex Map directly from the database for any service (for better performance on service side).
     *
     * @return the updated Regex HashMap
     */
    public HashMap<String, Pattern> createRegexMapFromMongoDB(){

        HashMap<String, Pattern> regexMap = new HashMap<>();
        for (Regex regex: regexCollection.find()){
            regexMap.put(regex.getId(),Pattern.compile(regex.getExpression()));
        }
        return regexMap;
    }

    /**
     * Adds the new regex and returns true if valid i.e. regex id is not null and regex id doesn't already exist, else returns false
     *
     * @return boolean denoting succesful operation
     */
    public boolean addRegex(Regex regex){
        if(regex.getId() == null)
            return false;

        Regex toUpdate = regexCollection.find(Filters.eq("_id", regex.getId())).limit(1).iterator().tryNext();

        if (toUpdate != null) {
            return false;
        }
        try {
            regexCollection.insertOne(regex);
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;

    }

    /**
     * Deletes the new regex and returns true if possible i.e. regex id is not null and regex id already exists, else returns false
     *
     * @return boolean denoting succesful operation
     */
    public boolean deleteRegex(Regex regex){
        try {
            regexCollection.deleteOne(Filters.eq("_id", regex.getId()));
            return true;
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
