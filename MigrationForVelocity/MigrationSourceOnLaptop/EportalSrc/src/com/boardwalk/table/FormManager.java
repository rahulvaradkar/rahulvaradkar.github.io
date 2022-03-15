package com.boardwalk.table;
import java.util.*;
import java.io.*;

import com.boardwalk.database.DatabaseLoader;
import com.boardwalk.exception.*;
import com.boardwalk.database.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package

/*
	Sarang Kulkarni @ 1/21/05
*/

public class FormManager
{
	private static String CALL_BW_CR_FRM_DEF ="{CALL BW_CR_FRM_DEF(?,?,?)}";
	public static void addFormDefinition(Connection connection,
								  int table_id,
								  int frm_tbl_id,
								  int tx_id)
		throws SystemException
	{
		CallableStatement callableStatement = null;
		int formula_id;

		try {
			System.out.println("Form::addFormDefinition -> Calling BW_CR_FRM_DEF with table id = " + table_id + " and form_table_id = " + frm_tbl_id);
			callableStatement = connection.prepareCall(CALL_BW_CR_FRM_DEF);
			callableStatement.setInt(1, table_id);
			callableStatement.setInt(2, frm_tbl_id);
			callableStatement.setInt(3, tx_id);
			callableStatement.executeUpdate();
		} catch (SQLException sql1) {
			throw new SystemException(sql1);
		} finally {
			try {
				callableStatement.close();
			} catch (SQLException sql2) {
				throw new SystemException(sql2);
			}
		}
	}

	public static int getDefinitionTable(Connection connection,
										  int tableId,
										  int userNhId)
	throws SystemException
	{
		int defTableId = -1;

		System.out.println("Get the form definition");
		String query = "select frm_tbl_id from bw_tbl_frm where tbl_id = ?";
		ResultSet resultset = null;
		PreparedStatement preparedstatement = null;
		try {
			preparedstatement = connection.prepareStatement(query);
			preparedstatement.setInt(1, tableId);
			//preparedstatement.setInt(2, userNhId);
			resultset = preparedstatement.executeQuery();
			if ( resultset.next() ) //only one for now
			{
				defTableId = resultset.getInt("FRM_TBL_ID");
				System.out.println("Using table with id = " + defTableId + " for form definition");
			}
		}
		catch(SQLException sqlexception) {
			throw new SystemException(sqlexception);
		}
		finally {
			try {
				if (resultset != null)
				{
					resultset.close();
				}
				if (preparedstatement != null)
				{
					preparedstatement.close();
				}
			}
			catch(SQLException sqlexception1) {
				throw new SystemException(sqlexception1);
			}
		}

		return defTableId;
	}


