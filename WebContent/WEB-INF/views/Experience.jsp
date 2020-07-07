<%@page import="com.zygateley.resume.*,java.util.ArrayList" %> 



<%
	// Get experience set from Experience class
// 		(.getExperienceSet opens and closes database)
ArrayList<? extends SQLite.Section> experienceList = Experience.getExperienceList(request,  response);
int headerId = -1;
int experienceCount = experienceList.size();
for (int experienceIndex = 0; experienceIndex < experienceCount; experienceIndex++) {
	SQLite.Section record = experienceList.get(experienceIndex);
	int HEADER_ID = Integer.parseInt(record.getField("HEADER_ID"));
	
	if (headerId != HEADER_ID) {
		if (headerId > -1) {
			%>	

	</tbody>
</table>

			<%
		}
		%>
		
<div class="Header"><%= record.getField("HEADER") %></div>

<table border=0 cellspacing=0 cellpadding=0>
	<tbody>
		
		<%
		headerId = HEADER_ID;
	}
	
	String dateString = "";
	String START_DATE = record.getField("START_DATE");
	if (START_DATE != null && !START_DATE.isBlank()) {
		dateString += SQLite.formatSQLiteDate(START_DATE) + "&nbsp;&ndash;&nbsp;";
	}
	String END_DATE = record.getField("END_DATE");
	if (END_DATE != null && !END_DATE.isBlank()) {
		dateString += SQLite.formatSQLiteDate(END_DATE);
	}
	else if (!dateString.isBlank()) {
		dateString += "Present";
	}
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
			<td align=left valign=top class="SubSubHeader JobTitle NoWrap">
				<%= record.getField("TITLE") %>
			</td>
			<td align=left valign=top class="SubSubHeader NoWrap Right">
				<%= dateString %>
			</td>
		</tr>
		
		<tr>
			<td align=left valign=top colspan=2>
				<ul>
		
	<%
	
	// Make details more readily available
	ArrayList<SQLite.Section.Detail> detailSet = record.getDetails();
	int detailCount = detailSet.size();
	
	for (int detailIndex = 0; detailIndex < detailCount; detailIndex++) {
		%>
		
					<li>
						<%= detailSet.get(detailIndex).getField("TEXT") %>
					</li>
				
		<%
	} // Details
	
	%>
	
				</ul>
			</td>
		</tr>
		
		<!-- Spacer row between any two blocks -->
		<tr>
			<td class="TableSpacer">
				&nbsp;
			</td>
		</tr>
		
	
	<%
	
}	// Top level experiences
%>

	</tbody>
</table>