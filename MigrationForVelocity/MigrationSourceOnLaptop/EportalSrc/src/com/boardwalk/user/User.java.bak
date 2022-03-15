package com.boardwalk.user;
import java.util.*;
import java.io.*;

public class User {
   int     m_id;
   String  m_address;
   String m_password;
   String m_firstName;
   String m_lastName;
   int m_Active;			//Added by Rahul Varadkar on 08-August-2015
   boolean m_addUser;
   boolean m_deactivateUser;
   boolean m_addMember;
   boolean m_deleteMember;

 public User ()
	{}


  public User (int a_id, String a_address) {
    m_id = a_id;
    m_address = a_address;
    m_password="No Permission to see password";
    m_firstName = null;
    m_lastName = null;
  }


  public User (int a_id, String a_address, String a_firstName, String a_lastName) {
    m_id = a_id;
    m_address = a_address;
    m_password="No Permission to see password";
    m_firstName = a_firstName;
    m_lastName = a_lastName;
  }

//added isActive by Rahul Varadkar on 08-August-2015
  public User (int a_id, String a_address, String a_firstName, String a_lastName, int isActive) {
    m_id = a_id;
    m_address = a_address;
    m_password="No Permission to see password";
    m_firstName = a_firstName;
    m_lastName = a_lastName;
	m_Active = isActive;
  }

  public String getAddress () {
  	return m_address;
  }


  public int getId () {
	return m_id;
  }


  public String getPassword()
  {
      return m_password;
  }


  public int getActive()				//added isActive by Rahul Varadkar on 08-August-2015
  {
      return m_Active;
  }

  public void setPassword(String a_password)
  {
       m_password = a_password;
  }

  public String getFirstName()
  {
	  return m_firstName;
  }

  public String getLastName()
  {
	  return m_lastName;
  }


public boolean getAddUser()
  {
	  return m_addUser;
  }
  public boolean getDeactivateUser()
  {
	  return m_deactivateUser;
  }

public boolean getAddMember()
  {
	  return m_addMember;
  }

public boolean getDeleteMember()
  {
	  return m_deleteMember;
  }




  public void setAddUser(boolean a_addUser)
  {
       m_addUser = a_addUser;
  }
  public void setDeactivateUser(boolean a_deactivateUser)
  {
       m_deactivateUser = a_deactivateUser;
  }
  public void setAddMember(boolean a_addMember)
  {
       m_addMember = a_addMember;
  }
  public void setDeleteMember(boolean a_deleteMember)
  {
       m_deleteMember = a_deleteMember;
  }

};

