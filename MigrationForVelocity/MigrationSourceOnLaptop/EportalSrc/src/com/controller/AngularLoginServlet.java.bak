package com.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.model.PersonData;

public class AngularLoginServlet extends HttpServlet {
        private static final long serialVersionUID = 1L;

        public AngularLoginServlet() {
                super();
        }

        protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
                PersonData personData = new PersonData();
                personData.setFirstName("Rahul");
                personData.setLastName("Varadkar");

                String json = new Gson().toJson(personData);
                response.setContentType("application/json");
                response.getWriter().write(json);
        }
}