/*
 * @(#)BoardwalkTableNode.java    1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.collaboration;

import java.util.*;
import com.boardwalk.collaboration.*;
import com.boardwalk.whiteboard.*;
import com.boardwalk.table.*;
/**
 * BoardwalkTableNode object contains information about whiteboards
 * and tables contained in a single collaboration
 */
 public class BoardwalkTableNode
 {
    protected TableTreeNode ttn;

    private BoardwalkTableNode(){}

    protected BoardwalkTableNode(TableTreeNode a_ttn)
    {
        ttn = a_ttn;
    }

	public int getId ()
	{
		return ttn.getId();
	}

	public String getName ()
	{
		return ttn.getName();
	}

	public String getDescription ()
	{
		return ttn.getPurpose();
	}

	public int getWhiteboardId()
	{
		return ttn.getWhiteboardId();
	}

 };