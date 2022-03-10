package com.boardwalk.database;

import java.util.*;
import java.io.*;

public class Transaction {
   int m_id;
   int m_created_by_userId;
   String	m_created_by_email_address;
   long 	m_created_on;
   String  	m_created_on_str;
   String	m_description;
   String	m_comment;


	public Transaction(int a_id, int a_created_by_userId, String a_created_by_email_address, long a_created_on, String a_description, String a_comment)
	{
		m_id = a_id;
		m_created_by_userId = a_created_by_userId;
		m_created_by_email_address = a_created_by_email_address;
		m_created_on = a_created_on;
		m_description = a_description;
		m_comment = a_comment;
		m_created_on_str = null;
	}
	public Transaction(int a_id, int a_created_by_userId, String a_created_by_email_address, String a_created_on, String a_description, String a_comment)
	{
		m_id = a_id;
		m_created_by_userId = a_created_by_userId;
		m_created_by_email_address = a_created_by_email_address;
		m_created_on_str = a_created_on;
		m_description = a_description;
		m_comment = a_comment;
		m_created_on = -1;
	}

	//get
	public int getId () {
		return m_id;
	}

	public int getCreatedByUserId () {
		return m_created_by_userId;
	}
	public String getCreatedByUserAddress () {
		return m_created_by_email_address;
	}

	public String getCreatedOn () {
		return m_created_on_str;
	}
	public long getCreatedOnTime () {
		return m_created_on;
	}
	public String getDescription () {
		return m_description;
	}

	public String getComment () {
		return m_comment;
	}


};