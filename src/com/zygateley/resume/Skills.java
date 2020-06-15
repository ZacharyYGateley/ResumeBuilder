package com.zygateley.resume;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.*;
import javax.servlet.http.*;

// Mirrors a single record in Skill table
class SkillBlock {
	public final int ID;
	public final String TITLE;
	public SkillBlock(int id, String title) {
		this.ID = id;
		this.TITLE = title;
	}
}

//Mirrors a single record in SkillDetail table
class SkillDetailBlock {
	public final int ID;
	public final int SKILLS_ID;
	public final String TITLE;
	public final int PROFICIENCY;
	SkillDetailBlock(int id, int skills_id, String title, int proficiency) {
		this.ID = id;
		this.SKILLS_ID = skills_id;
		this.TITLE = title;
		this.PROFICIENCY = proficiency;
	}
}


public class Skills {
	private static final String query = "SELECT * FROM Skills";
	private static final String embeddedQuery = "SELECT * FROM SkillsDetail WHERE SKILLS_ID=?";
	private static final int sectionCount = 3; 
	
	public static void writeFormOptions(HttpServletRequest request, HttpServletResponse response, SQLite database, PrintWriter out) throws IOException, SQLException {
		Statement statement = database.createStatement();
		ResultSet results = statement.executeQuery(Skills.query);
		
		try {
			while (results.next()) {
				int ID = results.getInt("ID");
				String TITLE = results.getString("TITLE");
				out.println("<label class=\"SubSubHeader\">");
				out.println(String.format(
						"<input type=\"checkbox\" name=\"SKILL_ID\" value=\"%d\">%s</option>",
						ID, TITLE)
					);
				out.println("</label>");
				out.println("<br />");
			}
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
		ArrayList<Integer> skills = new ArrayList<>(
				Arrays.stream(request.getParameterValues("SKILL_ID"))
				.map(s -> Integer.parseInt(s))
				.collect(Collectors.toList())
				);
		
		Statement statement = database.createStatement();
		ResultSet results = statement.executeQuery(Skills.query);
		
		ArrayList<SkillBlock> records = new ArrayList<>();
		try {
			while (results.next()) {
				int id = results.getInt("ID");
				if (!skills.contains(id)) {
					continue;
				}
				SkillBlock next = new SkillBlock(
						id,
						results.getString("TITLE")
						);
				records.add(next);
			}
		}
		finally {
			statement.close();
		}
		int recordsCount = records.size();
		
		for (int skillRecord = 0; skillRecord < recordsCount; skillRecord++) {
			SkillBlock record = records.get(skillRecord);
			
			out.println("<tr>");
			out.print("<td align=left valign=top class=\"SubSubHeader OtherSkillsTop NoWrap Column1\">");
			out.print(record.TITLE);
			out.println("</td>");
			
			PreparedStatement _statement = database.prepareStatement(Skills.embeddedQuery);
			try {
				_statement.setInt(1, record.ID);
				ResultSet _results = _statement.executeQuery();
				
				// Have to cut skills into sections
				ArrayList<SkillDetailBlock> _records = new ArrayList<>();
				while (_results.next()) {
					SkillDetailBlock next = new SkillDetailBlock(
							_results.getInt("ID"),
							_results.getInt("SKILLS_ID"),
							_results.getString("TITLE"),
							_results.getInt("PROFICIENCY")
							);
					_records.add(next);
				}
				int recCount = _records.size();
				int perColumn = (int) (Math.ceil(recCount / (double) Skills.sectionCount));
				
				// Loop columns
				int recordIndex = 0;
				OUTER_LOOP: for (int col = 0; col < recCount; col++) {
					// Last column should span the rest of the columns
					boolean isMultispan = recordIndex == recCount - 1;
					
					
					out.print("<td align=left valign=top class=\"OtherSkillsTop NoWrap");
					if (isMultispan) {
						out.print("\" colspan=\"" + (Skills.sectionCount - col));
					}
					else {
						out.print(" Column" + (col + 2));
					}
					out.println("\">");
					out.println("<ul  class=\"List" + (skillRecord % 2) + "\">");
					for (int i = 0; i < perColumn; i++, recordIndex++) {
						SkillDetailBlock next;
						try {
							next = _records.get(recordIndex);
						}
						catch (Exception err) {
							break OUTER_LOOP;
						}
						out.println("<li>" + next.TITLE);
						out.println("<span class=\"Proficiency\">");
						if (next.PROFICIENCY == 5) {
							out.println("(Proficient)");
						}
						else if (next.PROFICIENCY > 2) {
							out.println("(Familiar)");
						}
						else {
							out.println("(Somewhat familiar)");
						}
						out.println("</span>");
						out.println("</li>");
					}
				}
				out.println("</ul>");
				out.println("</td>");

				// Spacer between each Skill set
				out.println("<tr><td class=\"TableSpacer\">&nbsp;</td></tr>");
			}
			finally {
				_statement.close();
				
			}
		}
		
		return;
	}
}
