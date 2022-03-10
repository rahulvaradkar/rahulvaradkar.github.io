package com.boardwalk.table;

import java.util.*;
import java.io.*;
import com.boardwalk.database.*;

import java.sql.*;                  // JDBC package
import javax.sql.*;                 // extended JDBC package


public class ColumnTypeManager {

    static Vector   columntypelist = null;
	public static String GET_COLUMN_TYPES="SELECT COLUMN_TYPE FROM BW_COLUMN_TYPE";

    public ColumnTypeManager() {

		if ( columntypelist == null ) {
			init();
		}
	}


    private void init  () {
	columntypelist  = new Vector();

	try {

			DatabaseLoader dl = new DatabaseLoader(new Properties() );
			Connection conn = dl.getConnection();
			PreparedStatement ps = conn.prepareStatement(GET_COLUMN_TYPES);
			ResultSet rs = ps.executeQuery();
			while( rs.next() ) {
			 Columntype ctype = new Columntype(rs.getString("COLUMN_TYPE")) ;
			 columntypelist.addElement(ctype);
			}
			rs.close();
			ps.close();
			conn.close();
		 }
		 catch(Exception e ) {
				 e.printStackTrace();
		}


    }

    public Vector getcolumntypelist () {
	return columntypelist;
    }

    public void addcolumntype ( Columntype c) {
	columntypelist.addElement ( c );
    }



    public void printcolumntypelist () {
	Enumeration e = columntypelist.elements ();
	Columntype c;

	System.out.println ( "ColumnType  Records");
	System.out.println ( "     " + "ColumnType");
	while (e.hasMoreElements () == true ) {
	    c = (Columntype) e.nextElement ();

	    System.out.println ( "     " + c.getType ());
	}
    }

    
    public Columntype getColumnType(String type) {
	Iterator i = columntypelist.iterator();
	Columntype c;
	while (i.hasNext()) {
	    c = (Columntype)i.next();
	    if (c.getType().equals(type))
		return c;
	}

	return null;
    }
};


