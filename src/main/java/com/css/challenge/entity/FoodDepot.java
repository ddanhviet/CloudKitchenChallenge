package com.css.challenge.entity;

import lombok.Data;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

@Data
public class FoodDepot {

  private static final Map<String, Integer> MAX_CAPACITY = Map.of(
      Order.COLD, 6,
      Order.HOT, 6,
      Order.ROOM, 12
  );

  PriorityQueue<Order> cooler;
  PriorityQueue<Order> heater;
  PriorityQueue<Order> shelf;

  Map<String, PriorityQueue<Order>> orderMap;

  public FoodDepot() {
    cooler = new PriorityQueue<>();
    heater = new PriorityQueue<>();
    shelf = new PriorityQueue<>();

    orderMap = Map.of(
        Order.COLD, cooler,
        Order.HOT, heater,
        Order.ROOM, shelf
    );
  }

  private Comparator<Order> getComparator() {
    return Comparator
          .comparing(Order::getFreshness)
          .thenComparing(Order::getLastUpdated);
  }

  public void moveOrderToSCooler(Order order) {
    removeOrder(order);
    cooler.add(order);
  }

  public void moveOrderToHeater(Order order) {
    removeOrder(order);
    heater.add(order);
  }

  public void moveOrderToShelf(Order order) {
    removeOrder(order);
    shelf.add(order);
  }

  public void pickUpOrder(Order order) {
    removeOrder(order);
  }

  public void storeOrder(Order order) {
    // find the most optimal location / then decide where to store
    var preferredLocation = orderMap.get(order.getTemp());
    if (preferredLocation == null)
      throw new IllegalArgumentException("Invalid order temperature");

    if (preferredLocation.size() < MAX_CAPACITY.get(order.getTemp())) {
      preferredLocation.add(order);
    } else {
      // have to discard regardless
      var discardLocation = orderMap.get(order.getTemp());
      discardLocation.poll();

      shelf.add(order);
    }
  }

  private void removeOrder(Order order) {
    cooler.remove(order);
    heater.remove(order);
    shelf.remove(order);
  }
}
