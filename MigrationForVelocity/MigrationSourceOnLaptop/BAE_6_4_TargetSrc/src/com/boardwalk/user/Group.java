package com.boardwalk.user;
import java.util.*;
import java.io.*;

public class Group {
   int     m_id;
   int     m_external_id;
   String  m_group_name;
   int     m_isSecure;
   String  m_managed_by;


   public Group(int a_id, int a_external_id, String a_group_name, int a_isSecure, String a_managedBy) {
    m_id = a_id;
    m_group_name = a_group_name;
    m_external_id = a_external_id;
    m_isSecure = a_isSecure;
    m_managed_by = a_managedBy;
  }  
  
 
    
  public String getName () {
  return m_group_name;
  }
  

  public int getId () {
   return m_id;
  }

  
  public int getExternalId () {
   return m_external_id;
  }
  
  public int getIsSecure() {
    return m_isSecure;
  }
  
  public String getManagingUser()
  {
    return m_managed_by;
  }
  
  public void print()
  {
      
    System.out.println(" GROUP_ID = " + getId() + " GROUP_NAME = " + getName() + " GROUP_EXTERNAL_ID = " + getExternalId() + " GROUP_SECURE = " + getIsSecure()+ " GROUP_MANAGER = " + getManagingUser() );
      
  }
  
  
  
};

  
