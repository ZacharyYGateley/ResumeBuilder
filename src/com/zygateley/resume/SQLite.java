package com.zygateley.resume;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class SQLite {
	private Connection connection;
	private String url = "jdbc:sqlite:/CV.db";
	
	/**
	 * Create a new connection object 
	 * Use default database
	 */
	public SQLite() {
	}
	/**
	 * Create a new connection object
	 * @param url new database url e.g. "jdbc:sqlite:/.....db"
	 */
	public SQLite(String url) {
		this.url = url;
	}
	
	/**
	 * Connect to the CV database 
	 */
    public void connect() {
    	// Driver, type, database location
        try {
            // create a connection to the database
            this.connection = DriverManager.getConnection(this.url);
            
            System.out.append("Connection to SQLite has been established.\n");
            System.out.append(this.url + "\n");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());   
        }
    }
    
    public void close() {
    	try {
            if (this.connection != null) {
                this.connection.close();
                System.out.append("Connection to SQLite database has been closed.\n");
                System.out.append(this.url + "\n");
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    
    /**
     * main
     * 
     * Tests connection to CV (or other) database
     * 
     * @param args the command line arguments
     * @throws ClassNotFoundException
     */
    public static void main(String[] args) {
    	SQLite test = new SQLite("jdbc:sqlite:C:\\sqlite\\db\\chinook.db");
    	test.connect();
    	test.close();
    	System.out.flush();
	}
}