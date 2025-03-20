package com.css.challenge.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
/** Order is a json-friendly representation of an order. */
public class Order implements Comparable<Order> {

  public static final String COLD = "cold";
  public static final String HOT = "hot";
  public static final String ROOM = "room";

  private final String id; // order id
  private final String name; // food name
  private final String temp; // ideal temperature
  private final int freshness; // freshness in seconds

  @Setter
  private long lastUpdated;

  public Order(
      @JsonProperty("id") String id,
      @JsonProperty("name") String name,
      @JsonProperty("temp") String temp,
      @JsonProperty("freshness") int freshness) {
    this.id = id;
    this.name = name;
    this.temp = temp;
    this.freshness = freshness;
  }

  public static List<Order> parse(String json) throws JsonProcessingException {
    return new ObjectMapper().readValue(json, new TypeReference<List<Order>>() {});
  }

  public void updateLastUpdated() {
    setLastUpdated(System.currentTimeMillis());
  }

  @Override
  public String toString() {
    return "{id: " + id + ", name: " + name + ", temp: " + temp + ", freshness:" + freshness + " }";
  }

  @Override
  public int compareTo(Order o) {
    return this.freshness - o.freshness;
  }
}
