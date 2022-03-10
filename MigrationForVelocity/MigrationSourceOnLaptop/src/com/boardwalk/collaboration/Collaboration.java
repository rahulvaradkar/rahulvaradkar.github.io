/*******************************************************************************
 * Copyright (c) 2000, 2003 Boardwalk Tech Inc
 * All rights reserved. Patents pending.
 *******************************************************************************/


package com.boardwalk.collaboration;
import java.util.*;
import java.io.*;

public class Collaboration {
   int     m_id;
   String  m_name;
   String  m_purpose;
   String  m_manager;
   int  m_access;
   String  m_neighborhood;
   int  m_status;
   int  m_private_access;
   int  m_peer_access;
   int  m_friend_access;


  public Collaboration (int a_collab_id, String a_name, String a_purpose, String a_manager, int a_access, String a_neighborhood, int a_status, int a_private_access, int a_peer_access, int a_friend_access  ) {
    m_id = a_collab_id;
    m_name    = a_name;
    m_purpose = a_purpose;
    m_manager  = a_manager;
    m_access   = a_access;
    m_neighborhood = a_neighborhood;
    m_status = a_status;
    m_private_access = a_private_access;
    m_peer_access = a_peer_access;
    m_friend_access = a_friend_access;
  }

  public int getId () {
  return m_id;
  }

  public String getName () {
  return m_name;
  }
  public String getPurpose () {
  return m_purpose;
  }

  public String getManager () {
  return m_manager;
  }

  public int getAccess () {
  return m_access;
  }

  public String getAccessLevel()
  {
   if ( m_access == 0 )
          return "NO";
      else
          if ( m_access == 1 )
              return "RO";
          else
              return "RW";

  }

  public String getPrivateAccessLevel()
  {
   if ( m_private_access == 0 )
          return "NO";
      else
          if ( m_private_access == 1 )
              return "RO";
          else
              return "RW";

  }


  public String getPeerAccessLevel()
  {
   if ( m_peer_access == 0 )
          return "NO";
      else
          if ( m_peer_access == 1 )
              return "RO";
          else
              return "RW";

  }


  public String getFriendAccessLevel()
  {
   if ( m_friend_access == 0 )
          return "NO";
      else
          if ( m_friend_access == 1 )
              return "RO";
          else
              return "RW";

  }




  public String getNeighborhood () {
  return m_neighborhood;
  }

  public int getStatus() {
   return m_status;
  }

  public void printCollaboration()
  {
    System.out.println( " ID = " + m_id + " NAME " + m_name + " PURPOSE " + m_purpose + " ACCESS " + m_access + " STATUS " + m_status  + " MANAGER " + m_manager + " NEIGHBORHOOD " + m_neighborhood );
  }

};


