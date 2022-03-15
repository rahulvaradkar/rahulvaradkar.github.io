package com.boardwalk.query;
import java.util.*;
import java.io.*;

import java.sql.*;
import javax.sql.*;

/*
	This class will do the job returning executable queries based on rowquery
	for table content and table content with baseline.
	This can be further extented for all the subsecuent querys that have to be merged.
	Sanjeev dated:12th Oct 2007

*/

public class QueryMaker{

	/* This method will return the query for filtred table content.
		based on the baseline Id sent, i.e. if the basline is > 0 then it will
		return the query for baseline, else the normal query will be returned.
	*/

	public static String getFiltredQueryStringForBrowserChanged(String asRowQuery, String reqColids)
	{
		StringBuffer lsRetStr		= new StringBuffer();

		System.out.println(" getFiltredQueryStringForBrowserChanged getTableDelta getTableDelta");

		String lsQueryStr =

		"  CREATE TABLE  #ACCESSIBLE_ROWS " +
		" ( " +
		" 	ROWID  INT  PRIMARY KEY NOT NULL " +
		" ) " +
		" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery +

		" CREATE TABLE #CHANGE_TRANSACTIONS  "+
		" ( TX_ID INT, CELL_ID INT ) "+
		" insert into #CHANGE_TRANSACTIONS "+
		" select ctx.* from BW_getChangeTransactionsDelta(?, ?, ? ) ctx "+ // Tableid , Stdate, Eddate

		" SELECT "+
		" BWCELL.ID AS CELL_ID,"+
		" BWROW.ROWID AS ROW_ID,"+
		" c.colid AS COLUMN_ID,"+
		" BW_SV.STRING_VALUE AS CELL_STRING_VALUE,"+
		" CELL_INTEGER_VALUE = -1,"+
		" CELL_DOUBLE_VALUE =1.1,"+
		" BW_TXS.TX_ID,"+
		" BW_TXS.CREATED_BY AS TX_CREATED_BY,"+
		" BW_TXS.CREATED_ON,"+
		" BW_TXS.DESCRIPTION,"+
		" BW_TXS.COMMENT_,"+
		" BW_USER.EMAIL_ADDRESS,"+
		" BW_FORMULA_VALUE.FORMULA"+
		" FROM   #ACCESSIBLE_ROWS AS BWROW,"+
		" BW_CELL AS BWCELL,"+
		" BW_STRING_VALUE   AS BW_SV,"+
		" #CHANGE_TRANSACTIONS as CTXS,"+
		" BW_TXS,"+
		" BW_USER,"+
		" BW_GetColumnAccess( ?,?,?) as c,"+
		" BW_STRVAL_FORMULA,"+
		" BW_FORMULA_VALUE"+
		" WHERE"+
		" BWCELL.BW_ROW_ID = BWROW.ROWID"+
		" AND 	BWCELL.BW_COLUMN_ID = c.colid"+
		" AND	BWCELL.CELL_TYPE = 'STRING'"+
		" AND  	BW_SV.BW_CELL_ID = BWCELL.ID"+
		" AND	BWCELL.ID = CTXS.CELL_ID"+
		" AND	BW_SV.TX_ID = CTXS.TX_ID"+
		" AND	BW_TXS.TX_ID = CTXS.TX_ID"+
		" AND 	BW_TXS.CREATED_BY = BW_USER.ID"+
		" AND 	BW_SV.ID = BW_STRVAL_FORMULA.STRVAL_ID"+
		" AND 	BW_STRVAL_FORMULA.FVAL_ID = BW_FORMULA_VALUE.ID"+

		" UNION"+

		" SELECT"+
		" BWCELL.ID AS CELL_ID,"+
		" BWROW.ROWID AS ROW_ID,"+
		" c.colid AS COLUMN_ID,"+
		" BW_SV.STRING_VALUE AS CELL_STRING_VALUE,"+
		" CELL_INTEGER_VALUE = -1,"+
		" CELL_DOUBLE_VALUE =1.1,"+
		" BW_TXS.TX_ID,"+
		" BW_TXS.CREATED_BY AS TX_CREATED_BY,"+
		" BW_TXS.CREATED_ON,"+
		" BW_TXS.DESCRIPTION,"+
		" BW_TXS.COMMENT_,"+
		" BW_USER.EMAIL_ADDRESS,"+
		" NULL AS FORMULA"+
		" FROM  #ACCESSIBLE_ROWS AS BWROW,"+
		" BW_CELL AS BWCELL,"+
		" BW_STRING_VALUE   AS BW_SV,"+
		" #CHANGE_TRANSACTIONS as CTXS,"+
		" BW_TXS,"+
		" BW_USER,"+
		" BW_GetColumnAccess( ?,?,?) c"+
		" WHERE"+
		" BWCELL.BW_ROW_ID = BWROW.ROWID"+
		" AND 	BWCELL.BW_COLUMN_ID = c.colid"+
		" AND	BWCELL.CELL_TYPE = 'STRING'"+
		" AND  	BW_SV.BW_CELL_ID = BWCELL.ID"+
		" AND	BWCELL.ID = CTXS.CELL_ID"+
		" AND	BW_SV.TX_ID = CTXS.TX_ID"+
		" AND	BW_TXS.TX_ID = CTXS.TX_ID"+
		" AND 	BW_TXS.CREATED_BY = BW_USER.ID"+
		" AND 	BW_SV.ID NOT IN"+
		" ( SELECT BW_STRVAL_FORMULA.STRVAL_ID FROM BW_STRVAL_FORMULA )"+
		" ORDER BY ROW_ID ";


		return lsQueryStr;
	}

