<%@page import="com.zygateley.resume.*,java.util.ArrayList" %> 

<div class="Header">Skills</div>

<table border=0 cellspacing=0 cellpadding=0>
	<tbody>

<%
	// Get skillset from Skills class
// 		(.getSkillSet opens and closes database)
ArrayList<? extends SQLite.Section> skillList = Skills.getSkillList(request,  response);
int skillCount = skillList.size();
for (int skillIndex = 0; skillIndex < skillCount; skillIndex++) {
	SQLite.Section record = skillList.get(skillIndex);
%>

		<tr>
			<td align=left valign=top class="SubSubHeader OtherSkillsTop NoWrap Column1">
				<%= record.getField("TITLE") %>
			</td>

	<%
	// Make details more readily available
	ArrayList<SQLite.Section.Detail> detailSet = record.getDetails();
	int detailCount = detailSet.size();
	int detailIndex = 0;
	
	// Skills are cut into columns (SECTION_COUNT)
	int perColumn = (int) (Math.ceil(detailCount / (double) Skills.SECTION_COUNT));
	OUTER_LOOP: for (int column = 0; column < Skills.SECTION_COUNT; column++) {
		
		// Last column should span the rest of the columns
		boolean isMultispan = (detailIndex + perColumn >= detailCount);
	%>
			
			<td align=left valign=top class="OtherSkillsTop NoWrap 
			<%=(isMultispan) ? 
					("\" colspan=\"" + (Skills.SECTION_COUNT - column)) : 
					(" Column" + (column + 2))%>">
				<ul class="List<%=(detailIndex % 2)%>">

		<%
		// Output specific skills (details)
		for (int i = 0; i < perColumn; i++, detailIndex++) {
			SQLite.Section.Detail detail = detailSet.get(detailIndex);
		%>
				
					<li>
						<%= detail.getField("DETAIL_TITLE") %>
						<span class="Proficiency">
							(<%= detail.getField("PROFICIENCY") %>)
						</span>
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
			<td class="TableSpacer">&nbsp;</td>
		</tr>
		
	<%
}	// OUTER_LOOP
%>

	</tbody>
</table>