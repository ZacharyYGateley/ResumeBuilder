<%@page import="com.zygateley.resume.SQLite,com.zygateley.resume.Details" %>

<%
	// Open connection with database
	SQLite database = new SQLite();
	database.connect();
	
	String output; 

	String typeString = request.getParameter("type");
	int parentId = Integer.parseInt(request.getParameter("parentId"));
	Details.Type type;
	switch (typeString) {
	case "EDUCATION":
		output = Details.getDetails(Details.Type.EDUCATION, database, parentId);
		break;
	case "EXPERIENCE":
		output = Details.getDetails(Details.Type.EXPERIENCE, database, parentId);
		break;
	case "SKILLS":
		output = Details.getDetails(Details.Type.SKILLS, database, parentId);
		break;
	default:
		output = "";
		break;
	} 

	database.close();
	
	%>
 
 <%= output %>
