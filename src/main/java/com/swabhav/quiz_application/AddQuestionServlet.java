package com.swabhav.quiz_application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class AddQuestionServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect("adminLogin.html");
            return;
        }
        response.sendRedirect("addQuestion.html");
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        int sectionId = Integer.parseInt(request.getParameter("sectionId"));
        String q = request.getParameter("question");
        String a = request.getParameter("a");
        String b = request.getParameter("b");
        String c = request.getParameter("c");
        String d = request.getParameter("d");
        String corr = request.getParameter("correct");
        try (Connection con = DBUtility.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO questions (section_id,question_text,option_a,option_b,option_c,option_d,correct_option) VALUES (?,?,?,?,?,?,?)")) {
            ps.setInt(1, sectionId);
            ps.setString(2, q);
            ps.setString(3, a);
            ps.setString(4, b);
            ps.setString(5, c);
            ps.setString(6, d);
            ps.setString(7, corr);
            ps.executeUpdate();
            response.sendRedirect("addQuestion.html?msg=Question+added");
        } catch (Exception e) {
            response.sendRedirect("addQuestion.html?err=Unable+to+add");
        }
    }
}
