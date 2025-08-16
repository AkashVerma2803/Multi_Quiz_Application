package com.swabhav.quiz_application;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ViewResultsServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect("adminLogin.html");
            return;
        }
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'><title>Results</title></head><body>");
        out.print("<div class='container'><h2>All Attempts</h2>");
        out.print("<table style='width:100%;border-collapse:collapse'><tr><th>User</th><th>Section</th><th>Score</th><th>Time</th></tr>");
        try (Connection con = DBUtility.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT u.username, s.section_name, a.score, a.attempt_time FROM quiz_attempts a JOIN users u ON a.user_id=u.user_id JOIN sections s ON a.section_id=s.section_id ORDER BY a.attempt_time DESC")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                out.print("<tr><td style='padding:8px;border:1px solid #ddd'>" + rs.getString(1) + "</td><td style='padding:8px;border:1px solid #ddd'>" + rs.getString(2) + "</td><td style='padding:8px;border:1px solid #ddd'>" + rs.getInt(3) + "</td><td style='padding:8px;border:1px solid #ddd'>" + rs.getTimestamp(4) + "</td></tr>");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
        out.print("</table><div style='margin-top:15px'><a href='addSection'><button>Add Section</button></a><a href='addQuestion' style='margin-left:10px'><button>Add Question</button></a></div></div></body></html>");
    }
}
