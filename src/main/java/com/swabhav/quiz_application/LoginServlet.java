package com.swabhav.quiz_application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String uname = request.getParameter("username");
        String pass = request.getParameter("password");
        try (Connection con = DBUtility.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT user_id, username, role FROM users WHERE username=? AND password=?")) {
            ps.setString(1, uname);
            ps.setString(2, pass);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                HttpSession session = request.getSession(true);
                session.setAttribute("userId", rs.getInt("user_id"));
                session.setAttribute("username", rs.getString("username"));
                session.setAttribute("role", rs.getString("role"));
                if ("ADMIN".equals(rs.getString("role"))) {
                    response.sendRedirect("viewResults");
                } else {
                    response.sendRedirect("dashboard");
                }
            } else {
                response.sendRedirect("login.html?err=Invalid+credentials");
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }
}
