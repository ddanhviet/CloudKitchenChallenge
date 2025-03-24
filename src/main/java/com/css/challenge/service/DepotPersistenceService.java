package com.css.challenge.service;

import com.css.challenge.entity.Order;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * order table has current order values
 * depot table has order that is stored
 * action table has order action details
 */
public class DepotPersistenceService {

  static final String TABLE_ACTION = "action";
  static final String TABLE_DEPOT = "depot";
  static final String TABLE_ORDER = "order";

  private static final int MAX_CONNECTIONS = 10;

  private Queue<Connection> connectionPool;
  private List<Connection> inuseConnections;

  // TODO: close connections? or reuse them?
  // inuseConnection thread safe?
  // TODO: use hardcoded user and pwd
  public DepotPersistenceService(String url, String user, String pwd) throws SQLException {
    connectionPool = new ArrayBlockingQueue<>(MAX_CONNECTIONS);
    inuseConnections = new ArrayList<>();
    for (int i = 0; i < MAX_CONNECTIONS; i++) {
      connectionPool.add(DriverManager.getConnection(url, user, pwd));
    }
  }

  public void addOrder(Order order, String option) throws SQLException {

  }

  // TODO: factor out connection part
  public void storeOrder(String orderId, String option) throws SQLException {
    var connection = connectionPool.poll();
    inuseConnections.add(connection);
    var statement = connection.createStatement();

    String insertSQL = "INSERT INTO " + TABLE_DEPOT + " (id, option) VALUES (" + orderId + ", " + option + ")";
    statement.executeUpdate(insertSQL);

    inuseConnections.remove(connection);
    connectionPool.add(connection);
  }

  // skeleton
  public List<Order> getOrders() throws SQLException {
    var connection = connectionPool.poll();
    var statement = connection.createStatement();

    String querySQL = "";
    ResultSet rs = statement.executeQuery(querySQL);
    List<Order> orders = new ArrayList<>();
    while (rs.next()) {
      orders.add(new Order(rs.getString("id"), rs.getString("name"), rs.getString("temp"), rs.getInt("freshness")));
      System.out.println("ID: " + rs.getInt("id") +
            ", Name: " + rs.getString("name"));
    }

    inuseConnections.remove(connection);
    connectionPool.add(connection);

    return orders;
  }

  public void pickupOrder(String orderId) throws SQLException {
    var connection = connectionPool.poll();
    inuseConnections.add(connection);
    var statement = connection.createStatement();

    var deleteSQL = "DELETE FROM " + TABLE_DEPOT
        + "WHERE orderId" + " = " + orderId + ";";
    statement.executeUpdate(deleteSQL);

    var actionSQL = "INSERT INTO " + TABLE_ACTION + " (timestamp, order_id, action)"
        + "VALUES (" + System.currentTimeMillis() + ", "+ orderId + ", 'pickup');";
    statement.executeUpdate(actionSQL);

    inuseConnections.remove(connection);
    connectionPool.add(connection);
  }

  public void removeExpiredOrders(String option) throws SQLException {
    var connection = connectionPool.poll();
    inuseConnections.add(connection);
    var statement = connection.createStatement();

    String deleteSQL = "DELETE FROM " + TABLE_DEPOT
          + " JOIN " + TABLE_ORDER + " on orders.id = depot.id"
          + " WHERE " + TABLE_DEPOT + ".location = " + option
          + " AND " + TABLE_ORDER + ".freshness <= 0;";
    statement.executeUpdate(deleteSQL);

    var actionSQL = "INSERT INTO " + TABLE_ACTION + " (timestamp, order_id, action)"
        + "VALUES (CURRENT_TIMESTAMP, 101, 'pickup');";
    // TODO: action for all removed orders

    inuseConnections.remove(connection);
    connectionPool.add(connection);
  }

  public void updateFoodFreshness() throws SQLException {
    var connection = connectionPool.poll();
    var statement = connection.createStatement();

    String querySQL = ""; // TODO: decrease all food freshness by 1 second (which is measured in milliseconds)
    ResultSet rs = statement.executeQuery(querySQL);

    inuseConnections.remove(connection);
    connectionPool.add(connection);

  }
}
