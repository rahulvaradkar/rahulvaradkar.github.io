/*
 * @(#)BoardwalkDataArea.java 1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;

import com.boardwalk.exception.BoardwalkException;

/**
 * BoardwalkDataArea is a section of the Boardwalk table
 */
 public class BoardwalkDataArea extends BoardwalkArea
 {
	protected Vector _rowGroups;
	protected Vector _colGroups;
	protected int _xoffset;
	protected int _yoffset;
	protected boolean _displayHeaders;
	protected int _startRowPos;
	protected int _endRowPos;
	protected int _startColPos;
	protected int _endColPos;

	protected BoardwalkDataArea (
		                  BoardwalkRowGroup rowGroup,
                          BoardwalkColumnGroup colGroup,
                          int xPosition,
                          int yPosition,
                          int rowspan,
                          int colspan,
                          boolean displayHeaders)
	{
		super(1, xPosition,yPosition,colspan,rowspan);

		_rowGroups = new Vector();
		_rowGroups.addElement(rowGroup);
		_colGroups = new Vector();
		_colGroups.addElement(colGroup);
		_displayHeaders = displayHeaders;
		_startRowPos = rowGroup.getStartIndex() - 1;
		_endRowPos = rowGroup.getEndIndex();
		_startColPos = colGroup.getStartIndex() - 1;
		_endColPos = colGroup.getEndIndex();
	}

	protected BoardwalkDataArea (
						  Vector rowGroups,
                          Vector colGroups,
                          int xPosition,
                          int yPosition,
                          int rowspan,
                          int colspan,
                          boolean displayHeaders)
	{

		super(1,xPosition,yPosition,colspan,rowspan);

		_rowGroups = rowGroups;
		_colGroups = colGroups;
		_displayHeaders = displayHeaders;

		_startRowPos = ((BoardwalkRowGroup)rowGroups.firstElement()).getStartIndex() - 1;
		_endRowPos = ((BoardwalkRowGroup)rowGroups.firstElement()).getEndIndex();
		_startColPos = ((BoardwalkColumnGroup)colGroups.firstElement()).getStartIndex() - 1;
		_endColPos = ((BoardwalkColumnGroup)colGroups.firstElement()).getEndIndex();

		Iterator ri = rowGroups.iterator();
		while (ri.hasNext())
		{
			BoardwalkRowGroup brg = (BoardwalkRowGroup)ri.next();
			if(brg.getStartIndex() - 1 < _startRowPos - 1)
				_startRowPos = brg.getStartIndex() - 1;
			if(brg.getEndIndex() > _endRowPos)
				_endRowPos = brg.getEndIndex();
		}
		Iterator ci = colGroups.iterator();
		while (ci.hasNext())
		{
			BoardwalkColumnGroup bcg = (BoardwalkColumnGroup)ci.next();
			if(bcg.getStartIndex() -1 < _startColPos - 1)
				_startColPos = bcg.getStartIndex() - 1;
			if(bcg.getEndIndex() > _endColPos)
				_endColPos = bcg.getEndIndex();
		}
	}
     // set functions
     // get functions

     public Vector getRowGroups()
     {
		 return _rowGroups;
	 }

	 public Vector getColumnGroups()
	 {
		 return _colGroups;
	 }

	 public boolean getDisplayHeaders()
	 {
		 return _displayHeaders;
	 }

	 protected boolean overlaps(BoardwalkDataArea bda)
	 {
		 // check for column overlap
		 int iLeft = java.lang.Math.max(bda._startColPos, _startColPos);
		 int iTop = java.lang.Math.max(bda._startRowPos, _startRowPos);
		 int iRight = java.lang.Math.max(bda._endColPos, _endColPos);
		 int iBottom = java.lang.Math.max(bda._endRowPos, _endRowPos);
		 if (iRight > iLeft && iBottom > iTop)
		 {
			 return false;
		 }
		 else
		 {
			 return true;
		 }
	 }

	 protected void printArea()
	 {
		 System.out.println("Printing Data Area");
		 // check for contiguous rows and columns
		 Iterator ri = _rowGroups.iterator();
		 while(ri.hasNext())
		 {
			 BoardwalkRowGroup rg = (BoardwalkRowGroup)ri.next();
			 System.out.println("rg.name = " + rg.getName());
			 System.out.println("rg.si = " + rg.getStartIndex());
			 System.out.println("rg.ei = " + rg.getEndIndex());
			 System.out.println("rg.siv = " + rg.getStartIndexForView());
			 System.out.println("rg.eiv = " + rg.getEndIndexForView());
			 System.out.println("rg.display = " + rg.displayInView());
		 }

		 Iterator ci = _colGroups.iterator();
		 while(ci.hasNext())
		 {
			 BoardwalkColumnGroup cg = (BoardwalkColumnGroup)ci.next();
			 System.out.println("cg.name = " + cg.getName());
			 System.out.println("cg.si = " + cg.getStartIndex());
			 System.out.println("cg.ei = " + cg.getEndIndex());
			 System.out.println("cg.siv = " + cg.getStartIndexForView());
			 System.out.println("cg.eiv = " + cg.getEndIndexForView());
			 System.out.println("cg.display = " + cg.displayInView());
		 }
		 System.out.println("startRowPos = " + _startRowPos);
		 System.out.println("endRowPos = " + _endRowPos);
		 System.out.println("startColPos = " + _startColPos);
		 System.out.println("endColPos = " + _endColPos);
	 }
 };