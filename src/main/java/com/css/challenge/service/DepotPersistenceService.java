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

  //TODO: factor out connection part
  public void storeOrder(String orderId, String option) throws SQLException {
    var connection = connectionPool.poll();
    inuseConnections.add(connection);
    var statement = connection.createStatement();

    String insertSQL = "INSERT INTO " + TABLE_DEPOT + " (id, option) VALUES (" + orderId + ", " + option + ")";
    statement.executeUpdate(insertSQL);

    connectionPool.add(connection);
    inuseConnections.remove(connection);
  }

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
    return orders;
  }

  public void removeExpiredOrder(String orderId, String option) throws SQLException {
    var connection = connectionPool.poll();
    inuseConnections.add(connection);
    var statement = connection.createStatement();

    String deleteSQL = "DELETE FROM " + TABLE_DEPOT
          + " JOIN " + TABLE_ORDER + " on orders.id = depot.id"
          + " WHERE " + TABLE_DEPOT + ".location = " + option
          + " AND " + TABLE_ORDER + ".freshness <= 0";
    statement.executeUpdate(deleteSQL);

    connectionPool.add(connection);
    inuseConnections.remove(connection);
  }
}
