/*
 * Member.java
 *
 * Created on July 5, 2002, 1:39 PM
 */

package com.boardwalk.member;

/**
 *
 * @author  Anuradha Kulkarni
 */
public final class Member implements java.io.Serializable {
    int     m_id;
    int     m_nhid;
    int     m_userid;
    String m_nhName = "";


    /** Creates a new instance of Member */
    public Member(int id, int userid,int nhid, String a_nhName)
    {
        m_id = id;
        m_nhid = nhid;
        m_userid = userid;
        m_nhName = a_nhName;
    }

    public int getUserId() {
        return m_userid;
    }

    public int getNeighborhoodId() {
        return m_nhid;
    }

    public int getId() {
        return m_id;
    }

    public String getNeighborhoodName()
    {
		return m_nhName;
	}

	public void print()
	{
		System.out.println("Member::Id="+getId()+"NhId="+m_nhid+"UserId="+m_userid+"NhName="+m_nhName);
	}

}
