package com.css.challenge;

import com.css.challenge.entity.FoodDepot;
import lombok.extern.slf4j.Slf4j;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
public class KitchenApplication {

  static final int DEFAULT_MIN_PICKUP = 4000000;
  static final int DEFAULT_MAX_PICKUP = 8000000;

  private static final int MILLISECOND = 1000;

  private static final Random random = new Random();

  private int rate;
  private int minPickup = DEFAULT_MIN_PICKUP;
  private int maxPickup = DEFAULT_MAX_PICKUP;

  private Thread pickupThread;
  private Thread receiveThread;
  private Thread rateLimitThread;

  // take in Problem?
  public KitchenApplication(int rate, int minPickUp, int maxPickup) {
    this.rate = rate;
    this.minPickup = minPickUp;
    this.maxPickup = maxPickup;
  }

  public void start() {
    // thread for picking up order
    pickupThread = Thread.startVirtualThread(this::pickupOrder);
    // thread for processing orders
    receiveThread = Thread.startVirtualThread(this::receiveOrders);
    // thread for reset rate limiting
    rateLimitThread = Thread.startVirtualThread(this::rateLimit);
  }

  /*
    per thread:
    - can shut down
    - log
    - catch exception
   */
  private void standardThreadProcedure() {
    try {

    } catch (Exception e) {

    }
  }

  /*
    if rate is good, get next message
    have x number of thread / receive order
   */
  private void receiveOrders(ExecutorService executorService) {
    try {
      var time = System.currentTimeMillis();
      while (!Thread.currentThread().isInterrupted()) { // while not shutdown
        for (int i=0; i<rate; i++) {
          // receive order

          // process order
        }
      }
      var endTime = System.currentTimeMillis();
      Thread.sleep(MILLISECOND - (endTime - time)); // 1s
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

    FoodDepot foodDepot = new FoodDepot();
    updateFreshness(foodDepot);
  }

  private void pickupOrder() {
    try {
      // every second?
      // go through all orders and random if the order is getting picked up (that retains the randomness)
      // from minPickUp to maxPickup
      // there is a timestamp
      var pickUp = random.nextBoolean();
      if (pickUp) {
        // add to action
      }
      Thread.sleep(random.nextInt(minPickup, maxPickup + 1)); // no this is per order
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }

  }

  // no need
  private void updateFreshness(FoodDepot foodDepot) {
    try {
      Thread.sleep(1000); // 1s
    } catch (InterruptedException ie) {
      throw new RuntimeException(ie);
    }
  }

  private void rateLimit() {
    try {
      // reset counter
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
