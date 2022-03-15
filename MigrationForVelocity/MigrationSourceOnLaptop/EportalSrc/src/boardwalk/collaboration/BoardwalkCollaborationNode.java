/*
 * @(#)BoardwalkCollaborationNode.java    1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.collaboration;

import java.util.*;
import com.boardwalk.collaboration.*;
import com.boardwalk.whiteboard.*;
/**
 * BoardwalkCollaborationNode object contains information about whiteboards
 * and tables contained in a single collaboration
 */
 public class BoardwalkCollaborationNode
 {
    protected CollaborationTreeNode ctn;

    private BoardwalkCollaborationNode(){}

    protected BoardwalkCollaborationNode(CollaborationTreeNode a_ctn)
    {
        ctn = a_ctn;
    }

	public int getId ()
	{
		return ctn.getId();
	}

	public String getName ()
	{
		return ctn.getName();
	}
	public String getPurpose ()
	{
		return ctn.getPurpose();
	}

	public Vector getWhiteboards ()
	{
		Vector wbl = ctn.getWhiteboards();
		Iterator wbli = wbl.iterator();
		Vector bwbl = new Vector();
		while (wbli.hasNext())
		{
			WhiteboardTreeNode wbtn = (WhiteboardTreeNode)wbli.next();
			bwbl.addElement(new BoardwalkWhiteboardNode(wbtn));
		}

		return bwbl;
	}

 };