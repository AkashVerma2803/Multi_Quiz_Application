package com.swabhav.quiz_application;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class DashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }
        int userId = (int) session.getAttribute("userId");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'><title>Dashboard</title></head><body>");
        out.print("<div class='container'><h2>Welcome, " + session.getAttribute("username") + "</h2>");
        out.print("<a href='section'><button>Take a Test</button></a><a href='logout' style='margin-left:10px;'><button>Logout</button></a>");
        out.print("<h3 style='margin-top:20px'>Previous Attempts</h3>");
        out.print("<table style='width:100%;border-collapse:collapse;margin-top:10px'><tr><th>Section</th><th>Score</th><th>Time</th></tr>");
        try (Connection con = DBUtility.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT s.section_name, a.score, a.attempt_time FROM quiz_attempts a JOIN sections s ON a.section_id=s.section_id WHERE a.user_id=? ORDER BY a.attempt_time DESC")) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            boolean found = false;
            while (rs.next()) {
                found = true;
                out.print("<tr><td style='padding:8px;border:1px solid #ddd'>" + rs.getString(1) + "</td><td style='padding:8px;border:1px solid #ddd'>" + rs.getInt(2) + "</td><td style='padding:8px;border:1px solid #ddd'>" + rs.getTimestamp(3) + "</td></tr>");
            }
            if (!found) {
                out.print("<tr><td colspan='3' style='padding:8px;border:1px solid #ddd;text-align:center'>No attempts yet</td></tr>");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
        out.print("</table></div></body></html>");
    }
}
