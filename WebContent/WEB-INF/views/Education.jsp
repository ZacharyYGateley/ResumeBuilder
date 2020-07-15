<%@page import="com.zygateley.resume.*,java.util.ArrayList" %> 

<div class="Header">Education</div>

<table border=0 cellspacing=0 cellpadding=0>
	<tbody>

<%
	// Get education set from Education class
// 		(.getEducationSet opens and closes database)
ArrayList<? extends SQLite.Section> educationList = Education.getEducationList(request,  response);
int educationCount = educationList.size();
for (int educationIndex = 0; educationIndex < educationCount; educationIndex++) {
	SQLite.Section record = educationList.get(educationIndex);
%>

		<tr>
			<td align=left valign=top class="SubHeader NoWrap">
				<%= record.getField("ORGANIZATION") %>
			</td>
			<td align=left valign=top class="SubHeader Right NoWrap">
				<%= record.getField("LOCATION") %>
			</td>
		</tr>
		
		<tr>
			<td align=left valign=top colspan=2>
		

<table border=0 cellspacing=0 cellpadding=0 style="padding:0;">
	<tbody>
	<%
	
	// Make details more readily available
	ArrayList<SQLite.Section.Detail> detailSet = record.getDetails();
	int detailCount = detailSet.size();
	
	for (int detailIndex = 0; detailIndex < detailCount; detailIndex++) {
		SQLite.Section.Detail detail = detailSet.get(detailIndex);
		String GPA = detail.getField("GPA");
		String LATIN_HONORS = detail.getField("LATIN_HONORS");
		boolean haveGPA = GPA != null && !GPA.isBlank();
		boolean haveHonors = LATIN_HONORS != null && !LATIN_HONORS.isBlank();
		
		String endDate = SQLite.formatSQLiteDate(detail.getField("END_DATE"));
		%>
		
		<tr>
			<td align=left valign=top style="width:10px;" class="SubSubHeader NoWrap Education1">
				<%= 
					detail.getField("DEGREE_TYPE")
				%><%= 
					((!detail.getField("DEGREE_TYPE").isBlank()) ? "," : "")
					%>
			</td>
			<td align=left valign=top style="width:10px;" class="SubSubHeader NoWrap <%= (haveGPA || haveHonors) ? "Education2" : "" %>">
				&nbsp;&nbsp;<%= detail.getField("DEGREE_SUBJECT") %>&nbsp;&nbsp;
			</td>
			
		<%
		
		if (haveGPA || haveHonors) {
			
			%>
			
			<td align=center valign=top style="width:10px;">
				&mdash;
			</td>
			
			<td align=left valign=top class="NoWrap">
				<span <%= (haveGPA ? "style=\"font-style:normal;\"" : "class=\"latin\"") %> >
					&nbsp;&nbsp;<%= (haveGPA ? "GPA: " + GPA : LATIN_HONORS) %>
				</span>
			</td>
			
			<%
			
		}
		else {
			
			%>
			
			<td></td><td></td>
			
			<%
			
		}
		
		%>
		
			<td align=left valign=top class="SubSubHeader Right">
				<%= (Integer.parseInt(detail.getField("IS_EXPECTED")) > 0) ? "Expected " : "" %>
				<%= endDate %>
			</td>
		</tr>
		
		<%
		
		String DETAILS = detail.getField("DETAILS");
		boolean haveDetails = DETAILS != null && !DETAILS.isBlank();
		if (haveDetails) {
			
		%>
		
		<tr>
			<td align=left valign=middle colspan=5>
				<%= DETAILS %>
			</td>
		</tr>
		
		<%
			
		}
		
	} // Details
	
	%>
		
	</tbody>
</table>
	
			</td>
		</tr>
		
		<!-- Spacer row between any two blocks -->
		<tr>
			<td>
				<div class ="TableSpacer"></div>
			</td>
		</tr>	
	
	<%
	
}	// Top level educations
%>

	</tbody>
</table>