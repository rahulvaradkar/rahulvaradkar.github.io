package com.boardwalk.user;

import java.util.*;
import java.io.*;

public class NewUser {

   String       m_address;
   String		m_externalUserId;
   String       m_password;
   String       m_fname;
   String       m_lname;
   String       m_encryptedPassword;
   int			m_is_active;


   	// declaration modified to handle PBKDF2 encryption - shirish 20150724
	//public NewUser(String a_address, String a_password, String a_fname, String a_lname, int a_is_active) throws java.security.NoSuchAlgorithmException, java.security.NoSuchAlgorithmException, java.io.UnsupportedEncodingException
   	public NewUser(String a_address, String a_password, String a_fname, String a_lname, int a_is_active) throws java.security.NoSuchAlgorithmException, java.security.spec.InvalidKeySpecException
   	{
		m_address			= a_address;
		m_externalUserId	= a_address;
		m_password			= a_password;
		m_fname				= a_fname;
		m_lname				= a_lname;
		m_is_active			= a_is_active;
		PasswordService ps	= PasswordService.getInstance();
		m_encryptedPassword = ps.encrypt(m_password );
	}

   	// declaration modified to handle PBKDF2 encryption - shirish 20150724
	//public NewUser(String a_address, String a_externalUserId, String a_password, String a_fname, String a_lname, int a_is_active) throws java.security.NoSuchAlgorithmException, java.security.NoSuchAlgorithmException, java.io.UnsupportedEncodingException
	public NewUser(String a_address, String a_externalUserId, String a_password, String a_fname, String a_lname, int a_is_active) throws java.security.NoSuchAlgorithmException, java.security.spec.InvalidKeySpecException
	{
		m_address			= a_address;
		m_externalUserId	= a_externalUserId;
		m_password			= a_password;
		m_fname				= a_fname;
		m_lname				= a_lname;
		m_is_active			= a_is_active;
		PasswordService ps	= PasswordService.getInstance();
		m_encryptedPassword = ps.encrypt(m_password );
	}

	public String getFirstName () {
		return m_fname;
	}

	public String getLastName () {
		return m_lname;
	}

	public String getAddress () {
		return m_address;
	}

	public String getExternalUserId () {
		return m_externalUserId;
	}

	public String getEncryptedPassword() {
		return m_encryptedPassword;
	}

	public int getIsActive() {
		return m_is_active;
	}

};
