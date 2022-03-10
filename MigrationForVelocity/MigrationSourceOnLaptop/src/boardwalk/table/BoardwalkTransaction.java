/*
 * @(#)BoardwalkTransaction.java	1.0
 *
 * Copyright 2004 BoardwalkTech, Inc. All rights reserved.
 * BoardwalkTech/CONFIDENTIAL. Use is subject to license terms.
 */

package boardwalk.table;

import java.util.*;
import com.boardwalk.database.Transaction;
/**
 * BoardwalkTransaction object contains information about a single transaction
 * in Boardwalk system
 */
 public class BoardwalkTransaction
 {
    protected Transaction txs;

	private BoardwalkTransaction(){}

	protected BoardwalkTransaction(Transaction a_txs)
	{
		txs = a_txs;
	}


	public int getId ()
	{
		return txs.getId();
	}

	public int getUserId ()
	{
		return txs.getCreatedByUserId();
	}

	public String getUserName()
	{
		return txs.getCreatedByUserAddress();
	}

	public long getTime ()
	{
		return txs.getCreatedOnTime();
	}

	public String getComment ()
	{
		return txs.getComment();
	}

	public String getAction()
	{
		return txs.getDescription();
	}

 };