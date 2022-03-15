package servlets;
/*
 *  This presents a list of collaboration available to a user
 */
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class httpt_vb_ViewDifferences extends HttpServlet   implements
 SingleThreadModel {

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();


    public void service (HttpServletRequest request,
	    HttpServletResponse response)throws ServletException, IOException
    {
	System.out.println( "came to httpt_vb_ViewDifferences");

//        authenticate(request, response);

        // get the user id for the session



	response.setContentType ( "text/plain");

	ServletOutputStream servletOut = response.getOutputStream ();

	System.out.println( "Got output stream");
	BufferedReader br = request.getReader ();
/*

	 char[] buf = new char[10000];

	 br.read(buf, 0, 1000);

	 for( int i = 0; i < 50; i++ )
	 {

		 System.out.println(new Character(buf[i]).toString() + "::" + (long)buf[i]);
	 }

*/


        System.out.println( "Got input stream");


        StringBuffer sb = new 	StringBuffer ();

        String  line = new String();
        line = br.readLine ();
	    while( line != null )
	    {
			System.out.println("appending line:::::: " + line);
			sb.append(line);
			line = br.readLine ();
			if ( line != null )
			{
				sb.append("\n");
			}
		}
		String fullTable = sb.toString();

        System.out.println("Updating Table with data                      ");
 //       System.out.println(fullTable);


		/*
        httpt_vb_createcells cc = new httpt_vb_createcells ( fullTable );

        if ( cc.loginUser() )
        {
            String responseToUpdate;
            System.out.println("user is valid");
            boolean result = cc.Parse ();
            if (result)
            {
                responseToUpdate = "Success";
                responseToUpdate = responseToUpdate + Seperator + "XID" + ContentDelimeter;
                responseToUpdate = responseToUpdate + cc.getTransactionIdForNewCommit() + ContentDelimeter;

                Hashtable newColumns = cc.getNewColumns();
                Hashtable newRows = cc.getNewRows();

                if ( newColumns.size() > 0 || newRows.size() > 0 )
                {

					responseToUpdate = responseToUpdate;


					Enumeration rowkeys = newRows.keys();

					if ( newRows.size() > 0 )
					{
						responseToUpdate = responseToUpdate + Seperator + "ROW" + ContentDelimeter;
						while( rowkeys.hasMoreElements() )
						{
							String rowAddress = (String)rowkeys.nextElement();

							String rowid = (String)newRows.get( rowAddress );

							responseToUpdate = responseToUpdate + rowid + ContentDelimeter;
							responseToUpdate = responseToUpdate + rowAddress + ContentDelimeter;

						}
					}
					Enumeration columnkeys = newColumns.keys();
					if ( newColumns.size() > 0 )
					{
						responseToUpdate = responseToUpdate + Seperator  +  "COLUMN" + ContentDelimeter;
						while( columnkeys.hasMoreElements() )
						{
							String colAddress = (String)columnkeys.nextElement();
							String colid = (String) newColumns.get( colAddress );

							responseToUpdate = responseToUpdate + colid + ContentDelimeter;
							responseToUpdate = responseToUpdate + colAddress + ContentDelimeter;

						}
					}


				}




				System.out.println("UpdateBoardwalkResponse: " + responseToUpdate );
                response.setContentLength ( responseToUpdate.length() );
                servletOut.print(responseToUpdate);
            }
            else
            {
                responseToUpdate = "Failure";
                response.setContentLength ( responseToUpdate.length() );
                servletOut.print(responseToUpdate);
            }

        }
        else
        {
            System.out.println("user is invalid");
            String invalid = new String("userinvalid");
            response.setContentLength (invalid.length());
            servletOut.print(invalid);
        }
        */

        servletOut.close ();

    }

}
