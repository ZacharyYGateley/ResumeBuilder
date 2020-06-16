package com.zygateley.resume;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.*;

// Mirrors a single record in Education table
class EducationBlock {
	public final int ID;
	public final String ORGANIZATION;
	public final String LOCATION;
	public EducationBlock(int id, String organization, String location) {
		this.ID = id;
		this.ORGANIZATION = organization;
		this.LOCATION = location;
	}
}

// Mirrors a single record in EducationDetail table
class EducationDetailBlock {
	public final int ID;
	public final int EDUCATION_ID;
	public final String END_DATE;
	public final boolean IS_EXPECTED;
	public final String DEGREE_TYPE;
	public final String DEGREE_SUBJECT;
	public final String GPA;
	public final String LATIN_HONORS;
	public final String DETAILS;
	public EducationDetailBlock(ResultSet results) throws SQLException {
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


public class Education {
	private static final String query = "SELECT Education.*, EducationDetail.END_DATE FROM Education LEFT OUTER JOIN EducationDetail ON Education.ID = EducationDetail.ID ORDER BY EducationDetail.END_DATE DESC";
	private static final String embeddedQuery = "SELECT * FROM EducationDetail WHERE EDUCATION_ID=? ORDER BY END_DATE DESC"; 
	
	public static void writeFormOptions(HttpServletRequest request, HttpServletResponse response, SQLite database, PrintWriter out) throws IOException, SQLException {
		Statement statement = database.createStatement();
		ResultSet results = statement.executeQuery(Education.query);
		
		try {
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
				Arrays.stream(request.getParameterValues("EDUCATION_ID"))
				.map(s -> Integer.parseInt(s))
				.collect(Collectors.toList())
				);
		
		Statement statement = database.createStatement();
		ResultSet results = statement.executeQuery(Education.query);
		
		// So that there are no embedded statements
		// Create ArrayLis5t with results
		ArrayList<EducationBlock> records = new ArrayList<>();
		try {
			while (results.next()) {
				int id = results.getInt("ID");
				if (!organizations.contains(id)) {
					continue;
				}
				EducationBlock nextBlock = new EducationBlock(
						id,
						results.getString("ORGANIZATION"),
						results.getString("LOCATION")
						);
				records.add(nextBlock);
			}
		}
		finally {
			statement.close();
		}
		int recordCount = records.size();

		out.println("<table border=0 cellspacing=0 cellpadding=0>");
		out.println("<tbody>");
		for (int educationIndex = 0; educationIndex < recordCount; educationIndex++) {
			EducationBlock record = records.get(educationIndex);
			
			// Output organization and location
			out.println("<tr>");
			out.print("<td align=left valign=top class=\"SubHeader NoWrap\">");
			out.print(record.ORGANIZATION);
			out.println("</td>");
			out.println("<td align=left valign=top class=\"SubHeader Right NoWrap\">");
			out.println(record.LOCATION);
			out.println("</td>");
			out.println("</tr>");
			
			// Outer table
			out.println("<tr>");
			out.println("<td align=left valign=top colspan=2>");
			
			// Output each degree (education instance) from this organization
			PreparedStatement _statement = database.prepareStatement(Education.embeddedQuery);
			try {
				_statement.setInt(1, record.ID);
				ResultSet _results = _statement.executeQuery();
				
				// Copied from Skills
				// Not strictly necessary, but it keeps things a little cleaner
				// if a little slower
				ArrayList<EducationDetailBlock> _records = new ArrayList<>();
				while (_results.next()) {
					EducationDetailBlock next = new EducationDetailBlock(_results);
					_records.add(next);
				}
				int _recordCount = _records.size();
				
				// Loop columns
				out.println("<table border=0 cellspacing=0 cellpadding=0 style=\"padding:0;\">");
				out.println("<tbody>");
				for (int i = 0; i < _recordCount; i++) {
					EducationDetailBlock _record = _records.get(i);
					
					out.println("<tr>");
					out.println("<td align=left valign=top style=\"width:10px;\" class=\"SubSubHeader NoWrap\">");
					out.print(_record.DEGREE_TYPE);
					if (_record.DEGREE_SUBJECT != null && !_record.DEGREE_SUBJECT.isEmpty()) {
						out.print(",");
					}
					out.print("</td>");
					out.println("<td align=left valign=top style=\"width:10px;\" class=\"SubSubHeader NoWrap\">");
					out.println("&nbsp;&nbsp;" + _record.DEGREE_SUBJECT + "&nbsp;&nbsp;");
					out.println("</td>");
					if (_record.GPA != null && !_record.GPA.isBlank() || 
							_record.LATIN_HONORS != null && !_record.LATIN_HONORS.isBlank()) {
						out.println("<td align=center valign=top style=\"width:10px;\">");
						out.println("—");
						out.println("</td>");
						out.println("<td align=left valing=top class=\"NoWrap\">");
						out.print("&nbsp;&nbsp;");
						if (_record.GPA != null && !_record.GPA.isBlank()) {
							out.print("<span style=\"font-style:normal;\">GPA: ");
							out.print(_record.GPA);
							out.println("</span>");
						}
						else if (_record.LATIN_HONORS != null && !_record.LATIN_HONORS.isBlank()) {
							out.print("<span class=\"latin\">");
							out.print(_record.LATIN_HONORS);
							out.println("</span>");
						}
						out.println("</td>");
					}
					out.println("<td align=left valign=top class=\"SubSubHeader Right\">");
					if (_record.IS_EXPECTED) {
						out.print("Expected ");
					}
					out.println(SQLite.formateSQLiteDate(_record.END_DATE));
					out.println("</td>");
					out.println("</tr>");
					
					// Details are optional
					if (_record.DETAILS != null && !_record.DETAILS.isBlank()) {
						out.println("<tr><td align=left valign=top>");
						out.println(_record.DETAILS);
						out.println("</td></tr>");
					}
					
				}
				// Close off inner table
				out.println("</tbody>");
				out.println("</table>");
				
				// Close off outer table
				out.println("</td>");
				out.println("</tr>");

				// Spacer between each education organization block
				out.println("<tr><td class=\"TableSpacer\">&nbsp;</td></tr>");
			}
			finally {
				_statement.close();
			}
		}
		out.println("</tbody>");
		out.println("</table>");
		
		return;
	}
}
