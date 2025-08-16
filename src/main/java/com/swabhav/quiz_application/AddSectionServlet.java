package com.swabhav.quiz_application;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class AddSectionServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        HttpSession session = request.getSession(false);
        if (session == null || !"ADMIN".equals(session.getAttribute("role"))) {
            response.sendRedirect("adminLogin.html");
            return;
        }
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print("<!DOCTYPE html><html><head><link rel='stylesheet' href='style.css'><title>Add Section</title></head><body>");
        out.print("<div class='container'><h2>Add Section</h2>");
        String msg = request.getParameter("msg");
        String err = request.getParameter("err");
        if (msg != null) out.print("<div style='color:green;margin-bottom:10px'>" + msg + "</div>");
        if (err != null) out.print("<div style='color:red;margin-bottom:10px'>" + err + "</div>");
        out.print("<form method='post' action='addSection'>");
        out.print("<input type='text' name='sectionName' placeholder='Section Name' required>");
        out.print("<button type='submit'>Add Section</button>");
        out.print("</form>");
        out.print("<a href='' style='display:block;margin-top:15px'>⬅ Back to Dashboard</a>");
        out.print("</div></body></html>");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String name = request.getParameter("sectionName");
        try (Connection con = DBUtility.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO sections (section_name) VALUES (?)")) {
            ps.setString(1, name);
            ps.executeUpdate();
            response.sendRedirect("addSection?msg=Section+added");
        } catch (Exception e) {
            response.sendRedirect("addSection?err=Unable+to+add");
        }
    }
}
