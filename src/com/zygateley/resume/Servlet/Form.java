package com.zygateley.resume.Servlet;

import com.zygateley.resume.*;

import java.io.*;
import java.sql.SQLException;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

/**
 * Outputs a form for specific item selection given Resume
 */
@WebServlet("/FormServlet")
public class Form extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Form() {
        //super();
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
		out.println("<body style=\"margin:0;\" onload=\"selectMultiple(true);\">");
		out.println("<div class=\"Body\">");
		
		// Output header
		RequestDispatcher dispatch = request.getRequestDispatcher("views/Header.html");
		dispatch.include(request, response);
		
		try {
			// Form manipulation buttons
			dispatch = request.getRequestDispatcher("views/FormManipulation.html");
			dispatch.include(request, response);
			
			out.println("<form method=\"get\" action=\"resume.jsp\">");
			
			// EDUCATION
			Education.writeFormOptions(request, response, database, out);
			
			// EXPERIENCE
			// Outputs appropriate headers
			Experience.writeFormOptions(request, response, database, out);
			
			// SKILLS
			Skills.writeFormOptions(request,  response,  database, out);
			
			// SUBMIT
			out.println("<div style=\"text-align:center;margin-bottom:40px;\">");
			out.println("<input type=\"Submit\" value=\"Submit\">");
			out.println("</div>");
			out.println("</form>");
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
