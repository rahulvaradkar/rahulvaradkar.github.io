import java.sql.*;
import java.util.*;
import boardwalk.connection.*;
import boardwalk.neighborhood.*;
import com.boardwalk.exception.BoardwalkException;

public class UserTest
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
                System.out.println("Please enter valid user name");
            }

            String emailAddress = args[1];

            try
            {
                // Create a new user
                int userId = BoardwalkUserManager.createUser(
								  bwcon,
								  emailAddress,
								  emailAddress,
								  emailAddress,
								  emailAddress
								);
                System.out.println("Successfully created user with id = " + userId);

            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error creating the user, maybe user with same name exists");
            }
        }
        if (args[0].equals("-ga"))
        {
            try
            {
                // Get the list of users
                Vector userList = BoardwalkUserManager.getUserList(bwcon);
                Iterator ui = userList.iterator();
                while (ui.hasNext())
                {
					BoardwalkUser bu = (BoardwalkUser)ui.next();
					System.out.println(bu.getId() + ":" + bu.getUserName() + ":" + bu.getFirstName() + ":" + bu.getLastName());
				}
            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error fetching users from database");
            }
        }
        else if (args[0].equals("-cp"))
        {
            if (args[1] == null || args[2] == null || args[3] == null)
            {
                System.out.println("Please enter valid user name");
            }
            int userId = -1;
            try
            {
				userId = Integer.parseInt(args[1]);
			}
			catch(Exception e)
			{
				// no parent
				System.out.println("invalid userid");
			}
            String pswd = args[2];
            String oldpswd = args[3];

            try
            {
                BoardwalkUserManager.updatePassword(
										   bwcon,
                                           userId,
                                           oldpswd,
                                           pswd
                                         );
                System.out.println("Successfully updated password");

            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error creating the user, maybe user with same name exists");
            }
        }
        else if (args[0].equals("-ml"))
        {
            if (args[1] == null )
            {
                System.out.println("Please enter valid userid");
            }
            int userId = -1;
            try
            {
				userId = Integer.parseInt(args[1]);
			}
			catch(Exception e)
			{
				// no parent
				System.out.println("invalid userid");
			}

            try
            {
                Vector ml = BoardwalkUserManager.getMembershipList(
										bwcon,
                                        userId
                                         );
				Iterator mli = ml.iterator();
				while (mli.hasNext())
				{
					BoardwalkMember m = (BoardwalkMember)mli.next();
					System.out.println ("id = " + m.getId() + " userid = " + m.getUserId() +
						" nhid = " + m.getNeighborhoodId() + " nhname = " + m.getNeighborhoodName());
				}
            }
            catch (BoardwalkException bwe)
            {
                System.out.println("Error fetching membership, maybe userid is incorrect");
            }
        }
        else
        {
            System.out.println("Invalid arguments");
        }

    }

};
