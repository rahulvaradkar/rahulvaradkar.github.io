package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class TableActionUIPreference
{

	public final static String ADD_ROW  = "Add Row";
	public final static String DELETE_ROW  = "Delete Row";

	public final static String ADD_COLUMN = "Add Column";
	public final static String DELETE_COLUMN  = "Delete Column";



	public final static String LATEST_OF_TABLE  = "All Rows";
	public final static String LATEST_OF_MY_ROWS  = "My Rows";
	public final static String MY_LATEST  = "My Entries";
	public final static String LATEST_OF_ALL  = "Consolidate All Entries";
	public final static String LATEST_OF_CHILDREN  = "Consolidate Entries of Children Neighborhoods";
	public final static String LATEST_VIEW_OF_ALL_USERS_IN_ANY_NEIGHBORHOOD  = "Consolidate  Specific Neighborhood";
	public final static String LATEST_VIEW_OF_ALL_USERS_IN_ANY_CHILDREN_NEIGHBORHOOD  = "Consolidate Specific Children Neighborhood";
	public final static String LATEST_ROWS_OF_ALL_USERS_IN_ANY_NEIGHBORHOOD  = "Rows by Neighborhood";





	public final static String SAVE_TABLE  = "Save Table";
	public final static String ADMINISTER_TABLE  = "Table Admin Page";
	public final static String SHOW_DEFAULT_VALUES  = "Design Mode";
	public final static String GET_QUERY_RESULTS  = "Search";




	private  static Hashtable  ActionToDefaultValues;




      int    m_actionUI_id;
	  int    m_table_id;
	  String m_actionUI_string;
	  String m_action;
	  int m_actionId;

   public TableActionUIPreference(
	   													  int     a_actionUI_id,
														  int    a_table_id,
														  String  a_actionUI_string,
														  String a_action,
														  int a_actionId
														  )
{

	if ( ActionToDefaultValues == null )
	{

						ActionToDefaultValues = new Hashtable();
						ActionToDefaultValues.put("ADD_ROW" , ADD_ROW );
						ActionToDefaultValues.put("DELETE_ROW" , DELETE_ROW );
						ActionToDefaultValues.put("ADD_COLUMN" , ADD_COLUMN );
						ActionToDefaultValues.put("DELETE_COLUMN" , DELETE_COLUMN );
						ActionToDefaultValues.put("LATEST_OF_TABLE" , LATEST_OF_TABLE );
						ActionToDefaultValues.put("LATEST_OF_MY_ROWS" , LATEST_OF_MY_ROWS );
						ActionToDefaultValues.put("MY_LATEST" , MY_LATEST );
						ActionToDefaultValues.put("LATEST_OF_ALL" , LATEST_OF_ALL );
						ActionToDefaultValues.put("LATEST_OF_CHILDREN" , LATEST_OF_CHILDREN );

						ActionToDefaultValues.put("LATEST_VIEW_OF_ALL_USERS_IN_ANY_NEIGHBORHOOD" , LATEST_VIEW_OF_ALL_USERS_IN_ANY_NEIGHBORHOOD );
						ActionToDefaultValues.put("LATEST_VIEW_OF_ALL_USERS_IN_ANY_CHILDREN_NEIGHBORHOOD" , LATEST_VIEW_OF_ALL_USERS_IN_ANY_CHILDREN_NEIGHBORHOOD );
						ActionToDefaultValues.put("LATEST_ROWS_OF_ALL_USERS_IN_ANY_NEIGHBORHOOD" , LATEST_ROWS_OF_ALL_USERS_IN_ANY_NEIGHBORHOOD );




						ActionToDefaultValues.put("SAVE_TABLE" , SAVE_TABLE );
						ActionToDefaultValues.put("ADMINISTER_TABLE" , ADMINISTER_TABLE );
						ActionToDefaultValues.put("SHOW_DEFAULT_VALUES" , SHOW_DEFAULT_VALUES );
						ActionToDefaultValues.put("GET_QUERY_RESULTS" , GET_QUERY_RESULTS );



	}

      m_actionUI_id=a_actionUI_id;
   	  m_table_id=a_table_id;
   	  if ( m_actionUI_id == -1 && ( a_actionUI_string == null || ( a_actionUI_string != null && a_actionUI_string.trim().equals("") ) ) )
   	  {
		  m_actionUI_string=(String)ActionToDefaultValues.get(a_action);
	  }
	  else
	  {
	  	m_actionUI_string=a_actionUI_string;
	  }

   	  m_action=a_action;
	  m_actionId=a_actionId;
  }



	public static Hashtable  getActionToDefaultValues () {
		  if ( ActionToDefaultValues != null &&   ActionToDefaultValues.size() > 0 )
		  		return ActionToDefaultValues;
		  else
		  		return null;
  }

  public int getId () {
		  return m_actionUI_id;
  }

  public int getTableId () {
    return m_table_id;
    }

  public String getActionUIString () {
  return m_actionUI_string;
  }

 public String getAction () {
    return m_action;
  }

  public int getActionId () {
  return m_actionId;
  }


  public void print()
  {
	  System.out.println(" Table Action UI String for Table Id " + getId() + " Action " + m_action + "  Action UI String  " +  m_actionUI_string );
  }


};


