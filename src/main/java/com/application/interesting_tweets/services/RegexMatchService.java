package com.application.interesting_tweets.services;

import com.application.interesting_tweets.daos.Regexdao;
import com.mongodb.client.MongoClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/*
 * A read-only Regex service to provide matching funtionalities over tweets.
 * This class can be easily merged with dao. The reason I separated is that problem statement implied
 * that the application can only read but not edit regex (which can be done by admin)
 * */

public class RegexMatchService {

    private Regexdao regexdao;

    private HashMap<String, Pattern> regexMap;

    public RegexMatchService(MongoClient mongoClient, String databaseName, String collectionName){
        regexdao = new Regexdao(mongoClient, databaseName, collectionName);
        regexMap = regexdao.createRegexMapFromMongoDB();
    }

    public void updateRegex(){
        regexMap = regexdao.createRegexMapFromMongoDB();
    }

    public boolean matchAgainstRegex(String text){

        if ((text == null) || text.isEmpty()){
            return false;
        }
        else{
            for (Pattern v : regexMap.values()){
                if (v.matcher(text).matches()) return true;
            }
            return false;
        }
    }

}
