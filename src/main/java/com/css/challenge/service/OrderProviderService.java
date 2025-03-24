package com.css.challenge.service;

import com.css.challenge.entity.Order;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;

public class OrderProviderService {

  private Queue<Order> orders;

  public OrderProviderService(List<Order> incomingOrders) {
    orders = new ConcurrentLinkedDeque<>(incomingOrders);
  }

  public Order getNextOrder() {
    return orders.poll();
  }
}
