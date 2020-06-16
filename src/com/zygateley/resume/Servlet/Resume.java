package com.zygateley.resume.Servlet;

import java.io.*;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.zygateley.resume.*;

/**
 * Servlet implementation class Resume
 */
@WebServlet("/Resume")
public class Resume extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Resume() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Connect to database
		SQLite database = new SQLite();
		database.connect();
		
		response.setContentType("text/html; charset=UTF-8");
		
		// Must initialize PrintWriter before any includes
		PrintWriter out = response.getWriter();

		// HTML Start
		out.println("<!DOCTYPE html>");
		out.println("<html>");
		out.println("<head>");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">");
		out.println("<link rel=\"stylesheet\" type=\"text/css\" href=\"styles/Resume.css\" />");
		out.println("<title>Resume - Zach Gateley</title>");
		out.println("</head>");
		out.println("<body style=\"margin:0;\">");
		out.println("<div class=\"Body\">");
		
		// Output header
		RequestDispatcher dispatch = request.getRequestDispatcher("/includes/header.html");
		dispatch.include(request, response);
		
		try {
			out.println("<form method=\"get\" action=\"/Resume/resume.jsp\">");
			
			// EDUCATION
			if (request.getParameter("EDUCATION_ID") != null) {
				out.println("<div class=\"Header\">EDUCATION</div>");
				Education.writeOutput(request, response, database, out);
			}
			
			// EXPERIENCE
			if (request.getParameter("EXPERIENCE_ID") != null) {
				out.println("<div class=\"Header\">EXPERIENCE</div>");
				Experience.writeOutput(request, response, database, out);
			}
			
			// SKILLS
			if (request.getParameter("SKILL_ID") != null) { 
				out.println("<div class=\"Header\">SKILLS</div>");
				Skills.writeOutput(request,  response,  database, out);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		try {
			out.println("</div>");
			out.println("</body>");
			out.println("</html>");			
		}
		finally {
			out.close();
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
