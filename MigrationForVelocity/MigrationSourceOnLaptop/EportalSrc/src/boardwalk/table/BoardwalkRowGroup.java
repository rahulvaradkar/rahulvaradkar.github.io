/*
 * @(#)BoardwalkRowGroup.java 1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;

import com.boardwalk.exception.BoardwalkException;


 public class BoardwalkRowGroup
 implements Comparable
 {
     private String _name;
     private int _startIndex;
     private int _endIndex;
     private int _startIndexInView;
     private int _endIndexInView;
     private int _break;
     private int _nameVisible;
     private Vector _actions;

     private BoardwalkRowGroup(){}

     public BoardwalkRowGroup ( String name,
     							   int startIndex,
     							   int endIndex,
     							   boolean breakAfterGroup,
     							   boolean nameVisible)
	 throws BoardwalkException
     {
		if (endIndex < startIndex) throw new BoardwalkException(15200);

        _name = name;
        _startIndex = startIndex;
        _endIndex = endIndex;
     	_startIndexInView = -1;
     	_endIndexInView = -1;
        _actions = new Vector();
        _break = breakAfterGroup==true?1:0;
        _nameVisible = nameVisible==true?1:0;
     }

	public void addInsertFirstAction(String controlName, int num,
							String preAction, String postAction)
	{
		BoardwalkClientAction bca = new BoardwalkClientAction(controlName,
						BoardwalkClientAction.INSERT_FIRST,
						preAction, postAction);
		bca.addArgument(new Integer(num));
		_actions.addElement(bca);

	}

	public void addInsertLastAction(String controlName, int num,
							String preAction, String postAction)
	{
		BoardwalkClientAction bca = new BoardwalkClientAction(controlName,
						BoardwalkClientAction.INSERT_LAST,
						preAction, postAction);
		bca.addArgument(new Integer(num));
		_actions.addElement(bca);
	}

	public void addInsertGroupFirstAction(String controlName,
							String preAction, String postAction)
	{
		BoardwalkClientAction bca = new BoardwalkClientAction(controlName,
						BoardwalkClientAction.INSERT_GROUP_FIRST,
						preAction, postAction);
		_actions.addElement(bca);
	}

	public void addInsertGroupLastAction(String controlName,
							String preAction, String postAction)
	{
		BoardwalkClientAction bca = new BoardwalkClientAction(controlName,
						BoardwalkClientAction.INSERT_GROUP_LAST,
						preAction, postAction);
		_actions.addElement(bca);
	}

	public void addDeleteAction(String controlName,
							String preAction, String postAction)
	{
		BoardwalkClientAction bca = new BoardwalkClientAction(controlName,
						BoardwalkClientAction.DELETE,
						preAction, postAction);
		_actions.addElement(bca);
	}
	public void addDeleteGroupAction(String controlName,
							String preAction, String postAction)
	{
		BoardwalkClientAction bca = new BoardwalkClientAction(controlName,
						BoardwalkClientAction.DELETE_GROUP,
						preAction, postAction);
		_actions.addElement(bca);
	}
     public String getName()
     {
         return _name;
     }

     public int getStartIndex()
     {
         return _startIndex;
     }

     public int getEndIndex()
     {
         return _endIndex;
     }
     protected int getStartIndexForView()
     {
         return _startIndexInView;
     }
     protected void setStartIndexForView(int idx)
     {
         _startIndexInView = idx;
     }
     protected int getEndIndexForView()
     {
         return _endIndexInView;
     }
     protected void setEndIndexForView(int idx)
     {
         _endIndexInView = idx;
     }

     protected boolean displayInView()
     {
         if (_startIndexInView > 0 && _endIndexInView >0)
         	return true;
         else
         	return false;
     }
	 protected int getNumRows()
	 {
		 return _endIndex - _startIndex + 1;
	 }

	 public boolean equals(Object obj)
	 {
		 if (!(obj instanceof BoardwalkRowGroup)) return false;
		 if (((BoardwalkRowGroup)obj).getStartIndex() == _startIndex &&
		     ((BoardwalkRowGroup)obj).getEndIndex() == _endIndex)
		 {
			 return true;
		 }
		 else
		 {
			 return false;
		 }
	 }

	 public int compareTo(Object o)
	 throws ClassCastException
	 {
		 BoardwalkRowGroup brg = (BoardwalkRowGroup)o;
		 if (_startIndex < brg.getStartIndex())
		 	return -1;
		 else if (_startIndex > brg.getStartIndex() )
		 	return 1;
		 else
		 	return 0;
	 }

     public Vector getActions()
     {
		 return _actions;
	 }

	 public boolean getBreakAfterGroup()
	 {
		 return _break==1?true:false;
	 }
	 public boolean getNameVisible()
	 {
		 return _nameVisible==1?true:false;
	 }
 };