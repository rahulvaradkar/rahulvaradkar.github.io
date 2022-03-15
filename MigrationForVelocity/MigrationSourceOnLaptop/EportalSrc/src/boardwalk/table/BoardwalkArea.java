/*
 * @(#)BoardwalkArea.java 1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;

import com.boardwalk.exception.BoardwalkException;

/**
 * BoardwalkArea is a section of the Boardwalk table
 */
 abstract public class BoardwalkArea
 {
    protected int _xoffset;
    protected int _yoffset;
    protected int _rowspan;
    protected int _colspan;
    protected int _type;

    private BoardwalkArea(){}

    protected BoardwalkArea (
						  int type,
                          int xPosition,
                          int yPosition,
                          int hspan,
                          int vspan)
    {
        _xoffset = xPosition;
        _yoffset = yPosition;
        _rowspan = hspan;
        _colspan = vspan;
        _type = type;
    }
     // set functions
     // get functions
     public int getPositionX()
     {
		 return _xoffset;
	 }
     public int getPositionY()
     {
		 return _yoffset;
	 }
     public int getHorizontalSpan()
     {
		 return _rowspan;
	 }
     public int getVerticalSpan()
     {
		 return _colspan;
	 }
	 public int getType()
	 {
		 return _type;
	 }

 };