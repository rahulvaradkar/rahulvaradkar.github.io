package com.boardwalk.wizard;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.*;
import com.boardwalk.collaboration.CollaborationManager;
import com.boardwalk.whiteboard.WhiteboardManager;
import com.boardwalk.table.*;
import com.boardwalk.database.*;
import com.boardwalk.user.UserManager;
import com.boardwalk.query.*;
import com.boardwalk.neighborhood.NeighborhoodManager;
import com.boardwalk.util.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package

public class BoardwalkUserRequest
{
		public String nh0;

		public String nh1;
		public String nh2;
		public String nh3;

		public int  nhLevel = -1;

		public boolean  nhSecure;


		public String fName;
		public String lName;
		public String extId;
		public String Email;
		public String password;

		public String action; // ADD
		public int actionCellId;
		public String result = " ";
		public int resultCellId;
		public String comment;
		public int commentCellId;
		public int passwordCellId;
		public String lastaction;
		public int lastactionCellId;




		public void print()
		{
				System.out.println("nh0="+nh0);
				System.out.println("nh1="+nh1);
				System.out.println("nh2="+nh2);
				System.out.println("nh3="+nh3);
				System.out.println("nhLevel="+nhLevel);
				//System.out.println("fName="+fName);
				//System.out.println("lName="+lName);
				//System.out.println("Email="+Email);
				//System.out.println("password="+password);
				System.out.println("action="+action);
				System.out.println("resultCellId="+resultCellId);
				System.out.println("actionCellId="+actionCellId);
				System.out.println("commentCellId="+commentCellId);

		}

}