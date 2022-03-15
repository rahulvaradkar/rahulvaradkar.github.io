/*
 * @(#)BoardwalkTableDisplay.java 1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;

import com.boardwalk.exception.BoardwalkException;

/**
 * BoardwalkTableDisplay contains information about the layout display of
 * a Boardwalk table
 */
 public class BoardwalkTableDisplay
 {
     private String _name;
     private int _tableId;
/*
     private Vector _rowGroups; //name -> Vector of rowids
     private Vector _columnGroups; //name -> Vector of columnids
*/
     private Vector _areas;
     private Vector _rowGrps;
     private Vector _colGrps;
     private int _numRows;
     private int _numCols;
     private int _positionR;
     private int _positionC;
     private boolean _transpose;
	 private String _view;
	 private int _mode;

     private BoardwalkTableDisplay(){}

     protected BoardwalkTableDisplay ( String name, int tableId)
     {
        _name = name;
        _tableId = tableId;
        /*
        _rowGroups = new Vector();
        _columnGroups = new Vector();
        */
        _areas = new Vector();
        _rowGrps = new Vector();
        _colGrps = new Vector();
        _numRows = 0;
        _numCols = 0;
        _positionR = 1;
        _positionC = 1;
        _transpose = false;
		_view = "LATEST";
		_mode = 0;
     }

     // set functions
	 public void setView(String view)
	 {
		 _view = view;
	 }

	 public void setMode(int mode)
	 {
		 _mode = mode;
	 }

     public void setTranspose(boolean val)
     {
         _transpose = val;
     }

     public void setPlacement(int row, int column)
     {
         _positionR = row;
         _positionC = column;
     }
/*
     public BoardwalkColumnGroup addColumnGroup(String groupName,
     							int startIndex,
     							int endIndex,
     							boolean breakAfterGroup)
     {
		BoardwalkColumnGroup bcg = new BoardwalkColumnGroup(groupName,
        													startIndex,
        													endIndex,
        													breakAfterGroup);

        _columnGroups.addElement(bcg);

        return bcg;
     }

     public BoardwalkRowGroup addRowGroup(String groupName,
     							int startIndex,
     							int endIndex,
     							boolean breakAfterGroup)
     {
		 BoardwalkRowGroup brg = new BoardwalkRowGroup(groupName,
														startIndex,
														endIndex,
														breakAfterGroup);
         _rowGroups.addElement(brg);

         return brg;
     }
*/
     public void addDataArea(
						BoardwalkRowGroup rowGroup,
						BoardwalkColumnGroup columnGroup,
						int xoffset,
						int yoffset,
						int rowspan,
						int colspan,
						boolean displayHeaders
						)
	 throws BoardwalkException
     {
		 BoardwalkDataArea me = new BoardwalkDataArea(
			 									 rowGroup,
         										 columnGroup,
         										 xoffset,
         										 yoffset,
         										 rowspan,
         										 colspan,
         										 displayHeaders);

		 // check is the area overlaps with existent area
		 Iterator ari = _areas.iterator();
		 while(ari.hasNext())
		 {
			 BoardwalkDataArea bda = (BoardwalkDataArea)ari.next();
			 if (me.overlaps(bda))
			 {
				System.out.println("Area Overlaps!!!!!");
				System.out.println("New Area");
				me.printArea();
				System.out.println("Overlapping Area");
				bda.printArea();
				throw new BoardwalkException(15100);
			 }
		 }


         _areas.addElement(me);
		System.out.println("Added area to display = " + _name);
		me.printArea();

		// add the master rg and cg info to the display
		if (!_rowGrps.contains(rowGroup))
		{
			_rowGrps.addElement(rowGroup);
			_numRows = _numRows + rowGroup.getNumRows();
		}
		if (!_colGrps.contains(columnGroup))
		{
			_colGrps.addElement(columnGroup);
			_numCols = _numCols + columnGroup.getNumColumns();
		}
     }
     public void addDataArea(
						Vector rowGroups,
						Vector colGroups,
						int xoffset,
						int yoffset,
						int rowspan,
						int colspan,
						boolean displayHeaders
						)
	 throws BoardwalkException
     {
		 BoardwalkDataArea me = new BoardwalkDataArea(
			 								rowGroups,
			 								colGroups,
			 								xoffset,
			 								yoffset,
			 								rowspan,
			 								colspan,
			 								displayHeaders);

		 // check is the area overlaps with existent area
		 Iterator ari = _areas.iterator();
		 while(ari.hasNext())
		 {
			 BoardwalkDataArea bda = (BoardwalkDataArea)ari.next();
			 if (me.overlaps(bda))
			 {
				System.out.println("Area Overlaps!!!!!");
				System.out.println("New Area");
				me.printArea();
				System.out.println("Overlapping Area");
				bda.printArea();
				throw new BoardwalkException(15100);
			 }
		 }

         _areas.addElement(me);

		System.out.println("Added area to display = " + _name);
		me.printArea();

		// add the master rg and cg info to the display
		Iterator ri = rowGroups.iterator();
		while (ri.hasNext())
		{
			BoardwalkRowGroup rowGroup = (BoardwalkRowGroup)ri.next();
			if (!_rowGrps.contains(rowGroup))
			{
				_rowGrps.addElement(rowGroup);
				_numRows = _numRows + rowGroup.getNumRows();
			}
		}
		Iterator ci = colGroups.iterator();
		while (ci.hasNext())
		{
			BoardwalkColumnGroup columnGroup = (BoardwalkColumnGroup)ci.next();
			if (!_colGrps.contains(columnGroup))
			{
				_colGrps.addElement(columnGroup);
				_numCols = _numCols + columnGroup.getNumColumns();
			}
	 	}

		System.out.println("The display row/column groups");
	 	printRowAndColumnGroups();
     }

     protected void resetDisplay()
     {

	 }

     // get functions
     public String getName()
     {
         return _name;
     }

	 public String getView()
	 {
		 return _view;
	 }

     public int getTableId()
     {
         return _tableId;
     }
/*
     public Vector getRowGroups()
     {
         return _rowGroups;
     }

     public Vector getColumnGroups()
     {
         return _columnGroups;
     }
*/
     public Vector getAreas()
     {
         return _areas;
     }

     public int getRowPlacement()
     {
		 return _positionR;
	 }

	 public int getColumnPlacement()
	 {
		return _positionC;
	 }

	 public boolean  getTranspose()
	 {
	 	return _transpose;
	 }

	 protected Vector getRowGroups()
	 {
		 return _rowGrps;
	 }

	 protected Vector getColumnGroups()
	 {
		 return _colGrps;
	 }

	 protected int getNumRows()
	 {
		 return _numRows;
	 }

	 protected int getNumColumns()
	 {
		 return _numCols;
	 }

	 protected int getMode()
	 {
		 return _mode;
	 }

	 protected boolean checkDisplay()
	 {
		 System.out.println("TableDisplay::checkDisplay()");
		 // sort the row and column groups
		 Collections.sort(_rowGrps);
		 Collections.sort(_colGrps);

		 // check for contiguous rows and columns
		 int rsidx = 1;
		 Iterator ri = _rowGrps.iterator();
		 while(ri.hasNext())
		 {
			 BoardwalkRowGroup rg = (BoardwalkRowGroup)ri.next();
			 if (rg.displayInView() == false) continue;

			 if (rg.getStartIndexForView() != rsidx)
			 {
			 	return false;
			 }

			 rsidx = rg.getEndIndexForView() + 1;
		 }
		 int csidx = 1;
		 Iterator ci = _colGrps.iterator();
		 while(ci.hasNext())
		 {
			 BoardwalkColumnGroup cg = (BoardwalkColumnGroup)ci.next();
			 if (cg.displayInView() == false) continue;

			 if (cg.getStartIndexForView() != csidx)
			 {
				 return false;
			 }

			 csidx = cg.getEndIndexForView() + 1;
		 }

		 return true;
	 }

	 protected void resetGroupIndices()
	 {
		 Iterator ri = _rowGrps.iterator();
		 while(ri.hasNext())
		 {
			 BoardwalkRowGroup rg = (BoardwalkRowGroup)ri.next();
			 rg.setStartIndexForView(-1);
			 rg.setEndIndexForView(-1);
		 }
		 int csidx = 1;
		 Iterator ci = _colGrps.iterator();
		 while(ci.hasNext())
		 {
			 BoardwalkColumnGroup cg = (BoardwalkColumnGroup)ci.next();
			 cg.setStartIndexForView(-1);
			 cg.setEndIndexForView(-1);
		 }
	 }


	 protected boolean printRowAndColumnGroups()
	 {
		 System.out.println("Printing Table Display");
		 // check for contiguous rows and columns
		 Iterator ri = _rowGrps.iterator();
		 while(ri.hasNext())
		 {
			 BoardwalkRowGroup rg = (BoardwalkRowGroup)ri.next();
			 System.out.println("rg.si = " + rg.getStartIndex());
			 System.out.println("rg.ei = " + rg.getEndIndex());
			 System.out.println("rg.siv = " + rg.getStartIndexForView());
			 System.out.println("rg.eiv = " + rg.getEndIndexForView());
			 System.out.println("rg.display = " + rg.displayInView());
		 }

		 Iterator ci = _colGrps.iterator();
		 while(ci.hasNext())
		 {
			 BoardwalkColumnGroup cg = (BoardwalkColumnGroup)ci.next();
			 System.out.println("cg.si = " + cg.getStartIndex());
			 System.out.println("cg.ei = " + cg.getEndIndex());
			 System.out.println("cg.siv = " + cg.getStartIndexForView());
			 System.out.println("cg.eiv = " + cg.getEndIndexForView());
			 System.out.println("cg.display = " + cg.displayInView());
		 }

		 return true;
	 }
 };