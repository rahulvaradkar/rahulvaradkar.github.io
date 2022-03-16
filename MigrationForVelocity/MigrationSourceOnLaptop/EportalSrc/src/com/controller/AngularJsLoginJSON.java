package com.controller;


import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.json.JSONException;

import com.google.gson.Gson;
//import com.model.MessageData;

// Added for processing 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AngularJsLoginJSON extends HttpServlet {
        private static final long serialVersionUID = 1L;

        public AngularJsLoginJSON() {
                super();
        }

		@Override
        protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {


			StringBuilder sb = new StringBuilder();
			BufferedReader br = request.getReader();
			String str = null;
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}

			//MessageData msgData = new MessageData();
			JSONObject jsonReturn = new JSONObject();
			Map<String, String> map;

			try
			{
				JSONObject jObj = new JSONObject(sb.toString());

				String login = jObj.getString("login");
				String pwd = jObj.getString("pwd");
				String role = jObj.getString("role");

				System.out.println("login POST METHOD : " + login);
				System.out.println("pwd : POST METHOD " + pwd);
				System.out.println("role : POST METHOD " + role);


				if (login.equals("rahulv") && pwd.equals("0") && role.equals("SuperUser"))
				{
					//msgData.setMessageHeader("Login Successful");
					//msgData.setMessageDetail("Valid login and password. ");

								map = new HashMap<String, String>();
								map.put("Status", "Success");
								map.put("FailureReason", "None");
								map.put("DataElements", "2");

								jsonReturn = new JSONObject();
								jsonReturn.accumulate("Header", map);

								List<String> list = new ArrayList<String>();
								list.add("Action=64");
								list.add(login);
								
								jsonReturn.accumulate("Elements", list);
								System.out.println(jsonReturn.toString());

								response.setContentType("application/json");
								response.getWriter().write(jsonReturn.toString());


					//String msg = new Gson().toJson(msgData);
					//response.setContentType("application/json");
					//response.getWriter().write(msg);
				}
				else
				{

								map = new HashMap<String, String>();
								map.put("Status", "Login Failed");
								map.put("FailureReason", "Invalid Username or Password. You need SuperUser access for successful login.");
								map.put("DataElements", "0");

								jsonReturn = new JSONObject();
								jsonReturn.accumulate("Header", map);

								List<String> list = new ArrayList<String>();
								jsonReturn.accumulate("Elements", list);
								System.out.println(jsonReturn.toString());

								response.setContentType("application/json");
								response.getWriter().write(jsonReturn.toString());
					
					
					//msgData.setMessageHeader("Login Failed");
					//msgData.setMessageDetail("Invalid login or password. Please try again.");

					//String msg = new Gson().toJson(msgData);
					//response.setContentType("application/json");
					//response.getWriter().write(msg);
				}

			}
			catch (JSONException e) {
//				msgData.setMessageHeader("Request Failed");
//				msgData.setMessageDetail("Failed to read JSon object");	
//				String msg = new Gson().toJson(msgData);

//				map = new HashMap<String, String>();
//				map.put("Status", "Failed");
//				map.put("FailureReason", "Request Failed. Failed to read JSon object.");
//				map.put("DataElements", "0");

				jsonReturn = new JSONObject();
//				jsonReturn = "{ 'Status' : 'Failed', 'FailureReason' : 'Request Failed. Failed to read JSon object.', 'DataElements', '0' }";
//				jsonReturn.accumulate("Header", map);

//				List<String> list = new ArrayList<String>();
//				jsonReturn.accumulate("Elements", list);
				System.out.println(jsonReturn.toString());
				
				response.setContentType("application/json");
				response.getWriter().write(jsonReturn.toString());
			}  
		}
}