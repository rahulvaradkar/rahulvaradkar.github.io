/*
 * @(#)BoardwalkUser.java    1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.neighborhood;

import java.util.*;
import com.boardwalk.user.User;
/**
 * BoardwalkUser object contains details about a user membership
 */
 public class BoardwalkUser
 {
    protected User user;

    private BoardwalkUser(){}

    protected BoardwalkUser(User a_user)
    {
        user = a_user;
    }

    public int getId()
    {
        return user.getId();
    }
	  //to return extuserid  - sujith 05/11/2016
    public String getExtUserName()
    {
        return user.getExtUserId();
    }
    
    public String getUserName()
    {
        return user.getAddress();
    }

    public String getFirstName()
    {
        return user.getFirstName();
    }

    public String getLastName()
    {
        return user.getLastName();
    }
	 public int getActive()			//Added by Rahul Varadkar on 8-August-2015
    {
        return user.getActive();
    }

 };