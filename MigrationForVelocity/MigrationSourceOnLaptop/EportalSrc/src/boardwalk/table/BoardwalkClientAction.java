/*
 * @(#)BoardwalkClientAction.java 1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;

import com.boardwalk.exception.BoardwalkException;


public class BoardwalkClientAction
{
	protected String _controlName;
	protected int _action;
	protected String _preAction;
	protected String _postAction;
	protected Vector _args;

	// distribution packet
	public static final int ON_BWS_LOAD = 0;
	// sheet
	public static final int EXPORT_ALL = 1;
	public static final int IMPORT_ALL = 2;
	public static final int EXPORT = 3;
	public static final int IMPORT = 4;
	public static final int CUSTOM = 100;
	// rowgroups, columngroups
	public static final int INSERT_FIRST = 5;
	public static final int INSERT_LAST = 6;
	public static final int INSERT_GROUP_FIRST = 7;
	public static final int INSERT_GROUP_LAST = 8;
	public static final int DELETE = 9;
	public static final int DELETE_GROUP = 10;


	private BoardwalkClientAction(){}

	protected BoardwalkClientAction ( String controlName, int action,
							String preAction, String postAction)
	{
		_controlName = controlName;
		_action = action;
		_preAction = preAction;
		_postAction = postAction;
		_args = new Vector();
	}

	public void addArgument(Object o)
	{
		_args.addElement(o);
	}

	public Vector getArgumentList()
	{
		return _args;
	}

	public void setArgumentList(Vector argumentList)
	{
		_args = argumentList;

	}
 };