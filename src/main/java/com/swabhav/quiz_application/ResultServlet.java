package com.swabhav.quiz_application;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class ResultServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login.html");
            return;
        }
        @SuppressWarnings("unchecked")
        List<Integer> questionIds = (List<Integer>) session.getAttribute("questionIds");
        @SuppressWarnings("unchecked")
        Map<Integer, String> answersMap = (Map<Integer, String>) session.getAttribute("answersMap");
        if (questionIds == null || answersMap == null) {
            response.sendRedirect("dashboard");
            return;
        }
        int userId = (int) session.getAttribute("userId");
        int sectionId = (int) session.getAttribute("currentSectionId");
        int score = 0;
        try (Connection con = DBUtility.getConnection()) {
            con.setAutoCommit(false);
            PreparedStatement attStmt = con.prepareStatement("INSERT INTO quiz_attempts (user_id, section_id, score) VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            attStmt.setInt(1, userId);
            attStmt.setInt(2, sectionId);
            attStmt.setInt(3, 0);
            attStmt.executeUpdate();
            ResultSet gen = attStmt.getGeneratedKeys();
            gen.next();
            int attemptId = gen.getInt(1);
            PreparedStatement qStmt = con.prepareStatement("SELECT question_id, correct_option FROM questions WHERE question_id=?");
            PreparedStatement insAns = con.prepareStatement("INSERT INTO attempt_answers (attempt_id, question_id, selected_option, is_correct) VALUES (?, ?, ?, ?)");
            for (Integer qid : questionIds) {
                qStmt.setInt(1, qid);
                ResultSet rs = qStmt.executeQuery();
                if (rs.next()) {
                    String correct = rs.getString("correct_option");
                    String selected = answersMap.getOrDefault(qid, "");
                    boolean ok = correct != null && correct.equalsIgnoreCase(selected);
                    if (ok) score += 1;
                    insAns.setInt(1, attemptId);
                    insAns.setInt(2, qid);
                    insAns.setString(3, selected == null ? "" : selected);
                    insAns.setBoolean(4, ok);
                    insAns.addBatch();
                }
            }
            insAns.executeBatch();
            PreparedStatement upd = con.prepareStatement("UPDATE quiz_attempts SET score=? WHERE attempt_id=?");
            upd.setInt(1, score);
            upd.setInt(2, attemptId);
            upd.executeUpdate();
            con.commit();
            @SuppressWarnings("unchecked")
            java.util.Set<Integer> attempted = (java.util.Set<Integer>) session.getAttribute("attemptedSections");
            if (attempted == null) attempted = new java.util.HashSet<>();
            attempted.add(sectionId);
            session.setAttribute("attemptedSections", attempted);
            session.removeAttribute("questionIds");
            session.removeAttribute("answersMap");
            session.removeAttribute("currentIndex");
            session.removeAttribute("currentSectionId");
        } catch (Exception e) {
            throw new ServletException(e);
        }
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'><title>Result</title></head><body>");
        out.print("<div class='container'><h2>Your attempt is saved</h2>");
        out.print("<a href='dashboard'><button>Back to Dashboard</button></a>");
        out.print("</div></body></html>");
    }
}
