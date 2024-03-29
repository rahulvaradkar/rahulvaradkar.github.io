package com.boardwalk.wizard;
import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.net.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.*;
import com.boardwalk.collaboration.CollaborationManager;
import com.boardwalk.whiteboard.WhiteboardManager;
import com.boardwalk.table.*;
import com.boardwalk.database.*;
import com.boardwalk.user.*;
import com.boardwalk.member.*;
import com.boardwalk.query.*;
import com.boardwalk.neighborhood.*;
import com.boardwalk.util.*;


import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package

public class BoardwalkUserWizards
{
	String smtpfrom;
	String smtppsswd;

	

	public String addUsersInBulk(Connection connection, int tableId, int userId, int memberId, int userNhId, int tid, String msSmtpServer, String msSmtpPort,String msUserName, String msPassword  ) throws Exception
	{
		smtpfrom = msUserName;
		smtppsswd = msPassword;


		Hashtable rowIdToUserRequest = new Hashtable();
		Vector CellIds = new Vector();
		TableContents tbc = null;
		Hashtable columns = new Hashtable();
		Vector columnNames = new Vector();
		Hashtable cellsByRowId = new Hashtable();
		Vector columnsSortedBySeqNum = new Vector();
		Vector rowids = new Vector();
		Hashtable rowObjsById = null;
		Hashtable NhURLToNhId = new Hashtable();
		BoardwalkUserRequest bwUserRequest = null;
		Integer a_rowIntegerId = null;

		System.out.println(" table Id = " + tableId);
		tbc = TableManager.getTableContents(connection,
														  tableId,
														  userId,
														  memberId,
														  userNhId,
														  -1,
														  ViewPreferenceType.LATEST,
														  QueryPreferenceType.ROWS_BY_ROW_SEQ_ID,
														  new Vector(),
														  false,
															-1,
															10000000,
															-1,
															10000000);

		columns = tbc.getColumnsByColumnId();
		columnNames = tbc.getColumnNames();
		columnsSortedBySeqNum = tbc.getColumnsSortedBySeqNum();
		cellsByRowId = tbc.getCellsByRowId();
		rowids = tbc.getRowIds();
		rowObjsById = tbc.getRowObjsByRowId();

		// check column names if wrong send error message


		Hashtable userTableStructure = new Hashtable();
		userTableStructure.put("Nh0", "Nh0");
		userTableStructure.put("Nh1", "Nh1");
		userTableStructure.put("Nh2", "Nh2");
		userTableStructure.put("Nh3", "Nh3");
		userTableStructure.put("Nhsecure", "Nhsecure");
		userTableStructure.put("FirstName", "FirstName");
		userTableStructure.put("LastName", "LastName");
		userTableStructure.put("ExternalId", "ExternalId");
		userTableStructure.put("Email", "Email");
		userTableStructure.put("Password", "Password");
		userTableStructure.put("Action", "Action");
		userTableStructure.put("Comment", "Comment");



		if (columnNames.size() > 0)
		{
			for (int cindex = 0; cindex < columnNames.size(); cindex++)
			{
				String col = (String)columnNames.elementAt(cindex);

				if (userTableStructure.get(col.trim()) != null)
				{
					userTableStructure.remove(col.trim());
				}
			}

			if (userTableStructure.size() > 0)
			{
				// some columns are missing
				String result = " Some columns are missing Please add the following columns in the table ";
				Enumeration cNames = userTableStructure.keys();
				while (cNames.hasMoreElements())
				{
					result = result + " " + cNames.nextElement();
				}
				return result;
			}


		}
		else
		{
			return " No columns in the table Please add the following columns and populate the table Nh0,Nh1,Nh2Nh3,Nhsecure,FirstName,LastName,ExternalId,Email,Password,Action,Comment ";
		}





		if (rowids.size() > 0)
		{

			for (int rowIndex = 0; rowIndex < rowids.size(); rowIndex++)
			{
				a_rowIntegerId = (Integer)rowids.elementAt(rowIndex);
				Vector cells = (Vector)((Vector)cellsByRowId.get(a_rowIntegerId)).elementAt(0);
				int rowId = ((Integer)rowids.elementAt(rowIndex)).intValue();

				bwUserRequest = new BoardwalkUserRequest();
				rowIdToUserRequest.put(a_rowIntegerId, bwUserRequest);

				for (int cellIndex = 0; cellIndex < cells.size(); cellIndex++)
				{
					VersionedCell cell = (VersionedCell)cells.elementAt(cellIndex);
					//cell.printCell();

					if (cell.getColumnName().equalsIgnoreCase("Nh0"))
					{
						bwUserRequest.nh0 = cell.getValueAsString().trim();
					}


					if (cell.getColumnName().equalsIgnoreCase("Nh1"))
					{
						bwUserRequest.nh1 = cell.getValueAsString().trim();

					}

					if (cell.getColumnName().equalsIgnoreCase("Nh2"))
					{
						bwUserRequest.nh2 = cell.getValueAsString().trim();

					}


					if (cell.getColumnName().equalsIgnoreCase("Nh3"))
					{
						bwUserRequest.nh3 = cell.getValueAsString().trim();
					}

					if (cell.getColumnName().equalsIgnoreCase("NhSecure"))
					{
						String secure = cell.getValueAsString().trim();
						if (secure.equalsIgnoreCase(""))
							bwUserRequest.nhSecure = true;

						if (secure.equalsIgnoreCase("true"))
							bwUserRequest.nhSecure = true;

						if (secure.equalsIgnoreCase("false"))
							bwUserRequest.nhSecure = false;
					}









					if (cell.getColumnName().equalsIgnoreCase("FirstName"))
					{
						bwUserRequest.fName = cell.getValueAsString().trim();
					}

					if (cell.getColumnName().equalsIgnoreCase("LastName"))
					{
						bwUserRequest.lName = cell.getValueAsString().trim();
					}

					if (cell.getColumnName().equalsIgnoreCase("ExternalId"))
					{
						bwUserRequest.extId = cell.getValueAsString().trim();
					}

					if (cell.getColumnName().equalsIgnoreCase("Email"))
					{
						bwUserRequest.Email = cell.getValueAsString().trim();
						if (bwUserRequest.Email.trim().indexOf("<a") == 0 || bwUserRequest.Email.trim().indexOf("<A") == 0)
						{
							// Email is a http link we need to parse it
							int indexendofATag = bwUserRequest.Email.trim().indexOf(">");
							int indexstartofSlashATag = bwUserRequest.Email.trim().indexOf("<", indexendofATag);
							String email = bwUserRequest.Email.trim().substring(indexendofATag + 1, indexstartofSlashATag).trim();
							bwUserRequest.Email = email;
						}

					}

					if (cell.getColumnName().equalsIgnoreCase("Password"))
					{
						bwUserRequest.password = cell.getValueAsString().trim();
						bwUserRequest.passwordCellId = cell.getId();
					}

					if (cell.getColumnName().equalsIgnoreCase("Action"))
					{
						bwUserRequest.action = cell.getValueAsString().trim();
						bwUserRequest.actionCellId = cell.getId();
					}

					if (cell.getColumnName().equalsIgnoreCase("Result"))
					{
						bwUserRequest.resultCellId = cell.getId();
					}

					if (cell.getColumnName().equalsIgnoreCase("Last Action"))
					{
						bwUserRequest.lastaction = cell.getValueAsString().trim();
						bwUserRequest.lastactionCellId = cell.getId();
					}

					if (cell.getColumnName().equalsIgnoreCase("Comment"))
					{
						bwUserRequest.comment = cell.getValueAsString().trim();
						bwUserRequest.commentCellId = cell.getId();
					}




				} // for all cells in a row


				bwUserRequest.print();

				if (!bwUserRequest.action.trim().equalsIgnoreCase("NO CHANGE"))
				{
					// determine nh level
					if (!bwUserRequest.nh0.trim().equals(""))
					{
						bwUserRequest.nhLevel = 0;
						if (!bwUserRequest.nh1.trim().equals(""))
						{
							bwUserRequest.nhLevel = 1;
							if (!bwUserRequest.nh2.trim().equals(""))
							{
								bwUserRequest.nhLevel = 2;
								if (!bwUserRequest.nh3.trim().equals(""))
								{
									bwUserRequest.nhLevel = 3;
								}
							}
						}
					}




					if (bwUserRequest.nhLevel > -1)
					{
						if (bwUserRequest.nhLevel == 0)
						{
							if (!bwUserRequest.nh1.trim().equals("") || !bwUserRequest.nh2.trim().equals("") || !bwUserRequest.nh3.trim().equals(""))
							{
								bwUserRequest.action = "FIX DATA";
								bwUserRequest.result = "FAILURE";
								bwUserRequest.comment = "One of the Neighborhoods following Nh0 is not empty. Please add a missing neighborhood or remove the leaf neighbrohood from the cell";
							}
						}


						if (bwUserRequest.nhLevel == 1)
						{
							if (!bwUserRequest.nh2.trim().equals("") || !bwUserRequest.nh3.trim().equals(""))
							{
								bwUserRequest.action = "FIX DATA";
								bwUserRequest.result = "FAILURE";
								bwUserRequest.comment = "One of the Neighborhoods following Nh1 is not empty. Please add a missing neighborhood or remove the leaf neighbrohood from the cell";
							}
						}
					}


					// check if user is valid ?
					if (!bwUserRequest.Email.trim().equals(""))
					{
						if (bwUserRequest.password.trim().equals(""))
						{
							bwUserRequest.action = "FIX DATA";
							bwUserRequest.result = "FAILURE";
							bwUserRequest.comment = bwUserRequest.comment + " \n " + " User password is missing ";
						}
					}

				}



			} // for a specific row

			// Now execute the request .....

			Vector nhTree = NeighborhoodManager.getNeighborhoodTree(connection, userId, true);


			for (int rowIndex = 0; rowIndex < rowids.size(); rowIndex++)
			{
				a_rowIntegerId = (Integer)rowids.elementAt(rowIndex);
				bwUserRequest = (BoardwalkUserRequest)rowIdToUserRequest.get(a_rowIntegerId);
				
				if (bwUserRequest.action.trim().equalsIgnoreCase("CHANGE PASSWORD"))
				{
					System.out.println("CHANGE PASSWORD");
					bwUserRequest.print();

					try
					{
						UserManager.updatePassword( connection,bwUserRequest.Email, bwUserRequest.password );
					}
					catch (Exception e)
					{
						e.printStackTrace();
						bwUserRequest.result = "FAILURE";
						bwUserRequest.comment = bwUserRequest.comment + "\n" + e.getMessage();
					}
					
				}

				if (bwUserRequest.action.trim().equalsIgnoreCase("DEACTIVATE USER"))
				{
					try
					{	
						System.out.println("DEACTIVATE USER");
						bwUserRequest.print();
						User u = UserManager.getUser(connection,  bwUserRequest.Email);
						if (u != null && u.getId() > -1 )
						{
							System.out.println("DEACTIVATE USER" + u.getId() );
							MemberManager.deactivateUser( connection, u.getId() );
						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						bwUserRequest.result = "FAILURE";
						bwUserRequest.comment = bwUserRequest.comment + "\n" + e.getMessage();
					}
					
				}

				if (bwUserRequest.action.trim().equalsIgnoreCase("DELETE MEMBERSHIP"))
				{
					Member m = null;
					try
					{
							System.out.println("DELETE MEMBERSHIP");
							bwUserRequest.print();
					
							User u = UserManager.getUser(connection,  bwUserRequest.Email);
							Vector mList = UserManager.getMembershipListForUser(connection,u.getId() );
							NeighborhoodLevelId nlevel = NeighborhoodManager.getNeighborhoodLevelId(connection,bwUserRequest.nh0,bwUserRequest.nh1,bwUserRequest.nh2,bwUserRequest.nh3, bwUserRequest.nhLevel );
							
							if (mList.size() != 1)
							{
								Iterator mi = mList.iterator();

								while (mi.hasNext())
								{
									m = (Member)mi.next();
									if (m.getNeighborhoodId() ==  nlevel.getId())
									{
										MemberManager.deleteMember (connection, m.getId());
										break;
									}
								}
							}
							else
							{
								m = (Member)mList.firstElement();
								if (m.getNeighborhoodId() == nlevel.getId())
								{
									MemberManager.deleteMember (connection, m.getId());
								}
								
								
							}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						bwUserRequest.result = "FAILURE";
						bwUserRequest.comment = bwUserRequest.comment + "\n" + e.getMessage();
					}
				}




				if (bwUserRequest.action.trim().equalsIgnoreCase("RESET PASSWORD"))
				{
					PasswordGenerator pg = new PasswordGenerator();
					String newPassword = pg.randomstring(8);
					boolean success = UserManager.updatePassword( connection,bwUserRequest.Email,newPassword);
					
								
					String mailBody = "Hi";
					mailBody = mailBody + "\n";
					mailBody = mailBody + "Your login information for Boardwalk is as follows" + "\n";
					mailBody = mailBody + "\n";
					mailBody = mailBody + "Login Name:" + bwUserRequest.Email + "\n";
					mailBody = mailBody + "Password:" + newPassword + "\n";
					mailBody = mailBody + "\n";
					mailBody = mailBody + "\n";
					mailBody = mailBody + "Please don't reply to this system generated email" + "\n";

					if(success)
					{
						try
						{
							Properties props = new Properties();
							System.out.println("######### Using smtp host = " + msSmtpServer);
							SMTPAuthenticatorUserBulk auth = new SMTPAuthenticatorUserBulk ();
							props.put("mail.smtp.host", msSmtpServer);
							props.put("mail.smtp.port", msSmtpPort);
							props.put("mail.smtp.auth", "true");
							//props.put("mail.smtp.auth", "true");
							Session session = Session.getDefaultInstance(props, auth);
							session.setDebug(true);
							//Mailer.send(username, "admin@boardwalktech.com", "Boardwalk Password", mailBody);
							try
							{
								// create a message
								MimeMessage msg = new MimeMessage(session);
								msg.setFrom(new InternetAddress(msUserName));
								InternetAddress[] address = {new InternetAddress(bwUserRequest.Email)};
								msg.setRecipients(Message.RecipientType.TO, address);

								msg.setSubject("New Password for the Boardwalk application");
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
							e.printStackTrace();
							bwUserRequest.result = "FAILURE";
							bwUserRequest.comment = bwUserRequest.comment + "\n" + e.getMessage();
				
						}
					}
					
				}

				
				if (bwUserRequest.action.trim().equalsIgnoreCase("ADD"))
				{



					try
					{
						String nhUrl = "";

						if (bwUserRequest.nhLevel == 0)
						{

							nhUrl = bwUserRequest.nh0.trim();
						}

						if (bwUserRequest.nhLevel == 1)
						{

							nhUrl = bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim();
						}

						if (bwUserRequest.nhLevel == 2)
						{

							nhUrl = bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + "_" + bwUserRequest.nh2.trim();
						}

						if (bwUserRequest.nhLevel == 3)
						{

							nhUrl = bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + "_" + bwUserRequest.nh2.trim() + "_" + bwUserRequest.nh3.trim();

						}






						System.out.println(" nhurl = " + nhUrl + " email = " + bwUserRequest.Email + " level =  " + bwUserRequest.nhLevel);
						NeighborhoodLevel_0 nhl0 = null;
						NeighborhoodLevel_1 nhl1 = null;
						NeighborhoodLevel_2 nhl2 = null;
						NeighborhoodLevel_3 nhl3 = null;
						NeighborhoodLevelId nhl = null;

						if (bwUserRequest.nhLevel > -1)
						{


							if (NhURLToNhId.get(nhUrl) == null)
							{
								System.out.println(" Neighborhood may exist in database or may need to be created  = " + nhUrl + " at level " + bwUserRequest.nhLevel);
								nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, bwUserRequest.nh0.trim(),
																																							  bwUserRequest.nh1.trim(),
																																							  bwUserRequest.nh2.trim(),
																																							  bwUserRequest.nh3.trim(),
																																							  bwUserRequest.nhLevel);
								if (nhl != null)
								{

									System.out.println(" Neighborhood " + nhUrl + " exists so let us add it to the Hashtable");
									NhURLToNhId.put(nhUrl, nhl);
								}
								else
								{
									// New Neighborhood  ...we need to add it ...
									//nh0
									if (NhURLToNhId.get(bwUserRequest.nh0.trim()) == null && bwUserRequest.nhLevel > -1)
									{
										nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, bwUserRequest.nh0.trim(),
																																									  " ",
																																									  " ",
																																									  " ",
																																									  0);
										if (nhl != null)
										{
											System.out.println(" Neighborhood " + bwUserRequest.nh0.trim() + " exists so let us add it to the Hashtable");
											NhURLToNhId.put(bwUserRequest.nh0.trim(), nhl);
											nhl0 = NeighborhoodManagerLevel_0.getNeighborhoodLevel_0_by_Neighborhood_Id(connection, nhl.getId());
										}
										else
										{
											//create level 0 nh
											System.out.println(" Neighborhood " + bwUserRequest.nh0.trim() + " doesn't exists so let us create it");
											nhl0 = NeighborhoodManagerLevel_0.createNeighborhood(connection, bwUserRequest.nh0, tid, bwUserRequest.nhSecure);
											System.out.println(" Created Neighborhood " + bwUserRequest.nh0 + " at level 0" + " nh id = " + nhl0.getNhId());
											nhl = new NeighborhoodLevelId(nhl0.getNhId(), nhl0.getName(), nhl0.getId(), nhl0.getLevel());
											NhURLToNhId.put(bwUserRequest.nh0.trim(), nhl);

										}
									}
									else
									{
										System.out.println(" Neighborhood " + bwUserRequest.nh0.trim() + " exists ");

										nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, bwUserRequest.nh0.trim(),
																																																					  " ",
																																																					  " ",
																																																					  " ",
																																																					  0);
										nhl0 = NeighborhoodManagerLevel_0.getNeighborhoodLevel_0_by_Neighborhood_Id(connection, nhl.getId());

									}


									System.out.println(" NHL= " + nhl.getId() + " at level " + nhl.getLevel() + " with name = " + nhl.getName());

									// nh1
									if (NhURLToNhId.get(bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim()) == null && bwUserRequest.nhLevel > 0)
									{
										System.out.println("Working on nh at level 1 ");
										nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, bwUserRequest.nh0.trim(),
																																									 bwUserRequest.nh1.trim(),
																																									 " ",
																																									 " ",
																																									 1);
										if (nhl != null)
										{
											System.out.println(" Neighborhood with id " + nhl.getId() + " with url " + bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + " exists so let us add it to the Hashtable");

											NhURLToNhId.put(bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim(), nhl);
											nhl1 = NeighborhoodManagerLevel_1.getNeighborhoodLevel_1_by_Neighborhood_Id(connection, nhl.getId());

										}
										else
										{
											//create level 1 nh
											System.out.println(" Neighborhood " + bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + " doesn't exists so let us create it");
											nhl1 = NeighborhoodManagerLevel_1.createNeighborhood(connection, bwUserRequest.nh1, nhl0.getNhId(), tid, bwUserRequest.nhSecure);
											System.out.println(" Created Neighborhood " + bwUserRequest.nh1 + " at level1  with parent id " + nhl0.getNhId() + " and got nh id = " + nhl1.getNhId());
											nhl = new NeighborhoodLevelId(nhl1.getNhId(), nhl1.getName(), nhl1.getId(), nhl1.getLevel());
											NhURLToNhId.put(bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim(), nhl);
										}
									}
									else
										if (bwUserRequest.nhLevel > 0)
										{
											System.out.println(" Neighborhood " + bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + " exists ");

											nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, bwUserRequest.nh0.trim(),
																																										  bwUserRequest.nh1.trim(),
																																										  " ",
																																										  " ",
																																										  1);
											System.out.println(" Neighborhood " + nhl.getId() + "_" + nhl.getLevelId() + " exists ");
											nhl1 = NeighborhoodManagerLevel_1.getNeighborhoodLevel_1_by_Neighborhood_Id(connection, nhl.getId());


										}

									System.out.println(" NHL= " + nhl.getId() + " at level " + nhl.getLevel() + " with name = " + nhl.getName());


									// nh2

									if (NhURLToNhId.get(bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + "_" + bwUserRequest.nh2.trim()) == null && bwUserRequest.nhLevel > 1)
									{

										System.out.println("Working on nh at level 2 ");
										nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, bwUserRequest.nh0.trim(),
																																									 bwUserRequest.nh1.trim(),
																																									 bwUserRequest.nh2.trim(),
																																									 " ",
																																									 2);
										if (nhl != null)
										{
											System.out.println(" Neighborhood with id " + nhl.getId() + " with url " + bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + "_" + bwUserRequest.nh2.trim() + " exists so let us add it to the Hashtable");
											NhURLToNhId.put(bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + "_" + bwUserRequest.nh2.trim(), nhl);
											nhl2 = NeighborhoodManagerLevel_2.getNeighborhoodLevel_2_by_Neighborhood_Id(connection, nhl.getId());
										}
										else
										{
											//create level 2 nh
											System.out.println(" Neighborhood " + bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + "_" + bwUserRequest.nh2.trim() + " doesn't exists so let us create it");

											nhl2 = NeighborhoodManagerLevel_2.createNeighborhood(connection, bwUserRequest.nh2, nhl1.getNhId(), tid, bwUserRequest.nhSecure);
											System.out.println(" Created Neighborhood " + bwUserRequest.nh2 + " at level 2  with parent id " + nhl1.getNhId() + " and got nh id = " + nhl2.getNhId());
											nhl = new NeighborhoodLevelId(nhl2.getNhId(), nhl2.getName(), nhl2.getId(), nhl2.getLevel());
											NhURLToNhId.put(bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + "_" + bwUserRequest.nh2.trim(), nhl);
										}
									}
									else
										if (bwUserRequest.nhLevel > 1)
										{
											System.out.println(" Neighborhood " + bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + "_" + bwUserRequest.nh2.trim() + " exists");

											nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, bwUserRequest.nh0.trim(),
																																									bwUserRequest.nh1.trim(),
																																										  bwUserRequest.nh2.trim(),
																																										  " ", 2);
											nhl2 = NeighborhoodManagerLevel_2.getNeighborhoodLevel_2_by_Neighborhood_Id(connection, nhl.getId());

										}
									System.out.println(" NHL= " + nhl.getId() + " at level " + nhl.getLevel() + " with name = " + nhl.getName());


									// nh3

									if (NhURLToNhId.get(bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + "_" + bwUserRequest.nh2.trim() + "_" + bwUserRequest.nh3.trim()) == null && bwUserRequest.nhLevel > 2)
									{

										System.out.println("Working on nh at level 3 ");

										nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, bwUserRequest.nh0.trim(),
																																									 bwUserRequest.nh1.trim(),
																																									 bwUserRequest.nh2.trim(),
																																									 bwUserRequest.nh3.trim(),
																																									 3);
										if (nhl != null)
										{
											System.out.println(" Neighborhood with id " + nhl.getId() + " with url " + bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + "_" + bwUserRequest.nh2.trim() + "_" + bwUserRequest.nh3.trim() + " exists so let us add it to the Hashtable");
											NhURLToNhId.put(bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + "_" + bwUserRequest.nh2.trim() + "_" + bwUserRequest.nh3.trim(), nhl);
											nhl3 = NeighborhoodManagerLevel_3.getNeighborhoodLevel_3_by_Neighborhood_Id(connection, nhl.getId());
										}
										else
										{
											//create level 3 nh
											System.out.println(" Neighborhood " + bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + "_" + bwUserRequest.nh2.trim() + "_" + bwUserRequest.nh3.trim() + " doesn't exists so let us create it");

											nhl3 = NeighborhoodManagerLevel_3.createNeighborhood(connection, bwUserRequest.nh3, nhl2.getNhId(), tid, bwUserRequest.nhSecure);
											System.out.println(" Created Neighborhood " + bwUserRequest.nh3 + " at level 3 with parent id " + nhl2.getNhId() + " and got nhid " + nhl3.getNhId());
											nhl = new NeighborhoodLevelId(nhl3.getNhId(), nhl3.getName(), nhl3.getId(), nhl3.getLevel());
											NhURLToNhId.put(bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + "_" + bwUserRequest.nh2.trim() + "_" + bwUserRequest.nh3.trim(), nhl);
										}
									}
									else
										if (bwUserRequest.nhLevel > 2)
										{

											System.out.println(" Neighborhood " + bwUserRequest.nh0.trim() + "_" + bwUserRequest.nh1.trim() + "_" + bwUserRequest.nh2.trim() + "_" + bwUserRequest.nh3.trim() + " exists ");

											nhl = NeighborhoodManager.getNeighborhoodLevelId(connection, bwUserRequest.nh0.trim(),
																																																  bwUserRequest.nh1.trim(),
																																																  bwUserRequest.nh2.trim(),
																																																  bwUserRequest.nh3.trim(),
																																																  3);
											nhl3 = NeighborhoodManagerLevel_3.getNeighborhoodLevel_3_by_Neighborhood_Id(connection, nhl.getId());
										}

								}




							} // 	if ( NhURLToNhId.get( nhUrl ) == null )
							else
							{
								System.out.println(" url " + nhUrl + " exists in the in memory map ");
								nhl = (NeighborhoodLevelId)NhURLToNhId.get(nhUrl);

							}
						}


						if (!bwUserRequest.Email.trim().equals(""))
						{

							User user = UserManager.getUser(connection, bwUserRequest.Email);
							int newUserId = -1;
							if (user == null)
							{
								System.out.println("adding a new user " + bwUserRequest.Email );
								NewUser nu = new NewUser(bwUserRequest.Email, bwUserRequest.extId, bwUserRequest.password, bwUserRequest.fName, bwUserRequest.lName, 1);
								newUserId = UserManager.createUser(connection, nu);
								//System.out.println("got  a new user id " + newUserId );
							}
							else
							{
								newUserId = user.getId();
							}

							if (bwUserRequest.nhLevel > -1)
							{
								//	System.out.println("  Does the user exist as a member or we need to add a member ");
								Vector memberships = UserManager.getMembershipListForUser(connection, newUserId);
								int userMemberId = -1;
								if (memberships.size() > 0)
								{
									//	System.out.println("  is user a member already ?");



									for (int memIndex = 0; memIndex < memberships.size(); memIndex++)
									{
										Member mem = (Member)memberships.elementAt(memIndex);
										mem.print();
										int memNhId = mem.getNeighborhoodId();
										String memNhName = mem.getNeighborhoodName();
										System.out.println(" Nh Id = " + nhl.getId() + " Nh Name " + nhl.getName());
										if (memNhId == nhl.getId() && memNhName.equals(nhl.getName()))
										{
											userMemberId = mem.getId();
											break;
										}
									}

								}

								if (userMemberId == -1)
								{
									//	System.out.println(" add user as member ");
									userMemberId = MemberManager.createMember(connection, tid, newUserId, nhl.getId());
								}
							}

						}
					}
					catch (Exception e)
					{
						e.printStackTrace();
						bwUserRequest.result = "FAILURE";
						bwUserRequest.comment = bwUserRequest.comment + "\n" + e.getMessage();
					}
				}

			}  // for (  rowIndex=0; rowIndex < rowids.size(); rowIndex++ )

			Vector vec_cellContents = new Vector();

			for (int rowIndex = 0; rowIndex < rowids.size(); rowIndex++)
			{
				a_rowIntegerId = (Integer)rowids.elementAt(rowIndex);
				bwUserRequest = (BoardwalkUserRequest)rowIdToUserRequest.get(a_rowIntegerId);
				if (!bwUserRequest.action.trim().equalsIgnoreCase("NO CHANGE"))
				{




					if (!bwUserRequest.result.equals("FAILURE"))
					{
						CellContents action = new CellContents(bwUserRequest.actionCellId, "STRING","NO CHANGE" );
						CellContents lastaction = new CellContents(bwUserRequest.lastactionCellId, "STRING", bwUserRequest.action.trim());
						CellContents result = new CellContents(bwUserRequest.resultCellId, "STRING", "SUCCESS");
						CellContents comment = new CellContents(bwUserRequest.commentCellId, "STRING", "Done");
						vec_cellContents.addElement(action);
						vec_cellContents.addElement(lastaction);
						vec_cellContents.addElement(result);
						vec_cellContents.addElement(comment);
						CellContents passwordr = new CellContents(bwUserRequest.passwordCellId, "STRING", "********");
						vec_cellContents.addElement(passwordr);


					}
					else
					{
						CellContents result = new CellContents(bwUserRequest.resultCellId, "STRING", bwUserRequest.result);
						vec_cellContents.addElement(result);
						CellContents action = new CellContents(bwUserRequest.actionCellId, "STRING", bwUserRequest.action);
						vec_cellContents.addElement(action);
						CellContents comment = new CellContents(bwUserRequest.commentCellId, "STRING", bwUserRequest.comment);
						vec_cellContents.addElement(comment);
						

					}
				}

//				if (bwUserRequest.action.trim().equalsIgnoreCase("NO CHANGE"))
//				{
//				
//
//					if (bwUserRequest.)
//					{
//					}
//					CellContents result = new CellContents(bwUserRequest.resultCellId, "STRING", "");
//					vec_cellContents.addElement(result);
//					CellContents passwordr = new CellContents(bwUserRequest.passwordCellId, "STRING", "********");
//					vec_cellContents.addElement(passwordr);
//				}


			}

			if (vec_cellContents.size() > 0)
			{
				System.out.println("Saving cells " +  vec_cellContents.size() );
				TableManager.commitCellsByCellId(connection, tid, vec_cellContents, false);
			}



			return "Table processed succesfully";




		}
		else
		{
			return "There are no rows in this table";
		}



	}


class SMTPAuthenticatorUserBulk extends javax.mail.Authenticator {
		public javax.mail.PasswordAuthentication getPasswordAuthentication()
		{
			return new javax.mail.PasswordAuthentication(smtpfrom, smtppsswd);
		}
	}



};
