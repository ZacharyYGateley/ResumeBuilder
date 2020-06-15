package com.zygateley.resume;


import java.sql.*;


public class SQLite {
	private Connection connection;
	private String url = "jdbc:sqlite:C:\\Users\\Zachary Gateley\\Dropbox\\Languages\\Java\\Resume\\CV.db";
	
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
    	System.out.println("Current working directory: " + System.getProperty("user.dir"));
    	// Driver, type, database location
        try {
            // create a connection to the database
            this.connection = DriverManager.getConnection(this.url);
            
            System.out.append("Connection to SQLite has been established.\n");
            System.out.append(this.url + "\n");
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            System.out.println("Please make sure that the driver is installed,");
            System.out.println("and the current working directory is the project directory.");
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
    
    public Statement createStatement() throws SQLException {
    	if (this.connection != null) {
    		try {
    			return this.connection.createStatement();
    		}
    		catch (SQLException ex) {
    			System.out.println(ex.getMessage());
    		}
    	}
    	return null;
    }
    
    public PreparedStatement prepareStatement(String stmt) throws SQLException {
    	if (this.connection != null) {
    		try {
    			return this.connection.prepareStatement(stmt);
    		}
    		catch (SQLException ex) {
    			System.out.println(ex.getMessage());
    		}
    	}
    	return null;
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