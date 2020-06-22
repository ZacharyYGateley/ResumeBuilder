package com.zygateley.resume;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

import javax.servlet.http.*;


public class Education implements TopLevel {
	private static final String QUERY = "SELECT Education.*, EducationDetail.END_DATE FROM Education LEFT OUTER JOIN EducationDetail ON Education.ID = EducationDetail.ID ORDER BY EducationDetail.END_DATE DESC";
	private static final String QUERY_FULL = 
			"SELECT e.*, " + 
			"ed.ID as DETAIL_ID, ed.EDUCATION_ID, " +
			"ed.END_DATE, ed.IS_EXPECTED, ed.DEGREE_TYPE, " +
			"ed.DEGREE_SUBJECT, ed.GPA, ed.LATIN_HONORS, " +
			"ed.DETAILS " +
			"FROM EducationDetail as ed " +
			"LEFT JOIN Education as e " +
			"ON e.ID=ed.EDUCATION_ID " +
			"ORDER BY ed.END_DATE DESC";
	
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
				this.addFields(
						results, 
						"ID", "EDUCATION_ID", "END_DATE", "IS_EXPECTED",
						"DEGREE_TYPE", "DEGREE_SUBJECT", "GPA", "LATIN_HONORS",
						"DETAILS"
						);
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
					"ID", "ORGANIZATION", "LOCATION"
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
			Education.Section.Detail newDetail = new Education.Section.Detail(results);
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
	public static void writeFormOptions(HttpServletRequest request, HttpServletResponse response, SQLite database, PrintWriter out) throws IOException, SQLException {
		Statement statement = database.createStatement();
		ResultSet results = statement.executeQuery(Education.QUERY);
		
		try {
			out.println("<div class=\"Header\">Education</div>");
			out.println("<table border=0 cellspacing=0 cellpadding=0>");
			out.println("<tbody>");
			out.println("<tr>");
			out.println("<td align=left valign=top>");
			while (results.next()) {
				int ID = results.getInt("ID");
				String ORGANIZATION = results.getString("ORGANIZATION");
				out.println("<label class=\"SubHeader\">");
				out.println(String.format(
						"<input type=\"checkbox\" name=\"EDUCATION_ID\" value=\"%d\">%s</option>",
						ID, ORGANIZATION)
					);
				out.println("</label>");
				out.println("<br />");
			}
			out.println("</td></tr></tbody></table>");
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
	 * getEducationList
	 * 
	 * Return a list of Education.Section that represents
	 * all of the data of this type to be output on this resume.
	 * Called from Education.jsp
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
	public static ArrayList<? extends SQLite.Section> getEducationList(
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
		new com.zygateley.resume.Education();
		return SQLite.getList(
				request, 
				response, 
				Education.QUERY_FULL, 
				"EDUCATION_ID", 
				"EDUCATION_DETAIL_ID",
				Education.Section.class
				);
	}
}
