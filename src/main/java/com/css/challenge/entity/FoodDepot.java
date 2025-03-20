package com.css.challenge.entity;

import lombok.Data;

import java.util.Comparator;
import java.util.Map;
import java.util.PriorityQueue;

@Data
public class FoodDepot {

  private static final Comparator<Order> ORDER_COMPARATOR = Comparator
        .comparing(Order::getFreshness);
  private static final Map<String, Integer> MAX_CAPACITY = Map.of(
      Order.COLD, 6,
      Order.HOT, 6,
      Order.ROOM, 12
  );

  private PriorityQueue<Order> cooler;
  private PriorityQueue<Order> heater;
  private PriorityQueue<Order> shelf;

  Map<String, PriorityQueue<Order>> orderStorageMap;

  private Integer orderRemovalCount;
  private Boolean coolerLock, heaterLock, shelfLock;

  private int lockRemainThreshold;

  public FoodDepot(int lockRemainThreshold) {
    cooler = new PriorityQueue<>(ORDER_COMPARATOR);
    heater = new PriorityQueue<>(ORDER_COMPARATOR);
    shelf = new PriorityQueue<>(ORDER_COMPARATOR);

    orderStorageMap = Map.of(
        Order.COLD, cooler,
        Order.HOT, heater,
        Order.ROOM, shelf
    );

    this.lockRemainThreshold = lockRemainThreshold;
  }

  public void increaseOrderRemovalCount() {
    synchronized (orderRemovalCount) {
      orderRemovalCount++;
    }
  }

  public void accommodateNewOrders() {
    synchronized (orderRemovalCount) {
      for (int i = 0; i < orderRemovalCount; i++) {
      }
      orderRemovalCount = 0;
    }
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
    // don't have removal logic in here
    // try to store, if not possible, then add orderRemovalCount ?
    // every second, do something after pass threshold
    var preferredLocation = orderStorageMap.get(order.getTemp());
    if (preferredLocation == null)
      throw new IllegalArgumentException("Invalid order temperature");

    if (preferredLocation.size() >= lockRemainThreshold) {
      // lock
      synchronized (preferredLocation) {
        if (preferredLocation.size() < MAX_CAPACITY.get(order.getTemp())) {
          preferredLocation.add(order);
        } else {
          // have to discard regardless
          var discardLocation = orderStorageMap.get(order.getTemp());
          discardLocation.poll();

          shelf.add(order);
          //preferredLocation.add(order); ?
        }
      }
    }
  }

  private void removeOrder(Order order) {
    cooler.remove(order);
    heater.remove(order);
    shelf.remove(order);

    Thread.startVirtualThread();
  }

  //private void checkCoolerLock() {
  //  if (cooler.size() < lockRemainThreshold) {
  //    synchronized (coolerLock) {
  //      coolerLock = false;
  //    }
  //  }
  //}
}
