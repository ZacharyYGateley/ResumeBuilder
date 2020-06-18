package com.zygateley.resume;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.*;


public class Education {
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
	
	// Mirrors a single record in Education table
	public static class Section {
		// Mirrors a single record in EducationDetail table
		public static class Detail {
			public final int ID;
			public final int EDUCATION_ID;
			public final String END_DATE;
			public final boolean IS_EXPECTED;
			public final String DEGREE_TYPE;
			public final String DEGREE_SUBJECT;
			public final String GPA;
			public final String LATIN_HONORS;
			public final String DETAILS;
			public Detail(ResultSet results) throws SQLException {
				this.ID = results.getInt("ID");
				this.EDUCATION_ID = results.getInt("EDUCATION_ID");
				this.END_DATE = results.getString("END_DATE");
				this.IS_EXPECTED = results.getInt("IS_EXPECTED") == 1;
				this.DEGREE_TYPE = results.getString("DEGREE_TYPE");
				this.DEGREE_SUBJECT = results.getString("DEGREE_SUBJECT");
				this.GPA = results.getString("GPA");
				this.LATIN_HONORS = results.getString("LATIN_HONORS");
				this.DETAILS = results.getString("DETAILS");
			}
		}
		
		public final int ID;
		public final String ORGANIZATION;
		public final String LOCATION;
		public ArrayList<Education.Section.Detail> details;
		public Section(ResultSet results) throws SQLException {
			this.ID = results.getInt("ID");
			this.ORGANIZATION = results.getString("ORGANIZATION");
			this.LOCATION = results.getString("LOCATION");
			this.details = new ArrayList<Education.Section.Detail>();
		}
		
		public void addDetail(ResultSet results) throws SQLException {
			Education.Section.Detail newDetail = new Education.Section.Detail(results);
			this.details.add(newDetail);
		}
	}
	
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
		
	public static ArrayList<Education.Section> getEducationList(
			HttpServletRequest request, 
			HttpServletResponse response
			) throws SQLException, IOException {

		// Connect to database
		SQLite database = new SQLite();
		database.connect();
		
		// Return array
		ArrayList<Education.Section> educationList = null;
		
		try {		
			// Find which education items should be output
			ArrayList<Integer> includedEducationById = new ArrayList<>(
					Arrays.stream(request.getParameterValues("EDUCATION_ID"))
					.map(s -> Integer.parseInt(s))
					.collect(Collectors.toList())
					);
	
			// Return array
			educationList = new ArrayList<Education.Section>();
			
			Statement statement = database.createStatement();
			ResultSet results = statement.executeQuery(Education.QUERY_FULL);
			
			try {
				int educationId = -1;
				Education.Section educationItem = null;
				while (results.next()) {
					int id = results.getInt("ID");
					if (!includedEducationById.contains(id)) {
						continue;
					}
					if (educationId != id) {
						// Ordered by ID
						educationId = id;
						educationItem = new Education.Section(results);
						educationList.add(educationItem);
					}
					// Add this detail to the selected education item
					educationItem.addDetail(results);
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
		
		return educationList;
	}
}
