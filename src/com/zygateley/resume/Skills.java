package com.zygateley.resume;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

import javax.servlet.http.*;




public class Skills implements TopLevel {
	private static final String QUERY_FULL = 
			"SELECT s.ID, s.TITLE, \r\n" + 
			"sd.ID as DETAIL_ID, sd.SKILLS_ID,\r\n" + 
			"sd.TITLE AS DETAIL_TITLE, sd.PROFICIENCY\r\n" + 
			"FROM SkillsDetail AS sd LEFT JOIN Skills AS s\r\n" + 
			"ON s.ID=sd.SKILLS_ID\r\n" + 
			"ORDER BY s.ID, DETAIL_ID";
	public static final int SECTION_COUNT = 3; 
	
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
			Detail(ResultSet results) throws SQLException {
				super();
				this.addFields(results, "DETAIL_TITLE");
				
				// Proficiency has extra processing
				// Get string proficiency from 1-5 proficiency
				int proficiency = results.getInt("PROFICIENCY");
				String proficiencyString = Skills.proficiencyIntegerToString(proficiency);
				this.fields.put("PROFICIENCY", proficiencyString);
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
					"ID", "TITLE"
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
			Skills.Section.Detail newDetail = new Skills.Section.Detail(results);
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
		ResultSet results = statement.executeQuery(Skills.QUERY_FULL);
		
		try {
			out.println("<div class=\"Header\">Skills</div>");
			out.println("<table border=0 cellspacing=0 cellpadding=0>");
			out.println("<tbody>");
			out.println("<tr>");
			out.println("<td align=left valign=top>");
			int skillIndex = -1;
			while (results.next()) {
				int SKILL_ID = results.getInt("ID");
				if (skillIndex != SKILL_ID) {
					if (skillIndex > -1) {
						out.println("</p>");
					}
					String TITLE = results.getString("TITLE");
					out.println("<label class=\"SubSubHeader\">");
					out.println(String.format(
							"<input type=\"checkbox\" data-skill-id=\"%d\" name=\"SKILL_ID\" value=\"%d\" " +
							"onclick=\"selectMultiple(this.checked, \'data-parent\', \'%d\');\"" +
							">%s</option>",
							SKILL_ID, SKILL_ID, SKILL_ID, TITLE)
						);
					out.println("</label>");
					out.println("<p>");
					
					skillIndex = SKILL_ID;
				}
				int DETAIL_ID = results.getInt("DETAIL_ID");
				String DETAIL_TITLE = results.getString("DETAIL_TITLE");
				String PROFICIENCY = Skills.proficiencyIntegerToString(results.getInt("PROFICIENCY"));
				out.println("<label>");
				out.println(String.format(
						"<input type=\"checkbox\" data-parent=\"%d\" " +  
						"name=\"SKILL_DETAIL_ID\" value=\"%d\" " + 
						"onclick=\"if(!this.checked)return;selectMultiple(this.checked, 'data-skill-id', '%d');\">%s (%s)</option>",
						SKILL_ID, DETAIL_ID, SKILL_ID, DETAIL_TITLE, PROFICIENCY)
					);
				out.println("</label>");
				out.println("<br />");
			}
			if (skillIndex > -1) {
				out.println("</p>");
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
	 * getSkillList
	 * 
	 * Return a list of Education.Section that represents
	 * all of the data of this type to be output on this resume.
	 * Called from Skills.jsp
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
	public static ArrayList<? extends SQLite.Section> getSkillList(
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
				Skills.QUERY_FULL, 
				"SKILL_ID", 
				"SKILL_DETAIL_ID",
				Skills.Section.class
				);
	}
	
	/**
	 * proficiencyIntegerToString
	 * 
	 * Proficiency is stored in the database as an integer.
	 * (At the time of writing: with intended range 1-5).
	 * This method converts proficiency integers to 
	 * resume output strings. 
	 * 
	 * e.g. Proficient, Familiar, Somewhat Familiar
	 * 
	 * @param proficiencyInt proficiency integer from database
	 * @return formatted proficiency string
	 */
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
