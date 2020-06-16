package com.zygateley.resume;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.*;


public class Experience {
	private static final String QUERY = "SELECT * FROM Experience ORDER BY END_DATE DESC";
	private static final String QUERY_FULL = 
			"SELECT e.*, \r\n" + 
			"ed.SORT_ORDER, ed.TEXT \r\n" + 
			"FROM ExperienceDetail AS ed JOIN Experience AS e\r\n" + 
			"ON e.ID=ed.EXPERIENCE_ID \r\n" + 
			"ORDER BY e.END_DATE DESC, ed.SORT_ORDER ASC";
	
	// Mirrors a single record in Experience table
	public static class Section {
		// Mirrors a single record in ExperienceDetail table
		public static class Detail {
			public final String TEXT;
			public Detail(String text) {
				this.TEXT = text;
			}
		}
		
		public final int ID;
		public final String TYPE;
		public final String ORGANIZATION;
		public final String LOCATION;
		public final String TITLE;
		public final String URL;
		public final String START_DATE;
		public final String END_DATE;
		public ArrayList<Experience.Section.Detail> details;
		public Section(ResultSet results) throws SQLException {
			this.ID = results.getInt("ID");
			this.TYPE = results.getString("TYPE");
			this.ORGANIZATION = results.getString("ORGANIZATION");
			this.LOCATION = results.getString("LOCATION");
			this.TITLE = results.getString("TITLE");
			this.URL = results.getString("URL");
			this.START_DATE = results.getString("START_DATE");
			this.END_DATE = results.getString("END_DATE");
			this.details = new ArrayList<Experience.Section.Detail>();
		}
		
		public void addDetail(String text) {
			Experience.Section.Detail newDetail = new Experience.Section.Detail(text);
			this.details.add(newDetail);
		}
	}
	
	public static void writeFormOptions(HttpServletRequest request, HttpServletResponse response, SQLite database, PrintWriter out) throws IOException, SQLException {
		Statement statement = database.createStatement();
		ResultSet results = statement.executeQuery(Experience.QUERY);
		
		try {
			out.println("<table border=0 cellpadding=0 cellspacing=0>");
			out.println("<tbody>");
			while (results.next()) {
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
	
	public static ArrayList<Experience.Section> getExperienceList(
			HttpServletRequest request, 
			HttpServletResponse response
			) throws SQLException, IOException {

		// Connect to database
		SQLite database = new SQLite();
		database.connect();
		
		// Return array
		ArrayList<Experience.Section> experienceList = null;
		
		try {		
			// Find which experiences should be output
			ArrayList<Integer> includedExperienceById = new ArrayList<>(
					Arrays.stream(request.getParameterValues("EXPERIENCE_ID"))
					.map(s -> Integer.parseInt(s))
					.collect(Collectors.toList())
					);
	
			// Return array
			experienceList = new ArrayList<Experience.Section>();
			
			Statement statement = database.createStatement();
			ResultSet results = statement.executeQuery(Experience.QUERY_FULL);
			
			try {
				int experienceId = -1;
				Experience.Section experience = null;
				while (results.next()) {
					int id = results.getInt("ID");
					if (!includedExperienceById.contains(id)) {
						continue;
					}
					if (experienceId != id) {
						// Ordered by ID
						experienceId = id;
						experience = new Experience.Section(results);
						experienceList.add(experience);
					}
					// Add this detail to the selected experience
					String text = results.getString("TEXT"); 
					experience.addDetail(text);
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
		
		return experienceList;
	}
}
