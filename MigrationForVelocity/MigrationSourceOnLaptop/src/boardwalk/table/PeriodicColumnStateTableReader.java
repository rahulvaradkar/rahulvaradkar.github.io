package boardwalk.table;

import java.sql.*;
import java.util.*;
import boardwalk.connection.*;

import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.table.*;
import com.boardwalk.database.*;
import boardwalk.common.*;

public class PeriodicColumnStateTableReader
{

	public PeriodicColumnStateTableReader()
	{

	}

	public static ArrayList getState(Connection connection, int aiTableId, String asState)throws SQLException
	{
		// Read the entire State and insert it in Hash map
		// The cells here will be stored Rowwise, column wise
		PreparedStatement	prepstatement	= null;
		ResultSet			resultset		= null;
		ArrayList StateData					= null;

		String CALL_BW_READ_CURRENT_STATE = "{CALL BW_READ_CURRENT_STATE(?,?)}";
		try
		{
			String	ColName		= "";
			String	CellValue	= "";
			int		RowId		= -1;
			int		ColId		= -1;
			int		CellID		= -1;

			StateData			= new ArrayList();

			prepstatement = connection.prepareStatement(CALL_BW_READ_CURRENT_STATE);
			prepstatement.setInt(1,aiTableId);
			prepstatement.setString(2,asState);

			resultset = prepstatement.executeQuery();

			// Row id and colid will be required for updating the state to done
			// From pending state.
			while (resultset.next())
			{
				ColName		= resultset.getString("COLNAME");
				CellValue	= resultset.getString("CELLVALUE");
				RowId		= resultset.getInt("ROWID");
				ColId		= resultset.getInt("COLID");
				CellID		= resultset.getInt("CELLID");

				NewCell StateCell = new NewCell(CellID, RowId, ColId, ColName, CellValue);

				StateData.add(StateCell);
			}

		}
		catch (SQLException sqlexe)
		{
			  throw sqlexe;
		}
		finally
		{
			try
			{
				prepstatement.close();
			}
			catch (SQLException sqlexe1)
			{
				throw sqlexe1;
			}
		}

		return StateData;
	}

	public static boolean validateState(Connection connection, ArrayList asColCollection, String asTableIds)throws SQLException
	{
		boolean lbRetValue = false ;
		System.out.println(" validateState called for no of columns " + asColCollection.size() );

		PreparedStatement	prepstatement	= null;
		ResultSet			resultset		= null;

		String lsSql = " SELECT COUNT(*) FROM BW_COLUMN WHERE BW_COLUMN.NAME = ? AND BW_COLUMN.IS_ACTIVE = 1 AND BW_COLUMN.BW_TBL_ID = ? ";
		int columnCount	= -1;

		String TempStr = "";
		TempStr = asTableIds.substring(1);
		TempStr = TempStr.substring(0,TempStr.length()-1);
		String tableArray[] = TempStr.split(",");

		prepstatement = connection.prepareStatement(lsSql);

		try
		{
			for(int j=0 ; j < tableArray.length ; j++ )
			{
				for(int i=0 ; i < asColCollection.size() ; i++)
				{
					String lsColName = (String) asColCollection.get(i);
					prepstatement.setString(1,lsColName);
					prepstatement.setInt(2,Integer.parseInt(tableArray[j]));

					resultset = prepstatement.executeQuery();
					while(resultset.next())
						columnCount = resultset.getInt(1);

					if(columnCount < 1 )
					{
						System.out.println(" Missing the following Column Name in previous state "+lsColName+" in Table "+ tableArray[j]);
						break;
					}
				}
			}
		}
		catch (SQLException sqlexe)
		{
			throw sqlexe;
		}
		finally
		{
			try
			{
				prepstatement.close();
			}
			catch (SQLException sqlexe1)
			{
				throw sqlexe1;
			}
		}

		if(columnCount == 1 )
			lbRetValue = true ;

		return lbRetValue;
	}

}