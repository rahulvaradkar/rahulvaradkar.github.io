import java.sql.*;
import java.util.*;
import boardwalk.connection.*;
import boardwalk.neighborhood.*;
import com.boardwalk.exception.BoardwalkException;

public class NhTest
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
			System.out.println("Fetching membership for user admin");
			Vector mList = BoardwalkConnectionManager.getMemberships(connection, "admin", "admin");
			Iterator mi = mList.iterator();
			while (mi.hasNext())
			{
				BoardwalkMember bm = (BoardwalkMember)mi.next();
				System.out.println("id="+bm.getId()+":"+
									"userId="+bm.getUserId()+":"+
									"neighborhoodId="+bm.getNeighborhoodId()+":"+
									"neighborhoodName="+bm.getNeighborhoodName());
			}
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
                System.out.println("Please enter valid neighborhood name");
            }
            String nhName = args[1];
            int parentId = -1;
            try
            {
				parentId = Integer.parseInt(args[2]);
			}
			catch(Exception e)
			{
				// no parent
				System.out.println("No parent nh specified");
			}

            try
            {
				System.out.println("Creating a new neighborhood under neighborhood id = " + parentId);
                // Create a new neighborhood
                int nhId = BoardwalkNeighborhoodManager.createNeighborhood(
											bwcon,
                                            nhName,
                                            false,
                                            parentId
                                         );
                System.out.println("Successfully created neighborhood with id = " + nhId);

            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error creating the nh, maybe nh with same name exists");
            }
        }
        else if (args[0].equals("-d"))
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
                System.out.println("Not a valid neighborhood id");
            }
            // delete the neighborhood
            try
            {
				System.out.println("Deleting neighborhood id = " + nhId );
                BoardwalkNeighborhoodManager.deleteNeighborhood(bwcon, nhId);
                System.out.println("Sucessfully deleted the neighborhood from the database");
            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error deleting neighborhood, maybe the nhid is incorrect or there is a baseline");
            }
        }
        else if (args[0].equals("-g"))
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
            // get the neighborhood tree
            try
            {
                Vector nh0v = BoardwalkNeighborhoodManager.getNeighborhoodTree( bwcon, nhId);
                Iterator nh0i = nh0v.iterator();
				while (nh0i.hasNext())
				{
					BoardwalkNeighborhoodNode bnn = (BoardwalkNeighborhoodNode)nh0i.next();
					printNH(bnn);

				}
            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error fetching neighborhood");
            }
        }
        else if (args[0].equals("-cm"))
        {
            int nhId = -1;
            int userId = -1;
            if (args[1] == null || args[2] == null)
            {
                System.out.println("Error in syntax");
            }
            try
            {
                nhId = Integer.parseInt(args[1]);
                userId = Integer.parseInt(args[2]);
            }
            catch (Exception e)
            {
                System.out.println("Not a valid nhid or userid");
            }
            try
            {
				int memberId = BoardwalkNeighborhoodManager.createMember(
									bwcon,
									nhId,
									userId
								   );
				System.out.println("Successfully created membership for user, memberId = " + memberId);

            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error creating membership");
            }
        }
        else if (args[0].equals("-dm"))
        {
            int memberId = -1;
            if (args[1] == null )
            {
                System.out.println("Error in syntax");
            }
            try
            {
                memberId = Integer.parseInt(args[1]);
            }
            catch (Exception e)
            {
                System.out.println("Not a valid memberId");
            }
            try
            {
				BoardwalkNeighborhoodManager.deleteMember(
									bwcon,
									memberId
								   );
				System.out.println("Successfully deleted membership for user");

            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error deleting membership");
            }
        }
        else if (args[0].equals("-ml"))
        {
            int nhId = -1;
            if (args[1] == null )
            {
                System.out.println("Error in syntax");
            }
            try
            {
                nhId = Integer.parseInt(args[1]);
            }
            catch (Exception e)
            {
                System.out.println("Not a valid neighborhood id");
            }
            try
            {
				Vector mList = BoardwalkNeighborhoodManager.getMemberList(
									bwcon,
									nhId
								   );
				System.out.println("Successfully fetched membership list for nh");
				Iterator mi = mList.iterator();
				while (mi.hasNext())
				{
					BoardwalkMember bm = (BoardwalkMember)mi.next();
					System.out.println("id="+bm.getId()+":"+
										"userId="+bm.getUserId()+":"+
										"neighborhoodId="+bm.getNeighborhoodId()+":"+
										"neighborhoodName="+bm.getNeighborhoodName());
				}
            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error deleting membership");
            }
        }
        else if (args[0].equals("-cr"))
        {
            int nhId = -1;
            String relation = null;
            Vector targetNeighborhoods = new Vector();
            if (args[1] == null || args[2] == null)
            {
                System.out.println("Error in syntax");
            }
            try
            {
                nhId = Integer.parseInt(args[1]);
                relation = args[2];
                for (int i = 3; i < args.length; i++)
                {
					try
					{
						int tnhid = Integer.parseInt(args[i]);
						targetNeighborhoods.addElement(new Integer(tnhid));
					}
					catch (Exception e)
					{
						System.out.println("\""+args[i]+"\" "+"is not a valid input");
					}
				}
            }
            catch (Exception e)
            {
                System.out.println("Not a valid nhid or relation");
            }
            try
            {
				BoardwalkNeighborhoodManager.createRelation(
									bwcon,
									nhId,
									relation,
									targetNeighborhoods
								   );
				System.out.println("Successfully created relationship");

            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error creating relation");
            }
        }
        else if (args[0].equals("-dr"))
        {
            int nhId = -1;
            String relation = null;
            if (args[1] == null || args[2] == null)
            {
                System.out.println("Error in syntax");
            }
            try
            {
                nhId = Integer.parseInt(args[1]);
                relation = args[2];
            }
            catch (Exception e)
            {
                System.out.println("Not a valid nhid or relation");
            }
            try
            {
				BoardwalkNeighborhoodManager.deleteRelation(
									bwcon,
									nhId,
									relation
								   );
				System.out.println("Successfully deleted relation");

            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error deleting relation");
            }
        }

        else
        {
            System.out.println("Invalid arguments");
        }

    }

    private static void printNH(BoardwalkNeighborhoodNode bnn)
    {
		for (int i = 0; i<= bnn.getNeighborhood().getLevel(); i++)
			System.out.print("\t");

		System.out.println(bnn.getNeighborhood().getName() + " id=" + bnn.getNeighborhood().getId());

		Vector children = bnn.getChildren();
		Iterator ci = children.iterator();
		while (ci.hasNext())
		{
			BoardwalkNeighborhoodNode bnnc = (BoardwalkNeighborhoodNode)ci.next();
			printNH(bnnc);
		}
	}

};
