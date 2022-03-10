package servlets;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC packa
import javax.mail.*;
import javax.mail.internet.*;
import java.net.*;

import com.boardwalk.neighborhood.*;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;
import com.boardwalk.member.*;
import com.boardwalk.user.*;
import com.boardwalk.util.*;
import boardwalk.common.*;


public class forgotPasswordLogic extends xlServiceLogic
{

	public final static String Seperator = new Character((char)1).toString();
	public final static String ContentDelimeter = new Character((char)2).toString();
	StringTokenizer st;
	int tid;
	String msSmtpServer;
	String msSmtpPort;
	String msUserName;
    String msPassword;
    
    public forgotPasswordLogic(forgotPassword srv) {
        super(srv);
    }

    public void service (HttpServletRequest request,
	    					HttpServletResponse response)
	throws ServletException, IOException
    {

        BoardwalkMessages bwMsgs = new BoardwalkMessages();

		String buf = getRequestBuffer(request).toString();
		//System.out.println("Recieved Buffer = " + buf);
		st = new StringTokenizer( buf );
		msSmtpServer	= getServletConfig().getInitParameter("smptserver");
		msSmtpPort		= getServletConfig().getInitParameter("smtpport");
		msUserName		= getServletConfig().getInitParameter("username");
		msPassword		= getServletConfig().getInitParameter("password");

		//System.out.println(" msSmtpServer = " + msSmtpServer );
		//System.out.println(" msSmtpPort = " + msSmtpPort);
		//System.out.println(" msUserName = " + msUserName);
		//System.out.println(" msPassword = " + msPassword);

		StringTokenizer st2;
		String wrkstr;
		String username;
		String responseBuffer = null;
		StringBuffer responseToUpdate = new StringBuffer();

		wrkstr 		= st.nextToken(Seperator);
		username 	= wrkstr.replaceAll("\\n|\\r","");

		int m_user_id 	= -1;
		int tid 		= -1;
		Connection connection = null;
		TransactionManager tm = null;

		try
		{
			DatabaseLoader databaseloader 	= new DatabaseLoader(new Properties());
			connection 						= databaseloader.getConnection();
			
			tm 	= new TransactionManager( connection,1);
			tid = tm.startTransaction();
			
			PasswordGenerator pg 	= new PasswordGenerator();
			String newPassword 		= pg.randomstring(8);
			boolean success 		= UserManager.updatePassword(connection,username,newPassword);
			
			tm.commitTransaction();

			String mailBody = "Hi";
			mailBody = mailBody + "\n";
			mailBody = mailBody + "Your login information for Boardwalk is as follows" + "\n";
			mailBody = mailBody + "\n";
			mailBody = mailBody + "Login Name	: " + username + "\n";
			mailBody = mailBody + "Password 	: " + newPassword + "\n";
			mailBody = mailBody + "\n";
			mailBody = mailBody + "\n";
			mailBody = mailBody + "Please don't reply to this system generated email" + "\n";

			if(success)
			{
				System.out.println("Password updated in the database");
				
				try
				{
					Properties props 		= new Properties();
					//System.out.println("######### Using smtp host = " + msSmtpServer);
					
					SMTPAuthenticator auth 	= new SMTPAuthenticator(msUserName,msPassword);

					props.put("mail.smtp.starttls.enable", "true"); //Added by Lakshman on 20181008 to fix the Issue Id: 15383
					props.put("mail.smtp.host", msSmtpServer);
					props.put("mail.smtp.port", msSmtpPort);
					props.put("mail.smtp.auth", "true");
					//props.put("mail.smtp.ssl.enable", "true");
					//props.put("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory");  

					Session session 		= Session.getDefaultInstance(props, auth);
					//session.setDebug(true); //Commented by Lakshman on 20181008 to fix printing the password in the logs

					//Mailer.send(username, "admin@boardwalktech.com", "Boardwalk Password", mailBody);
					try
					{
						// create a message
						MimeMessage msg = new MimeMessage(session);
						msg.setFrom(new InternetAddress(msUserName));
						InternetAddress[] address = {new InternetAddress(username)};
						msg.setRecipients(Message.RecipientType.TO, address);

						msg.setSubject("Change password");
						msg.setSentDate(new java.util.Date());

						MimeBodyPart mbp1 = new MimeBodyPart();
						mbp1.setText(mailBody);
						Multipart mp = new MimeMultipart();
						mp.addBodyPart(mbp1);
						msg.setContent(mp);

						// send the message
						Transport.send(msg);
					}

					catch (MessagingException mex)
					{
						success = false;
						mex.printStackTrace();
						Exception ex = null;
						if ((ex = mex.getNextException()) != null)
						{
							ex.printStackTrace();
						}
					}
				}

				catch (Exception e)
				{
					success = false;
					e.printStackTrace();
				}
			}
			
			if(success)
			{
				System.out.println("Mail Sent after changing the password");
				
				responseToUpdate.append("Success"+ Seperator + Seperator );
				responseBuffer = responseToUpdate.toString();
			}
			else
			{
				BoardwalkException bwe = (BoardwalkException)new Exception();
				responseToUpdate.append("Failure");
				responseToUpdate.append( Seperator);
				responseToUpdate.append( bwe.getErrorCode());
				responseToUpdate.append( ContentDelimeter);
				responseToUpdate.append( bwe.getMessage());
				responseToUpdate.append( ContentDelimeter);
				responseToUpdate.append( bwe.getPotentialSolution());
				responseToUpdate.append( ContentDelimeter);
				responseToUpdate.append( Seperator);
				responseToUpdate.append( Seperator);
				responseBuffer = responseToUpdate.toString();
			}

			try
			{
				commitResponseBuffer(responseBuffer, response);
			}

			catch( java.io.IOException ioe)
			{
				System.out.println("Here 4 ");
				ioe.printStackTrace();
			}
		}

		catch ( Exception e )
		{
			try
			{
				tm.rollbackTransaction();
			}
		
			catch( SQLException sqlfatal )
			{
				System.out.println("Here 5 ");
				sqlfatal.printStackTrace();
			}

			if ( e.getClass().getName().equals("com.boardwalk.exception.BoardwalkException") )
			{
				BoardwalkException bwe = (BoardwalkException)e;
				System.out.println("Here 6 "+bwe);
				responseToUpdate.append("Failure");
				responseToUpdate.append( Seperator);
				responseToUpdate.append( bwe.getErrorCode());
				responseToUpdate.append( ContentDelimeter);
				responseToUpdate.append( bwe.getMessage());
				responseToUpdate.append( ContentDelimeter);
				responseToUpdate.append( bwe.getPotentialSolution());
				responseToUpdate.append( ContentDelimeter);
				responseToUpdate.append( Seperator);
				responseToUpdate.append( Seperator);
				responseBuffer = responseToUpdate.toString();
			
				try
				{
					commitResponseBuffer(responseBuffer, response);
				}
				
				catch( java.io.IOException ioe)
				{
					ioe.printStackTrace();
				}
				return;
			}
		}
		
		finally
		{
			try
			{
				connection.close();
			}
			
			catch ( SQLException sql )
			{
				System.out.println("Here 7 ");
				sql.printStackTrace();
			}
		}
	}

}