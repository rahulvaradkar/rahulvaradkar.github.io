/*
 * BoardwalkMessages.java
 *
 * Created on May 1, 2002, 8:24 AM
 */

package com.boardwalk.exception;

/**
 *
 * @author  administrator
 * @version
 */
import java.util.*;
public class BoardwalkMessages {

    /** Creates new BoardwalkMessages */
    static boolean init = false;
    Hashtable errorMessages = new Hashtable();


    private void initMessages()
    {
        if ( init == false )
        {
            errorMessages.put( new Integer( 10000 ), new BoardwalkMessage( 10000, "COLLABORATION EXCEPTION", 5 ,"A Collaboration with this name already exists within this Neighborhood",  "The Collaboration Name has to be unique within a Neighborhood, Please choose a different name"));
            errorMessages.put( new Integer( 10002 ), new BoardwalkMessage( 10002, "BOARDWALK EXCEPTION", 5 ,"No Entity Selected",  "You have selected an action that requires an entity to be selected. Please select an item first"));
			errorMessages.put( new Integer( 10003 ), new BoardwalkMessage( 10003, "WHITEBOARD EXCEPTION", 5, "Selected Whiteboard could not be purged", "The whiteboard may be on a baseline or you might not have permissions to delete it"));
            errorMessages.put( new Integer( 10004 ), new BoardwalkMessage( 10004, "NEIGHBORHOOD EXCEPTION", 5 ,"Selected Neighborhood could not be purged",  "The neighborhood may contain collaborations that need to be purged first"));
            errorMessages.put( new Integer( 10005 ), new BoardwalkMessage( 10005, "ACCESS EXCEPTION", 5 ,"You dont have the priviliges to execute this action ",  "Please contact the owner of the table to setup necessary access control"));
            errorMessages.put( new Integer( 10006 ), new BoardwalkMessage( 10006, "NEIGHBORHOOD EXCEPTION", 5 ,"Please join a Neighborhood before creating  a new  Collaboration",  "Click on the Neighborhoods menubar and join a neighborhood"));
            errorMessages.put( new Integer( 10007 ), new BoardwalkMessage( 10007, "NEIGHBORHOOD EXCEPTION", 5 ,"Please join a Neighborhood before creating  a new  Table",  "Click on the Neighborhoods menubar and join a neighborhood"));
			errorMessages.put( new Integer( 10009 ), new BoardwalkMessage( 10009, "ACCESS EXCEPTION", 5 ,"The password you have entered is incorrect",  "Please try again"));
			errorMessages.put( new Integer( 10010 ), new BoardwalkMessage( 10010, "BOARDWALK EXCEPTION", 5, "No such Collaboration and/or Whiteboard exist", "Please check the collaboration name and whiteboard name"));

			//Added for REST APIS
            errorMessages.put( new Integer( 10011 ), new BoardwalkMessage( 10011, "WHITEBOARD EXCEPTION", 5 ,"A Whiteboard with this name already exists within this Collaboration",  "The Whiteboard Name has to be unique within a Collaboration, Please choose a different name"));
            errorMessages.put( new Integer( 10012 ), new BoardwalkMessage( 10012, "TABLE CREATE EXCEPTION", 5 ,"A Table with this name already exists within Collaboration/s",  "The Table Name has to be unique across all Collaborations, Please choose a different name"));
            errorMessages.put( new Integer( 10013 ), new BoardwalkMessage( 10013, "COLLABORATION RENAME EXCEPTION", 5 ,"A Collaboration with this name already exists within Neighborhood",  "The Collaboration Name has to be unique within the Neighborhood, Please choose a different name"));
            errorMessages.put( new Integer( 10014 ), new BoardwalkMessage( 10014, "WHITEBOARD RENAME EXCEPTION", 5 ,"A Whiteboard with this name already exists within Collaboration",  "The Whiteboard Name has to be unique within the Collaboration, Please choose a different name"));
            errorMessages.put( new Integer( 10015 ), new BoardwalkMessage( 10015, "TABLE RENAME EXCEPTION", 5 ,"A Table with this name already exists within Whiteboard",  "The TableName has to be unique within the Whiteboard, Please choose a different name"));
            errorMessages.put( new Integer( 10016 ), new BoardwalkMessage( 10016, "NEIGHBORHOOD CREATE EXCEPTION", 5 ,"A Neighborhood with this name already exists within it's Parent Neighborhood",  "The Neighborhood Name has to be unique within it's Parent Neighborhood, Please choose a different name"));
            errorMessages.put( new Integer( 10017 ), new BoardwalkMessage( 10017, "CREATE MEMBER EXCEPTION", 5 ,"This User is already a member of the Neighborhood",  "User membership already exists for the Neighborhood"));
            errorMessages.put( new Integer( 10018 ), new BoardwalkMessage( 10018, "COLLABORATION EXCEPTION", 5 ,"Collaboration does not exist for this Collaboration Id",  "Use existing Collaboration Id"));
            errorMessages.put( new Integer( 10019 ), new BoardwalkMessage( 10019, "COLLABORATION EXCEPTION", 5 ,"Collaboration does not exist for this Neighborhood",  "Use Neighborhood Id of existing Collaboration"));
            errorMessages.put( new Integer( 10020 ), new BoardwalkMessage( 10020, "NEIGHBORHOOD RELATION EXCEPTION", 5 ,"Neighborhood Relation Exists",  "Neighborhood Relation Name has to be Unique within it's Parent Neighborhood, Please choose a different Relation Name"));
			//Added for REST APIS

			errorMessages.put( new Integer( 10040 ), new BoardwalkMessage( 10040, "DELETE MEMBER EXCEPTION", 5 ,"Dependency Exists",  "Membership cannot be deleted as there are dependencies on this membership")); //Added by Lakshman on 20181011 to fix the issue Id: 14324
			
			/* TABLE UPDATE ERRORS */
			errorMessages.put( new Integer( 12001 ), new BoardwalkMessage( 12001, "TABLE UPDATE EXCEPTION", 5 ,"Columns are not unqiue",  "Please make sure the columns are unique"));
			errorMessages.put( new Integer( 12002 ), new BoardwalkMessage( 12002, "TABLE UPDATE EXCEPTION", 5 ,"The values entered are not valid",  "Please make sure the values are correct wrt to the lookup column values"));
			errorMessages.put( new Integer( 12003 ), new BoardwalkMessage( 12003, "TABLE UPDATE EXCEPTION", 5 ,"The values entered are not valid",  "Please make sure the values are correct wrt to the lookup column values"));
			errorMessages.put( new Integer( 12004 ), new BoardwalkMessage( 12004, "TABLE UPDATE EXCEPTION", 5 ,"The data type is incorrect",  "Please make changes and try again"));
			errorMessages.put( new Integer( 12005 ), new BoardwalkMessage( 12005, "TABLE UPDATE EXCEPTION", 5 ,"The data type is incorrect",  "Please make changes and try again"));
			errorMessages.put( new Integer( 12006 ), new BoardwalkMessage( 12006, "TABLE UPDATE EXCEPTION", 5 ,"The table is locked",  "Please request the user to unlock the table"));
			errorMessages.put( new Integer( 12007 ), new BoardwalkMessage( 12007, "TABLE UPDATE EXCEPTION", 5 ,"The table name is empty",  "Please request the enter a table name"));
			errorMessages.put( new Integer( 12008 ), new BoardwalkMessage( 12008, "TABLE UPDATE EXCEPTION", 5 ,"The table is being updated by another user",  "Please try later"));
			errorMessages.put( new Integer( 12009 ), new BoardwalkMessage( 12009, "TABLE UPDATE EXCEPTION", 5 ,"It seems like you have sorted the table in Excel",  "Please check and try again"));
			errorMessages.put( new Integer( 12010 ), new BoardwalkMessage( 12010, "TABLE UPDATE EXCEPTION", 5 ,"You don't access to add a new column",  "Please contact the owner of the Table"));
			errorMessages.put( new Integer( 12011 ), new BoardwalkMessage( 12011, "TABLE UPDATE EXCEPTION", 5 ,"There are many errors",  "Please resolve errors and try again"));
			errorMessages.put( new Integer( 12012 ), new BoardwalkMessage( 12012, "TABLE UPDATE EXCEPTION", 5 ,"You don't have access to add a new row",  "Please resolve errors and try again"));
			errorMessages.put( new Integer( 12013 ), new BoardwalkMessage( 12013, "TABLE UPDATE EXCEPTION", 5 ,"You don't have access to delete a row",  "Use Import All to recover the row into your sheet"));
			errorMessages.put( new Integer( 12014 ), new BoardwalkMessage( 12014, "TABLE UPDATE EXCEPTION", 5 ,"You have made changes in a restricted column",  "Use Import to recover the original values"));
			errorMessages.put( new Integer( 12015 ), new BoardwalkMessage( 12015, "TABLE UPDATE EXCEPTION", 5 ,"User specfied for row assignments does not exist in the system",  "Check the username and try again"));
			errorMessages.put( new Integer( 12016 ), new BoardwalkMessage( 12016, "TABLE UPDATE EXCEPTION", 5 ,"You are trying to update a cell for which you do not have access",""));
			errorMessages.put( new Integer( 12017 ), new BoardwalkMessage( 12017, "TABLE UPDATE EXCEPTION", 5 ,"There has been Critical update(s) to this table after your last import, please refresh first!",""));

			/* LOGIN ERRORS */
			errorMessages.put( new Integer( 11001 ), new BoardwalkMessage( 11001, "LOGIN EXCEPTION", 5 ,"You are not a member of any Neighborhood",  "Please join your neighborhood"));
			errorMessages.put( new Integer( 11002 ), new BoardwalkMessage( 11002, "LOGIN EXCEPTION", 5 ,"You are a member of multiple neighborhoods",  "Only membership of one neighborhood is supported, please remove other memberships"));
			errorMessages.put( new Integer( 11003 ), new BoardwalkMessage( 11003, "LOGIN EXCEPTION", 5 ,"That user name already exists",  "Please submit a different one"));
			errorMessages.put( new Integer( 11004 ), new BoardwalkMessage( 11004, "LOGIN EXCEPTION", 5 ,"Username or Password is invalid",  "Please enter the correct username and password. If you are a new user please register for access to Boardwalk"));
			errorMessages.put( new Integer( 11005 ), new BoardwalkMessage( 11005, "LOGIN EXCEPTION", 5, "Membership is not valid", "This might be because you are working with local data fetched by another user or using another membership"));
			errorMessages.put( new Integer( 11007 ), new BoardwalkMessage( 11007, "LOGIN EXCEPTION", 5, "Membership or Neighborhood Hierarchy is not valid", "This might be because the membership or Neighborhood Hierarchy is not valid"));

			errorMessages.put( new Integer( 11011 ), new BoardwalkMessage( 11011, "LOGIN EXCEPTION", 5 ,"Incorrect User Id or Password",  "User Id or Password is incorrect. Please try again.")); //Modified by Lakshman on 20180227 to fix the Issue Id: 14242
			errorMessages.put( new Integer( 11012 ), new BoardwalkMessage( 11012, "LOGIN EXCEPTION", 5 ,"User is Unavailable",  "You are not a valid user. Please contact your Boardwalk Administrator."));
			errorMessages.put( new Integer( 11013 ), new BoardwalkMessage( 11013, "LOGIN EXCEPTION", 5 ,"User is Inactive",  "Your account is not active. Please contact your Boardwalk Administrator."));
			errorMessages.put( new Integer( 11014 ), new BoardwalkMessage( 11014, "LOGIN EXCEPTION", 5 ,"Password Expired",  "Your password is expired. Please change your password to login."));
			errorMessages.put( new Integer( 11015 ), new BoardwalkMessage( 11015, "LOGIN EXCEPTION", 5 ,"Account Locked Permanently",  "Your account is locked. Please contact your Boardwalk Administrator."));
			errorMessages.put( new Integer( 11016 ), new BoardwalkMessage( 11016, "LOGIN EXCEPTION", 5 ,"Account Locked Temporarily",  "Your account is locked. Please try after sometime."));
			errorMessages.put( new Integer( 11017 ), new BoardwalkMessage( 11017, "LOGIN EXCEPTION", 5 ,"Password Should be Changed",  "You are not allowed to use this password. Please change your password to login."));
			
			errorMessages.put( new Integer( 11021 ), new BoardwalkMessage( 11021, "PASSWORD EXCEPTION", 5 ,"Password Update Failed",  "The password is not updated as it is matching with one of your previous passwords. Please try again with a new password.")); //Added by Lakshman on 20180323 to fix the Issue Id: 14248

			/* SYSTEM ERRORS */
			errorMessages.put( new Integer( 13001 ), new BoardwalkMessage( 13001, "SYSTEM EXCEPTION", 5 ,"The server is busy",  "Please try later"));
			errorMessages.put( new Integer( 13002 ), new BoardwalkMessage( 13002, "SYSTEM EXCEPTION", 5 ,"The database is down",  "Please contact the system administrator"));
			errorMessages.put( new Integer( 13003 ), new BoardwalkMessage( 13003, "SYSTEM EXCEPTION", 5, "The Excel Add-In version that you are using needs to be updated", "Please update the Boardwalk Add-In."));
			errorMessages.put( new Integer( 13004 ), new BoardwalkMessage( 13004, "SYSTEM EXCEPTION", 5, "The Boardwalk Template version that you are using needs to be updated", "Please update the template."));
			errorMessages.put( new Integer( 13005 ), new BoardwalkMessage( 13005, "SYSTEM EXCEPTION", 5, "The Excel client version that you are using is out of date.", "Please download the the latest template."));
			errorMessages.put( new Integer( 13006 ), new BoardwalkMessage( 13006, "SYSTEM EXCEPTION", 5, "The Boardwalk Template version that you are using is out of date.", "Please download the template again."));

			/* FORM ERRORS */
			errorMessages.put( new Integer( 14001 ), new BoardwalkMessage( 14001, "FORM EXCEPTION", 5 ,"The Form Definition Template is ill defined",  "Please make sure that the syntax is correct."));

			/* API EXCEPTIONS */
			errorMessages.put( new Integer( 15001 ), new BoardwalkMessage( 15001, "BOARDWALK SHEET EXCEPTION", 5 ,"The sheet name is already used.",  ""));
			errorMessages.put( new Integer( 15002 ), new BoardwalkMessage( 15002, "BOARDWALK SHEET EXCEPTION", 5 ,"Sheet does not exist.",  ""));
			errorMessages.put( new Integer( 15003 ), new BoardwalkMessage( 15003, "BOARDWALK SHEET EXCEPTION", 5, "You are using an old sheet.", "Please download the data again."));
			
			errorMessages.put( new Integer( 15100 ), new BoardwalkMessage( 15100, "BOARDWALK AREA EXCEPTION", 5 ,"Area overlaps with another area",  ""));

			errorMessages.put( new Integer( 15200 ), new BoardwalkMessage( 15200, "BOARDWALK ROW/COLUMN GROUP EXCEPTION", 5 ,"Start Index cannot be greater that end index",  ""));

			errorMessages.put( new Integer( 15300 ), new BoardwalkMessage( 15300, "BOARDWALK TABLE DISPLAY EXCEPTION", 5 ,"Row or column indices are not contiguous",  ""));
			errorMessages.put( new Integer( 15310 ), new BoardwalkMessage( 15310, "BOARDWALK TABLE DISPLAY EXCEPTION", 5 ,"Some columns are not represented in the table display",  ""));
			errorMessages.put( new Integer( 15320 ), new BoardwalkMessage( 15320, "BOARDWALK TABLE DISPLAY EXCEPTION", 5 ,"Some rows are not represented in the table display",  ""));
			errorMessages.put( new Integer( 20001 ), new BoardwalkMessage( 20001, "LOGIN EXCEPTION", 5 ,"Application Offline",  ""));
        }
    }

    public BoardwalkMessages()
    {
        if ( init == false )
            initMessages();
    }

    public BoardwalkMessage getBoardwalkMessage( int a_errorCode )
    {
        return (BoardwalkMessage)errorMessages.get(new Integer( a_errorCode));
    }
}
