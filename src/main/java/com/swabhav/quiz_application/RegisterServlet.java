package com.swabhav.quiz_application;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.*;

public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String uname = request.getParameter("username");
        String pass = request.getParameter("password");
        try (Connection con = DBUtility.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO users (username,password,role) VALUES (?,?, 'USER')")) {
            ps.setString(1, uname);
            ps.setString(2, pass);
            ps.executeUpdate();
            response.sendRedirect("login.html?msg=Registered+successfully");
        } catch (Exception e) {
            response.sendRedirect("register.html?err=Unable+to+register");
        }
    }
}
