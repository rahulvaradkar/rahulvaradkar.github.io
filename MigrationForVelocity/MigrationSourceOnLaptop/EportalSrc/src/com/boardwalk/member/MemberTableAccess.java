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
public final class MemberTableAccess {
    int     m_id;
    int     m_nhid;
    String m_nh_name;
    int     m_userid;
   int  	 	 m_acl;
   String 		m_relationship;

    /** Creates a new instance of Member */
    public MemberTableAccess(int id, int nhid, String a_nh_name, int userid, int a_acl, String a_relationship)
    {
        m_id = id;
        m_nhid = nhid;
       m_nh_name= a_nh_name;
        m_userid = userid;
        m_acl =  a_acl;
       m_relationship = a_relationship;
    }

    public int getUserId() {
        return m_userid;
    }

    public int getNeighborhoodId() {
        return m_nhid;
    }

    public String  getNeighborhoodName() {
	        return m_nh_name;
    }


    public int getId() {
        return m_id;
    }

	public int getACL() {
        return m_acl;
    }

    public String getRelationship() {
	        return m_relationship;
	    }

	public void print()
	{
		System.out.println("MEMBER_ID = " +getId()+ " NH_ID " +getNeighborhoodId()+   " USER_ID " + getUserId() + " ACL  " +getACL()+ "  REL " +getRelationship() );
	}


}
