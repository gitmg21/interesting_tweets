package com.application.interesting_tweets.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Tweet {

    private String id;

    private String text;

    private String conversation_id;

    private String referenced_tweets;

    public Tweet() {};

    public Tweet(Map<String, String> map) {
        this.id = map.get("id");
        this.text = map.get("text");
        this.conversation_id = map.get("conversation_id");
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("text")
    public String getText() {
        return text;
    }

    @JsonProperty("text")
    public void setText(String text) {
        this.text = text;
    }

    @JsonProperty("conversation_id")
    public String getConversation_id() {
        return conversation_id;
    }

    @JsonProperty("conversation_id")
    public void setConversation_id(String conversation_id) {
        this.conversation_id = conversation_id;
    }

    public String getReferencedTweets() {
        return referenced_tweets;
    }

    public void setReferencedTweets(String referencedTweets) {
        this.referenced_tweets = referencedTweets;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();

        map.put("id", id);
        map.put("text", text);
        map.put("conversation_id", conversation_id);
        //Do not include Referenced tweets as its a complex data sturcture
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tweet that = (Tweet) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(text, that.text) &&
                Objects.equals(conversation_id, that.conversation_id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, text, conversation_id);
    }

    @Override
    public String toString() {
        return "MeterReading{" +
                "id=" + id +
                ", text=" + text +
                ", conversation_id=" + conversation_id +
                ", referenced_tweets=" + referenced_tweets +
                '}';
    }
}
