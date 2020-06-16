package com.zygateley.resume;

import java.io.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.servlet.http.*;




public class Skills {
	private static final String QUERY = "SELECT * FROM Skills";
	private static final String QUERY_FULL = 
			"SELECT s.ID, s.TITLE, \r\n" + 
			"sd.ID as DETAIL_ID, sd.SKILLS_ID,\r\n" + 
			"sd.TITLE AS DETAIL_TITLE, sd.PROFICIENCY\r\n" + 
			"FROM SkillsDetail AS sd LEFT JOIN Skills AS s\r\n" + 
			"ON s.ID=sd.SKILLS_ID\r\n" + 
			"ORDER BY s.ID, DETAIL_ID";
	public static final int SECTION_COUNT = 3; 
	
	// Skill joined structure
	// Mirrors single Skills record
	// Contains mirrors of SkillsDetails records
	public static class Section {
		// SkillDetail structure
		// Mirrors a single record in SkillDetail table
		public static class Detail {
			public final String TITLE;
			public final String PROFICIENCY;
			Detail(String title, String proficiency) {
				this.TITLE = title;
				this.PROFICIENCY = proficiency;
			}
		}
		
		public final int ID;
		public final String TITLE;
		public ArrayList<Detail> details;
		public Section(int id, String title) {
			this.ID = id;
			this.TITLE = title;
			this.details = new ArrayList<Skills.Section.Detail>();
		}
		
		public void addDetail(String title, String proficiency) {
			Detail newDetail = new Skills.Section.Detail(title, proficiency);
			this.details.add(newDetail);
		}
	}
	
	public static void writeFormOptions(HttpServletRequest request, HttpServletResponse response, SQLite database, PrintWriter out) throws IOException, SQLException {
		Statement statement = database.createStatement();
		ResultSet results = statement.executeQuery(Skills.QUERY);
		
		try {
			out.println("<table border=0 cellspacing=0 cellpadding=0>");
			out.println("<tbody>");
			out.println("<tr>");
			out.println("<td align=left valign=top>");
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
	
	public static ArrayList<Skills.Section> getSkillList(
			HttpServletRequest request, 
			HttpServletResponse response
			) throws SQLException, IOException {

		// Connect to database
		SQLite database = new SQLite();
		database.connect();
		
		// Return array
		ArrayList<Skills.Section> skillList = null;
		
		try {		
			// Find which skills should be output
			ArrayList<Integer> includedSkillsById = new ArrayList<>(
					Arrays.stream(request.getParameterValues("SKILL_ID"))
					.map(s -> Integer.parseInt(s))
					.collect(Collectors.toList())
					);
	
			// Return array
			skillList = new ArrayList<Skills.Section>();
			
			Statement statement = database.createStatement();
			ResultSet results = statement.executeQuery(Skills.QUERY_FULL);
			
			try {
				int skillId = -1;
				Skills.Section skill = null;
				while (results.next()) {
					int id = results.getInt("ID");
					if (!includedSkillsById.contains(id)) {
						continue;
					}
					if (skillId != id) {
						// Ordered by ID
						skillId = id;
						skill = new Skills.Section(
								id,
								results.getString("TITLE")
								);
						skillList.add(skill);
					}
					// Add this detail to the selected skill
					String title = results.getString("DETAIL_TITLE");
					int proficiency = results.getInt("PROFICIENCY");
					String proficiencyString = Skills.proficiencyIntegerToString(proficiency); 
					skill.addDetail(title, proficiencyString);
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
		
		return skillList;
	}
	
	private static String proficiencyIntegerToString(int proficiencyInt) {
		if (proficiencyInt == 5) {
			return "Proficient";
		}
		else if (proficiencyInt> 2) {
			return "Familiar";
		}
		else {
			return "Somewhat familiar";
		}
	}
}
