package servlets;

import javax.mail.*;

class SMTPAuthenticator extends Authenticator 
{
	String msUserName;
	String msPassword;

	public SMTPAuthenticator(String asUserName, String asPassword)
	{
		msUserName = asUserName;
		msPassword = asPassword;		
	}

	public PasswordAuthentication getPasswordAuthentication()
	{
		return new PasswordAuthentication(msUserName,msPassword);
	}

}