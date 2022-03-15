package com.boardwalk.whiteboard;

import java.util.*;
import java.io.*;

public class WhiteboardTreeNode {
    int     m_id;
    int     m_collabid;
    String  m_name;
    Vector m_Tables = new Vector();

    public WhiteboardTreeNode (int a_id, int a_collabid, String a_name)
    {
	    m_name     = a_name;
	    m_collabid = a_collabid;
	    m_id = a_id;
	}

    public WhiteboardTreeNode () {
    }

    public int getCollab () {
	return m_collabid;
    }

    public int getId () {
	return m_id;
    }

    public String getName () {
        return m_name;
    }

    public boolean equals(Object w) {
	if (((Whiteboard)w).getId() == m_id)
        {
            System.out.println("match wb found ");
            return true;
        }
	else
        {
            System.out.println("match wb  not found ");
            return false;
        }
    }

    public int hashCode() {
	return m_id;
    }

	public Vector getTables()
	{
			return m_Tables;
	}

	public void printWhiteboard() {

	   System.out.println( " ID = " + m_id + " NAME " + m_name + " COLLABORATION_ID " + m_collabid);

	}
};


