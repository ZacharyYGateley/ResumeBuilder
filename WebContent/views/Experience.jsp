<%@page import="com.zygateley.resume.*,java.util.ArrayList" %> 



<%
	// Get experience set from Experience class
// 		(.getExperienceSet opens and closes database)
ArrayList<Experience.Section> experienceList = Experience.getExperienceList(request,  response);
int headerId = -1;
int experienceCount = experienceList.size();
for (int experienceIndex = 0; experienceIndex < experienceCount; experienceIndex++) {
	Experience.Section record = experienceList.get(experienceIndex);
	
	if (headerId != record.HEADER_ID) {
		if (headerId > -1) {
			%>	

	</tbody>
</table>

			<%
		}
		%>
		
<div class="Header"><%= record.HEADER %></div>

<table border=0 cellspacing=0 cellpadding=0>
	<tbody>
		
		<%
		headerId = record.HEADER_ID;
	}
	
	String dateString = "";
	if (record.START_DATE != null && !record.START_DATE.isBlank()) {
		dateString += SQLite.formateSQLiteDate(record.START_DATE) + "&nbsp;&ndash;&nbsp;";
	}
	if (record.END_DATE != null && !record.END_DATE.isBlank()) {
		dateString += SQLite.formateSQLiteDate(record.END_DATE);
	}
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
			<td align=left valign=top class="SubSubHeader JobTitle NoWrap">
				<%= record.TITLE %>
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
	ArrayList<Experience.Section.Detail> detailSet = record.details;
	int detailCount = detailSet.size();
	
	for (int detailIndex = 0; detailIndex < detailCount; detailIndex++) {
		%>
		
					<li>
						<%= detailSet.get(detailIndex).TEXT %>
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