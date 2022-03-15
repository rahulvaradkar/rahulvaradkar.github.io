import java.sql.*;
import java.util.*;
import boardwalk.connection.*;
import boardwalk.neighborhood.*;
import boardwalk.table.*;
import com.boardwalk.exception.BoardwalkException;

public class AccessTest
{
    public static void main(String args[])
    {
        if (args[0] == null)
        {
            System.out.println("No arguments specified");
        }
        // get the connection
        Connection connection = null;
        try
        {
            DriverManager.registerDriver(new com.microsoft.jdbc.sqlserver.SQLServerDriver());
        }
        catch(Exception e)
        {
            System.out.println("Problem registering JDBC driver");
        }
        String jdbcConnectionString = "jdbc:microsoft:sqlserver://localhost:4809;DatabaseName=BWDEVL1.5;user=BOARDWALK_APPLICATION_USER;password=BOARDWALK_APPLICATION_USER";

        try
        {
            connection =  DriverManager.getConnection(jdbcConnectionString);
            System.out.println("Connection established successfully");
        }
        catch( SQLException sqe )
        {
            System.out.println("There is a Database connection problem");
        }

        // Get an authenticated boardwalk connection
        BoardwalkConnection bwcon = null;
        try
        {
            bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection, "admin", "admin",-1);
            System.out.println("Successfully obtained authenticated Boardwalk connection");
        }
        catch(BoardwalkException bwe)
        {
            System.out.println("Authentication/Connection Failed");
        }


        if (args[0].equals("-g"))
        {
            if (args[1] == null)
            {
                System.out.println("Please enter valid tableId");
            }

            int tableId = -1;
            try
            {
                tableId = Integer.parseInt(args[1]);
            }
            catch(Exception e)
            {
                // no parent
                System.out.println("invalid tableid");
            }

            try
            {
                // get the access
                BoardwalkTableAccess bta = BoardwalkTableAccessManager.getTableAccess(
														  bwcon,
														  tableId
														);
				printAccess(bta, bwcon);
            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error fetching access for table, check table id");
            }
        }
        if (args[0].equals("-s"))
        {
            if (args[1] == null)
            {
                System.out.println("Please enter valid tableId");
            }

            int tableId = -1;
            try
            {
                tableId = Integer.parseInt(args[1]);
            }
            catch(Exception e)
            {
                // no parent
                System.out.println("invalid tableid");
            }

            try
            {
                // get the access
                BoardwalkTableAccess bta = BoardwalkTableAccessManager.getTableAccess(
														  bwcon,
														  tableId
														);
				BoardwalkAdminAccess baa = bta.getAdminAccess("PUBLIC");
				baa.flipAddRow();
				baa.flipDeleteRow();
				baa.flipAdministerColumn();
				baa.flipAdministerTable();
				BoardwalkTableAccessManager.setAdminAccess(bwcon, baa);

				BoardwalkRowAccess bra = bta.getRowAccess("PUBLIC");
				bra.flipReadAllRows();
				bra.flipWriteMyRows();
				bra.flipWriteAllRows();
				bra.flipReadMyNeighborhoodRows();
				bra.flipWriteMyNeighborhoodRows();
				bra.flipReadMyNeighborhoodImmediateChildrenRows();
				bra.flipWriteMyNeighborhoodImmediateChildrenRows();
				bra.flipReadMyNeighborhoodHeirarchyRows();
				bra.flipWriteMyNeighborhoodHeirarchyRows();
				BoardwalkTableAccessManager.setRowAccess(bwcon, bra);

				try{
					BoardwalkTableContents btc = BoardwalkTableManager.getTableContents(
																	bwcon, bta.getTableId());
					Vector cols = btc.getColumns();
					Iterator colsi = cols.iterator();
					while (colsi.hasNext())
					{
						BoardwalkColumn c = (BoardwalkColumn)colsi.next();
						BoardwalkColumnAccess bca = bta.getColumnAccess("PUBLIC", c.getId());
						if (bca.canRead())
							bca.setNoAccess();
						else
							bca.setWriteAccess();
						BoardwalkTableAccessManager.setColumnAccess(bwcon, bca);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				printAccessRel(bta, bwcon, "PUBLIC");
            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error fetching access for table, check table id");
            }
        }
        else
        {
            System.out.println("Invalid arguments");
        }

    }

    private static void printAccess(BoardwalkTableAccess bta, BoardwalkConnection bwcon)
    {
		Vector rels = bta.getRelationList();
		Iterator relsi = rels.iterator();
		while (relsi.hasNext())
		{
			String rel = (String)relsi.next();
			printAccessRel(bta,bwcon,rel);
		}
	}
    private static void printAccessRel(BoardwalkTableAccess bta, BoardwalkConnection bwcon, String rel)
    {
		System.out.println ("Access Control for relation = " + rel);
		BoardwalkAdminAccess baa = bta.getAdminAccess(rel);
		System.out.println("canAddRow = " + baa.canAddRow());
		System.out.println("canDeleteRow = " + baa.canDeleteRow());
		System.out.println("canAdministerColumn = " + baa.canAdministerColumn());
		System.out.println("canAdministerTable = " + baa.canAdministerTable());

		BoardwalkRowAccess bra = bta.getRowAccess(rel);
		System.out.println("canReadAllRows = " + bra.canReadAllRows());
		System.out.println("canWriteMyRows = " + bra.canWriteMyRows());
		System.out.println("canWriteAllRows = " + bra.canWriteAllRows());
		System.out.println("canReadMyNeighborhoodRows = " + bra.canReadMyNeighborhoodRows());
		System.out.println("canWriteMyNeighborhoodRows = " + bra.canWriteMyNeighborhoodRows());
		System.out.println("canReadMyNeighborhoodImmediateChildrenRows = " + bra.canReadMyNeighborhoodImmediateChildrenRows());
		System.out.println("canWriteMyNeighborhoodImmediateChildrenRows = " + bra.canWriteMyNeighborhoodImmediateChildrenRows());
		System.out.println("canReadMyNeighborhoodHeirarchyRows = " + bra.canReadMyNeighborhoodHeirarchyRows());
		System.out.println("canWriteMyNeighborhoodHeirarchyRows = " + bra.canWriteMyNeighborhoodHeirarchyRows());
		try{
			BoardwalkTableContents btc = BoardwalkTableManager.getTableContents(bwcon, bta.getTableId());
			Vector cols = btc.getColumns();
			Iterator colsi = cols.iterator();
			while (colsi.hasNext())
			{
				BoardwalkColumn c = (BoardwalkColumn)colsi.next();
				BoardwalkColumnAccess bca = bta.getColumnAccess(rel, c.getId());
				System.out.println("can read column " + c.getName() + " = " + bca.canRead());
				System.out.println("can write column " + c.getName() + " = " + bca.canWrite());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
};
