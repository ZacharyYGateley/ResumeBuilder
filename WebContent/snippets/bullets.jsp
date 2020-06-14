<%@ page import="com.zygateley.resume.Bullets" %>



<ul>
  <li><%= request.getParameter("bullet") %></li>
<%
for (String bullet : Bullets.bullets) {
%>
  <li><%= bullet %></li>
<%
}
%>
</ul>