	public static String getFiltredQueryStringForBrowser(String asRowQuery, boolean withbaseline, boolean asOfDate, String reqColids)
	{
		StringBuffer lsRetStr = new StringBuffer();
		if(withbaseline)
		{
			System.out.println("withbaseline withbaselinewithbaseline");
			lsRetStr.append(" CREATE TABLE  #ACCESSIBLE_ROWS ( ROWID  INT  PRIMARY KEY NOT NULL )");
			lsRetStr.append(" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery );

			lsRetStr.append(" SELECT BWCELL.ID AS CELL_ID,");
			lsRetStr.append("	BWROW.ID AS ROW_ID,");
			lsRetStr.append("	BWCOLUMN.ID AS COLUMN_ID,");
			lsRetStr.append("	BW_SV.STRING_VALUE AS CELL_STRING_VALUE,");
			lsRetStr.append("	CELL_INTEGER_VALUE = -1,");
			lsRetStr.append("	CELL_DOUBLE_VALUE =1.1,");
			lsRetStr.append("	BW_TXS.TX_ID,");
			lsRetStr.append("	BW_TXS.CREATED_BY AS TX_CREATED_BY,");
			lsRetStr.append("	BW_TXS.CREATED_ON,");
			lsRetStr.append("	BW_TXS.DESCRIPTION,");
			lsRetStr.append("	BW_TXS.COMMENT_,");
			lsRetStr.append("	BW_USER.EMAIL_ADDRESS,");
			lsRetStr.append("	BW_FORMULA_VALUE.FORMULA");

			lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS RQ,");
			lsRetStr.append("	BW_GetColumnAccess( ?,?,?) c,");
			lsRetStr.append("	BW_ROW AS BWROW,");
			lsRetStr.append(" 	BW_COLUMN AS BWCOLUMN,");
			lsRetStr.append("	BW_CELL AS BWCELL,");
			lsRetStr.append("	BW_BL_ROW AS BWB_ROW,");
			lsRetStr.append("	BW_BL_COLUMN AS BWB_COLUMN,");
			lsRetStr.append("	BW_BL_CELL AS BWB_CELL,");
			lsRetStr.append("	BW_STRING_VALUE AS BW_SV,");
			lsRetStr.append("	BW_TXS,");
			lsRetStr.append("	BW_USER,");
			lsRetStr.append("	BW_STRVAL_FORMULA,");
			lsRetStr.append("	BW_FORMULA_VALUE");

			lsRetStr.append(" WHERE BWROW.BW_TBL_ID = BWCOLUMN.BW_TBL_ID ");
			lsRetStr.append("	AND BWCOLUMN.BW_TBL_ID = ?");
			lsRetStr.append("	AND BWB_ROW.BASELINE_ID = BWB_COLUMN.BASELINE_ID");
			lsRetStr.append("	AND BWB_COLUMN.BASELINE_ID = BWB_CELL.BASELINE_ID");
			lsRetStr.append("	AND BWB_ROW.ROW_ID = BWROW.ID");
			lsRetStr.append("	AND BWB_COLUMN.COLUMN_ID = BWCOLUMN.ID");
			lsRetStr.append("	AND BWCELL.BW_ROW_ID = BWROW.ID");
			lsRetStr.append("	AND BWCELL.BW_COLUMN_ID = BWCOLUMN.ID");
			lsRetStr.append("	AND BWCELL.CELL_TYPE = 'STRING'");
			lsRetStr.append("	AND BWB_CELL.CELL_ID = BWCELL.ID");
			lsRetStr.append("	AND BWB_CELL.BASELINE_ID = ?");
			lsRetStr.append("	AND BWB_CELL.STRING_VALUE_ID = BW_SV.ID");
			lsRetStr.append("	AND BW_SV.ID = BW_STRVAL_FORMULA.STRVAL_ID");
			lsRetStr.append("	AND BW_STRVAL_FORMULA.FVAL_ID = BW_FORMULA_VALUE.ID");
			lsRetStr.append("	AND BW_TXS.TX_ID = BW_SV.TX_ID");
			lsRetStr.append("	AND BW_TXS.CREATED_BY = BW_USER.ID");
			lsRetStr.append("	AND BWCOLUMN.ID = c.colid");
			lsRetStr.append("	AND BWROW.ID = RQ.ROWID");

		lsRetStr.append(" UNION ALL ");

			lsRetStr.append(" SELECT BWCELL.ID AS CELL_ID,");
			lsRetStr.append("	BWROW.ID AS ROW_ID,");
			lsRetStr.append("	BWCOLUMN.ID AS COLUMN_ID,");
			lsRetStr.append("	BW_SV.STRING_VALUE AS CELL_STRING_VALUE,");
			lsRetStr.append("	CELL_INTEGER_VALUE = -1,");
			lsRetStr.append("	CELL_DOUBLE_VALUE =1.1,");
			lsRetStr.append("	BW_TXS.TX_ID,");
			lsRetStr.append("	BW_TXS.CREATED_BY AS TX_CREATED_BY,");
			lsRetStr.append("	BW_TXS.CREATED_ON,");
			lsRetStr.append("	BW_TXS.DESCRIPTION,");
			lsRetStr.append("	BW_TXS.COMMENT_,");
			lsRetStr.append("	BW_USER.EMAIL_ADDRESS,");
			lsRetStr.append("	NULL AS FORMULA ");

			lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS RQ,");
			lsRetStr.append("	BW_GetColumnAccess( ?,?,?) c,");
			lsRetStr.append("	BW_ROW AS BWROW,");
			lsRetStr.append(" 	BW_COLUMN AS BWCOLUMN,");
			lsRetStr.append("	BW_CELL AS BWCELL,");
			lsRetStr.append("	BW_BL_ROW AS BWB_ROW,");
			lsRetStr.append("	BW_BL_COLUMN AS BWB_COLUMN,");
			lsRetStr.append("	BW_BL_CELL AS BWB_CELL,");
			lsRetStr.append("	BW_STRING_VALUE AS BW_SV,");
			lsRetStr.append("	BW_TXS,");
			lsRetStr.append("	BW_USER");


			lsRetStr.append(" WHERE BWROW.BW_TBL_ID = BWCOLUMN.BW_TBL_ID");
			lsRetStr.append("	AND BWCOLUMN.BW_TBL_ID = ?");
			lsRetStr.append("	AND BWB_ROW.BASELINE_ID = BWB_COLUMN.BASELINE_ID");
			lsRetStr.append("	AND BWB_COLUMN.BASELINE_ID = BWB_CELL.BASELINE_ID ");
			lsRetStr.append("	AND BWB_ROW.ROW_ID = BWROW.ID");
			lsRetStr.append("	AND BWB_COLUMN.COLUMN_ID = BWCOLUMN.ID");
			lsRetStr.append("	AND BWCELL.BW_ROW_ID = BWROW.ID");
			lsRetStr.append("	AND BWCELL.BW_COLUMN_ID = BWCOLUMN.ID");
			lsRetStr.append("	AND BWCELL.CELL_TYPE = 'STRING'");
			lsRetStr.append("	AND BWB_CELL.CELL_ID = BWCELL.ID");
			lsRetStr.append("	AND BWB_CELL.BASELINE_ID = ?");
			lsRetStr.append("	AND BWB_CELL.STRING_VALUE_ID = BW_SV.ID");
			lsRetStr.append("	AND BW_TXS.TX_ID = BW_SV.TX_ID");
			lsRetStr.append("	AND BW_TXS.CREATED_BY = BW_USER.ID");
			lsRetStr.append("	AND BWCOLUMN.ID = c.colid");
			lsRetStr.append("	AND BWROW.ID = RQ.ID");
			lsRetStr.append("	AND BW_SV.ID NOT IN	(SELECT BW_STRVAL_FORMULA.STRVAL_ID FROM BW_STRVAL_FORMULA)" );

			lsRetStr.append(" UNION ALL ");

			lsRetStr.append(" SELECT BWCELL.ID AS CELL_ID,");
			lsRetStr.append("	BWROW.ID AS ROW_ID,");
			lsRetStr.append("	BWCOLUMN.ID AS COLUMN_ID,");
			lsRetStr.append("	CELL_STRING_VALUE='_',");
			lsRetStr.append("	BW_IV.INTEGER_VALUE AS CELL_INTEGER_VALUE,");
			lsRetStr.append("	CELL_DOUBLE_VALUE =1.1,");
			lsRetStr.append("	BW_TXS.TX_ID,");
			lsRetStr.append("	BW_TXS.CREATED_BY AS TX_CREATED_BY,");
			lsRetStr.append("	BW_TXS.CREATED_ON,");
			lsRetStr.append("	BW_TXS.DESCRIPTION,");
			lsRetStr.append("	BW_TXS.COMMENT_,");
			lsRetStr.append("	BW_USER.EMAIL_ADDRESS,");
			lsRetStr.append("	NULL AS FORMULA");

			lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS RQ,");
			lsRetStr.append("	BW_GetColumnAccess(?,?,?) c,");
			lsRetStr.append("	BW_ROW AS BWROW,");
			lsRetStr.append(" 	BW_COLUMN AS BWCOLUMN,");
			lsRetStr.append("	BW_CELL AS BWCELL,");
			lsRetStr.append("	BW_BL_ROW AS BWB_ROW,");
			lsRetStr.append("	BW_BL_COLUMN AS BWB_COLUMN,");
			lsRetStr.append("	BW_BL_CELL AS BWB_CELL,");
			lsRetStr.append("	BW_INTEGER_VALUE AS BW_IV,");
			lsRetStr.append("	BW_TXS,");
			lsRetStr.append("	BW_USER");

			lsRetStr.append(" WHERE BWROW.BW_TBL_ID = BWCOLUMN.BW_TBL_ID");
			lsRetStr.append("	AND BWCOLUMN.BW_TBL_ID = ?");
			lsRetStr.append("	AND BWB_ROW.BASELINE_ID = BWB_COLUMN.BASELINE_ID");
			lsRetStr.append("	AND BWB_COLUMN.BASELINE_ID = BWB_CELL.BASELINE_ID");
			lsRetStr.append("	AND BWB_ROW.ROW_ID = BWROW.ID");
			lsRetStr.append("	AND BWB_COLUMN.COLUMN_ID = BWCOLUMN.ID");
			lsRetStr.append("	AND BWCELL.BW_ROW_ID = BWROW.ID");
			lsRetStr.append("	AND BWCELL.BW_COLUMN_ID = BWCOLUMN.ID");
			lsRetStr.append("	AND BWCELL.CELL_TYPE = 'INTEGER'");
			lsRetStr.append("	AND BWB_CELL.CELL_ID = BWCELL.ID");
			lsRetStr.append("	AND BWB_CELL.BASELINE_ID = ?");
			lsRetStr.append("	AND BWB_CELL.INTEGER_VALUE_ID = BW_IV.ID");
			lsRetStr.append("	AND BW_TXS.TX_ID = BW_IV.TX_ID");
			lsRetStr.append("	AND BW_TXS.CREATED_BY = BW_USER.ID");
			lsRetStr.append("	AND BWCOLUMN.ID = c.colid");
			lsRetStr.append("	AND BWROW.ID = RQ.ID");

			lsRetStr.append(" UNION ALL ");

			lsRetStr.append(" SELECT BWCELL.ID AS CELL_ID,");
			lsRetStr.append("	BWROW.ID AS ROW_ID,");
			lsRetStr.append("	BWCOLUMN.ID AS COLUMN_ID,");
			lsRetStr.append("	CELL_STRING_VALUE ='_',");
			lsRetStr.append("	CELL_INTEGER_VALUE =-1,");
			lsRetStr.append("	BW_DV.DOUBLE_VALUE AS CELL_DOUBLE_VALUE,");
			lsRetStr.append("	BW_TXS.TX_ID,");
			lsRetStr.append("	BW_TXS.CREATED_BY AS TX_CREATED_BY,");
			lsRetStr.append("	BW_TXS.CREATED_ON,");
			lsRetStr.append("	BW_TXS.DESCRIPTION,");
			lsRetStr.append("	BW_TXS.COMMENT_,");
			lsRetStr.append("	BW_USER.EMAIL_ADDRESS,");
			lsRetStr.append("	NULL AS FORMULA ");

			lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS RQ,");
			lsRetStr.append("	BW_GetColumnAccess(?,?,?) c,");
			lsRetStr.append("	BW_ROW AS BWROW,");
			lsRetStr.append(" 	BW_COLUMN AS BWCOLUMN,");
			lsRetStr.append("	BW_CELL AS BWCELL,");
			lsRetStr.append("	BW_BL_ROW AS BWB_ROW,");
			lsRetStr.append("	BW_BL_COLUMN AS BWB_COLUMN,");
			lsRetStr.append("	BW_BL_CELL AS BWB_CELL,");
			lsRetStr.append("	BW_DOUBLE_VALUE AS BW_DV,");
			lsRetStr.append("	BW_TXS,");
			lsRetStr.append("	BW_USER");

			lsRetStr.append(" WHERE BWROW.BW_TBL_ID = BWCOLUMN.BW_TBL_ID ");
			lsRetStr.append("	AND BWCOLUMN.BW_TBL_ID = ?");
			lsRetStr.append("	AND BWB_ROW.BASELINE_ID = BWB_COLUMN.BASELINE_ID");
			lsRetStr.append("	AND BWB_COLUMN.BASELINE_ID = BWB_CELL.BASELINE_ID");
			lsRetStr.append("	AND BWB_ROW.ROW_ID = BWROW.ID");
			lsRetStr.append("	AND BWB_COLUMN.COLUMN_ID = BWCOLUMN.ID");
			lsRetStr.append("	AND BWCELL.BW_ROW_ID = BWROW.ID");
			lsRetStr.append("	AND BWCELL.BW_COLUMN_ID = BWCOLUMN.ID");
			lsRetStr.append("	AND BWCELL.CELL_TYPE = 'FLOAT'");
			lsRetStr.append("	AND BWB_CELL.CELL_ID = BWCELL.ID");
			lsRetStr.append("	AND BWB_CELL.BASELINE_ID = ?");
			lsRetStr.append("	AND BWB_CELL.DOUBLE_VALUE_ID = BW_DV.ID");
			lsRetStr.append("	AND BW_TXS.TX_ID = BW_DV.TX_ID");
			lsRetStr.append("	AND BW_TXS.CREATED_BY = BW_USER.ID");
			lsRetStr.append("	AND BWCOLUMN.ID = c.colid");
			lsRetStr.append("	AND BWROW.ID = RQ.ID");

			lsRetStr.append("	ORDER BY BWROW.SEQUENCE_NUMBER,  BWCOLUMN.SEQUENCE_NUMBER ");
			lsRetStr.append("	DROP TABLE #ACCESSIBLE_ROWS");
		}
		else
		{
			if(asOfDate)
			{
				String lsGetColAccess = "";
				if(reqColids != null)
				{
					System.out.println("asOfDateasOfDate asOfDateasOfDate with selected columns ");

					lsRetStr.append(" CREATE TABLE  #ACCESSIBLE_ROWS ( ROWID  INT  PRIMARY KEY NOT NULL )");
					lsRetStr.append(" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery );

					lsRetStr.append("SELECT");
					lsRetStr.append("	BWCELL.ID AS CELL_ID,");
					lsRetStr.append("	BWCELL.BW_ROW_ID AS ROW_ID,");
					lsRetStr.append("	c.colid AS COLUMN_ID,");
					lsRetStr.append("	BW_SV.STRING_VALUE AS CELL_STRING_VALUE,");
					lsRetStr.append("	CELL_INTEGER_VALUE = -1,");
					lsRetStr.append("	CELL_DOUBLE_VALUE =1.1,");
					lsRetStr.append("	BW_TXS.TX_ID,");
					lsRetStr.append("	BW_TXS.CREATED_BY AS TX_CREATED_BY,");
					lsRetStr.append("	BW_TXS.CREATED_ON,");
					lsRetStr.append("	BW_TXS.DESCRIPTION,");
					lsRetStr.append("	BW_TXS.COMMENT_,");
					lsRetStr.append("	BW_USER.EMAIL_ADDRESS,");
					lsRetStr.append("	BW_FORMULA_VALUE.FORMULA");

					lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS RQ,");
					lsRetStr.append("	( SELECT CTX.* FROM  BW_GetColumnAccess( ? , ?, ?) CTX ParseIdList("+reqColids+") rc where rc.ID = CTX.colid ) AS c,");
					lsRetStr.append("	BW_CELL AS BWCELL,");
					lsRetStr.append("	BW_STRING_VALUE   AS BW_SV,");
					lsRetStr.append("	BW_getChangeTransactions(?,?) as CTXS,");
					lsRetStr.append("	BW_getStatusTransactions(?,?) as STXS,");
					lsRetStr.append("	BW_TXS,");
					lsRetStr.append("	BW_USER,");
					lsRetStr.append("	BW_CELL_STATUS,");
					lsRetStr.append("	BW_STRVAL_FORMULA,");
					lsRetStr.append("	BW_FORMULA_VALUE");

					lsRetStr.append(" WHERE	");
					lsRetStr.append("	BWCELL.BW_ROW_ID = RQ.ROWID");
					lsRetStr.append("	AND BWCELL.BW_COLUMN_ID = c.colid");
					lsRetStr.append("	AND	BWCELL.CELL_TYPE = 'STRING'");
					lsRetStr.append("	AND	BWCELL.ID = BW_CELL_STATUS.BW_CELL_ID");
					lsRetStr.append("	AND	BWCELL.ID = STXS.CELL_ID");
					lsRetStr.append("	AND	BW_CELL_STATUS.TX_ID = STXS.TX_ID");
					lsRetStr.append("	AND	BW_CELL_STATUS.ACTIVE = 1");
					lsRetStr.append("	AND BW_SV.BW_CELL_ID = BWCELL.ID");
					lsRetStr.append("	AND	BWCELL.ID = CTXS.CELL_ID");
					lsRetStr.append("	AND	BW_SV.TX_ID = CTXS.TX_ID");
					lsRetStr.append("	AND	BW_TXS.TX_ID = CTXS.TX_ID");
					lsRetStr.append("	AND BW_TXS.CREATED_BY = BW_USER.ID");
					lsRetStr.append("	AND BW_SV.ID = BW_STRVAL_FORMULA.STRVAL_ID");
					lsRetStr.append("	AND BW_STRVAL_FORMULA.FVAL_ID = BW_FORMULA_VALUE.ID");

					lsRetStr.append(" UNION ");

					lsRetStr.append(" SELECT");
					lsRetStr.append("	BWCELL.ID AS CELL_ID,");
					lsRetStr.append("	BWCELL.BW_ROW_ID AS ROW_ID,");
					lsRetStr.append("	c.colid AS COLUMN_ID,");
					lsRetStr.append("	BW_SV.STRING_VALUE AS CELL_STRING_VALUE,");
					lsRetStr.append("	CELL_INTEGER_VALUE = -1,");
					lsRetStr.append("	CELL_DOUBLE_VALUE =1.1,");
					lsRetStr.append("	BW_TXS.TX_ID,");
					lsRetStr.append("	BW_TXS.CREATED_BY AS TX_CREATED_BY,");
					lsRetStr.append("	BW_TXS.CREATED_ON,");
					lsRetStr.append("	BW_TXS.DESCRIPTION,");
					lsRetStr.append("	BW_TXS.COMMENT_,");
					lsRetStr.append("	BW_USER.EMAIL_ADDRESS,");
					lsRetStr.append("	NULL AS FORMULA");

					lsRetStr.append(" FROM #ACCESSIBLE_ROWS  AS RQ,");
					lsRetStr.append("	( SELECT CTX.* FROM  BW_GetColumnAccess( ? , ?, ?) CTX ParseIdList("+reqColids+") rc where rc.ID = CTX.colid ) AS c,");
					lsRetStr.append("	BW_CELL AS BWCELL,");
					lsRetStr.append("	BW_STRING_VALUE   AS BW_SV,");
					lsRetStr.append("	BW_getChangeTransactions(?,?) as CTXS,");
					lsRetStr.append("	BW_getStatusTransactions(?,?) as STXS,");
					lsRetStr.append("	BW_TXS,");
					lsRetStr.append("	BW_USER,");
					lsRetStr.append("	BW_CELL_STATUS");

					lsRetStr.append(" WHERE	");
					lsRetStr.append("	BWCELL.BW_ROW_ID = RQ.ROWID");
					lsRetStr.append("	AND BWCELL.BW_COLUMN_ID = c.colid");
					lsRetStr.append("	AND	BWCELL.CELL_TYPE = 'STRING'");
					lsRetStr.append("	AND	BWCELL.ID = BW_CELL_STATUS.BW_CELL_ID");
					lsRetStr.append("	AND	BWCELL.ID = STXS.CELL_ID");
					lsRetStr.append("	AND	BW_CELL_STATUS.TX_ID = STXS.TX_ID");
					lsRetStr.append("	AND	BW_CELL_STATUS.ACTIVE = 1");
					lsRetStr.append("	AND BW_SV.BW_CELL_ID = BWCELL.ID");
					lsRetStr.append("	AND	BWCELL.ID = CTXS.CELL_ID");
					lsRetStr.append("	AND	BW_SV.TX_ID = CTXS.TX_ID");
					lsRetStr.append("	AND	BW_TXS.TX_ID = CTXS.TX_ID");
					lsRetStr.append("	AND BW_TXS.CREATED_BY = BW_USER.ID");
					lsRetStr.append("	AND BW_SV.ID NOT IN");
					lsRetStr.append("	(SELECT BW_STRVAL_FORMULA.STRVAL_ID FROM BW_STRVAL_FORMULA )");

					lsRetStr.append("	ORDER BY BWROW.SEQUENCE_NUMBER,  c.SEQUENCE_NUMBER ");

					lsRetStr.append("	DROP TABLE #ACCESSIBLE_ROWS");

				}
				else
				{
					System.out.println("asOfDateasOfDate asOfDateasOfDate with all columns ");

					lsRetStr.append(" CREATE TABLE  #ACCESSIBLE_ROWS ( ROWID  INT  PRIMARY KEY NOT NULL )");
					lsRetStr.append(" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery );

					lsRetStr.append("SELECT");
					lsRetStr.append("	BWCELL.ID AS CELL_ID,");
					lsRetStr.append("	BWCELL.BW_ROW_ID AS ROW_ID,");
					lsRetStr.append("	c.colid AS COLUMN_ID,");
					lsRetStr.append("	BW_SV.STRING_VALUE AS CELL_STRING_VALUE,");
					lsRetStr.append("	CELL_INTEGER_VALUE = -1,");
					lsRetStr.append("	CELL_DOUBLE_VALUE =1.1,");
					lsRetStr.append("	BW_TXS.TX_ID,");
					lsRetStr.append("	BW_TXS.CREATED_BY AS TX_CREATED_BY,");
					lsRetStr.append("	BW_TXS.CREATED_ON,");
					lsRetStr.append("	BW_TXS.DESCRIPTION,");
					lsRetStr.append("	BW_TXS.COMMENT_,");
					lsRetStr.append("	BW_USER.EMAIL_ADDRESS,");
					lsRetStr.append("	BW_FORMULA_VALUE.FORMULA");

					lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS RQ,");
					lsRetStr.append("	( SELECT CTX.* FROM  BW_GetColumnAccess(?,?,?) CTX ) AS c, ");
					lsRetStr.append("	BW_CELL AS BWCELL,");
					lsRetStr.append("	BW_STRING_VALUE   AS BW_SV,");
					lsRetStr.append("	BW_getChangeTransactions(?,?) as CTXS,");
					lsRetStr.append("	BW_getStatusTransactions(?,?) as STXS,");
					lsRetStr.append("	BW_TXS,");
					lsRetStr.append("	BW_USER,");
					lsRetStr.append("	BW_CELL_STATUS,");
					lsRetStr.append("	BW_STRVAL_FORMULA,");
					lsRetStr.append("	BW_FORMULA_VALUE");

					lsRetStr.append(" WHERE	");
					lsRetStr.append("	BWCELL.BW_ROW_ID = RQ.ROWID");
					lsRetStr.append("	AND BWCELL.BW_COLUMN_ID = c.colid");
					lsRetStr.append("	AND	BWCELL.CELL_TYPE = 'STRING'");
					lsRetStr.append("	AND	BWCELL.ID = BW_CELL_STATUS.BW_CELL_ID");
					lsRetStr.append("	AND	BWCELL.ID = STXS.CELL_ID");
					lsRetStr.append("	AND	BW_CELL_STATUS.TX_ID = STXS.TX_ID");
					lsRetStr.append("	AND	BW_CELL_STATUS.ACTIVE = 1");
					lsRetStr.append("	AND BW_SV.BW_CELL_ID = BWCELL.ID");
					lsRetStr.append("	AND	BWCELL.ID = CTXS.CELL_ID");
					lsRetStr.append("	AND	BW_SV.TX_ID = CTXS.TX_ID");
					lsRetStr.append("	AND	BW_TXS.TX_ID = CTXS.TX_ID");
					lsRetStr.append("	AND BW_TXS.CREATED_BY = BW_USER.ID");
					lsRetStr.append("	AND BW_SV.ID = BW_STRVAL_FORMULA.STRVAL_ID");
					lsRetStr.append("	AND BW_STRVAL_FORMULA.FVAL_ID = BW_FORMULA_VALUE.ID");

					lsRetStr.append(" UNION ");

					lsRetStr.append(" SELECT");
					lsRetStr.append("	BWCELL.ID AS CELL_ID,");
					lsRetStr.append("	BWCELL.BW_ROW_ID AS ROW_ID,");
					lsRetStr.append("	c.colid AS COLUMN_ID,");
					lsRetStr.append("	BW_SV.STRING_VALUE AS CELL_STRING_VALUE,");
					lsRetStr.append("	CELL_INTEGER_VALUE = -1,");
					lsRetStr.append("	CELL_DOUBLE_VALUE =1.1,");
					lsRetStr.append("	BW_TXS.TX_ID,");
					lsRetStr.append("	BW_TXS.CREATED_BY AS TX_CREATED_BY,");
					lsRetStr.append("	BW_TXS.CREATED_ON,");
					lsRetStr.append("	BW_TXS.DESCRIPTION,");
					lsRetStr.append("	BW_TXS.COMMENT_,");
					lsRetStr.append("	BW_USER.EMAIL_ADDRESS,");
					lsRetStr.append("	NULL AS FORMULA");

					lsRetStr.append(" FROM #ACCESSIBLE_ROWS  AS RQ,");
					lsRetStr.append("	( SELECT CTX.* FROM  BW_GetColumnAccess(?,?,?) CTX ) AS c,");
					lsRetStr.append("	BW_CELL AS BWCELL,");
					lsRetStr.append("	BW_STRING_VALUE   AS BW_SV,");
					lsRetStr.append("	BW_getChangeTransactions(?,?) as CTXS,");
					lsRetStr.append("	BW_getStatusTransactions(?,?) as STXS,");
					lsRetStr.append("	BW_TXS,");
					lsRetStr.append("	BW_USER,");
					lsRetStr.append("	BW_CELL_STATUS");

					lsRetStr.append(" WHERE	");
					lsRetStr.append("	BWCELL.BW_ROW_ID = RQ.ROWID");
					lsRetStr.append("	AND BWCELL.BW_COLUMN_ID = c.colid");
					lsRetStr.append("	AND	BWCELL.CELL_TYPE = 'STRING'");
					lsRetStr.append("	AND	BWCELL.ID = BW_CELL_STATUS.BW_CELL_ID");
					lsRetStr.append("	AND	BWCELL.ID = STXS.CELL_ID");
					lsRetStr.append("	AND	BW_CELL_STATUS.TX_ID = STXS.TX_ID");
					lsRetStr.append("	AND	BW_CELL_STATUS.ACTIVE = 1");
					lsRetStr.append("	AND BW_SV.BW_CELL_ID = BWCELL.ID");
					lsRetStr.append("	AND	BWCELL.ID = CTXS.CELL_ID");
					lsRetStr.append("	AND	BW_SV.TX_ID = CTXS.TX_ID");
					lsRetStr.append("	AND	BW_TXS.TX_ID = CTXS.TX_ID");
					lsRetStr.append("	AND BW_TXS.CREATED_BY = BW_USER.ID");
					lsRetStr.append("	AND BW_SV.ID NOT IN");
					lsRetStr.append("	(SELECT BW_STRVAL_FORMULA.STRVAL_ID FROM BW_STRVAL_FORMULA )");
//					lsRetStr.append("	ORDER BY BWROW.SEQUENCE_NUMBER,  c.SEQUENCE_NUMBER ");
					lsRetStr.append("	DROP TABLE #ACCESSIBLE_ROWS");
				}
			}
			else
			{
				System.out.println("First Case Simple ");

				lsRetStr.append(" CREATE TABLE  #ACCESSIBLE_ROWS ( ROWID  INT  PRIMARY KEY NOT NULL )");
				lsRetStr.append(" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery );

				lsRetStr.append(" SELECT BWCELL.ID AS CELL_ID,");
				lsRetStr.append("	BWCELL.BW_ROW_ID AS ROW_ID,");
				lsRetStr.append("	BWCELL.BW_COLUMN_ID AS COLUMN_ID,");
				lsRetStr.append("	BWCELL.STRING_VALUE AS CELL_STRING_VALUE,");
				lsRetStr.append("	BWCELL.INTEGER_VALUE AS CELL_INTEGER_VALUE,");
				lsRetStr.append("	BWCELL.DOUBLE_VALUE AS CELL_DOUBLE_VALUE,");
				lsRetStr.append("	BW_TXS.TX_ID,");
				lsRetStr.append("	BW_TXS.CREATED_BY AS TX_CREATED_BY,");
				lsRetStr.append("	BW_TXS.CREATED_ON,");
				lsRetStr.append("	BW_TXS.DESCRIPTION,");
				lsRetStr.append("	BW_TXS.COMMENT_,");
				lsRetStr.append("	BW_USER.EMAIL_ADDRESS,");
				lsRetStr.append("	BWCELL.FORMULA");

				lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS RQ,");
				lsRetStr.append("	BW_ROW AS BWROW,");
				lsRetStr.append("	BW_GetColumnAccess( ?,?,?) c,");
				lsRetStr.append("	BW_CELL AS BWCELL,");
				lsRetStr.append("	BW_TXS,");
				lsRetStr.append("	BW_USER");

				lsRetStr.append(" WHERE	BWCELL.BW_ROW_ID = BWROW.ID");
				lsRetStr.append("	AND BWROW.BW_TBL_ID = ?" );
				lsRetStr.append("	AND BWCELL.TX_ID = BW_TXS.TX_ID");
				lsRetStr.append("	AND BW_TXS.CREATED_BY = BW_USER.ID");
				lsRetStr.append("	AND BWCELL.BW_COLUMN_ID = c.colid");
				lsRetStr.append("	AND BWROW.IS_ACTIVE = 1");
				lsRetStr.append("	AND BWROW.ID = RQ.ROWID");
				lsRetStr.append("	ORDER BY BWROW.SEQUENCE_NUMBER,  c.SEQUENCE_NUMBER ");
				lsRetStr.append("	DROP TABLE #ACCESSIBLE_ROWS");
			}
		}

		return lsRetStr.toString();
	}

	// Single trans queries with RowQuery join
	public static String getFiltredQueryBefXL(String asRowQuery)
	{
		//BW_GET_TBL_BEF_T

		String query =
		"  CREATE TABLE  #ACCESSIBLE_ROWS " +
		" ( " +
		" 	ROWID  INT  PRIMARY KEY NOT NULL " +
		" ) " +
		" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery +
		" CREATE TABLE  #CTXS " +
		" ( " +
		"   SV_ID INT PRIMARY KEY NOT NULL " +
		" ) " +
		" INSERT INTO #CTXS " +
		" 	SELECT  " +
		" 		MAX(BW_STRING_VALUE.ID) AS SV_ID " +
		" 	FROM  " +
		" 		BW_STRING_VALUE,   BW_CELL,   " +
		" 		BW_GetColumnAccessNew(?, ?, ?)  C, " +
		" 		#ACCESSIBLE_ROWS R " +
		" 	WHERE " +
		" 		 BW_CELL.BW_COLUMN_ID = C.COLID " +
		" 		 AND BW_CELL.BW_ROW_ID = R.ROWID " +
		" 		 AND BW_CELL.ID = BW_STRING_VALUE.BW_CELL_ID " +
		" 		 AND BW_STRING_VALUE.TX_ID  < ? " +
		" 	GROUP BY BW_STRING_VALUE.BW_CELL_ID " +
		" CREATE TABLE  #STXS " +
		" ( " +
		"   CS_ID INT  PRIMARY KEY NOT NULL " +
		" ) " +
		" INSERT INTO #STXS " +
		" 	SELECT   " +
		" 		MAX (BW_CELL_STATUS.ID) AS CS_ID " +
		" 	FROM  " +
		" 		BW_CELL_STATUS, " +
		" 		BW_CELL, " +
		" 		BW_GetColumnAccessNew(?, ?, ?) C, " +
		" 		#ACCESSIBLE_ROWS R " +
		" 	WHERE " +
		" 		BW_CELL.BW_COLUMN_ID = C.COLID " +
		" 		AND BW_CELL.BW_ROW_ID = R.ROWID " +
		" 		AND BW_CELL.ID = BW_CELL_STATUS.BW_CELL_ID " +
		" 		AND BW_CELL_STATUS.TX_ID < ?  " +
		" 	GROUP BY BW_CELL_STATUS.BW_CELL_ID " +

		" SELECT  " +
		" 	BW_COLUMN.ID AS COLUMN_ID , " +
		" 	BW_ROW.ID AS ROW_ID , " +
		" 	BW_COLUMN.SEQUENCE_NUMBER AS COL_SEQ, " +
		" 	BW_ROW.SEQUENCE_NUMBER AS ROW_SEQ, " +
		" 	BW_STRING_VALUE.STRING_VALUE, " +
		"  	BW_FORMULA_VALUE.FORMULA, " +
		" 	BW_COLUMN.NAME " +
		" FROM  " +
		" 	#CTXS AS CTXS, " +
		" 	#STXS AS STXS, " +
		" 	BW_CELL, " +
		" 	BW_STRING_VALUE, " +
		" 	BW_CELL_STATUS, " +
		" 	BW_ROW, " +
		" 	BW_COLUMN, " +
		" 	BW_STRVAL_FORMULA, " +
		" 	BW_FORMULA_VALUE " +
		" WHERE " +
		" 	BW_CELL.ID = BW_STRING_VALUE.BW_CELL_ID " +
		" 	AND BW_STRING_VALUE.ID = CTXS.SV_ID " +
		" 	AND BW_CELL.ID = BW_CELL_STATUS.BW_CELL_ID " +
		" 	AND BW_CELL_STATUS.ID = STXS.CS_ID " +
		" 	AND BW_CELL.BW_ROW_ID = BW_ROW.ID " +
		" 	AND BW_CELL.BW_COLUMN_ID = BW_COLUMN.ID " +
		" 	AND BW_STRVAL_FORMULA.STRVAL_ID = BW_STRING_VALUE.ID " +
		" 	AND BW_STRVAL_FORMULA.FVAL_ID = BW_FORMULA_VALUE.ID " +
		" 	AND BW_CELL_STATUS.ACTIVE = 1	 " +
		" UNION ALL " +
		" SELECT " +
		" 	BW_COLUMN.ID AS COLUMN_ID , " +
		" 	BW_ROW.ID AS ROW_ID , " +
		" 	BW_COLUMN.SEQUENCE_NUMBER AS COL_SEQ,  " +
		" 	BW_ROW.SEQUENCE_NUMBER AS ROW_SEQ, " +
		" 	BW_STRING_VALUE.STRING_VALUE, " +
		" 	 NULL AS FORMULA, " +
		" 	BW_COLUMN.NAME " +
		" FROM  " +
		" 	#CTXS AS CTXS,	 " +
		"  	#STXS AS STXS, " +
		" 	BW_CELL, " +
		" 	BW_STRING_VALUE, " +
		" 	BW_CELL_STATUS, " +
		" 	BW_ROW, " +
		" 	BW_COLUMN " +
		" WHERE " +
		" 	 BW_CELL.ID = BW_STRING_VALUE.BW_CELL_ID " +
		" 	AND BW_STRING_VALUE.ID = CTXS.SV_ID " +
		" 	AND BW_CELL.ID = BW_CELL_STATUS.BW_CELL_ID " +
		" 	AND BW_CELL_STATUS.ID = STXS.CS_ID " +
		" 	AND BW_CELL.BW_ROW_ID = BW_ROW.ID " +
		" 	AND BW_CELL.BW_COLUMN_ID = BW_COLUMN.ID " +
		" 	AND BW_CELL_STATUS.ACTIVE = 1		 " +
		" 	AND NOT EXISTS (SELECT BW_STRVAL_FORMULA.ID FROM BW_STRVAL_FORMULA WHERE BW_STRVAL_FORMULA.STRVAL_ID = BW_STRING_VALUE.ID) " +
		" ORDER BY BW_COLUMN.SEQUENCE_NUMBER, BW_ROW.SEQUENCE_NUMBER " +
		" DROP TABLE #CTXS " +
		" DROP TABLE #STXS " +
		" DROP TABLE #ACCESSIBLE_ROWS ";

		return query;
	}

	// Value Chages Single Trans with RowQuery join
	public static String getFiltredQueryValueChangesXL(String asRowQuery)
	{
//		BW_GET_VALUE_CHANGES_FOR_TID
//		StringBuffer lsRetStr		= new StringBuffer();
//		return lsRetStr.toString();
		String lsQueryStr =

		"  CREATE TABLE  #ACCESSIBLE_ROWS " +
		" ( " +
		" 	ROWID  INT  PRIMARY KEY NOT NULL " +
		" ) " +
		" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery +
		" SELECT "+
		"	BWROW.ROWID AS ROW_ID,"+
		"   C.COLID AS COLUMN_ID,"+
		"   BW_SV.STRING_VALUE AS CELL_STRING_VALUE,"+
		"   BW_TXS.CREATED_BY AS TX_CREATED_BY,"+
		"   BW_TXS.CREATED_ON,"+
		"   BW_TXS.COMMENT_,"+
		"   BW_USER.EMAIL_ADDRESS,"+
		"   BW_FORMULA_VALUE.FORMULA,"+
		"   BW_COLUMN.NAME, "+
		"   BW_COLUMN.SEQUENCE_NUMBER AS COL_SEQ,"+
		"   BW_ROW.SEQUENCE_NUMBER AS ROW_SEQ"+
		" FROM "+
		"   #ACCESSIBLE_ROWS AS BWROW,"+
		"   BW_CELL AS BWCELL,"+
		"   BW_STRING_VALUE   AS BW_SV,"+
		"   BW_TXS,"+
		"   BW_USER,"+
		"   BW_GetColumnAccessNew(? , ?, ?)  C,"+ // TableId, Userid, MemberId
		"   BW_STRVAL_FORMULA,"+
		"   BW_FORMULA_VALUE,"+
		"   BW_COLUMN,"+
		"   BW_ROW"+
		" WHERE "+
		"	BWCELL.BW_ROW_ID = BWROW.ROWID"+
		"   AND     BWROW.ROWID = BW_ROW.ID"+
		"   AND     BWCELL.BW_COLUMN_ID = C.COLID"+
		"   AND     BWCELL.BW_COLUMN_ID = BW_COLUMN.ID"+
		"   AND     BWCELL.CELL_TYPE = 'STRING'"+
		"   AND     BW_SV.BW_CELL_ID = BWCELL.ID"+
		"   AND     BW_SV.TX_ID = ? "+ // Txid
		"   AND     BW_TXS.TX_ID = ? "+ // Txid
		"   AND     BW_TXS.CREATED_BY = BW_USER.ID"+
		"   AND     BW_SV.ID = BW_STRVAL_FORMULA.STRVAL_ID"+
		"   AND     BW_STRVAL_FORMULA.FVAL_ID = BW_FORMULA_VALUE.ID"+
		" UNION "+
		" SELECT "+
		"   BWROW.ROWID AS ROW_ID,"+
		"   C.COLID AS COLUMN_ID,"+
		"   BW_SV.STRING_VALUE AS CELL_STRING_VALUE,"+
		"   BW_TXS.CREATED_BY AS TX_CREATED_BY,"+
		"   BW_TXS.CREATED_ON,"+
		"   BW_TXS.COMMENT_,"+
		"   BW_USER.EMAIL_ADDRESS,"+
		"   NULL AS FORMULA,"+
		"   BW_COLUMN.NAME,"+
		"   BW_COLUMN.SEQUENCE_NUMBER AS COL_SEQ,"+
		"   BW_ROW.SEQUENCE_NUMBER AS ROW_SEQ"+
		" FROM   "+
		"	#ACCESSIBLE_ROWS AS BWROW,"+
		"	BW_CELL AS BWCELL,"+
		"	BW_STRING_VALUE   AS BW_SV,"+
		"	BW_TXS,"+
		"	BW_USER,"+
		"	BW_GetColumnAccessNew(? , ?, ?)  C,"+ // TableId, Userid, MemberId
		"   BW_COLUMN,"+
		"   BW_ROW"+
		" WHERE "+
		"	BWCELL.BW_ROW_ID = BWROW.ROWID"+
		"   AND     BWROW.ROWID = BW_ROW.ID"+
		"   AND     BWCELL.BW_COLUMN_ID = C.COLID"+
		"   AND     BWCELL.BW_COLUMN_ID = BW_COLUMN.ID"+
		"   AND     BWCELL.CELL_TYPE = 'STRING'"+
		"   AND     BW_SV.BW_CELL_ID = BWCELL.ID"+
		"   AND     BW_SV.TX_ID = ? "+ // Txid
		"   AND     BW_TXS.TX_ID = ? "+ // Txid
		"   AND     BW_TXS.CREATED_BY = BW_USER.ID"+
		"   AND     NOT EXISTS"+
		"	(SELECT BW_STRVAL_FORMULA.STRVAL_ID FROM BW_STRVAL_FORMULA WHERE BW_STRVAL_FORMULA.STRVAL_ID  = BW_SV.ID)"+
		" ORDER BY BW_TXS.CREATED_ON "+
		" DROP TABLE #ACCESSIBLE_ROWS ";

		return lsQueryStr ;
	}

	// Status changes Single Trans with RowQuery join
	public static String getFiltredQueryStatusChangesXL(String asRowQuery)
	{
//		StringBuffer lsRetStr = new StringBuffer();
//		return lsRetStr.toString();
// BW_GET_STATUS_CHANGES_FOR_TID
		String lsQueryStr =

		"  CREATE TABLE  #ACCESSIBLE_ROWS " +
		" ( " +
		" 	ROWID  INT  PRIMARY KEY NOT NULL " +
		" ) " +
		" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery +

		" SELECT "+
		" 	BWROW.ROWID AS ROW_ID,"+
		" 	C.COLID AS COLUMN_ID,"+
		" 	BW_CELL_STATUS.ACTIVE,"+
		" 	BW_TXS.CREATED_BY AS TX_CREATED_BY,"+
		" 	BW_TXS.CREATED_ON,"+
		" 	BW_TXS.COMMENT_,"+
		" 	BW_USER.EMAIL_ADDRESS,"+
		"	BW_COLUMN.SEQUENCE_NUMBER as COL_SEQ, "+
		"	BW_ROW.SEQUENCE_NUMBER as ROW_SEQ"+
		" FROM "+
		"   #ACCESSIBLE_ROWS AS BWROW,"+
		"   BW_CELL AS BWCELL,"+
		"   BW_TXS,"+
		"   BW_USER,"+
		"   BW_GetColumnAccessNew(? , ?, ?)  C,"+ // Tid , uid, memberid
		"   BW_CELL_STATUS,"+
		"   BW_ROW,"+
		"   BW_COLUMN"+
		" WHERE"+
		" 			  BWCELL.BW_ROW_ID = BWROW.ROWID"+
		"     AND     BWCELL.BW_ROW_ID = BW_ROW.ID"+
		"     AND     BWCELL.BW_COLUMN_ID = C.COLID"+
		"     AND     BWCELL.BW_COLUMN_ID = BW_COLUMN.ID"+
		"     AND     BW_CELL_STATUS.BW_CELL_ID = BWCELL.ID"+
		"     AND     BW_CELL_STATUS.TX_ID = BW_TXS.TX_ID"+
		"     AND     BW_TXS.TX_ID = ? "+ // Etid
		"     AND	  BW_TXS.CREATED_BY = BW_USER.ID"+

		" UNION "+

		" SELECT "+
		" 	BWROW.ROWID AS ROW_ID,"+
		" 	C.COLID AS COLUMN_ID,"+
		" 	BW_CELL_STATUS.ACTIVE,"+
		" 	BW_TXS.CREATED_BY AS TX_CREATED_BY,"+
		" 	BW_TXS.CREATED_ON,"+
		" 	BW_TXS.COMMENT_,"+
		" 	BW_USER.EMAIL_ADDRESS,"+
		"	BW_COLUMN.SEQUENCE_NUMBER as COL_SEQ, "+
		"	BW_ROW.SEQUENCE_NUMBER as ROW_SEQ"+
		" FROM"+
		"     #ACCESSIBLE_ROWS AS BWROW,"+
		"     BW_CELL AS BWCELL,"+
		"     BW_TXS,"+
		"     BW_USER,"+
		"     BW_GetColumnAccessNew(? , ?, ?)  C,"+ // Tid , uid, memberid
		"     BW_CELL_STATUS,"+
		"     BW_ROW,"+
		"     BW_COLUMN"+
		" WHERE"+
		" 			BWCELL.BW_ROW_ID = BWROW.ROWID"+
		"	AND		BWCELL.BW_ROW_ID = BW_ROW.ID"+
		"   AND     BWCELL.BW_COLUMN_ID = C.COLID"+
		"   AND     BWCELL.BW_COLUMN_ID = BW_COLUMN.ID"+
		"   AND     BW_CELL_STATUS.BW_CELL_ID = BWCELL.ID"+
		"   AND     BW_CELL_STATUS.TX_ID = BW_TXS.TX_ID"+
		"   AND     BW_TXS.TX_ID = ? "+ //
		"   AND		BW_TXS.CREATED_BY = BW_USER.ID"+
		"   AND     BW_CELL_STATUS.ACTIVE = 0"+
		"	ORDER BY BW_COLUMN.SEQUENCE_NUMBER, BW_ROW.SEQUENCE_NUMBER, BW_TXS.CREATED_ON"+
		" DROP TABLE #ACCESSIBLE_ROWS ";

		return lsQueryStr;
	}

	// Transaction list with RowQuery join for BL
	public static String getFiltredTransactionListBL(String asRowQuery)
	{
		StringBuffer lsRetStr = new StringBuffer();
		/* rows added */

		lsRetStr.append(" CREATE TABLE  #ACCESSIBLE_ROWS ");
		lsRetStr.append(" ( ROWID  INT  PRIMARY KEY NOT NULL  ) " );
		lsRetStr.append(" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery );

		lsRetStr.append(" select bw_txs.tx_id, bw_txs.created_on, bw_user.email_address as created_by, bw_txs.comment_ , action='ROWADD' ");

		lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS bwrow,");
		lsRetStr.append(" bw_txs,");
		lsRetStr.append(" bw_cell_status,");
		lsRetStr.append(" bw_cell,");
		lsRetStr.append(" bw_user");

		lsRetStr.append(" where");
		lsRetStr.append(" bw_cell.bw_row_id = bwrow.ROWID");
		lsRetStr.append(" and bw_cell_status.bw_cell_id = bw_cell.id");
		lsRetStr.append(" and bw_cell_status.tx_id = bw_txs.tx_id");
		lsRetStr.append(" and bw_cell_status.active = 1");
		lsRetStr.append(" and bw_txs.created_on >= ? "); //SDATE
		lsRetStr.append(" and bw_txs.created_on <= ? "); //EDATE
		lsRetStr.append(" and bw_txs.created_by = bw_user.id");

		lsRetStr.append(" union");
		/* rows deleted */
		lsRetStr.append(" select bw_txs.tx_id, bw_txs.created_on, bw_user.email_address as created_by, bw_txs.comment_, action='ROWDEL'");
		lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS bwrow,");
		lsRetStr.append(" bw_txs,");
		lsRetStr.append(" bw_cell_status,");
		lsRetStr.append(" bw_cell,");
		lsRetStr.append(" bw_user");

		lsRetStr.append(" where");
		lsRetStr.append(" bw_cell.bw_row_id = bwrow.ROWID");
		lsRetStr.append(" and bw_cell_status.bw_cell_id = bw_cell.id");
		lsRetStr.append(" and bw_cell_status.tx_id = bw_txs.tx_id");
		lsRetStr.append(" and bw_cell_status.active = 0");
		lsRetStr.append(" and bw_txs.created_on >= ? "); //SDATE
		lsRetStr.append(" and bw_txs.created_on <= ? "); //EDATE
		lsRetStr.append(" and bw_txs.created_by = bw_user.id");

		lsRetStr.append(" union");
		/* string value updates */
		lsRetStr.append(" select bw_txs.tx_id, bw_txs.created_on, bw_user.email_address as created_by, bw_txs.comment_ , action='CELLUPD'");
		lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS bwrow,");
		lsRetStr.append(" bw_txs,");
		lsRetStr.append(" bw_string_value,");
		lsRetStr.append(" bw_cell,");
		lsRetStr.append(" bw_user");

		lsRetStr.append(" where");
		lsRetStr.append(" bw_cell.bw_row_id = bwrow.ROWID");
		lsRetStr.append(" and bw_string_value.bw_cell_id = bw_cell.id");
		lsRetStr.append(" and bw_string_value.tx_id = bw_txs.tx_id");
		lsRetStr.append(" and bw_txs.created_on >= ? "); //SDATE
		lsRetStr.append(" and bw_txs.created_on <= ? "); //EDATE
		lsRetStr.append(" and bw_txs.created_by = bw_user.id");

		lsRetStr.append(" union");
		/* formula update */
		lsRetStr.append(" select bw_txs.tx_id, bw_txs.created_on, bw_user.email_address as created_by, bw_txs.comment_ , action='FRMUPD'");
		lsRetStr.append(" FROM #ACCESSIBLE_ROWS  AS bwrow,");
		lsRetStr.append(" bw_txs,");
		lsRetStr.append(" bw_string_value,");
		lsRetStr.append(" bw_strval_formula,");
		lsRetStr.append(" bw_formula_value,");
		lsRetStr.append(" bw_cell,");
		lsRetStr.append(" bw_user");

		lsRetStr.append(" where");
		lsRetStr.append(" bw_cell.bw_row_id = bwrow.ROWID");
		lsRetStr.append(" and bw_string_value.bw_cell_id = bw_cell.id");
		lsRetStr.append(" and bw_string_value.id = bw_strval_formula.strval_id");
		lsRetStr.append(" and bw_strval_formula.fval_id = bw_formula_value.id");
		lsRetStr.append(" and bw_formula_value.tx_id = bw_txs.tx_id");
		lsRetStr.append(" and bw_txs.created_on >= ? "); //SDATE
		lsRetStr.append(" and bw_txs.created_on <= ? "); //EDATE
		lsRetStr.append(" and bw_txs.created_by = bw_user.id");

		lsRetStr.append(" union ");
		/* column added */
		lsRetStr.append(" select bw_txs.tx_id, bw_txs.created_on, bw_user.email_address as created_by, bw_txs.comment_ , action='COLADD'");
		lsRetStr.append(" from");
		lsRetStr.append(" bw_txs,");
		lsRetStr.append(" bw_column,");
		lsRetStr.append(" bw_tbl,");
		lsRetStr.append(" bw_user");

		lsRetStr.append(" where");
		lsRetStr.append(" bw_column.bw_tbl_id = bw_tbl.id");
		lsRetStr.append(" and bw_tbl.id = ? ");
		lsRetStr.append(" and bw_column.tx_id = bw_txs.tx_id");
		lsRetStr.append(" and bw_txs.created_on >= ? "); //SDATE
		lsRetStr.append(" and bw_txs.created_on <= ? "); //EDATE
		lsRetStr.append(" and bw_txs.created_by = bw_user.id");
		lsRetStr.append(" and bw_column.is_active = 1");

		lsRetStr.append(" union ");
		/* column deleted */
		lsRetStr.append(" select bw_txs.tx_id, bw_txs.created_on, bw_user.email_address as created_by, bw_txs.comment_ , action='COLDEL'");
		lsRetStr.append(" from");
		lsRetStr.append(" bw_txs,");
		lsRetStr.append(" bw_column,");
		lsRetStr.append(" bw_tbl,");
		lsRetStr.append(" bw_user");

		lsRetStr.append(" where");
		lsRetStr.append(" bw_column.bw_tbl_id = bw_tbl.id");
		lsRetStr.append(" and bw_tbl.id = ? ");
		lsRetStr.append(" and bw_column.tx_id = bw_txs.tx_id");
		lsRetStr.append(" and bw_txs.created_on >= ? "); //SDATE
		lsRetStr.append(" and bw_txs.created_on <= ? "); //EDATE
		lsRetStr.append(" and bw_txs.created_by = bw_user.id");
		lsRetStr.append(" and bw_column.is_active = 0");

		lsRetStr.append(" union");
		/* Base line added */
		lsRetStr.append(" select bw_txs.tx_id, bw_txs.created_on, bw_user.email_address as created_by, bw_txs.comment_ , action='BLNADD'");
		lsRetStr.append(" from");
		lsRetStr.append(" bw_txs,");
		lsRetStr.append(" bw_bl,");
		lsRetStr.append(" bw_bl_tbl,");
		lsRetStr.append(" bw_user");

		lsRetStr.append(" where");
		lsRetStr.append(" bw_bl.id = bw_bl_tbl.baseline_id");
		lsRetStr.append(" and bw_bl_tbl.table_id = ? ");
		lsRetStr.append(" and bw_bl.tx_id = bw_txs.tx_id");
		lsRetStr.append(" and bw_txs.created_on >= ? "); //SDATE
		lsRetStr.append(" and bw_txs.created_on <= ? "); //EDATE
		lsRetStr.append(" and bw_txs.created_by = bw_user.id");
		lsRetStr.append(" order by bw_txs.tx_id");
		lsRetStr.append(" DROP TABLE #ACCESSIBLE_ROWS");

		return lsRetStr.toString();
	}

	// Transaction list with RowQuery join for Without BL
	public static String getFiltredTransactionListNoBL(String asRowQuery)
	{
		StringBuffer lsRetStr = new StringBuffer();
		/* rows assignments */
		lsRetStr.append(" CREATE TABLE  #ACCESSIBLE_ROWS ");
		lsRetStr.append(" ( ROWID  INT  PRIMARY KEY NOT NULL  ) " );
		lsRetStr.append(" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery );

//        lsRetStr.append(" SELECT DISTINCT ( BW_ROW.OWNER_TID) , bw_txs.created_on, bw_user.email_address as created_by,  bw_txs.comment_, action='ROWADD' ");
//        lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS RQ, BW_ROW, BW_TXS, BW_USER");
//        lsRetStr.append(" WHERE BW_ROW.BW_TBL_ID = ? AND "); // TBLID
//        lsRetStr.append(" BW_ROW.OWNER_TID > ? and "); // STID
//        lsRetStr.append(" BW_ROW.OWNER_ID = ? "); // UID
//        lsRetStr.append(" and BW_TXS.tx_id = BW_ROW.TX_ID ");
//        lsRetStr.append(" and bw_txs.created_by = bw_user.id");
//        lsRetStr.append(" and BW_ROW.ID = RQ.ROWID");

//// WHERE BW_ROW.BW_TBL_ID =@TABLE_ID AND BW_ROW.OWNER_TID >= @STID and BW_ROW.OWNER_ID = @USER_ID

//        lsRetStr.append(" union");

		/*NEW  rows */
		//lsRetStr.append(" SELECT DISTINCT ( BW_ROW.TX_ID ), bw_txs.created_on, bw_user.email_address as created_by,  bw_txs.comment_, action='ROWADD' ");
		//lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS RQ, BW_TXS, BW_ROW, BW_USER");
		//lsRetStr.append(" WHERE BW_ROW.BW_TBL_ID = ? "); // TBLID
		//lsRetStr.append(" AND BW_ROW.TX_ID > ?  "); // STID
		//lsRetStr.append(" and bw_txs.tx_id = BW_ROW.TX_ID ");
		//lsRetStr.append(" and bw_txs.created_by <> ?"); // UID
		//lsRetStr.append(" and BW_ROW.ID = RQ.ROWID");
		//lsRetStr.append(" and bw_txs.created_by = bw_user.id");

		//lsRetStr.append(" union");

		///* rows deleted */
		//lsRetStr.append(" select DISTINCT ( bw_txs.tx_id ), bw_txs.created_on, bw_user.email_address as created_by,  bw_txs.comment_, action='ROWDEL' ");
		//lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS RQ, bw_txs, bw_cell_status, bw_cell, BW_ROW, bw_user");
		//lsRetStr.append(" where bw_cell.bw_ROW_id = BW_ROW.id");
		//lsRetStr.append(" and BW_ROW.BW_TBL_ID = ? "); // TBLID
		//lsRetStr.append(" and bw_cell_status.bw_cell_id = bw_cell.id");
		//lsRetStr.append(" and bw_cell_status.tx_id = bw_txs.tx_id");
		//lsRetStr.append(" and bw_cell_status.active = 0");
		//lsRetStr.append(" and bw_txs.TX_ID > ? "); // STID
		//lsRetStr.append(" and bw_txs.created_by <> ? "); // UID
		//lsRetStr.append(" and bw_txs.created_by = bw_user.id");
		//lsRetStr.append(" and BW_ROW.ID = RQ.ROWID");

		//lsRetStr.append(" union");

		/* string value updates */

		lsRetStr.append(" SELECT DISTINCT BW_TXS.TX_ID, BW_TXS.CREATED_ON, BW_USER.EMAIL_ADDRESS AS CREATED_BY ,  BW_TXS.COMMENT_ , ACTION='CELLUPD' ");
		lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS RQ, BW_STRING_VALUE, BW_CELL, BW_COLUMN, BW_TXS, BW_USER ");
		lsRetStr.append(" WHERE ");
		lsRetStr.append(" BW_CELL.BW_COLUMN_ID = BW_COLUMN.ID ");
		lsRetStr.append(" AND BW_CELL.BW_ROW_ID = RQ.ROWID "); // TBLID
		lsRetStr.append(" AND BW_COLUMN.BW_TBL_ID = ? ");
		lsRetStr.append(" AND BW_CELL.ACTIVE = 1 "); // STID
		lsRetStr.append(" AND BW_STRING_VALUE.BW_CELL_ID = BW_CELL.ID ");
		lsRetStr.append(" AND BW_STRING_VALUE.TX_ID = BW_TXS.TX_ID "); // UID
		lsRetStr.append(" AND BW_CELL.TX_ID > ? ");
		lsRetStr.append(" AND BW_TXS.CREATED_BY = BW_USER.ID ");
		lsRetStr.append(" AND BW_USER.ID <> ? ");
		lsRetStr.append(" AND BW_TXS.TX_ID =  BW_STRING_VALUE.TX_ID ");
		lsRetStr.append(" DROP TABLE #ACCESSIBLE_ROWS");
		return lsRetStr.toString();
	}

	// Compare table queries with RowQuery
	public static String getFiltredQueryTBLCmpXL(String asRowQuery)
	{
		//BW_GET_TBL_AT_T

		String lsQueryStr =
		"  CREATE TABLE  #ACCESSIBLE_ROWS " +
		" ( " +
		" 	ROWID  INT  PRIMARY KEY NOT NULL " +
		" ) " +
		" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery +

		" CREATE TABLE  #CTXS"+
		"( "+
		"	SV_ID INT PRIMARY KEY NOT NULL"+
		" ) "+
		" INSERT INTO #CTXS "+
		" SELECT "+
		" MAX(BW_STRING_VALUE.ID) AS SV_ID "+
		" FROM "+
		" BW_STRING_VALUE,   BW_CELL,"+
		" BW_GetColumnAccessNew(? , ?, ?)  C, "+ // Table Id, User Id, Member
		" #ACCESSIBLE_ROWS R"+
		" WHERE"+
		" BW_CELL.BW_COLUMN_ID = C.COLID"+
		" AND BW_CELL.BW_ROW_ID = R.ROWID"+
		" AND BW_CELL.ID = BW_STRING_VALUE.BW_CELL_ID"+
		" AND BW_STRING_VALUE.TX_ID  <= ?"+ // Txid
		" GROUP BY BW_STRING_VALUE.BW_CELL_ID"+

		" CREATE TABLE  #STXS"+
		" ( "+
		" CS_ID INT  PRIMARY KEY NOT NULL"+
		" )"+

		" INSERT INTO #STXS"+
		" SELECT "+
		" MAX (BW_CELL_STATUS.ID) AS CS_ID"+
		" FROM "+
		" BW_CELL_STATUS,"+
		" BW_CELL, "+
		" BW_GetColumnAccessNew(?, ?, ?) C,"+ // Table Id, User Id, Member
		" #ACCESSIBLE_ROWS R "+
		" WHERE"+
		" BW_CELL.BW_COLUMN_ID = C.COLID"+
		" AND BW_CELL.BW_ROW_ID = R.ROWID"+
		" AND BW_CELL.ID = BW_CELL_STATUS.BW_CELL_ID"+
		" AND BW_CELL_STATUS.TX_ID <= ? "+ // Txid
		" GROUP BY BW_CELL_STATUS.BW_CELL_ID"+

		" SELECT "+
		" 	BW_COLUMN.ID as COLID, "+
		" 	BW_ROW.ID as ROWID,"+
		" 	BW_COLUMN.SEQUENCE_NUMBER AS COLSEQ,"+
		" 	BW_ROW.SEQUENCE_NUMBER  AS ROWSEQ,"+
		" 	BW_STRING_VALUE.STRING_VALUE,"+
		" 	BW_FORMULA_VALUE.FORMULA,"+
		" 	BW_COLUMN.NAME"+
		" FROM"+
		" 	#CTXS AS CTXS,"+
		" 	#STXS AS STXS,"+
		" 	BW_CELL,"+
		" 	BW_STRING_VALUE,"+
		" 	BW_CELL_STATUS,"+
		" 	BW_ROW,"+
		" 	BW_COLUMN,"+
		" 	BW_STRVAL_FORMULA,"+
		" 	BW_FORMULA_VALUE"+
		" WHERE"+
		" 	BW_CELL.ID = BW_STRING_VALUE.BW_CELL_ID"+
		" 	AND BW_STRING_VALUE.ID = CTXS.SV_ID"+
		" 	AND BW_CELL.ID = BW_CELL_STATUS.BW_CELL_ID"+
		" 	AND BW_CELL_STATUS.ID = STXS.CS_ID"+
		" 	AND BW_CELL.BW_ROW_ID = BW_ROW.ID"+
		" 	AND BW_CELL.BW_COLUMN_ID = BW_COLUMN.ID"+
		" 	AND BW_STRVAL_FORMULA.STRVAL_ID = BW_STRING_VALUE.ID"+
		" 	AND BW_STRVAL_FORMULA.FVAL_ID = BW_FORMULA_VALUE.ID"+
		" 	AND BW_CELL_STATUS.ACTIVE = 1"+

		" UNION ALL"+

		" SELECT"+
		" 	BW_COLUMN.ID ,"+
		" 	BW_ROW.ID,"+
		" 	BW_COLUMN.SEQUENCE_NUMBER,"+
		" 	BW_ROW.SEQUENCE_NUMBER,"+
		" 	BW_STRING_VALUE.STRING_VALUE,"+
		" 	NULL AS FORMULA,"+
		" 	BW_COLUMN.NAME"+
		" 	FROM"+
		" 		#CTXS AS CTXS,"+
		" 		#STXS AS STXS,"+
		" 		BW_CELL,"+
		" 		BW_STRING_VALUE,"+
		" 		BW_CELL_STATUS,"+
		" 		BW_ROW,"+
		" 		BW_COLUMN"+
		" 	WHERE"+
		" 		BW_CELL.ID = BW_STRING_VALUE.BW_CELL_ID"+
		" 		AND BW_STRING_VALUE.ID = CTXS.SV_ID"+
		" 		AND BW_CELL.ID = BW_CELL_STATUS.BW_CELL_ID"+
		" 		AND BW_CELL_STATUS.ID = STXS.CS_ID"+
		" 		AND BW_CELL.BW_ROW_ID = BW_ROW.ID"+
		" 		AND BW_CELL.BW_COLUMN_ID = BW_COLUMN.ID"+
		" 		AND NOT EXISTS (SELECT BW_STRVAL_FORMULA.ID FROM BW_STRVAL_FORMULA WHERE BW_STRVAL_FORMULA.STRVAL_ID = BW_STRING_VALUE.ID)"+
		" 		AND BW_CELL_STATUS.ACTIVE = 1"+
		" 	ORDER BY BW_COLUMN.SEQUENCE_NUMBER, BW_ROW.SEQUENCE_NUMBER"	+
		" DROP TABLE #CTXS " +
		" DROP TABLE #STXS " +
		" DROP TABLE #ACCESSIBLE_ROWS ";
		return lsQueryStr;
	}

	// Compare table queries with RowQuery
	public static String getFiltredQueryValueChangesCmpXL(String asRowQuery)
	{
		//BW_GET_VALUE_CHANGES_BETN_TID
	//	StringBuffer lsRetStr	= new StringBuffer();

	//	return lsRetStr.toString();

		String lsQueryStr =

		"  CREATE TABLE  #ACCESSIBLE_ROWS " +
		" ( " +
		" 	ROWID  INT  PRIMARY KEY NOT NULL " +
		" ) " +
		" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery +

		" SELECT "+
		" 	BWROW.ROWID AS ROW_ID,"+
		" 	C.COLID AS COLUMN_ID,"+
		" 	BW_SV.STRING_VALUE AS CELL_STRING_VALUE,"+
		" 	BW_TXS.CREATED_BY AS TX_CREATED_BY,"+
		" 	BW_TXS.CREATED_ON,"+
		" 	BW_TXS.COMMENT_,"+
		" 	BW_USER.EMAIL_ADDRESS,"+
		" 	BW_FORMULA_VALUE.FORMULA ,"+
		" 	BW_COLUMN.SEQUENCE_NUMBER AS COLSEQ,"+
		" 	BW_ROW.SEQUENCE_NUMBER  AS ROWSEQ,"+
		" 	BW_COLUMN.NAME"+
		" FROM"+
		" 	#ACCESSIBLE_ROWS AS BWROW,"+
		" 	BW_CELL AS BWCELL,"+
		" 	BW_STRING_VALUE   AS BW_SV,"+
		" 	BW_TXS,"+
		" 	BW_USER,"+
		" 	BW_GetColumnAccessNew(?, ?, ?)  C,"+ // Table Id ,user Id, MemberId
		" 	BW_STRVAL_FORMULA,"+
		" 	BW_FORMULA_VALUE,"+
		" 	BW_COLUMN,"+
		" 	BW_ROW"+
		" WHERE"+
		" 	BWCELL.BW_ROW_ID = BWROW.ROWID"+
		" 	AND     BWCELL.BW_COLUMN_ID = C.COLID"+
		" 	AND     BWCELL.CELL_TYPE = 'STRING'"+
		" 	AND     BW_SV.BW_CELL_ID = BWCELL.ID"+
		" 	AND     BW_SV.TX_ID > ?"+ // STID
		" 	AND     BW_SV.TX_ID <= ?"+ // ETID
		" 	AND     BW_TXS.TX_ID = BW_SV.TX_ID"+
		" 	AND     BW_TXS.CREATED_BY = BW_USER.ID"+
		" 	AND     BW_SV.ID = BW_STRVAL_FORMULA.STRVAL_ID"+
		" 	AND     BW_STRVAL_FORMULA.FVAL_ID = BW_FORMULA_VALUE.ID"+
		" 	AND 	BWCELL.BW_COLUMN_ID = BW_COLUMN.ID"+
		" 	AND     BWCELL.BW_ROW_ID = BW_ROW.ID"+
		" UNION"+

		" SELECT"+
		" 	BWROW.ROWID AS ROW_ID,"+
		" 	C.COLID AS COLUMN_ID,"+
		" 	BW_SV.STRING_VALUE AS CELL_STRING_VALUE,"+
		" 	BW_TXS.CREATED_BY AS TX_CREATED_BY,"+
		" 	BW_TXS.CREATED_ON,"+
		" 	BW_TXS.COMMENT_,"+
		" 	BW_USER.EMAIL_ADDRESS,"+
		" 	NULL AS FORMULA ,"+
		" 	BW_COLUMN.SEQUENCE_NUMBER AS COLSEQ,"+
		" 	BW_ROW.SEQUENCE_NUMBER  AS ROWSEQ,"+
		" 	BW_COLUMN.NAME"+
		" FROM"+
		" 	#ACCESSIBLE_ROWS AS BWROW,"+
		" 	BW_CELL AS BWCELL,"+
		" 	BW_STRING_VALUE   AS BW_SV,"+
		" 	BW_TXS,"+
		" 	BW_USER,"+
		" 	BW_GetColumnAccessNew(? , ?, ?)  C,"+ // Table Id ,user Id, MemberId
		" 	BW_COLUMN,"+
		" 	BW_ROW"+
		" WHERE"+
		" 			BWCELL.BW_ROW_ID = BWROW.ROWID"+
		" 	AND     BWCELL.BW_COLUMN_ID = C.COLID"+
		" 	AND     BWCELL.CELL_TYPE = 'STRING'"+
		" 	AND     BW_SV.BW_CELL_ID = BWCELL.ID"+
		" 	AND     BW_SV.TX_ID > ? "+ // STID
		" 	AND     BW_SV.TX_ID <= ? "+ // ETID
		" 	AND     BW_TXS.TX_ID = BW_SV.TX_ID"+
		" 	AND     BW_TXS.CREATED_BY = BW_USER.ID"+
		" 	AND 	BWCELL.BW_COLUMN_ID = BW_COLUMN.ID"+
		" 	AND     BWROW.ROWID = BW_ROW.ID"+
		" 	AND     NOT EXISTS"+
		" 		(SELECT BW_STRVAL_FORMULA.STRVAL_ID FROM BW_STRVAL_FORMULA WHERE BW_STRVAL_FORMULA.STRVAL_ID  = BW_SV.ID)"+
		" ORDER BY BW_TXS.CREATED_ON "+
		" DROP TABLE #ACCESSIBLE_ROWS ";


		return lsQueryStr;
	}


	// Compare table queries with RowQuery
	// BW_GET_VALUE_CHANGES_AFTER_IMPORT
	public static String getFiltredQueryStatusChangesCmpXL(String asRowQuery)
	{
		//BW_GET_STATUS_CHANGES_BETN_TID
		String lsQueryStr =

		"  CREATE TABLE  #ACCESSIBLE_ROWS " +
		" ( " +
		" 	ROWID  INT  PRIMARY KEY NOT NULL " +
		" ) " +
		" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery +

		" SELECT "+
	    " BWROW.ROWID AS ROW_ID,"+
		" C.COLID AS COLUMN_ID,"+
		" BW_CELL_STATUS.ACTIVE,"+
		" BW_TXS.CREATED_BY AS TX_CREATED_BY,"+
		" BW_TXS.CREATED_ON,"+
		" BW_TXS.COMMENT_,"+
		" BW_USER.EMAIL_ADDRESS,"+
	    " BW_COLUMN.SEQUENCE_NUMBER as COL_SEQ,"+
		" BW_ROW.SEQUENCE_NUMBER as ROW_SEQ"+
		" FROM"+
		" #ACCESSIBLE_ROWS AS BWROW,"+
		" BW_CELL AS BWCELL,"+
		" BW_TXS,"+
		" BW_USER,"+
		" BW_GetColumnAccessNew(?, ?, ?)  C,"+ // TableId, UserId, Memberid
		" BW_CELL_STATUS,"+
		" BW_COLUMN,"+
		" BW_ROW"+
		" WHERE"+
	    " BW_ROW.ID = BWCELL.BW_ROW_ID"+
        " AND BWCELL.BW_ROW_ID = BWROW.ROWID "+
		" AND BW_COLUMN.ID = BWCELL.BW_COLUMN_ID"+
		" AND BWCELL.BW_COLUMN_ID = C.COLID"+
		" AND BW_CELL_STATUS.BW_CELL_ID = BWCELL.ID"+
		" AND BW_CELL_STATUS.TX_ID = BW_TXS.TX_ID"+
		" AND BW_TXS.TX_ID > ?"+ // STID
		" AND BW_TXS.TX_ID <= ? "+ // ETID
		" AND BW_TXS.CREATED_BY = BW_USER.ID"+
		" ORDER BY BW_COLUMN.SEQUENCE_NUMBER, BW_ROW.SEQUENCE_NUMBER, BW_TXS.CREATED_ON"+
		" DROP TABLE #ACCESSIBLE_ROWS ";

		return lsQueryStr;
	}

	//	BW_GET_VALUE_CHANGES_AFTER_IMPORT
	public static String getFiltredQueryValueChangesAfterImport(String asRowQuery)
	{
		//BW_GET_VALUE_CHANGES_AFTER_IMPORT
		String lsQueryStr =

		"  CREATE TABLE  #ACCESSIBLE_ROWS " +
		" ( " +
		" 	ROWID  INT  PRIMARY KEY NOT NULL " +
		" ) " +
		" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery +

		" SELECT "+
		" 	BWROW.ROWID AS ROW_ID,"+
		" 	C.COLID AS COLUMN_ID,"+
		" 	BW_SV.STRING_VALUE AS CELL_STRING_VALUE,"+
		" 	BW_TXS.CREATED_BY AS TX_CREATED_BY,"+
		" 	BW_TXS.CREATED_ON,"+
		" 	BW_TXS.COMMENT_,"+
		" 	BW_USER.EMAIL_ADDRESS,"+
		" 	BW_FORMULA_VALUE.FORMULA ,"+
		" 	BW_COLUMN.SEQUENCE_NUMBER AS COLSEQ,"+
		" 	BW_ROW.SEQUENCE_NUMBER  AS ROWSEQ,"+
		" 	BW_COLUMN.NAME"+
		" FROM"+
		" 	#ACCESSIBLE_ROWS AS BWROW,"+
		" 	BW_CELL AS BWCELL,"+
		" 	BW_STRING_VALUE   AS BW_SV,"+
		" 	BW_TXS,"+
		" 	BW_USER,"+
		" 	BW_GetColumnAccessNew(?, ?, ?)  C,"+ // Table id , user id memberid
		" 	BW_STRVAL_FORMULA,"+
		" 	BW_FORMULA_VALUE,"+
		" 	BW_COLUMN,"+
		" 	BW_ROW"+
		" WHERE "+
		" 	BWCELL.BW_ROW_ID = BWROW.ROWID "+
		" 	AND     BWCELL.BW_COLUMN_ID = C.COLID "+
		" 	AND     BWCELL.CELL_TYPE = 'STRING'"+
		" 	AND     BW_SV.BW_CELL_ID = BWCELL.ID"+
		" 	AND     BW_SV.TX_ID > ? "+ // Stid
		" 	AND     BW_TXS.TX_ID = BW_SV.TX_ID"+
		" 	AND     BW_TXS.CREATED_BY = BW_USER.ID"+
		" 	AND     BW_SV.ID = BW_STRVAL_FORMULA.STRVAL_ID"+
		" 	AND     BW_STRVAL_FORMULA.FVAL_ID = BW_FORMULA_VALUE.ID"+
		" 	AND 	BWCELL.BW_COLUMN_ID = BW_COLUMN.ID"+
		" 	AND     BWCELL.BW_ROW_ID = BW_ROW.ID"+

		" UNION "+

		" SELECT"+
		" 	BWROW.ROWID AS ROW_ID,"+
		" 	C.COLID AS COLUMN_ID,"+
		" 	BW_SV.STRING_VALUE AS CELL_STRING_VALUE,"+
		" 	BW_TXS.CREATED_BY AS TX_CREATED_BY,"+
		" 	BW_TXS.CREATED_ON,"+
		" 	BW_TXS.COMMENT_,"+
		" 	BW_USER.EMAIL_ADDRESS,"+
		" 	NULL AS FORMULA ,"+
		" 	BW_COLUMN.SEQUENCE_NUMBER AS COLSEQ,"+
		" 	BW_ROW.SEQUENCE_NUMBER  AS ROWSEQ,"+
		" 	BW_COLUMN.NAME"+
		" FROM"+
		" 	#ACCESSIBLE_ROWS AS BWROW,"+
		" 	BW_CELL AS BWCELL,"+
		" 	BW_STRING_VALUE   AS BW_SV,"+
		" 	BW_TXS,"+
		" 	BW_USER,"+
		" 	BW_GetColumnAccessNew(?, ?, ?)  C,"+ // Table id , user id memberid
		" 	BW_COLUMN,"+
		" 	BW_ROW"+
		" WHERE"+
		" 	    BWCELL.BW_ROW_ID = BWROW.ROWID"+
		" 	AND     BWCELL.BW_COLUMN_ID = C.COLID"+
		" 	AND     BWCELL.CELL_TYPE = 'STRING'"+
		" 	AND     BW_SV.BW_CELL_ID = BWCELL.ID"+
		" 	AND     BW_SV.TX_ID > ? "+ // STID
		" 	AND     BW_TXS.TX_ID = BW_SV.TX_ID"+
		" 	AND     BW_TXS.CREATED_BY = BW_USER.ID"+
		" 	AND 	BWCELL.BW_COLUMN_ID = BW_COLUMN.ID"+
		" 	AND     BWROW.ROWID = BW_ROW.ID"+
		" 	AND     NOT EXISTS"+
		" 		(SELECT BW_STRVAL_FORMULA.STRVAL_ID FROM BW_STRVAL_FORMULA WHERE BW_STRVAL_FORMULA.STRVAL_ID  = BW_SV.ID)"+

		" ORDER BY BW_TXS.CREATED_ON"+
		" DROP TABLE #ACCESSIBLE_ROWS ";

		return lsQueryStr;

	}

	public static String getFiltredQueryStatusChangesAfterImport(String asRowQuery)
	{
		//BW_GET_STATUS_CHANGES_AFTER_IMPORT
		String lsQueryStr =

		"  CREATE TABLE  #ACCESSIBLE_ROWS " +
		" ( " +
		" 	ROWID  INT  PRIMARY KEY NOT NULL " +
		" ) " +
		" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery +

		" SELECT" +
		"     BWROW.ROWID AS ROW_ID," +
		"     C.COLID AS COLUMN_ID," +
		"     BW_CELL_STATUS.ACTIVE," +
		"     BW_TXS.CREATED_BY AS TX_CREATED_BY," +
		"     BW_TXS.CREATED_ON," +
		"     BW_TXS.COMMENT_," +
		"     BW_USER.EMAIL_ADDRESS," +
		"     BW_COLUMN.SEQUENCE_NUMBER as COLSEQ," +
		"     BW_ROW.SEQUENCE_NUMBER as ROWSEQ" +
		" FROM" +
		"     #ACCESSIBLE_ROWS AS BWROW," +
		"     BW_CELL AS BWCELL," +
		"     BW_TXS," +
		"     BW_USER," +
		"     BW_GetColumnAccessNew(? , ?, ?)  C," + // Table id , user id memberid
		"     BW_CELL_STATUS," +
		"     BW_COLUMN," +
		"     BW_ROW" +
		" WHERE" +
		" 			  BW_ROW.ID = BWCELL.BW_ROW_ID" +
		"     AND	  BWCELL.BW_ROW_ID = BWROW.ROWID" +
		"     AND 	  BW_COLUMN.ID = BWCELL.BW_COLUMN_ID" +
		"     AND     BWCELL.BW_COLUMN_ID = C.COLID" +
		"     AND     BW_CELL_STATUS.BW_CELL_ID = BWCELL.ID" +
		"     AND     BW_CELL_STATUS.TX_ID = BW_TXS.TX_ID" +
		"     AND     BW_TXS.TX_ID > ? " + // STID
		"     AND	  BW_TXS.CREATED_BY = BW_USER.ID" +
		" ORDER BY BW_COLUMN.SEQUENCE_NUMBER, BW_ROW.SEQUENCE_NUMBER, BW_TXS.CREATED_ON" +
		" DROP TABLE #ACCESSIBLE_ROWS ";


	return lsQueryStr;

	}

	// BW_GET_TBL_TXLIST_AFTER_IMPORT
	public static String getFiltredTransactionListAfterImport(String asRowQuery)
	{
		StringBuffer lsRetStr = new StringBuffer();
		/* rows added */

		lsRetStr.append(" CREATE TABLE  #ACCESSIBLE_ROWS ");
		lsRetStr.append(" ( ROWID  INT  PRIMARY KEY NOT NULL  ) " );
		lsRetStr.append(" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery );

		lsRetStr.append(" select bw_txs.tx_id, bw_txs.created_on, bw_user.email_address as created_by, bw_txs.comment_ , action='ROWADD' ");

		lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS bwrow,");
		lsRetStr.append(" bw_txs,");
		lsRetStr.append(" bw_cell_status,");
		lsRetStr.append(" bw_cell,");
		lsRetStr.append(" bw_user");

		lsRetStr.append(" where");
		lsRetStr.append(" bw_cell.bw_row_id = bwrow.ROWID");
		lsRetStr.append(" and bw_cell_status.bw_cell_id = bw_cell.id");
		lsRetStr.append(" and bw_cell_status.tx_id = bw_txs.tx_id");
		lsRetStr.append(" and bw_cell_status.active = 1");
		lsRetStr.append(" and bw_txs.tx_id >= ? "); //SDATE
		lsRetStr.append(" and bw_txs.created_by = bw_user.id");

		lsRetStr.append(" union");
		/* rows deleted */
		lsRetStr.append(" select bw_txs.tx_id, bw_txs.created_on, bw_user.email_address as created_by, bw_txs.comment_, action='ROWDEL'");
		lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS bwrow,");
		lsRetStr.append(" bw_txs,");
		lsRetStr.append(" bw_cell_status,");
		lsRetStr.append(" bw_cell,");
		lsRetStr.append(" bw_user");

		lsRetStr.append(" where");
		lsRetStr.append(" bw_cell.bw_row_id = bwrow.ROWID");
		lsRetStr.append(" and bw_cell_status.bw_cell_id = bw_cell.id");
		lsRetStr.append(" and bw_cell_status.tx_id = bw_txs.tx_id");
		lsRetStr.append(" and bw_cell_status.active = 0");
		lsRetStr.append(" and bw_txs.tx_id >= ? "); //SDATE
		lsRetStr.append(" and bw_txs.created_by = bw_user.id");

		lsRetStr.append(" union");
		/* string value updates */
		lsRetStr.append(" select bw_txs.tx_id, bw_txs.created_on, bw_user.email_address as created_by, bw_txs.comment_ , action='CELLUPD'");
		lsRetStr.append(" FROM #ACCESSIBLE_ROWS AS bwrow,");
		lsRetStr.append(" bw_txs,");
		lsRetStr.append(" bw_string_value,");
		lsRetStr.append(" bw_cell,");
		lsRetStr.append(" bw_user");

		lsRetStr.append(" where");
		lsRetStr.append(" bw_cell.bw_row_id = bwrow.ROWID");
		lsRetStr.append(" and bw_string_value.bw_cell_id = bw_cell.id");
		lsRetStr.append(" and bw_string_value.tx_id = bw_txs.tx_id");
		lsRetStr.append(" and bw_txs.tx_id >= ? "); //SDATE
		lsRetStr.append(" and bw_txs.created_by = bw_user.id");

		lsRetStr.append(" union");
		/* formula update */
		lsRetStr.append(" select bw_txs.tx_id, bw_txs.created_on, bw_user.email_address as created_by, bw_txs.comment_ , action='FRMUPD'");
		lsRetStr.append(" FROM #ACCESSIBLE_ROWS  AS bwrow,");
		lsRetStr.append(" bw_txs,");
		lsRetStr.append(" bw_string_value,");
		lsRetStr.append(" bw_strval_formula,");
		lsRetStr.append(" bw_formula_value,");
		lsRetStr.append(" bw_cell,");
		lsRetStr.append(" bw_user");

		lsRetStr.append(" where");
		lsRetStr.append(" bw_cell.bw_row_id = bwrow.ROWID");
		lsRetStr.append(" and bw_string_value.bw_cell_id = bw_cell.id");
		lsRetStr.append(" and bw_string_value.id = bw_strval_formula.strval_id");
		lsRetStr.append(" and bw_strval_formula.fval_id = bw_formula_value.id");
		lsRetStr.append(" and bw_formula_value.tx_id = bw_txs.tx_id");
		lsRetStr.append(" and bw_txs.tx_id >= ? "); //SDATE
		lsRetStr.append(" and bw_txs.created_by = bw_user.id");

		lsRetStr.append(" union ");
		/* column added */
		lsRetStr.append(" select bw_txs.tx_id, bw_txs.created_on, bw_user.email_address as created_by, bw_txs.comment_ , action='COLADD'");
		lsRetStr.append(" from");
		lsRetStr.append(" bw_txs,");
		lsRetStr.append(" bw_column,");
		lsRetStr.append(" bw_tbl,");
		lsRetStr.append(" bw_user");

		lsRetStr.append(" where");
		lsRetStr.append(" bw_column.bw_tbl_id = bw_tbl.id");
		lsRetStr.append(" and bw_tbl.id = ? ");
		lsRetStr.append(" and bw_column.tx_id = bw_txs.tx_id");
		lsRetStr.append(" and bw_txs.tx_id >= ? "); //SDATE
		lsRetStr.append(" and bw_txs.created_by = bw_user.id");
		lsRetStr.append(" and bw_column.is_active = 1");

		lsRetStr.append(" union ");
		/* column deleted */
		lsRetStr.append(" select bw_txs.tx_id, bw_txs.created_on, bw_user.email_address as created_by, bw_txs.comment_ , action='COLDEL'");
		lsRetStr.append(" from");
		lsRetStr.append(" bw_txs,");
		lsRetStr.append(" bw_column,");
		lsRetStr.append(" bw_tbl,");
		lsRetStr.append(" bw_user");

		lsRetStr.append(" where");
		lsRetStr.append(" bw_column.bw_tbl_id = bw_tbl.id");
		lsRetStr.append(" and bw_tbl.id = ? ");
		lsRetStr.append(" and bw_column.tx_id = bw_txs.tx_id");
		lsRetStr.append(" and bw_txs.tx_id >= ? "); //SDATE
		lsRetStr.append(" and bw_txs.created_by = bw_user.id");
		lsRetStr.append(" and bw_column.is_active = 0");

		lsRetStr.append(" union");
		/* Base line added */
		lsRetStr.append(" select bw_txs.tx_id, bw_txs.created_on, bw_user.email_address as created_by, bw_txs.comment_ , action='BLNADD'");
		lsRetStr.append(" from");
		lsRetStr.append(" bw_txs,");
		lsRetStr.append(" bw_bl,");
		lsRetStr.append(" bw_bl_tbl,");
		lsRetStr.append(" bw_user");

		lsRetStr.append(" where");
		lsRetStr.append(" bw_bl.id = bw_bl_tbl.baseline_id");
		lsRetStr.append(" and bw_bl_tbl.table_id = ? ");
		lsRetStr.append(" and bw_bl.tx_id = bw_txs.tx_id");
		lsRetStr.append(" and bw_txs.tx_id >= ? "); //SDATE
		lsRetStr.append(" and bw_txs.created_by = bw_user.id");
		lsRetStr.append(" order by bw_txs.tx_id");
		lsRetStr.append(" DROP TABLE #ACCESSIBLE_ROWS");

		return lsRetStr.toString();
	}

	public static String getFiltredCriticalUpdate(String asRowQuery)
	{
		String lsQueryStr =

		"  CREATE TABLE  #ACCESSIBLE_ROWS " +
		" ( " +
		" 	ROWID  INT  PRIMARY KEY NOT NULL " +
		" ) " +
		" INSERT INTO #ACCESSIBLE_ROWS " + asRowQuery +

		//" SELECT DISTINCT BW_ROW.OWNER_TID, BW_TXS.CREATED_ON, BW_USER.EMAIL_ADDRESS AS CREATED_BY , BW_TXS.COMMENT_ , ACTION='ROWADD' "+
		//" FROM #ACCESSIBLE_ROWS AS RQ, BW_ROW, BW_USER, BW_SIGNIFICANT_TXS, BW_TXS "+
		//" WHERE BW_ROW.OWNER_TID > ? AND BW_ROW.OWNER_ID = ? AND RQ.ROWID = BW_ROW.ID "+ // @STID , @USER_ID
		//" AND BW_TXS.TX_ID = BW_ROW.OWNER_TID AND BW_TXS.TX_ID = BW_SIGNIFICANT_TXS.TX_ID AND BW_TXS.CREATED_BY = BW_USER.ID"+

		//" UNION "+
		" SELECT DISTINCT BW_ROW.TX_ID, BW_TXS.CREATED_ON, BW_USER.EMAIL_ADDRESS AS CREATED_BY, BW_TXS.COMMENT_ , ACTION='ROWADD'  "+
		" FROM #ACCESSIBLE_ROWS AS RQ, BW_ROW, BW_USER, BW_SIGNIFICANT_TXS, BW_TXS "+
		" WHERE BW_ROW.TX_ID > ? AND BW_TXS.TX_ID = BW_ROW.TX_ID AND BW_TXS.CREATED_BY <> ? "+ // @STID , @USER_ID
		" AND BW_TXS.TX_ID = BW_ROW.TX_ID AND BW_TXS.TX_ID = BW_SIGNIFICANT_TXS.TX_ID AND BW_TXS.CREATED_BY = BW_USER.ID AND BW_ROW.ID = RQ.ROWID" +

		" UNION "+
		" SELECT DISTINCT BW_TXS.TX_ID, BW_TXS.CREATED_ON, BW_USER.EMAIL_ADDRESS AS CREATED_BY, BW_TXS.COMMENT_, ACTION='ROWDEL' "+
        "    FROM BW_TXS, BW_CELL_STATUS, BW_CELL, #ACCESSIBLE_ROWS AS RQ, BW_USER, BW_SIGNIFICANT_TXS "+
		" WHERE "+
        "    BW_CELL.BW_ROW_ID = RQ.ROWID "+
        "    AND BW_CELL_STATUS.BW_CELL_ID = BW_CELL.ID"+
        "    AND BW_CELL_STATUS.TX_ID = BW_TXS.TX_ID"+
        "    AND BW_CELL_STATUS.ACTIVE = 0"+
        "    AND BW_TXS.TX_ID > ? "+ // @STID
        "    AND BW_TXS.CREATED_BY <> ? "+ //@USER_ID
		"	 AND BW_TXS.TX_ID = BW_SIGNIFICANT_TXS.TX_ID "+
		"	 AND BW_TXS.CREATED_BY = BW_USER.ID "+

		" UNION "+
		" SELECT DISTINCT BW_STRING_VALUE.TX_ID, BW_TXS.CREATED_ON, BW_USER.EMAIL_ADDRESS AS CREATED_BY , BW_TXS.COMMENT_, ACTION='CELLUPD' "+
		" FROM BW_STRING_VALUE, BW_CELL, #ACCESSIBLE_ROWS AS RQ, BW_TXS, BW_USER, BW_SIGNIFICANT_TXS "+
		" WHERE "+
        " BW_CELL.BW_ROW_ID = RQ.ROWID "+
        "	 AND BW_STRING_VALUE.BW_CELL_ID = BW_CELL.ID"+
        "    AND BW_STRING_VALUE.TX_ID > ? "+ //@STID
        "    AND BW_CELL.ACTIVE = 1 "+
        "    AND BW_TXS.CREATED_BY <> ? "+ //@USER_ID
        "    AND BW_TXS.TX_ID =  BW_STRING_VALUE.TX_ID"+
		"	 AND BW_TXS.TX_ID = BW_SIGNIFICANT_TXS.TX_ID "+
		"	 AND BW_TXS.CREATED_BY = BW_USER.ID "+

		" UNION"+
		" SELECT DISTINCT BW_COL_ACCESS.TID, BW_TXS.CREATED_ON, BW_USER.EMAIL_ADDRESS AS CREATED_BY, BW_TXS.COMMENT_, ACTION='COLACCCHD'"+
        "    FROM BW_COL_ACCESS, BW_GETCOLUMNACCESS(? , ?, ?) as COLACC, BW_USER, BW_SIGNIFICANT_TXS, BW_TXS "+ // @TABLE_ID, @USER_ID, @MEMBER_ID
		" WHERE BW_COL_ACCESS.COL_ID = COLACC.colid AND COLACC.PREV_ACCESS <> COLACC.ACCESS_ "+
		" AND BW_COL_ACCESS.TID > ? " +
		" AND BW_COL_ACCESS.TID = BW_TXS.TX_ID " + 
		" AND BW_TXS.TX_ID = BW_SIGNIFICANT_TXS.TX_ID "+
		" AND BW_TXS.CREATED_BY = BW_USER.ID ";

		//System.out.println(" lsQueryStr "+lsQueryStr);
		return lsQueryStr;
	}
};



