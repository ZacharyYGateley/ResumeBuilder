<%@page import="com.zygateley.resume.*,java.util.ArrayList" %> 

<div class="Header">Skills</div>

<table border=0 cellspacing=0 cellpadding=0>
	<tbody>

<%
	// Get skillset from Skills class
// 		(.getSkillSet opens and closes database)
ArrayList<? extends SQLite.Section> skillList = Skills.getSkillList(request,  response);
int skillCount = skillList.size();
boolean haveMultipleHeaders = !skillList.get(0).getField("TITLE")
	.equals(
			skillList.get(skillList.size() - 1).getField("TITLE"));
for (int skillIndex = 0; skillIndex < skillCount; skillIndex++) {
	SQLite.Section record = skillList.get(skillIndex);
%>

		<tr>
		
<%
	if (haveMultipleHeaders) {
%>

			<td align=left valign=top class="SubSubHeader OtherSkillsTop NoWrap Column1">
				<%= record.getField("TITLE") %>
			</td>

	<%
		}
		
		// Make details more readily available
		ArrayList<SQLite.Section.Detail> detailSet = record.getDetails();
		int detailCount = detailSet.size();
		int detailIndex = 0;
		
		// Skills are cut into columns (SECTION_COUNT)
		int columnCount = (haveMultipleHeaders ? Skills.COLUMN_COUNT : Skills.COLUMN_COUNT_SINGLE);
		int perColumn = (int) (Math.ceil(detailCount / (double) columnCount));
		OUTER_LOOP: for (int column = 0; column < columnCount; column++) {
			
			// Last column should span the rest of the columns
			boolean isMultispan = (detailIndex + perColumn >= detailCount);
	%>
			
			<td align=left valign=top class="OtherSkillsTop NoWrap 
			<%=(isMultispan) ? 
					("\" colspan=\"" + (columnCount - column)) : 
					(" Column" + (column + 2))%>">
				<ul class="List<%=(skillIndex % 2)%>">

		<%
		// Output specific skills (details)
		for (int i = 0; i < perColumn; i++, detailIndex++) {
			SQLite.Section.Detail detail = detailSet.get(detailIndex);
		%>
				
					<li>

<table border=0>
  <tbody>
    <tr>
      <td cellspacing=0 cellpadding=0 style="text-align:left;">
		<%= detail.getField("DETAIL_TITLE") %>
      </td>
      <td cellspacing=0 cellpadding=0 style="text-align:right;">
		<span class="Proficiency">
		  (<%= detail.getField("PROFICIENCY") %>)
		</span>
      </td>
    </tr>
  </tbody>
</table>
					</li>
					
			<%
			// Check to make sure not all skills have yet been output
			if (detailIndex + 1 >= detailCount) {
				break OUTER_LOOP;
			}
		}
		%>
		
				</ul>
			</td>
			
		<%
	}
	%>
	
		</tr>
		<!-- Spacer at the end of each block of any kind -->
		<tr>
			<td>
				<div class="TableSpacer"></div>
			</td>
		</tr>
		
	<%
}	// OUTER_LOOP
%>

	</tbody>
</table>