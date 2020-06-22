package com.zygateley.resume;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.time.*;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.*;

/**
 * SQLite
 * 
 * Create new SQLite connection object. 
 * May pass URL to specific SQLite CV database.
 * 
 * @author Zachary Gateley
 *
 */
public class SQLite {
	private Connection connection;
	private String url = "jdbc:sqlite:/path/to/working/directory/CV.db";

	/**
	 * Section
	 * 
	 * Mirrors a single record in the top-level tables.
	 * At the time of writing, these are Education, Experience, and Skills.
	 * For your java bean, extend this class as *bean*.Section
	 * and *bean*.Section.Detail
	 * 
	 * @author Zachary Gateley
	 *
	 */
	public static abstract class Section {
		/**
		 * 
		 * Mirrors a single record in the second-level (detail) tables.
		 * Extend this class from your *bean*.Section class implementation.
		 * 
		 * @author Zachary Gateley
		 *
		 */
		public static abstract class Detail {
			/**
			 * fields are stored as String, String no matter what.
			 * Any post processing must be done on the other side.
			 */
			protected HashMap<String, String> fields;
			/**
			 * Detail
			 * 
			 * Constructor initializes HashMap for fields.
			 */
			public Detail() {
				this.fields = new HashMap<String, String>();
			}
			/**
			 * Gets the value of a single field in this.fields.
			 * Called from jsp files on output. 
			 * 
			 * @param fieldName String fieldName as inserted by .addFields
			 * @return
			 */
			public String getField(String fieldName) {
				String fieldValue = this.fields.get(fieldName);
				if (fieldValue == null) fieldValue = "";
				return fieldValue;
			}
			/**
			 * addFields
			 * 
			 * Populates this.fields with fieldNames from ResultSet.
			 * 
			 * @param results ResultSet from SQL statement execution
			 * @param fieldNames String array of fields from ResultSet
			 * @throws SQLException
			 */
			protected void addFields(ResultSet results, String...fieldNames) throws SQLException {
				SQLite.Section.addFields(results, this.fields, fieldNames);
			}
		}
		
		/**
		 * fields are stored as String, String no matter what.
		 * Any post processing must be done on the other side.
		 */
		protected HashMap<String, String> fields;
		/**
		 * Override addDetail to add *.Section.Detail from your
		 * specific class.
		 */
		protected ArrayList<SQLite.Section.Detail> details;
		/**
		 * Section constructor
		 * 
		 * Constructor initializes fields HashMap and details ArrayList.
		 */
		public Section() {
			this.fields = new HashMap<String, String>();
			this.details = new ArrayList<SQLite.Section.Detail>();
		}
		/** addDetail
		 * 
		 * Must override for valid option. See Education.java, etc for examples.
		 * 
		 * @param results: ResultSet from an SQL statement execution
		 */
		public void addDetail(ResultSet results) throws SQLException {}
		/**
		 * getDetails
		 * 
		 * Your *.details should contain instances of 
		 * SQLite.Section.Detail subclasses to be useful.
		 * 
		 * @return ArrayList of *.Section.Detail
		 */
		public ArrayList<SQLite.Section.Detail> getDetails() {
			return this.details;
		}
		/**
		 * getField 
		 * 
		 * Called from jsp files
		 * 
		 * @param fieldName
		 * @return specifically a String copy of the database field value
		 */
		public String getField(String fieldName) {
			String fieldValue = this.fields.get(fieldName);
			if (fieldValue == null) fieldValue = "";
			return fieldValue;
		}
		/**
		 * addFields
		 * 
		 * Instance method calls class method with instance fields pointer.
		 * 
		 * @param results ResultSet from SQL statement execution
		 * @param fieldNames String array of field names from the ResultSet
		 * @throws SQLException
		 */
		protected void addFields(ResultSet results, String...fieldNames) throws SQLException {
			addFields(results, this.fields, fieldNames);
		}
		/**
		 * addFields
		 * 
		 * Class method updates HashMap fields parameter
		 * by adding each field from ResultSet (by String from fieldNames).
		 * Created as static method so that SQLite.Section.Detail
		 * could use the same code. It's a little questionable. I know. 
		 * 
		 * @param results ResultSet from SQL statement execution
		 * @param fields pointer to HashMap whose fields to update
		 * @param fieldNames String array of field names from the ResultSet
		 * @throws SQLException
		 */
		protected static void addFields(ResultSet results, HashMap<String, String> fields, String... fieldNames) throws SQLException {
			Arrays.asList(fieldNames)
				.stream()
				.forEach(s -> {
					try {
						fields.put(s, results.getString(s));
					}
					catch (SQLException e) {}
				});
		}
	};
	
	/**
	 * SQLite
	 * 
	 * Constructor create a new connection object 
	 * Use default database
	 */
	public SQLite() {}
	
	/**
	 * SQLite
	 * 
	 * Constructor create a new connection object
	 * @param url new database url e.g. "jdbc:sqlite:/.....db"
	 */
	public SQLite(String url) {
		this.url = url;
	}
	
