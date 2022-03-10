/*
 * @(#)BoardwalkNeighborhoodNode.java    1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.neighborhood;

import java.util.*;
import com.boardwalk.neighborhood.*;
/**
 * BoardwalkNeighborhoodNode object contains information about the
 * neighborhood and children of the neighborhood
 */
 public class BoardwalkNeighborhoodNode
 {
    protected NHTree nht;

    private BoardwalkNeighborhoodNode(){}

    protected BoardwalkNeighborhoodNode(NHTree a_nht)
    {
        nht = a_nht;
    }
/* relations not there
    public BoardwalkNeighborhood getNeighborhood() {
		return new BoardwalkNeighborhood(nht.getNeighborhood());
    }
*/
    /**
    * Fetch a children neighborhoods
    * @return a <code>Vector</code> of <code>BoardwalkNeighborhoodNode</code>
    *	objects
    */
    public Vector getChildren()
    {
		Vector children = new Vector();
		Vector nhtcl = nht.getChildren();
		Iterator nhtcli = nhtcl.iterator();
		while (nhtcli.hasNext())
		{
			NHTree nht = (NHTree)nhtcli.next();
			children.addElement(new BoardwalkNeighborhoodNode(nht));
		}
		return children;
    }
    /**
    * Get the neighborhood at this node
    * @return a <code>BoardwalkNeighborhood</code> object
    */
    public BoardwalkNeighborhood getNeighborhood()
    {
		return new BoardwalkNeighborhood(nht.getNeighborhood());
	}

 };