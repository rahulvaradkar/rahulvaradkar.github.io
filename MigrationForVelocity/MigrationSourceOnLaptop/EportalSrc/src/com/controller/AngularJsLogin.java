package com.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.model.PersonData;

public class AngularJsLogin extends HttpServlet {
        private static final long serialVersionUID = 1L;

        public AngularJsLogin() {
                super();
        }

        protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {

			String login = request.getParameter("login");

			String pwd = request.getParameter("pwd");

			System.out.println("login : " + login);
			System.out.println("pwd : " + pwd);


			if (login.equals("rahulv") && pwd.equals("0"))
			{
				response.setContentType("application/plain");
				response.getWriter().write("Valid Login");
			}
			else
			{
					response.setContentType("application/plain");
					response.getWriter().write("Login Failed");
			}
		}


	   // Method to handle POST method request.
	//   public void doPost(HttpServletRequest request, HttpServletResponse response)
	//	  throws ServletException, IOException {
	//	  doGet(request, response);
	  // }
}