	public static FormDefinition getDefinition(Connection connection,
										       int defTableId,
										       int userId,
										       int memberId,
										       int userNhId)
	throws BoardwalkException, Exception
	{
		Vector CellIds = new Vector();
		TableContents tbc = null;
		Hashtable columns = new Hashtable();
		Vector columnNames = new Vector();
		Hashtable cellsByRowId =new Hashtable();
		Vector columnsSortedBySeqNum = new Vector();
		Vector   rowids =new Vector();
		Hashtable rowObjsById = null;
		Integer a_rowIntegerId = null;

		System.out.println("Processing Form Definition table Id = " + defTableId );
		tbc = TableManager.getTableContents(connection,
										  defTableId,
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

		// check the columns to make sure structure is Ok
		// TBD
		FormDefinition fd = new FormDefinition();

		// loop over the rows and build the Form Definition
		if ( rowids.size() <= 0)
		{
			throw new BoardwalkException(14001);
		}

		FormSection currSection = null;
		Hashtable currSectionFields = null;
		FormField currField = null;
		int maxHoriz = 0;
		int maxHorizFields = 0;

		VersionedCell cell;
		for ( int rowIndex=0; rowIndex < rowids.size(); rowIndex++ )
		{
			a_rowIntegerId = (Integer)rowids.elementAt( rowIndex);
			Vector cells = (Vector) ( (Vector)cellsByRowId.get(a_rowIntegerId)).elementAt(0);
			int rowId = ((Integer)rowids.elementAt(rowIndex)).intValue();

			cell = (VersionedCell) cells.elementAt(0);
			String cellVal = cell.getValueAsString().trim();
			System.out.println ("TAG = " + cellVal);
			if (cellVal.equalsIgnoreCase("BW_TITLE"))
			{
				// currently only one property (TEXT)
				cell = (VersionedCell) cells.elementAt(2);
				String fTitle = cell.getValueAsString().trim();

				fd.setTitle(fTitle);
			}
			else if (cellVal.equalsIgnoreCase("BW_INSTRUCTIONS"))
			{
				// currently only one property (TEXT)
				cell = (VersionedCell) cells.elementAt(2);
				String fInstr = cell.getValueAsString().trim();

				fd.setInstructions(fInstr);
			}
			else // column name or section
			{
				if (cellVal.equalsIgnoreCase("Section")) // ui layout sections
				{


					cell = (VersionedCell) cells.elementAt(1);
					String prop = cell.getValueAsString().trim();
					// NAME is always the first property of a section
					if (prop.equalsIgnoreCase("Name")) // start a new section
					{
						maxHorizFields = 0;
						if (currSection !=null) // end currSection and start a new one
						{
							// stuff the curr field into the section
							Vector fields = null;
							fields = (Vector)currSection.fieldsByLevel.get(new Integer(currField.level));
							if (fields == null)
							{
								fields = new Vector();
							}
							fields.addElement(currField);
							currSection.fieldsByLevel.put(new Integer(currField.level), fields);
							System.out.println("Added " + currField + " to " + currSection + "at level " + currField.level);
							if (fields.size() > maxHorizFields)
							{
								maxHorizFields = fields.size();
							}
							currField = null;
							currSection.maxFieldsAtLevel = maxHorizFields;

							// add the section to fd
							Vector sections = null;
							sections = (Vector)fd.sectionByLevel.get(new Integer(currSection.level));
							if (sections == null)
							{
								sections = new Vector();
							}

							sections.addElement(currSection);
							fd.sectionByLevel.put(new Integer(currSection.level), sections);
							System.out.println("Added section " + currSection + "to form def");
							if (sections.size() > maxHoriz)
							{
								maxHoriz = sections.size();
							}
						}
						// start a new section
						currSection = new FormSection();
						cell = (VersionedCell) cells.elementAt(2);
						String val = cell.getValueAsString().trim();
						currSection.name = val;
					}
					else if (prop.equalsIgnoreCase("Level"))
					{
						cell = (VersionedCell) cells.elementAt(2);
						String val = cell.getValueAsString().trim();
						currSection.level = Integer.parseInt(val);

					}
					else
					{
						FormFieldProperty fProp = new FormFieldProperty();
						cell = (VersionedCell) cells.elementAt(2);
						String val = cell.getValueAsString().trim();
						fProp.property = prop;
						fProp.value = val;

						currSection.properties.addElement(fProp);

					}
				}
				else // bw data fields / columns
				{
					// create a new field when there is none or
					// encounter a new field. Set the current field.
					// it is expected that all properties of a field
					// will be together.
					if (currField == null)
						// first time
					{
						System.out.println("First Field");
						currField = new FormField(cellVal);

					}
					if (!currField.fieldName.equalsIgnoreCase(cellVal))
					{
						System.out.println("New Field");
						// add the prev field to the current section
						Vector fields = null;
						fields = (Vector)currSection.fieldsByLevel.get(new Integer(currField.level));
						if (fields == null)
						{
							fields = new Vector();
						}
						fields.addElement(currField);
						currSection.fieldsByLevel.put(new Integer(currField.level), fields);
						System.out.println("Added " + currField + " to " + currSection + "at level " + currField.level);
						if (fields.size() > maxHorizFields)
						{
							maxHorizFields = fields.size();
						}

						currField = new FormField(cellVal);
					}
					// in either case, add the new field property to the list
					// of properties for the current field.
					FormFieldProperty fProp = new FormFieldProperty();

					cell = (VersionedCell) cells.elementAt(1);
					String prop = cell.getValueAsString().trim();
					if (prop.equalsIgnoreCase("Label"))
					{
						cell = (VersionedCell) cells.elementAt(2);
						currField.label = cell.getValueAsString().trim();

					}
					else if (prop.equalsIgnoreCase("Level"))
					{
						cell = (VersionedCell) cells.elementAt(2);
						String val = cell.getValueAsString().trim();
						currField.level = Integer.parseInt(val);

					}
					else // property
					{
						cell = (VersionedCell) cells.elementAt(2);
						String val = cell.getValueAsString().trim();
						fProp.property = prop;
						fProp.value = val;

						currField.properties.addElement(fProp);
					}
				} // else (data fields)
			} // else sections or column names/datafields
		} // for all rows in the definition table

		// add the last info
		Vector fields = null;
		fields = (Vector)currSection.fieldsByLevel.get(new Integer(currField.level));
		if (fields == null)
		{
			fields = new Vector();
		}
		fields.addElement(currField);
		currSection.fieldsByLevel.put(new Integer(currField.level), fields);
		System.out.println("Added " + currField + " to " + currSection + "at level " + currField.level);
		if (fields.size() > maxHorizFields)
		{
			maxHorizFields = fields.size();
		}
		currSection.maxFieldsAtLevel = maxHorizFields;

		Vector sections = null;
		sections = (Vector)fd.sectionByLevel.get(new Integer(currSection.level));
		if (sections == null)
		{
			sections = new Vector();
		}
		sections.addElement(currSection);
		fd.sectionByLevel.put(new Integer(currSection.level), sections);
		System.out.println("Added section " + currSection + "to form def");
		if (sections.size() > maxHoriz)
		{
			maxHoriz = sections.size();
		}

		fd.maxSectionsAtLevel = maxHoriz;

		System.out.println (" Max Sections at One Level = "  + maxHoriz);
		System.out.println ("Successfully read form definition");
		return fd;
	}// getDefinition()
};