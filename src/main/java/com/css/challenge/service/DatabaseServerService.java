package com.css.challenge.service;

import org.hsqldb.Server;

public class DatabaseServerService {

  public void start() {
    Server server = new Server();
    server.setDatabaseName(0, "mainDb");
    server.setDatabasePath(0, "mem:mainDb");
    server.setDatabaseName(1, "standbyDb");
    server.setDatabasePath(1, "mem:standbyDb");
    server.setPort(9001); // this is the default port
    server.start();
  }
}
