/*
 * @(#)BoardwalkSheet.java 1.0
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
 public class BoardwalkSheet
 {
     protected String _name;
     protected String _templateSheet;
     protected Vector _tableDisplayList;
     protected Vector _actionList;
	 protected boolean _visible;

     private BoardwalkSheet(){}

     protected BoardwalkSheet(String name)
     {
        _name = name;
        _templateSheet = "";
        _tableDisplayList = new Vector();
        _actionList = new Vector();
		_visible = true;
     }

	public String getName()
	{
		return _name;
	}

	public String getTemplateSheet()
	{
		return _templateSheet;
	}

	public Vector getTableDisplayList()
	{
		return _tableDisplayList;
	}

	public Vector getSheetActionList()
	{
		return _actionList;
	}
    /**
    * Add a action to submit information from all tables in the local
    * spreadsheet to the server
    * @param templateSheetName the name of the sheet in the template to
    *   copy and use for this Boardwalk sheet
    */
	public void useTemplateSheet(String templateSheetName)
	throws BoardwalkException
	{
		_templateSheet = templateSheetName;
	}

    /**
    * Add a action to submit information from all tables in the local
    * spreadsheet to the server
    * @param sheetName the name of the sheet in which the action should
    * be placed.
    * @param controlName the name of the image in the sheet to which the
    * ExportAll macro needs to be attached.
    * @param preAction the name of the custom macro that will be called
    * before the Export.
    * @param postAction the name of the custom macro that will be called
    * after the Export.
    */
	public void addExportAllAction(String controlName,
							String preAction, String postAction)
	{
		BoardwalkClientAction bca = new BoardwalkClientAction(controlName,
						BoardwalkClientAction.EXPORT_ALL,
						preAction, postAction);
		_actionList.addElement(bca);
	}

    /**
    * Add a action to retrieve changes for all tables in the local
    * spreadsheet from the server
    * @param sheetName the name of the sheet in which the action should
    * be placed.
    * @param controlName the name of the image in the sheet to which the
    * ImportAll macro needs to be attached.
    * @param preAction the name of the custom macro that will be called
    * before the Import.
    * @param postAction the name of the custom macro that will be called
    * after the Import.
    */
	public void addImportAllAction(String controlName,
						String preAction, String postAction)
	{
		BoardwalkClientAction bca = new BoardwalkClientAction(controlName,
						BoardwalkClientAction.IMPORT_ALL,
						preAction, postAction);
		_actionList.addElement(bca);
	}
    /**
    * Add a custom action to be attached to the specified control
    * @param sheetName the name of the sheet in which the action should
    * be placed.
    * @param controlName the name of the image in the sheet to which the
    * ImportAll macro needs to be attached.
    * @param actionName the name of the subroutine to be called. Arguments
    * to this subroutine can be specified using the addAction() method of
    * the returned <code>BoardwalkClientAction</code> object
    * @return a <code>BoardwalkClientAction</code> object
    */
	public BoardwalkClientAction addCustomAction(
										String controlName,
										String actionName)
	{
		BoardwalkClientAction bca = new BoardwalkClientAction(controlName,
						BoardwalkClientAction.CUSTOM, actionName, "");
		_actionList.addElement(bca);
		return bca;
	}
	/**
	* Set the visiblity of the spreadsheet
	* @param visible the name of the sheet in which the action should
	* be placed.
	*/
	public void setVisible(boolean visible)
	{
		_visible = visible;
	}

	/**
	* Get the visiblity of the spreadsheet
	* @return a <code>boolean</code> visiblity flag
	*/
	public boolean isVisible()
	{
		return _visible;
	}

	public boolean equals(Object obj)
	{
		if (!(obj instanceof BoardwalkSheet)) return false;
		if (((BoardwalkSheet)obj).getName() == _name &&
			((BoardwalkSheet)obj).getName() == _name)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

 };