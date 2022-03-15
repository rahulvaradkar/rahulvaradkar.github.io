/*
 * Baseline.java
 *
 * Created on May 2, 2000, 9:48 AM
 */

package com.boardwalk.table;

/**
 *
 * @author  administrator
 * @version
 */
public class Baseline {

    int 				m_baseline_id;
    String  			m_baseline_name;
    String  			m_baseline_description;
    String  			m_createdBy;
    java.sql.Timestamp 	m_createdOn;


    /** Creates new Baseline */
    public Baseline( int a_baseline_id , String a_baseline_name, String a_baseline_description,String a_createdBy, java.sql.Timestamp a_createdOn ) {

        m_baseline_id = a_baseline_id;
        m_baseline_name = a_baseline_name;
        m_baseline_description = a_baseline_description;
        m_createdBy = a_createdBy;
        m_createdOn = a_createdOn;


    }

    public int getId()
    {
        return m_baseline_id;
    }


    public String getName()
    {
        return m_baseline_name;
    }

    public String getDescription()
    {

        return m_baseline_description;
    }


    public String getCreatedBy()
	{

		return m_createdBy;
	}


    public java.sql.Timestamp getCreatedOn()
	{

		return m_createdOn;
	}

    public void print()
    {
        System.out.println(" Baseline Id " + getId() + " Baseline Name " + getName() + " Baseline Description " + getDescription() );

    }

}
