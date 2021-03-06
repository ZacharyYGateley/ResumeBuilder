package com.zygateley.resume.Servlet;

import java.io.*;

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
		out.println("<script type=\"text/javascript\" src=\"scripts/Resume.js\"></script>");
		out.println("<title>Resume - Zach Gateley</title>");
		out.println("</head>");
		// cleanupResumeStyle from Resume.js
		out.println("<body style=\"margin:0;\" onload=\"cleanupResumeStyle()\">");
		out.println("<div class=\"Body\">");
		
		
		// Output header
		RequestDispatcher dispatch = request.getRequestDispatcher("WEB-INF/views/Header.html");
		dispatch.include(request, response);
		
		
		// EDUCATION
		if (request.getParameter("EDUCATION_ID") != null) {
			dispatch = request.getRequestDispatcher("WEB-INF/views/Education.jsp");
			dispatch.include(request, response);
		}
		
		// EXPERIENCE
		if (request.getParameter("EXPERIENCE_ID") != null) {
			dispatch = request.getRequestDispatcher("WEB-INF/views/Experience.jsp");
			dispatch.include(request, response);
		}
		
		// SKILLS
		if (request.getParameter("SKILL_ID") != null) { 
			dispatch = request.getRequestDispatcher("WEB-INF/views/Skills.jsp");
			dispatch.include(request, response);
		}
		
		
		// Finish html body
		out.println("</div>");
		out.println("</body>");
		out.println("</html>");			

		// Close output stream
		out.close();
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
