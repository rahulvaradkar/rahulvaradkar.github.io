package com.boardwalk.whiteboard;

import java.util.*;
import java.io.*;

public class Whiteboard {
    int     m_id;
    int     m_collabid;
    String  m_name;
    int 	m_neighborhood_id;
    int   	m_status;
    double 	m_sequence_number;

    public Whiteboard (int a_id, int a_collabid, String a_name, int a_neighboorhood_id, int a_status, double a_sequence_number )
    {

	    m_name     = a_name;
	    m_collabid = a_collabid;
	    m_id = a_id;
	    m_neighborhood_id = a_neighboorhood_id;
	    m_status = a_status;
	    m_sequence_number = a_sequence_number;
	}



    public Whiteboard () {
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

    public int getStatus() {

		return m_status;
	}

	public int getNeighborhoodId() {
	  return m_neighborhood_id;
	}

	public double  getSequenceNumber() {
		  return m_sequence_number;
}

	public void printWhiteboard() {

	   System.out.println( " ID = " + m_id + " NAME " + m_name + " COLLABORATION_ID " + m_collabid + " STATUS " + m_status  + " NEIGHBORHOOD " + m_neighborhood_id + " SEQUENCE_NUMBER " + m_sequence_number );

	}
};


