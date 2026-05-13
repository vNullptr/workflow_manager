package org.workflowmanager.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseService{

    private static DatabaseService instance;

    private String url;
    private String user;
    private String pass;
    private Connection conn;

    private DatabaseService(){
        this.url = System.getenv("DB_URL");
        this.user = System.getenv("DB_USERNAME");
        this.pass = System.getenv("DB_PASSWORD");

        try {
            this.conn = DriverManager.getConnection(this.url, this.user, this.pass);
        } catch ( SQLException e){
            throw new RuntimeException("Connexion échouée : " + e.getMessage());
        }
    }

    public static DatabaseService getInstance(){
        if (instance == null){
            instance = new DatabaseService();
        }

        return instance;
    }

    public Connection getConn() { return this.conn; }
}
