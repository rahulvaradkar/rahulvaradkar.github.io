/*
 * @(#)BoardwalkWhiteboardNode.java    1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.collaboration;

import java.util.*;
import com.boardwalk.collaboration.*;
import com.boardwalk.whiteboard.*;
import com.boardwalk.table.TableTreeNode;
/**
 * BoardwalkWhiteboardNode object contains information about whiteboards
 * and tables contained in a single collaboration
 */
 public class BoardwalkWhiteboardNode
 {
    protected WhiteboardTreeNode wbn;

    private BoardwalkWhiteboardNode(){}

    protected BoardwalkWhiteboardNode(WhiteboardTreeNode a_wbn)
    {
        wbn = a_wbn;
    }

    public int getParentCollabId ()
    {
		return wbn.getCollab();
    }

    public int getId ()
    {
		return wbn.getId();
    }

    public String getName ()
    {
        return wbn.getName();
    }

	public Vector getTables()
	{
		Vector tv = wbn.getTables();
		Iterator tvi = tv.iterator();
		Vector btv = new Vector();
		while (tvi.hasNext())
		{
			TableTreeNode ttn = (TableTreeNode)tvi.next();
			btv.addElement(new BoardwalkTableNode(ttn));
		}

		return btv;
	}

 };