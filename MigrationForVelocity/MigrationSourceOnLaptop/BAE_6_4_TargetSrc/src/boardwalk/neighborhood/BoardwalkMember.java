/*
 * @(#)BoardwalkMember.java    1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.neighborhood;

import java.util.*;
import com.boardwalk.member.Member;
/**
 * BoardwalkMember object contains details about a user membership
 */
 public class BoardwalkMember
 {
    protected Member member;

    private BoardwalkMember(){}

    BoardwalkMember(Member a_member)
    {
        member = a_member;
    }

    public BoardwalkMember(int id, int userId,int neighborhoodId, String neighborhoodName)
    {
        member = new Member(id, userId, neighborhoodId, neighborhoodName);
    }
    public int getId()
    {
        return member.getId();
    }

    public int getUserId()
    {
        return member.getUserId();
    }

    public int getNeighborhoodId()
    {
        return member.getNeighborhoodId();
    }

    public String getNeighborhoodName()
    {
		return member.getNeighborhoodName();
	}
 };