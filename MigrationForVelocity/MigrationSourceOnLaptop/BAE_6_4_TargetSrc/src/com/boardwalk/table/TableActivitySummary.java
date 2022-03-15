

package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class TableActivitySummary {
   int		m_id;
   int		m_num_updates;
   int		m_num_row_add;
   int		m_num_row_del;
   int		m_num_col_add;
   int		m_num_col_del;
   int		m_num_cells_upd;
   int		m_num_formula_add;
   int		m_num_formula_del;
   int		m_num_formula_upd;



  public TableActivitySummary (int a_id)
  {
	  m_id = a_id;
	  m_num_updates = 0;
	  m_num_row_add = 0;
	  m_num_row_del = 0;
	  m_num_col_add = 0;
	  m_num_col_del = 0;
	  m_num_cells_upd = 0;
	  m_num_formula_add = 0;
  	  m_num_formula_del = 0;
  	  m_num_formula_upd = 0;

  }

  public void addItem (int n, String action)
  {
	  if (action.equals("rowadd"))
	  {
		  m_num_row_add = n;
		  m_num_updates = m_num_updates + n;
	  }
	  else if (action.equals("rowdel"))
	  {
		  m_num_row_del = n;
		  m_num_updates = m_num_updates + n;
	  }
	  else if (action.equals("coladd"))
	  {
		  m_num_col_add = n;
		  m_num_updates = m_num_updates + n;
	  }
	  else if (action.equals("coldel"))
	  {
		  m_num_col_del = n;
		  m_num_updates = m_num_updates + n;
	  }
	  else if (action.equals("cellstringupdate"))
	  {
		  m_num_cells_upd = m_num_cells_upd + n;
		  m_num_updates = m_num_updates + n;
	  }
	  else if (action.equals("cellintegerupdate"))
	  {
		  m_num_cells_upd = m_num_cells_upd + n;
		  m_num_updates = m_num_updates + n;
	  }
	  else if (action.equals("celldoubleupdate"))
	  {
		  m_num_cells_upd = m_num_cells_upd + n;
		  m_num_updates = m_num_updates + n;
	  }
	  else if (action.equals("formulaupdate"))
	  {
		  m_num_formula_upd = n;
		  m_num_updates = m_num_updates + n;
	  }

  }

  public int getId ()
  {
  	return m_id;
  }

  public int numUpdates ()
  {
  	return m_num_updates;
  }
  public int numRowsAdded ()
  {
  	return m_num_row_add;
  }
  public int numRowsDeleted ()
  {
  	return m_num_row_del;
  }
  public int numColumnsAdded ()
  {
  	return m_num_col_add;
  }
  public int numColumnsDeleted ()
  {
  	return m_num_col_del;
  }
  public int numCellsUpdated ()
  {
  	return m_num_cells_upd;
  }
  public int numFormulaAdded ()
  {
  	return m_num_formula_add;
  }
  public int numFormulaDeleted ()
  {
  	return m_num_formula_del;
  }
  public int numFormulaUpdated ()
  {
  	return m_num_formula_upd;
  }




};




