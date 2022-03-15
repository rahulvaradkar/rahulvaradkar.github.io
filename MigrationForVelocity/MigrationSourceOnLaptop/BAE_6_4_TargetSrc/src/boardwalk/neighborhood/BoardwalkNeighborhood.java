/*
 * @(#)BoardwalkNeighborhood.java    1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.neighborhood;

import java.util.*;
import com.boardwalk.neighborhood.*;
/**
 * BoardwalkNeighborhood object contains details about a Boardwalk Neighborhood
 */
 public class BoardwalkNeighborhood
 {
    protected Neighborhood nh;

    private BoardwalkNeighborhood(){}

    protected BoardwalkNeighborhood(Neighborhood a_nh)
    {
        nh = a_nh;
    }

    public int getId()
    {
        return nh.getId();
    }

    public int getLevel()
    {
        return nh.getLevels();
    }

    public boolean isSecure()
    {
        return nh.isSecure();
    }

    public String getName()
    {
        return nh.getName();
    }
    public Vector getRelations()
    {
        return nh.getRelations();
    }
 };