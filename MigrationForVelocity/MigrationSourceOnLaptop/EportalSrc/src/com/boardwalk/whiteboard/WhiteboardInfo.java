package com.boardwalk.whiteboard;

import java.util.*;
import java.io.*;

public class WhiteboardInfo {
    int     m_wb_id;
    String  m_wb_name;
    int     m_wb_status;
    
    int     m_collab_id;
    String  m_collab_name;
    String  m_collab_purpose;
    String  m_collab_manager;
    int  m_collab_access;
    String  m_neighborhood;
    int  m_collab_status;
    int  m_collab_private_access;
    int  m_collab_peer_access;
    int  m_collab_friend_access;


  public WhiteboardInfo (int a_collab_id, String a_collab_name, String a_collab_purpose, String a_collab_manager, int a_collab_access, String a_neighborhood, int a_collab_status, int a_collab_private_access, int a_collab_peer_access, int a_collab_friend_access, int a_wb_id, String a_wb_name, int a_wb_status  ) {
    m_collab_id = a_collab_id;
    m_collab_name    = a_collab_name;
    m_collab_purpose = a_collab_purpose;
    m_collab_manager  = a_collab_manager;
    m_collab_access   = a_collab_access;
    m_neighborhood = a_neighborhood;
    m_collab_status = a_collab_status;
    m_collab_private_access = a_collab_private_access;
    m_collab_peer_access = a_collab_peer_access;
    m_collab_friend_access = a_collab_friend_access;
    
    m_wb_id=a_wb_id;
    m_wb_name=a_wb_name;
    m_wb_status=a_wb_status;
  
    
  }  
  
  public int getCollaborationId () {
  return m_collab_id;
  }
  
  public int getWhiteboardId () {
  return m_wb_id;
  }
  
  public String getCollaborationName () {
  return m_collab_name;
  }
  
  public String getWhiteboardName () {
  return m_wb_name;
  }
  
  public String getCollaborationPurpose () {
  return m_collab_purpose;
  }
  
  public String getCollaborationManager () {
  return m_collab_manager;
  }
  
  public int getCollaborationAccess () {
  return m_collab_access;
  }
  
  public String getCollaborationAccessLevel()
  {
   if ( m_collab_access == 0 )
          return "NO";
      else
          if ( m_collab_access == 1 )
              return "RO";
          else
              return "RW";
          
  }
  
  public String getCollaborationPrivateAccessLevel()
  {
   if ( m_collab_private_access == 0 )
          return "NO";
      else
          if ( m_collab_private_access == 1 )
              return "RO";
          else
              return "RW";
          
  }
  
  
  public String getCollaborationPeerAccessLevel()
  {
   if ( m_collab_peer_access == 0 )
          return "NO";
      else
          if ( m_collab_peer_access == 1 )
              return "RO";
          else
              return "RW";
          
  }
  
  
  public String getCollaborationFriendAccessLevel()
  {
   if ( m_collab_friend_access == 0 )
          return "NO";
      else
          if ( m_collab_friend_access == 1 )
              return "RO";
          else
              return "RW";
          
  }
  
  
  
  
  public String getNeighborhood () {
  return m_neighborhood;
  }
  
  public int getCollaborationStatus() {
   return m_collab_status;   
  }
  
  public int getWhiteboardStatus() {
   return m_wb_status;   
  }
  

  
};


