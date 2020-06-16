<%@page import="com.zygateley.resume.*,java.util.ArrayList" %> 

<div class="Header">EDUCATION</div>

<table border=0 cellspacing=0 cellpadding=0>
	<tbody>

<%
	// Get education set from Education class
// 		(.getEducationSet opens and closes database)
ArrayList<Education.Section> educationList = Education.getEducationList(request,  response);
int educationCount = educationList.size();
for (int educationIndex = 0; educationIndex < educationCount; educationIndex++) {
	Education.Section record = educationList.get(educationIndex);
%>

		<tr>
			<td align=left valign=top class="SubHeader NoWrap">
				<%= record.ORGANIZATION %>
			</td>
			<td align=left valign=top class="SubHeader Right NoWrap">
				<%= record.LOCATION %>
			</td>
		</tr>
		
		<tr>
			<td align=left valign=top colspan=2>
		

<table border=0 cellspacing=0 cellpadding=0 style="padding:0;">
	<tbody>
	<%
	
	// Make details more readily available
	ArrayList<Education.Section.Detail> detailSet = record.details;
	int detailCount = detailSet.size();
	
	for (int detailIndex = 0; detailIndex < detailCount; detailIndex++) {
		Education.Section.Detail detail = detailSet.get(detailIndex);
		String endDate = SQLite.formateSQLiteDate(detail.END_DATE);
		%>
		
		<tr>
			<td align=left valign=top style="width:10px;" class="SubSubHeader NoWrap">
				<%= 
					detail.DEGREE_TYPE 
				%><%= 
					(!detail.DEGREE_TYPE.isBlank()) ? "," : "" 
				%>
			</td>
			<td align=left valign=top style="width:10px;" class="SubSubHeader NoWrap">
				&nbsp;&nbsp;<%= detail.DEGREE_SUBJECT %>&nbsp;&nbsp;
			</td>
			
		<%
		
		boolean haveGPA = detail.GPA != null && !detail.GPA.isBlank();
		boolean haveHonors = detail.LATIN_HONORS != null && !detail.LATIN_HONORS.isBlank();
		if (haveGPA || haveHonors) {
			
			%>
			
			<td align=center valign=top style="width:10px;">
				&mdash;
			</td>
			
			<td align=left valign=top class="NoWrap">
				<span <%= (haveGPA ? "style=\"font-style:normal;\"" : "class=\"latin\"") %> >
					&nbsp;&nbsp;<%= (haveGPA ? "GPA: " + detail.GPA : detail.LATIN_HONORS) %>
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
				<%= (detail.IS_EXPECTED) ? "Expected " : "" %>
				<%= endDate %>
			</td>
		</tr>
		
		<%
		
		boolean haveDetails = detail.DETAILS != null && !detail.DETAILS.isBlank();
		if (haveDetails) {
			
		%>
		
		<tr>
			<td align=left valign=middle colspan=5>
				<%= detail.DETAILS %>
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
			<td class="TableSpacer">
				&nbsp;
			</td>
		</tr>	
	
	<%
	
}	// Top level educations
%>

	</tbody>
</table>