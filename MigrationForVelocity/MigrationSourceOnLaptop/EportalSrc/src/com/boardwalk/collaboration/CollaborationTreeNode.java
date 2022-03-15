package com.boardwalk.collaboration;
import java.util.*;
import java.io.*;

public class CollaborationTreeNode {
   int     m_id;
   String  m_name;
   String  m_purpose;
   Vector m_Whiteboards = new Vector();

  public CollaborationTreeNode (int a_collab_id, String a_name, String a_purpose  ) {
    m_id = a_collab_id;
    m_name    = a_name;
    m_purpose = a_purpose;
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

   public Vector getWhiteboards () {
    return m_Whiteboards;
  }

  public void printCollaboration()
  {
    System.out.println( " ID = " + m_id + " NAME " + m_name + " PURPOSE " + m_purpose);
  }

};


