import java.sql.*;
import java.util.*;
import boardwalk.connection.*;
import boardwalk.table.*;
import boardwalk.collaboration.*;
import com.boardwalk.exception.BoardwalkException;

public class CollabTest
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
            bwcon = BoardwalkConnectionManager.getBoardwalkConnection(connection, "admin", "admin", -1);
            System.out.println("Successfully obtained authenticated Boardwalk connection");
        }
        catch(BoardwalkException bwe)
        {
            System.out.println("Authentication/Connection Failed");
        }


        if (args[0].equals("-n"))
        {
            if (args[1] == null)
            {
                System.out.println("Please enter valid collaboration name");
            }
            String collabName = args[1];

            try
            {
				// Create a new collaboration
				int collabId = BoardwalkCollaborationManager.createCollaboration(bwcon,
											collabName, "");
				System.out.println("Successfully created collaboration with id = " + collabId);
				// create a new whiteboard
				int wbid = BoardwalkCollaborationManager.createWhiteboard(
											bwcon,
    									 	"Folder1",
    									    collabId );
                // Create a new table in the boardwalk system
                int tableId = BoardwalkTableManager.createTable( bwcon,
                											 collabId,
                											 wbid,
                											 "Table1",
                											 "");
            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error creating the collab, maybe collab with same name exists");
            }
        }
        else if (args[0].equals("-d"))
        {
            int collabId = -1;
            if (args[1] == null)
            {
                System.out.println("Error in syntax");
            }
            try
            {
                collabId = Integer.parseInt(args[1]);
            }
            catch (Exception e)
            {
                System.out.println("Not a valid collab id");
            }
            // delete the table
            try
            {
                BoardwalkCollaborationManager.deleteCollaboration(bwcon, collabId);
                System.out.println("Sucessfully deleted the collab from the database");
            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error deleting collab, maybe the collabid is incorrect or there is a baseline");
            }
        }
        else if (args[0].equals("-g"))
        {
            int collabId = -1;
            if (args[1] == null)
            {
                System.out.println("Error in syntax");
            }
            try
            {
                collabId = Integer.parseInt(args[1]);
            }
            catch (Exception e)
            {
                System.out.println("Not a valid collab id");
            }
            // get the table contents
            try
            {
                BoardwalkCollaborationNode bcn =
                	BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId);
                System.out.println("Sucessfully fetched the collab tree from the database");

				System.out.println("Collaboration = " + bcn.getName());
				Vector wv = bcn.getWhiteboards();
				Iterator wvi = wv.iterator();
				while ( wvi.hasNext())
				{
					BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
					System.out.println("\tWhiteboard = " + bwn.getName());
					Vector tv = bwn.getTables();
					Iterator tvi = tv.iterator();
					while (tvi.hasNext())
					{
						BoardwalkTableNode btn = (BoardwalkTableNode)tvi.next();
						System.out.println("\t\tTable = " + btn.getName());
					}
				}

            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error fetching collaboration");
            }
        }
        else if (args[0].equals("-gc"))
        {
            String collabName = null;
            if (args[1] == null)
            {
                System.out.println("Error in syntax");
            }
            else
            {
				collabName = args[1];
			}
            // get the table contents
            try
            {
                BoardwalkCollaborationNode bcn =
                	BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabName);
                System.out.println("Sucessfully fetched the collab tree from the database");

				System.out.println("Collaboration = " + bcn.getName());
				Vector wv = bcn.getWhiteboards();
				Iterator wvi = wv.iterator();
				while ( wvi.hasNext())
				{
					BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
					System.out.println("\tWhiteboard = " + bwn.getName());
					Vector tv = bwn.getTables();
					Iterator tvi = tv.iterator();
					while (tvi.hasNext())
					{
						BoardwalkTableNode btn = (BoardwalkTableNode)tvi.next();
						System.out.println("\t\tTable = " + btn.getName());
					}
				}
            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error fetching collaboration");
            }
        }
        else if (args[0].equals("-gn"))
        {
            int nhId = -1;
            if (args[1] == null)
            {
                System.out.println("Error in syntax");
            }
            try
            {
                nhId = Integer.parseInt(args[1]);
            }
            catch (Exception e)
            {
                System.out.println("Not a valid nh id");
            }
            // get the table contents
            try
            {
				Vector cl = BoardwalkCollaborationManager.getCollaborationsForNeighborhood(
												bwcon,
												nhId);
				Iterator cli = cl.iterator();
				while (cli.hasNext())
				{
					Integer collabId = (Integer)cli.next();
					BoardwalkCollaborationNode bcn =
						BoardwalkCollaborationManager.getCollaborationTree(bwcon, collabId.intValue());
					System.out.println("Sucessfully fetched the collab tree from the database");

					System.out.println("Collaboration = " + bcn.getName());
					Vector wv = bcn.getWhiteboards();
					Iterator wvi = wv.iterator();
					while ( wvi.hasNext())
					{
						BoardwalkWhiteboardNode bwn = (BoardwalkWhiteboardNode)wvi.next();
						System.out.println("\tWhiteboard = " + bwn.getName());
						Vector tv = bwn.getTables();
						Iterator tvi = tv.iterator();
						while (tvi.hasNext())
						{
							BoardwalkTableNode btn = (BoardwalkTableNode)tvi.next();
							System.out.println("\t\tTable = " + btn.getName());
						}
					}
				}
            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error fetching collaboration");
            }
        }
        else
        {
            System.out.println("Invalid arguments");
        }

    }

};
