package com.boardwalk.user;
import java.util.*;
import java.io.*;

public class NewGroup {
   
    int     m_external_id;
    String  m_group_name;
    int     m_isSecure;
    
  public NewGroup(int a_external_id, String a_group_name, int a_isSecure ) {
    m_group_name = a_group_name;
    m_external_id = a_external_id;
    m_isSecure = a_isSecure;
  }  
    
  public String getName () {
  return m_group_name;
  }

  public int getExternalId () {
   return m_external_id;
  }
  
  public int getIsSecure() {
   return m_isSecure;   
      
  }
};

  
