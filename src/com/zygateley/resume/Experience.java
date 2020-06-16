package com.zygateley.resume;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.*;

// Mirrors a single record in Experience table
class ExperienceBlock {
	public final int ID;
	public final String TYPE;
	public final String ORGANIZATION;
	public final String LOCATION;
	public final String TITLE;
	public final String URL;
	public final String DATE_START;
	public final String DATE_END;
	public ExperienceBlock(ResultSet results) throws SQLException {
		this.ID = results.getInt("ID");
		this.TYPE = results.getString("TYPE");
		this.ORGANIZATION = results.getString("ORGANIZATION");
		this.LOCATION = results.getString("LOCATION");
		this.TITLE = results.getString("TITLE");
		this.URL = results.getString("URL");
		this.DATE_START = results.getString("DATE_START");
		this.DATE_END = results.getString("DATE_END");
	}
}

// Mirrors a single record in ExperienceDetail table
class ExperienceDetailBlock {
	public final int ID;
	public final int EXPERIENCE_ID;
	public final String TEXT;
	public ExperienceDetailBlock(ResultSet results) throws SQLException {
		this.ID = results.getInt("ID");
		this.EXPERIENCE_ID = results.getInt("EXPERIENCE_ID");
		this.TEXT = results.getString("TEXT");
	}
}


public class Experience {
	private static final String QUERY = "SELECT * FROM Experience ORDER BY DATE_END DESC";
	private static final String QUERY_DETAIL = "SELECT * FROM ExperienceDetail WHERE EXPERIENCE_ID=? ORDER BY ORDER ASC"; 
	
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
			statement.close();
		}
		
		return;
	}
	
	public static void writeOutput(
			HttpServletRequest request, 
			HttpServletResponse response, 
			SQLite database, 
			PrintWriter out
			) throws SQLException, IOException {
		// Find which skills should be output
		ArrayList<Integer> organizations = new ArrayList<>(
				Arrays.stream(request.getParameterValues("EXPERIENCE_ID"))
				.map(s -> Integer.parseInt(s))
				.collect(Collectors.toList())
				);
		
		Statement statement = database.createStatement();
		ResultSet results = statement.executeQuery(Experience.QUERY);
		
		// So that there are no embedded statements
		// Create ArrayLis5t with results
		ArrayList<ExperienceBlock> records = new ArrayList<>();
		try {
			while (results.next()) {
				int id = results.getInt("ID");
				if (!organizations.contains(id)) {
					continue;
				}
				ExperienceBlock nextBlock = new ExperienceBlock(results);
				records.add(nextBlock);
			}
		}
		finally {
			statement.close();
		}
		int recordCount = records.size();
		
		out.println("<table border=0 cellspacing=0 cellpadding=0>");
		out.println("<tbody>");
		for (int experienceIndex = 0; experienceIndex < recordCount; experienceIndex++) {
			ExperienceBlock record = records.get(experienceIndex);
			
			// Output organization and location
			out.println("<tr>");
			out.print("<td align=left valign=top class=\"SubHeader NoWrap\">");
			out.print(record.ORGANIZATION);
			out.println("</td>");
			out.println("<td align=left valign=top class=\"SubHeader Right NoWrap\">");
			out.println(record.LOCATION);
			out.println("</td>");
			out.println("</tr>");
			
			// Output Title and Dates
			out.println("<tr>");
			out.println("<td align=left valign=top class=\"SubSubHeader NoWrap\">");
			out.println(record.TITLE);
			out.println("</td>");
			out.println("<td align=left valign=top class=\"SubSubHeader NoWrap Right\">");
			if (record.DATE_START != null && !record.DATE_START.isBlank()) {
				out.println(SQLite.formateSQLiteDate(record.DATE_START));
				out.println("–");
			}
			out.println(SQLite.formateSQLiteDate(record.DATE_END));
			out.println("</td>");
			out.println("</tr>");
			
			// Output each degree (experience instance) from this organization
			PreparedStatement _statement = database.prepareStatement(Experience.QUERY_DETAIL);
			try {
				_statement.setInt(1, record.ID);
				ResultSet _results = _statement.executeQuery();
				
				// Copied from Skills
				// Not strictly necessary, but it keeps things a little cleaner
				// if a little slower
				ArrayList<ExperienceDetailBlock> _records = new ArrayList<>();
				while (_results.next()) {
					ExperienceDetailBlock next = new ExperienceDetailBlock(_results);
					_records.add(next);
				}
				int _recordCount = _records.size();
				
				// Loop columns
				out.println("<tr>");
				out.println("<td align=left valign=top colspan=2>");
				out.println("<ul>");
				for (int i = 0; i < _recordCount; i++) {
					ExperienceDetailBlock _record = _records.get(i);
					out.println("<li>");
					out.println(_record.TEXT);
					out.println("</li>");
				}
				out.println("</td>");
				out.println("</tr>");
			}
			finally {
				_statement.close();
			}
			
			// Spacer between each experience
			out.println("<tr><td class=\"TableSpacer\">&nbsp;</td></tr>");
		}
		out.println("</tbody>");
		out.println("</table>");
		
		return;
	}
}
