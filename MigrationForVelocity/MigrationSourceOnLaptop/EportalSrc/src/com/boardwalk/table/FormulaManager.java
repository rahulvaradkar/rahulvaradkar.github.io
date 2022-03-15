/*
 * FormulaMananger.java
 *
 * sarang 3/1/05
 */

package com.boardwalk.table;

/**
 *
 * @author  administrator
 * @version
 */

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.NeighborhoodException;
import com.boardwalk.exception.SystemException;
import com.boardwalk.database.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package
import java.util.*;

/**
 *
 * @author  administrator
 * @version
 */


public class FormulaManager {

private static String CALL_BW_GET_TBL_FORMULAE="{CALL BW_GET_TBL_FORMULAE(?)}";
private static String CALL_BW_CR_FORMULA="{CALL BW_CR_FORMULA(?,?,?,?,?,?,?,?,?,?)}";
private static String CALL_BW_DEACTIVATE_FORMULA="{CALL BW_DEACTIVATE_FORMULA(?)}";

	/** Creates new ColumnManager */
	public FormulaManager()
	{
	}

	public static Vector getFormulae(Connection connection, int tableId)
	throws SystemException
	{
		PreparedStatement ps = null;
		ResultSet rs = null;
		Vector formulae = new Vector();
		try {

			ps = connection.prepareStatement(CALL_BW_GET_TBL_FORMULAE);
			ps.setInt(1,tableId);
			rs = ps.executeQuery();

			while ( rs.next() )
			{
				int formula_id;
				int start_col_id;
				int end_col_id;
				int start_row_id;
				int end_row_id;
				int ref_column_id;
				int ref_row_id;
				String formula;

				formula_id = rs.getInt("id");
				start_col_id = rs.getInt("start_col_id");
				end_col_id = rs.getInt("end_col_id");
				start_row_id = rs.getInt("start_row_id");
				end_row_id = rs.getInt("end_row_id");
				ref_column_id = rs.getInt("ref_col_id");
				ref_row_id = rs.getInt("ref_row_id");
				formula = rs.getString("formula");

				System.out.println("FormulaManager::getCellFormulae()-> formula : " +
					formula_id + ":" +
					start_col_id + ":" +
					end_col_id + ":" +
					start_row_id + ":" +
					end_row_id + ":" +
					ref_column_id + ":" +
					ref_row_id + ":" +
					formula);

				RangeFormula rf = new RangeFormula(formula_id,
				 								   start_col_id,
												   end_col_id,
												   start_row_id,
												   end_row_id,
												   ref_column_id,
												   ref_row_id,
												   formula);

				formulae.add(rf);

			}

		}
		catch( SQLException sql1 ) {
			throw new SystemException(sql1);
		}
		finally {
			try {
				ps.close();
			}
			catch( SQLException sql2 ) {
				throw new SystemException(sql2);

			}
		}

		return formulae;

	}
    public static int addFormula(Connection connection,
    							  int table_id,
    							  int start_col_id,
    							  int end_col_id,
    							  int start_row_id,
    							  int end_row_id,
    							  int ref_col_id,
    							  int ref_row_id,
    							  String formula,
    							  int tx_id)
	    throws SystemException
	{
		CallableStatement callableStatement = null;
		int formula_id;

		try {
			System.out.println("FormulaManager::addFormula -> Calling BW_CR_FORMULA");
			callableStatement = connection.prepareCall(CALL_BW_CR_FORMULA);
			callableStatement.setInt(1, table_id);
			callableStatement.setInt(2, start_col_id);
			callableStatement.setInt(3, end_col_id);
			callableStatement.setInt(4, start_row_id);
			callableStatement.setInt(5, end_row_id);
			callableStatement.setInt(6, ref_col_id);
			callableStatement.setInt(7, ref_row_id);
			callableStatement.setString(8, formula);
			callableStatement.setInt(9, tx_id);
			callableStatement.registerOutParameter(10,java.sql.Types.INTEGER);

			callableStatement.executeUpdate();

			formula_id = callableStatement.getInt(10);

		} catch (SQLException sql1) {
			throw new SystemException(sql1);
		} finally {
			try {
				callableStatement.close();
			} catch (SQLException sql2) {
				throw new SystemException(sql2);
			}
		}

		return formula_id;
	}

    public static void deactivateFormula(Connection connection, int fid)
	    throws SystemException
	{
		CallableStatement callableStatement = null;

		try
		{
			System.out.println("FormulaManager::deactivateFormula -> Calling BW_DEACTIVATE_FORMULA");
			callableStatement = connection.prepareCall(CALL_BW_DEACTIVATE_FORMULA);
			callableStatement.setInt(1, fid);
			callableStatement.executeUpdate();

		} catch (SQLException sql1) {
			throw new SystemException(sql1);
		} finally {
			try {
				callableStatement.close();
			} catch (SQLException sql2) {
				throw new SystemException(sql2);
			}
		}

	}
}
