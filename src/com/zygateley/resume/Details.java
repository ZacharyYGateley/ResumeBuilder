package com.zygateley.resume;


import java.sql.*;


public class Details {
	public static enum Type {
		VOID,
		EDUCATION,
		EXPERIENCE,
		SKILLS
	}
	
	public static String getDetails(Type type, SQLite database, int parentId) throws SQLException {
		Statement statement = database.createStatement();
		StringBuffer resultString = new StringBuffer();
		resultString.append("<ul>\n");
		ResultSet results;
		switch (type) {
		case EDUCATION:
			results = statement.executeQuery(
				"SELECT * FROM EducationDetail WHERE EDUCATION_ID='" + parentId + "'"
			);
			while (results.next()) {
				resultString.append("<li>");
				resultString.append(results.getString("DEGREE_TYPE"));
				resultString.append("</li>\n");
			}
		case EXPERIENCE:
			results = statement.executeQuery(
				"SELECT * FROM ExperienceDetail WHERE EXPERIENCE_ID='" + parentId + "'"
			);
			while (results.next()) {
				resultString.append("<li>");
				resultString.append(results.getString("TEXT"));
				resultString.append("</li>\n");
			}
		case SKILLS:
			results = statement.executeQuery(
				"SELECT * FROM SkillsDetail WHERE SKILLS_ID='" + parentId + "'"
			);
			while (results.next()) {
				resultString.append("<li>");
				resultString.append(results.getString("TITLE"));
				resultString.append("</li>\n");
			}			
		default:
			// Bad detail type
			results = null;
			break;
		}
		resultString.append("</ul>\n");
		return resultString.toString();
	}
	
	public static void main(String[] args) throws SQLException {
		SQLite database = new SQLite("jdbc:sqlite:C:\\Users\\Zachary Gateley\\Dropbox\\Languages\\Java\\Resume\\CV.db");
		database.connect();
		String output = Details.getDetails(Details.Type.EDUCATION, database, 3);
		database.close();
		
		System.out.println(output);
	}
}
