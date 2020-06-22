package com.zygateley.resume;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

import javax.servlet.http.*;


public class Experience implements TopLevel {
	private static final String QUERY = 
			"SELECT e.*, eh.HEADER " + 
			"FROM Experience AS e JOIN ExperienceHeader AS eh " +
			"ON e.HEADER_ID=eh.ID " +
			"ORDER BY eh.SORT_ORDER, e.SORT_ORDER DESC";
	private static final String QUERY_FULL = 
			"SELECT e.*, \r\n" + 
			"ed.ID as DETAIL_ID, e.ID, " +
			"ed.SORT_ORDER as ED_SORT_ORDER, ed.TEXT, \r\n" + 
			"eh.SORT_ORDER as EH_SORT_ORDER, eh.HEADER \r\n" + 
			"FROM ExperienceDetail AS ed JOIN Experience AS e \r\n" + 
			"ON e.ID=ed.EXPERIENCE_ID \r\n" + 
			"JOIN ExperienceHeader AS eh \r\n" + 
			"ON eh.ID=e.HEADER_ID \r\n" + 
			"ORDER BY eh.SORT_ORDER, e.SORT_ORDER DESC, ed.SORT_ORDER ASC";
	
	/** 
	 * Section extends SQLite.Section
	 * 
	 * Mirrors a single record in Experience table.
	 * See SQLite.Section for more details.
	 * 
	 * @author Zachary Gateley
	 *
	 */
	public static class Section extends SQLite.Section {
		/**
		 * Detail extends SQLite.Section.Detail
		 * 
		 * Mirrors a single record in ExperienceDetail table.
		 * See SQLite.Section.Detail for more information.
		 * 
		 * @author Zachary Gateley
		 *
		 */
		public static class Detail extends SQLite.Section.Detail {
			/**
			 * Detail
			 * 
			 * Constructor adds current line from ResultSet 
			 * from SQL call to CV database using QUERY_FULL.
			 * 
			 * @param results ResultSet from statement execution
			 * @throws SQLException
			 */
			public Detail(ResultSet results) throws SQLException {
				super();
				this.addFields(results, "TEXT");
			}
		}

		/**
		 * Section
		 * 
		 * super() creates private: 
		 * 	HashMap<String, String> this.fields
		 *  ArrayList<*.Detail> 	this.details
		 * 
		 * @param results SQL ResultSet from QUERY_FULL statement call. Current line will instantiate the Section. 
		 * @throws NumberFormatException
		 * @throws SQLException
		 */
		public Section(ResultSet results) throws NumberFormatException, SQLException {
			super();
			// Populates this.fields
			this.addFields(
					results,
					"ID", "HEADER_ID", "HEADER", "ORGANIZATION", "LOCATION",
					"TITLE", "URL", "START_DATE", "END_DATE"
			);
		}
		
		@Override
		/**
		 * addDetail
		 * 
		 * Must include results with pointer to current line
		 * from SQL statement call of QUERY_FULL.
		 * 
		 * @param results SQL ResultSet from statement execution.
		 */
		public void addDetail(ResultSet results) throws SQLException {
			Experience.Section.Detail newDetail = new Experience.Section.Detail(results);
			this.details.add(newDetail);
		}
	}
	
	/**
	 * writeFormOptions
	 * 
	 * Called from Form Servlet.
	 * Writes checkboxes for available output options.
	 * 
	 * @param request HTTP request from Servlet
	 * @param response HTTP response from Servlet
	 * @param database SQLite object with open connection
	 * @param out stream to which to write output
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void writeFormOptions(
			HttpServletRequest request, 
			HttpServletResponse response, 
			SQLite database, 
			PrintWriter out
			) throws IOException, SQLException {
		PreparedStatement statement = database.prepareStatement(Experience.QUERY);
		ResultSet results = statement.executeQuery();
		
		try {
			int headerId = -1;
			while (results.next()) {
				int HEADER_ID = results.getInt("HEADER_ID");
				if (headerId != HEADER_ID) {
					if (headerId > -1) {
						out.println("</tbody>");
						out.println("</table>");
					}
					
					String HEADER = results.getString("HEADER");
					out.println("<div class=\"Header\">" + HEADER + "</div>");
					out.println("<table border=0 cellpadding=0 cellspacing=0>");
					out.println("<tbody>");
					
					headerId = HEADER_ID;
				}
				out.println("<tr>");
				out.println("<td align=left valign=middle>");
				
				// Checkbox with label (organization)
				int ID = results.getInt("ID");
				String ORGANIZATION = results.getString("ORGANIZATION");
				out.println("<label class=\"SubHeader\">");
				out.println(String.format(
						"<input type=\"checkbox\" name=\"EXPERIENCE_ID\" value=\"%d\">%s</option>",
						ID, ORGANIZATION)
					);
				out.println("</label>");
				out.println("</td>");
				
				// Personal title at organization
				String TITLE = results.getString("TITLE");
				out.println("<td align=left valign=middle class=\"SubSubHeader\">&nbsp;&nbsp;");
				out.println(TITLE);
				out.println("</td>");
			}
			out.println("</tbody>");
			out.println("</table>");
		}
		catch (Exception err) {
			System.out.println("ERROR: " + err.getMessage());
		}
		finally {
			if (statement != null) {
				statement.close();
			}
		}
		
		return;
	}
	
	/**
	 * getExperienceList
	 * 
	 * Return a list of Education.Section that represents
	 * all of the data of this type to be output on this resume.
	 * Called from Experience.jsp
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
	public static ArrayList<? extends SQLite.Section> getExperienceList(
			HttpServletRequest request, 
			HttpServletResponse response
			) throws SQLException, 
					IOException, 
					InstantiationException, 
					IllegalAccessException, 
					IllegalArgumentException, 
					InvocationTargetException, 
					NoSuchMethodException, 
					SecurityException, 
					ClassNotFoundException {
		return SQLite.getList(
				request, 
				response, 
				Experience.QUERY_FULL, 
				"EXPERIENCE_ID", 
				"EXPERIENCE_DETAIL_ID",
				Experience.Section.class
				);
	}
}
