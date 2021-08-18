package com.application.interesting_tweets.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

import java.time.ZonedDateTime;
import java.util.Map;

/*
 * POJO denoting Regex JSON list stored in MongoDB
 * */
public class Regex {

    @JsonProperty("_id")
    private String id;

    private String expression;

    public Regex() {}

    public Regex(Map<String, String> map) {
        this.id = map.get("id");
        this.expression = map.get("expression");
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String text) {
        this.expression = text;
    }

}