	/**
	 * connect
	 * 
	 * Connect to the established CV SQLite database 
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
    /**
     * close
     * 
     * Closes connection opened by (SQLite).connect()
     */
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
     * createStatement
     * 
     * this.connection is private, so must create
     * statements by method calls
     * 
     * @return new created Statement or null
     * @throws SQLException
     */
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
    /**
     * prepareStatement
     * 
     * this.connection is private, so must create
     * prepared statements by method calls.
     * 
     * @param statement prepared statement string
     * @return new prepared statement
     * @throws SQLException
     */
    public PreparedStatement prepareStatement(String statement) throws SQLException {
    	if (this.connection != null) {
    		try {
    			return this.connection.prepareStatement(statement);
    		}
    		catch (SQLException ex) {
    			System.out.println(ex.getMessage());
    		}
    	}
    	return null;
    }
    

	/**
	 * convertStringToDate
	 * 
	 * Dates are stored in SQLite as yyyy-mm
	 * Convert to date string Mon yyyy
	 * 
	 * @param date string yyyy-mm
	 * @returns date string Mon yyyy
	 */
	public static String formatSQLiteDate(String sqliteDate) {
		int yyyy = Integer.parseInt(sqliteDate.substring(0, 4));
		int mm = Integer.parseInt(sqliteDate.substring(5, 7));
		LocalDate date = LocalDate.of(yyyy, mm, 1);
		Month mon = date.getMonth();
		return mon.getDisplayName(TextStyle.FULL, Locale.US) + " " + yyyy;
	}
	
	/**
	 * getIncludedById
	 * 
	 * Called by getList to find from request parameters
	 * which IDs from the current table are to be included
	 * when writing the resume. 
	 * 
	 * @param request HTTP request ultimately from Servlet
	 * @param includedId HTML form ID for checkboxes in question
	 * @return ArrayList of included IDs as integers
	 */
	private static ArrayList<Integer> getIncludedById(
			HttpServletRequest request,
			final String includedId
			) {
		String[] parameters = request.getParameterValues(includedId);
		ArrayList<Integer> includedById = null;
		if (parameters != null && parameters.length > 0) {
			includedById = new ArrayList<Integer>(
					Arrays.stream(parameters)
					.map(s -> Integer.valueOf(s))
					.collect(Collectors.toList())
			);			
		}
		return includedById;
	}
	/**
	 * getList
	 * 
	 * Return a list of *.Section that represents
	 * all of the data to be output on this resume.
	 * 
	 * @param request HTTP request from Servlet
	 * @param response HTTP response from Servlet
	 * @param QUERY_FULL appropriate SQL query. See TopLevel implementation for more details. 
	 * @param includedId HTML form checkbox name to find which table IDs are included in output
	 * @param includedDetailId HTML form checkbox name to find which table IDs are included in Detail output
	 * @param SectionType Class from calling *.java, *.Section extends SQLite.Section 
	 * 
	 * @return ArrayList of Sections
	 * 		Each section has an ArrayList of details.
	 * 		Even though the SQL tables are join left with multiple records of top-level ID,
	 * 		there is only one Section returned for each top-level ID.  
	 * 
	 * @throws SQLException
	 * @throws IOException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws ClassNotFoundException
	 */
	public static ArrayList<? extends SQLite.Section> getList(
			HttpServletRequest request, 
			HttpServletResponse response,
			final String QUERY_FULL,
			final String includedId,
			final String includedDetailId,
			final Class<? extends SQLite.Section> SectionType
			) throws SQLException, 
					IOException, 
					InstantiationException, 
					IllegalAccessException, 
					IllegalArgumentException, 
					InvocationTargetException, 
					NoSuchMethodException, 
					SecurityException, 
					ClassNotFoundException {
		// Connect to database
		SQLite database = new SQLite();
		database.connect();
		
		// Return array
		ArrayList<SQLite.Section> list = null;
		
		try {		
			// Find which first-level items should be output
			ArrayList<Integer> includedById = SQLite.getIncludedById(request, includedId);
			// Find which second-level items should be output
			ArrayList<Integer> includedDetailsById = null;
			if (!includedDetailId.isBlank()) {
				includedDetailsById = SQLite.getIncludedById(request, includedDetailId);
			}
	
			// Return array
			list = new ArrayList<SQLite.Section>();
			
			Statement statement = database.createStatement();
			ResultSet results = statement.executeQuery(QUERY_FULL);
			
			try {
				int topId = -1;
				SQLite.Section section = null;
				while (results.next()) {
					// Left join of ID <- DETAIL_ID
					int id = results.getInt("ID");
					int detail_id = results.getInt("DETAIL_ID");
					if (includedById == null || !includedById.contains(id) || 
							(includedDetailsById != null &&
							!includedDetailsById.contains(detail_id))) {
						continue;
					}
					if (topId != id) {
						// Ordered by ID
						topId = id;
						section = SectionType.getDeclaredConstructor(ResultSet.class).newInstance(results);
						list.add(section);
					}
					// Add this detail to the selected skill
					section.addDetail(results);
				}
			}
			finally {
				if (statement != null) {
					statement.close();
				}
			}
		}
		finally {
			if (database != null) {
				database.close();
			}
		}
		
		return list;
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
