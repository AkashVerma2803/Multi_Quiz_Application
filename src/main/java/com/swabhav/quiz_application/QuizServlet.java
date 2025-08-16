package com.swabhav.quiz_application;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class QuizServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("userId") == null) {
			response.sendRedirect("login.html");
			return;
		}
		@SuppressWarnings("unchecked")
		List<Integer> questionIds = (List<Integer>) session.getAttribute("questionIds");
		if (questionIds == null) {
			response.sendRedirect("dashboard");
			return;
		}
		int currentIndex = (int) session.getAttribute("currentIndex");
		if (currentIndex >= questionIds.size()) {
			response.sendRedirect("result");
			return;
		}
		int qid = questionIds.get(currentIndex);
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
		out.print(
				"<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'><title>Quiz</title></head><body>");
		out.print("<div class='container'><div class='timer' id='timer'>Time left: 30s</div>");
		try (Connection con = DBUtility.getConnection();
				PreparedStatement ps = con.prepareStatement(
						"SELECT question_text, option_a, option_b, option_c, option_d FROM questions WHERE question_id=?")) {
			ps.setInt(1, qid);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				out.print("<h3>Question " + (currentIndex + 1) + " of " + questionIds.size() + "</h3>");
				out.print("<form name='quizForm' method='post' action='quiz'>");
				out.print("<input type='hidden' name='questionId' value='" + qid + "'>");
				out.print("<p style='text-align:left'>" + rs.getString(1) + "</p>");
				
				out.print("<div class='options' style='text-align:left'>");
				out.print("<label class='option'><input type='radio' name='answer' value='A' > " + rs.getString(2) + "</label><br>");
				out.print("<label class='option'><input type='radio' name='answer' value='B'> " + rs.getString(3) + "</label><br>");
				out.print("<label class='option'><input type='radio' name='answer' value='C'> " + rs.getString(4) + "</label><br>");
				out.print("<label class='option'><input type='radio' name='answer' value='D'> " + rs.getString(5) + "</label><br>");
				out.print("</div>");

				
				out.print("<button type='submit' style='margin-top:15px'>Submit Answer</button>");
				out.print("</form>");
			} else {
				response.sendRedirect("result");
				return;
			}
		} catch (Exception e) {
			throw new ServletException(e);
		}
		out.print(
				"<script>let timeLeft=30;let t=setInterval(()=>{document.getElementById('timer').innerText='Time left: '+timeLeft+'s';timeLeft--;if(timeLeft<0){clearInterval(t);document.forms['quizForm'].submit();}},1000);</script>");
		out.print("</div></body></html>");
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("userId") == null) {
			response.sendRedirect("login.html");
			return;
		}
		String answer = request.getParameter("answer");
		String qidStr = request.getParameter("questionId");
		if (qidStr == null) {
			response.sendRedirect("dashboard");
			return;
		}
		int qid = Integer.parseInt(qidStr);
		@SuppressWarnings("unchecked")
		Map<Integer, String> answersMap = (Map<Integer, String>) session.getAttribute("answersMap");
		answersMap.put(qid, answer == null ? "" : answer);
		int currentIndex = (int) session.getAttribute("currentIndex");
		session.setAttribute("currentIndex", currentIndex + 1);
		response.sendRedirect("quiz");
	}
}