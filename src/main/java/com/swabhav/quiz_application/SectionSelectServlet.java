package com.swabhav.quiz_application;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class SectionSelectServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'><title>Select Section</title></head><body>");
        out.print("<div class='container'><h2>Select Test Section</h2>");
        String msg = request.getParameter("msg");
        if (msg != null) out.print("<div style='color:#e53935;margin-bottom:10px'>" + msg + "</div>");
        out.print("<form method='post' action='section'><select name='sectionId' required style='width:94%;padding:12px;border-radius:8px;margin-bottom:10px'><option value='' disabled selected>-- Select a Section --</option>");
        try (Connection con = DBUtility.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT section_id, section_name FROM sections ORDER BY section_name")) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                out.print("<option value='" + rs.getInt(1) + "'>" + rs.getString(2) + "</option>");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
        out.print("</select><br><button type='submit'>Start Test</button></form>");
        out.print("<a href='dashboard' style='display:block;margin-top:10px'>Back to Dashboard</a></div></body></html>");
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }
        int sectionId = Integer.parseInt(request.getParameter("sectionId"));
        @SuppressWarnings("unchecked")
        java.util.Set<Integer> attempted = (java.util.Set<Integer>) session.getAttribute("attemptedSections");
        if (attempted == null) {
            attempted = new java.util.HashSet<>();
            session.setAttribute("attemptedSections", attempted);
        }
        if (attempted.contains(sectionId)) {
            response.sendRedirect("section?msg=You+cannot+retake+the+same+section+until+you+logout.");
            return;
        }
        java.util.List<Integer> questionIds = new java.util.ArrayList<>();
        try (Connection con = DBUtility.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT question_id FROM questions WHERE section_id=? ORDER BY RAND() LIMIT 5")) {
            ps.setInt(1, sectionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) questionIds.add(rs.getInt(1));
        } catch (Exception e) {
            throw new ServletException(e);
        }
        if (questionIds.size() < 5) {
            response.sendRedirect("section?msg=Not+enough+questions+in+this+section.");
            return;
        }
        session.setAttribute("currentSectionId", sectionId);
        session.setAttribute("questionIds", questionIds);
        session.setAttribute("currentIndex", 0);
        session.setAttribute("answersMap", new java.util.HashMap<Integer, String>());
        session.setAttribute("questionStart", System.currentTimeMillis());
        response.sendRedirect("quiz");
    }
}
