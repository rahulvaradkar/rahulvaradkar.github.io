package boardwalk.rest;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.Vector;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

import com.boardwalk.database.TransactionManager;
import com.boardwalk.excel.xlColumn_import;
import com.boardwalk.excel.xlErrorNew;
import com.boardwalk.exception.BoardwalkException;
import com.boardwalk.exception.SystemException;
import com.boardwalk.table.ColumnManager;
import com.boardwalk.table.RowManager;
import com.boardwalk.table.TableAccessList;
import com.boardwalk.table.TableAccessRequest;
import com.boardwalk.table.TableInfo;
import com.boardwalk.table.TableManager;
import com.boardwalk.table.TableRowInfo;
import com.boardwalk.table.TableViewManager;

import boardwalk.connection.BoardwalkConnection;
import boardwalk.rest.GridManagement.GET_TBL;
import io.swagger.api.NotFoundException;
import io.swagger.model.CellChain;
import io.swagger.model.CellStatus;
import io.swagger.model.CellTransaction;
import io.swagger.model.ColumnChain;
import io.swagger.model.ErrorRequestObject;
import io.swagger.model.FormulaValue;
import io.swagger.model.GridChain;
import io.swagger.model.GridInfo;
import io.swagger.model.GridTransaction;

import io.swagger.model.RowChain;
import io.swagger.model.Transaction;

public class GridchainManagement {

	
	public GridchainManagement()
	{	
	}

    public enum GRID_CHAIN_INFO {
    	GRID_ID (0),
    	GRID_NAME (1),
    	GRID_PURPOSE (2),
    	COLLAB_ID (3),
    	WB_ID (4),
    	PEER_ACCESS (5),
    	PRIVATE_ACCESS (6),
    	FRIEND_ACCESS (7),
    	SEQUENCE_NUMBER (8),
    	NH_ID (9),
    	IS_ACTIVE (10) ,
    	VIEW_PREF (11) ,
    	TX_ID_CREATED (12) ,
    	TX_CREATED_ON (13),
    	TX_CREATED_USER_ID (14),
    	TX_CREATED_USER_EMAIL (15),
    	TX_CREATED_DESCRIPTION (16),
    	TX_CREATED_COMMENT (17),
    	IS_LOCKED (18) ,
    	TX_ID_LOCKED (19) ,
    	TX_LOCKED_ON (20),
    	TX_LOCKED_USER_ID (21),
    	TX_LOCKED_USER_EMAIL (22),
    	TX_LOCKED_DESCRIPTION (23),
    	TX_LOCKED_COMMENT (24),
    	MAX_TX_ID (25);

    	private int colNo;

    	GRID_CHAIN_INFO(int colNo) {
            this.colNo = colNo;
        }    	
        
        public int getcolNo() {
        	return this.colNo;
        }
    }
    
    public enum COLUMN_CHAIN_INFO {
    	COLUMN_ID (0),
    	COLUMN_NAME (1),
    	GRID_ID (2),
    	COLUMN_TYPE (3),
    	SEQUENCE_NUMBER (4),
    	LOOKUP_GRID (5),
    	LOOKUP_COLUMN(6),
    	IS_ENUMERATED (7),
    	COLUMN_WIDTH (8),
    	IS_ACTIVE (9),
    	COLUMN_SOURCE (10),
    	ATTR (11),
    	CREATED_TX_ID (12) ,
    	CREATED_TX_USER_ID (13) ,
    	CREATED_TX_USER_EMAIL (14) ,
    	CREATED_ON (15) ,
    	CREATED_TX_DESC (16) ,
    	CREATED_TX_COMMENT (17) ,
    	DELETED_TX_ID (18) ,
    	DELETED_TX_USER_ID (19) ,
    	DELETED_TX_USER_EMAIL (20) ,
    	DELETED_ON (21) ,
    	DELETED_TX_DESC (22) ,
    	DELETED_TX_COMMENT (23) ;

    	private int colNo;

    	COLUMN_CHAIN_INFO(int colNo) {
            this.colNo = colNo;
        }    	
        
        public int getcolNo() {
        	return this.colNo;
        }
    }

    public enum ROW_CHAIN_INFO {
        BW_ROW_ID (0),
        GRID_ID	(1),
        SEQUENCE_NUMBER (2),
        IS_ACTIVE (3),
        OWNER_ID (4),
        OWNER_EMAIL (5),
        CREATED_TX_ID (6),	
        CREATED_USER_ID (7),	
        CREATED_USER_EMAIL (8),	
        CREATED_ON (9),	
        CREATED_DESCRIPTION (10),	
        CREATED_COMMENT (11),
        DELETED_TX_ID (12),	
        DELETED_TX_USER_ID (13),	
        DELETED_TX_USER_EMAIL (14),	
        DELETED_ON (15),	
        DELETED_TX_DESCRIPTION (16),	
        DELETED_TX_COMMENT (17);

    	private int colNo;

    	ROW_CHAIN_INFO(int colNo) {
            this.colNo = colNo;
        }    	
        
        public int getcolNo() {
        	return this.colNo;
        }
    }
    

    public enum CELL_CHAIN_INFO {
        BW_CELL_ID (0),
        GRID_ID	(1),
        BW_ROW_ID (2),
    	BW_COLUMN_ID (3),
    	CELL_TYPE (4),
    	STRING_VALUE (5),
    	FORMULA (6),
    	IS_ACTIVE (7),
    	TX_ID (8),
    	TX_USER_ID (9),
    	TX_USER_EMAIL (10),
    	TX_CREATED_ON (11),
    	TX_DESCRIPTION (12),
    	TX_COMMENT (13)	;

    	private int colNo;

    	CELL_CHAIN_INFO(int colNo) {
            this.colNo = colNo;
        }    	
        
        public int getcolNo() {
        	return this.colNo;
        }
    }

    
    public enum STRING_VALUES {
        BW_STRING_VALUE_ID (0),
    	BW_CELL_ID (1),
    	STRING_VALUE (2),
    	TX_ID (3),
    	TX_CREATED_USER_ID (4),
    	TX_CREATED_USER_EMAIL (5),
    	TX_CREATED_ON (6),
    	TX_DESCRIPTION (7),
    	TX_COMMENT (8),
    	FORMULA_ID (9);

    	private int colNo;

    	STRING_VALUES(int colNo) {
            this.colNo = colNo;
        }    	
        
        public int getcolNo() {
        	return this.colNo;
        }
    }
    
    
    public enum CELL_STATUS {
        BW_CELL_STATUS_ID (0),
    	BW_CELL_ID (1),
    	IS_ACTIVE (2),
    	TX_ID (3),
    	TX_CREATED_USER_ID (4),
    	TX_CREATED_USER_EMAIL (5),
    	TX_CREATED_ON (6),
    	TX_DESCRIPTION (7),
    	TX_COMMENT (8);

    	private int colNo;

    	CELL_STATUS(int colNo) {
            this.colNo = colNo;
        }    	
        
        public int getcolNo() {
        	return this.colNo;
        }
    }

    public enum FORMULA_VALUE {
        FORMULA_ID (0),
    	FORMULA (1),
    	FORMULA_INDEX (2),
    	TX_ID (3),
    	TX_CREATED_USER_ID (4),
    	TX_CREATED_USER_EMAIL (5),
    	TX_CREATED_ON (6),
    	TX_DESCRIPTION (7),
    	TX_COMMENT (8);

    	private int colNo;

    	FORMULA_VALUE(int colNo) {
            this.colNo = colNo;
        }    	
        
        public int getcolNo() {
        	return this.colNo;
        }
    }

    //Used in Get Transaction Api Call
    public enum GRID_TRANSACTIONS {
    	TX_ID (0),
    	TX_CREATED_ON (1),
    	TX_CREATED_USER_EMAIL (2),
    	TX_COMMENT (3),
    	TX_ACTION (4);

    	private int colNo;

    	GRID_TRANSACTIONS(int colNo) {
            this.colNo = colNo;
        }    	
        
        public int getcolNo() {
        	return this.colNo;
        }
    }
    
    
    //@GET
    //@Path("/{gridId}")
	public static GridChain gridchainGridIdGet(int gridId, String chainWindow, int startTxId, int endTxId, ArrayList<ErrorRequestObject> ErrResps, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode)
	{
		GridChain gc = new GridChain();
		GridInfo gi = new GridInfo();
		ErrorRequestObject erb;

		// get the connection
    	Connection connection = null;
		int nhId = -1;
		int memberId = -1;
		
		connection = bwcon.getConnection();
		memberId = memberNh.get(0);
		nhId = memberNh.get(1);
		
		int userId = bwcon.getUserId();

		try
		{

			TableInfo tinfo = null;
			try 
			{
				System.out.println("Before TableManager.getTableInfo(connection, userId, gridId)");
				tinfo = TableManager.getTableInfo(connection, userId, gridId);
				System.out.println("After TableManager.getTableInfo(connection, userId, gridId)");

				if (tinfo == null)
				{
					System.out.println("tinfo == null IS TRUE");
		//			e.printStackTrace();
					erb = new ErrorRequestObject();
					erb.setError("GridId not found");
					erb.setPath("GridchainManagement.gridchainGridIdGet: TableManager.getTableInfo");
					erb.setProposedSolution("Provide an existing GridId");
					ErrResps.add(erb);
					statusCode.add(404);	//404: GridId not found.
					return gc ;
				}				
				System.out.println("tinfo == null IS FALSESSS");
				
			} 
			catch (Exception e) 
			{
				System.out.println("Inside Exception of tinfo.");
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			PreparedStatement preparedstatement = null;
			ResultSet rs = null ;
			boolean results = false;
			int rsCount = 0;
			
//			System.out.println("Calling BW_GET_GRID_DATA(?,?) - gridId:" + gridId + " ... memberId:" + memberId);
			String CALL_BW_GET_GRID_DATA  = null;
			
			System.out.println("$%&$^%**&*^& Inside GridchainManagement.java.....gridchainGridIdGet...");
	  	 	System.out.println("chainWindow : " + chainWindow);
	  	 	System.out.println("importTxId : " + startTxId);
	  	 	System.out.println("endTxId : " + endTxId);
	  	 	System.out.println("gridId : " + gridId);
	  	 	System.out.println("memberId : " + memberId);

//		    System.out.println("Inside gridchainGridIdGet......1");
	  	 	
			if (chainWindow.equals("ALLTX"))
			{
//			    System.out.println("Inside gridchainGridIdGet......2 (start)");
				CALL_BW_GET_GRID_DATA = "{CALL BW_GET_GRID_DATA(?,?)}";
				preparedstatement = connection.prepareStatement(CALL_BW_GET_GRID_DATA);
				preparedstatement.setInt(1, gridId);
				preparedstatement.setInt(2, memberId);
//			    System.out.println("Inside gridchainGridIdGet......2");
			}

			if (chainWindow.equals("LATESTTX"))
			{
//			    System.out.println("Inside gridchainGridIdGet......3 (start)");
				CALL_BW_GET_GRID_DATA = "{CALL BW_GET_GRID_DATA_LATEST_WITH_HISTORY (?,?)}";
				preparedstatement = connection.prepareStatement(CALL_BW_GET_GRID_DATA);
				preparedstatement.setInt(1, gridId);
				preparedstatement.setInt(2, memberId);
//			    System.out.println("Inside gridchainGridIdGet......3");
			}
			
			if (chainWindow.equals("ASOFTX"))
			{
//			    System.out.println("Inside gridchainGridIdGet......4 (start)");
				CALL_BW_GET_GRID_DATA = "{CALL BW_GET_GRID_DATA_WINDOW(?,?,?,?)}";
				preparedstatement = connection.prepareStatement(CALL_BW_GET_GRID_DATA);
				preparedstatement.setInt(1, gridId);
				preparedstatement.setInt(2, memberId);
				preparedstatement.setInt(3, -1);
				preparedstatement.setInt(4, endTxId);
//			    System.out.println("Inside gridchainGridIdGet......4");
			}
			
			if (chainWindow.equals("BETWEENTX"))
			{
//			    System.out.println("Inside gridchainGridIdGet......5 (start)");
				CALL_BW_GET_GRID_DATA = "{CALL BW_GET_GRID_DATA_WINDOW(?,?,?,?)}";
//			    System.out.println("Inside gridchainGridIdGet......5 (CALL_BW_GET_GRID_DATA " + CALL_BW_GET_GRID_DATA);
				preparedstatement = connection.prepareStatement(CALL_BW_GET_GRID_DATA);
				preparedstatement.setInt(1, gridId);
				preparedstatement.setInt(2, memberId);
				preparedstatement.setInt(3, startTxId);
				preparedstatement.setInt(4, endTxId);
//			    System.out.println("Inside gridchainGridIdGet......5");
			}
				
			System.out.println("CALL_BW_GET_GRID_DATA : " + CALL_BW_GET_GRID_DATA);  
			System.out.println("gridId : " + gridId);  
			System.out.println("memberId : " + memberId);  
			System.out.println("startTxId : " + startTxId);  
			System.out.println("endTxId : " + endTxId);  

//		    System.out.println("Inside gridchainGridIdGet......6");
			
//			CALL_BW_GET_GRID_DATA = "{CALL BW_GET_GRID_DATA(?,?)}";
//			preparedstatement = connection.prepareStatement(CALL_BW_GET_GRID_DATA);
//			preparedstatement.setInt(1, gridId);
//			preparedstatement.setInt(2, memberId);
			
			results = preparedstatement.execute();

//		    System.out.println("Inside gridchainGridIdGet......7");
			
			int nCol;
			String[] row ;
			ArrayList<String[]> table;
			
			ArrayList<Transaction> gts = new ArrayList<Transaction>();
			Transaction gt = new Transaction();
			//gts.add(gt);
			
			ArrayList<Integer> TxIds = new ArrayList<Integer>();
			
			ArrayList<Integer> cellIds = new ArrayList<Integer>();
			CellChain currCC ;
			ArrayList<CellChain>  Arrclc = new ArrayList<CellChain>() ;
			int cellIndex ;
			
			do
		    {
//			    System.out.println("Inside gridchainGridIdGet.......@ begining of do ......8");
				if (results)
				{

//				    System.out.println("Inside gridchainGridIdGet.......before getResultSet()......9");
					
					rs = preparedstatement.getResultSet();

//				    System.out.println("Inside gridchainGridIdGet.......after getResultSet()......10");
					
					switch(rsCount)
					{
					case 0:			//GridChain
						System.out.println("Processing " + rsCount + "th result");
						nCol = rs.getMetaData().getColumnCount();
						System.out.println("nCol : " + nCol);
						row = new String[nCol] ;
						table = new ArrayList<>();
						while (rs.next()) {
//						    System.out.println("Inside gridchainGridIdGet.......inside Case 0 (GridChain) loop ......11");

						    row = new String[nCol];
						    for( int iCol = 1; iCol <= nCol; iCol++ ){
						            Object obj = rs.getObject( iCol );
						            row[iCol-1] = (obj == null) ?null:obj.toString();
//						            System.out.println("iCol-1 ---> " + (iCol-1) +  "    row[iCol-1] ---> " + row[iCol-1]);
						    }

						    gi.setGridId(Integer.parseInt(row[GRID_CHAIN_INFO.GRID_ID.getcolNo()]));
						    gi.setGridName(row[GRID_CHAIN_INFO.GRID_NAME.getcolNo()]);
						    gi.setGridPurpose(row[GRID_CHAIN_INFO.GRID_PURPOSE.getcolNo()]);
						    gi.setCollabId(Integer.parseInt(row[GRID_CHAIN_INFO.COLLAB_ID.getcolNo()]));
						    gi.setWbId(Integer.parseInt(row[GRID_CHAIN_INFO.WB_ID.getcolNo()]));
						    gi.setPeerAccess(Integer.parseInt(row[GRID_CHAIN_INFO.PEER_ACCESS.getcolNo()]));
						    gi.setPrivateAccess(Integer.parseInt(row[GRID_CHAIN_INFO.PRIVATE_ACCESS.getcolNo()]));
						    gi.setFriendAccess(Integer.parseInt(row[GRID_CHAIN_INFO.FRIEND_ACCESS.getcolNo()]));
						    gi.setSequenceNumber(Float.parseFloat(row[GRID_CHAIN_INFO.SEQUENCE_NUMBER.getcolNo()]));
						    gi.setNhId(Integer.parseInt(row[GRID_CHAIN_INFO.NH_ID.getcolNo()]));
						    gi.setIsActive(row[GRID_CHAIN_INFO.IS_ACTIVE.getcolNo()].equals("true")? true: false);
						    gi.setView(row[GRID_CHAIN_INFO.VIEW_PREF.getcolNo()]);
						    gi.setCreationTxId(Integer.parseInt(row[GRID_CHAIN_INFO.TX_ID_CREATED.getcolNo()]));
						    gi.setLockTxId(Integer.parseInt(row[GRID_CHAIN_INFO.TX_ID_LOCKED.getcolNo()]));
						    gi.setImportTxId(Integer.parseInt(row[GRID_CHAIN_INFO.MAX_TX_ID.getcolNo()]));
//						    System.out.println("row[GRID_CHAIN_INFO.MAX_TX_ID.getcolNo()] ----->" + row[GRID_CHAIN_INFO.MAX_TX_ID.getcolNo()]);
//						    gi.setImportTxId(Integer.parseInt(row[25]));
						    
						    //						    gi.setLockTxId(Integer.parseInt(row[GRID_CHAIN_INFO.TX_ID_LOCKED.getcolNo()]));

/*						    gi.setAsOfTxId();
						    gi.setBaselineId();
						    gi.setColCount();
						    gi.setCriteriaTableId();
						    gi.setExportTxId();
						    gi.setFilter();
						    gi.setIsLocked(row[GRID_CHAIN_INFO.IS_LOCKED.getcolNo()]);
						    gi.setMaxTxId();
						    gi.setMemberId();
						    gi.setMode();
						    gi.setNeighborhoodHeirarchy(neighborhoodHeirarchy);
						    gi.setRowCount();					    
						    gi.setServerName();
						    gi.setServerURL();
						    gi.setUserId();*/

						    
							//gc.setCreatorName(row[GRID_CHAIN_INFO.TX_CREATED_USER_EMAIL.getcolNo()]);
/*							gc.setGridCells(gridCells);
							gc.setGridColumns(gridColumns);
							gc.setGridFormulas(gridFormulas);
							gc.setGridRows(gridRows);
							gc.setGridTransactions(gridTransactions);
*/

						    if ( !TxIds.contains(Integer.parseInt(row[GRID_CHAIN_INFO.TX_ID_CREATED.getcolNo()])) )
							{
//								System.out.println("Gridchain : Adding txId:" +  row[GRID_CHAIN_INFO.TX_ID_CREATED.getcolNo()]);
								TxIds.add(Integer.parseInt(row[GRID_CHAIN_INFO.TX_ID_CREATED.getcolNo()])) ;

								gt = new Transaction();
								//gt.setCreatedOn(Long.parseLong(row[GRID_CHAIN_INFO.TX_CREATED_ON.getcolNo()]));
								//gt.setCreatedOn(Long.valueOf(rs.getDate( Integer.parseInt(row[GRID_CHAIN_INFO.TX_CREATED_ON.getcolNo()] ))));
								
								//gt.setCreatedOn(java.sql.Timestamp.valueOf(row[GRID_CHAIN_INFO.TX_CREATED_ON.getcolNo()]).getTime()) ;
								gt.setTxTimeUTC(java.sql.Timestamp.valueOf(row[GRID_CHAIN_INFO.TX_CREATED_ON.getcolNo()]));
								gt.setDescription(row[GRID_CHAIN_INFO.TX_CREATED_DESCRIPTION.getcolNo()]);
								gt.setTxId(Integer.parseInt(row[GRID_CHAIN_INFO.TX_ID_CREATED.getcolNo()]));
								// following line give java.lang.NumberFormatException: For input string: "2018-09-20 11:41:17.64" Exception
								//gt.setTransactionTimeUTC( rs.getTimestamp( Integer.parseInt(row[GRID_CHAIN_INFO.TX_CREATED_ON.getcolNo()] ) ) );
								gt.setUserId(Integer.parseInt(row[GRID_CHAIN_INFO.TX_CREATED_USER_ID.getcolNo()]));
								gt.setUserEmail(row[GRID_CHAIN_INFO.TX_CREATED_USER_EMAIL.getcolNo()]);
								gt.setComment(row[GRID_CHAIN_INFO.TX_CREATED_COMMENT.getcolNo()]);
								gts.add(gt);
				
							}
							
							if (row[GRID_CHAIN_INFO.TX_ID_LOCKED.getcolNo()] != null )
							{
								if ( !TxIds.contains(Integer.parseInt(row[GRID_CHAIN_INFO.TX_ID_LOCKED.getcolNo()])) )
								{
//									System.out.println("Gridchain : Adding Locked txId:" +  row[GRID_CHAIN_INFO.TX_ID_LOCKED.getcolNo()]);
									TxIds.add(Integer.parseInt(row[GRID_CHAIN_INFO.TX_ID_LOCKED.getcolNo()])) ;

									gt = new Transaction();
									//gt.setCreatedOn(java.sql.Timestamp.valueOf(row[GRID_CHAIN_INFO.TX_LOCKED_ON.getcolNo()]).getTime()) ;
									gt.setTxTimeUTC(java.sql.Timestamp.valueOf(row[GRID_CHAIN_INFO.TX_LOCKED_ON.getcolNo()]));
									gt.setDescription(row[GRID_CHAIN_INFO.TX_LOCKED_DESCRIPTION.getcolNo()]);
									gt.setTxId(Integer.parseInt(row[GRID_CHAIN_INFO.TX_ID_LOCKED.getcolNo()]));
									gt.setUserId(Integer.parseInt(row[GRID_CHAIN_INFO.TX_LOCKED_USER_ID.getcolNo()]));
									gt.setUserEmail(row[GRID_CHAIN_INFO.TX_LOCKED_USER_EMAIL.getcolNo()]);
									gt.setComment(row[GRID_CHAIN_INFO.TX_LOCKED_COMMENT.getcolNo()]);
									gts.add(gt);
								}
							}

						}
						rs.close();						
						gc.setGridInfo(gi);
						break;
						
					case 1:			//ColumnChain
						System.out.println("Processing " + rsCount + "th result");
						nCol = rs.getMetaData().getColumnCount();
						row = new String[nCol] ;
						table = new ArrayList<>();
					    ArrayList<ColumnChain>  Arrcc = new ArrayList<ColumnChain>();
					    ColumnChain cc;
						while (rs.next()) {
							
//						    System.out.println("Inside gridchainGridIdGet.......inside Case 1 (ColumnChain) loop ......12");
							
						    row = new String[nCol];
						    for( int iCol = 1; iCol <= nCol; iCol++ ){
						            Object obj = rs.getObject( iCol );
						            row[iCol-1] = (obj == null) ?null:obj.toString();
						    }
						    cc = new ColumnChain();
						    cc.setColumnId(Integer.parseInt(row[COLUMN_CHAIN_INFO.COLUMN_ID.getcolNo()]));
						    cc.setColumnSource(row[COLUMN_CHAIN_INFO.COLUMN_SOURCE.getcolNo()]);
						    cc.setColumnType(row[COLUMN_CHAIN_INFO.COLUMN_TYPE.getcolNo()]);
						    cc.setColumnWidth(Integer.parseInt(row[COLUMN_CHAIN_INFO.COLUMN_WIDTH.getcolNo()]));
						    
						    //If Column is Active. DeletionTxId is null. -1 is set.
						    if (row[COLUMN_CHAIN_INFO.IS_ACTIVE.getcolNo()].equalsIgnoreCase("1"))
						    {
						    
						    	cc.setColumnCreationTxId(Integer.parseInt(row[COLUMN_CHAIN_INFO.CREATED_TX_ID.getcolNo()]));
						    	cc.setColumnDeletionTxId(-1);
							    cc.setOwnerEmail(row[COLUMN_CHAIN_INFO.CREATED_TX_USER_EMAIL.getcolNo()]);
							    cc.setOwnerUserId(Integer.parseInt(row[COLUMN_CHAIN_INFO.CREATED_TX_USER_ID.getcolNo()]));
						    }
					    	else
					    	{
							    //If Column is NOT Active. Both CreationTxId and DeletionTxId is set.
						    	cc.setColumnCreationTxId(Integer.parseInt(row[COLUMN_CHAIN_INFO.CREATED_TX_ID.getcolNo()]));
						    	cc.setColumnDeletionTxId(Integer.parseInt(row[COLUMN_CHAIN_INFO.DELETED_TX_ID.getcolNo()]));
							    cc.setOwnerEmail(null);
							    cc.setOwnerUserId(null);
					    	}
						    
						    cc.setGridId(Integer.parseInt(row[COLUMN_CHAIN_INFO.GRID_ID.getcolNo()]));
						    cc.setIsActive(row[COLUMN_CHAIN_INFO.IS_ACTIVE.getcolNo()].equalsIgnoreCase("1"));  
						    cc.setColumnName(row[COLUMN_CHAIN_INFO.COLUMN_NAME.getcolNo()]);
						    cc.setColumnSequenceNumber(Float.parseFloat(row[COLUMN_CHAIN_INFO.SEQUENCE_NUMBER.getcolNo()]));
						    Arrcc.add(cc);
						    
						    //COLUMN_CHAIN_INFO.CREATED_TX_ID
						    //COLUMN_CHAIN_INFO.DELETED_TX_ID
						    if (row[COLUMN_CHAIN_INFO.IS_ACTIVE.getcolNo()].equalsIgnoreCase("1"))
							{
								if ( !TxIds.contains(Integer.parseInt(row[COLUMN_CHAIN_INFO.CREATED_TX_ID.getcolNo()])) )
								{
									System.out.println("Columnchain : Adding txId:" +  row[COLUMN_CHAIN_INFO.CREATED_TX_ID.getcolNo()]);
	
									TxIds.add(Integer.parseInt(row[COLUMN_CHAIN_INFO.CREATED_TX_ID.getcolNo()])) ;
									
									gt = new Transaction();
	
									System.out.println("row[COLUMN_CHAIN_INFO.CREATED_ON.getcolNo()]) : " + row[COLUMN_CHAIN_INFO.CREATED_ON.getcolNo()]);
		
									//gt.setCreatedOn(java.sql.Timestamp.valueOf(row[COLUMN_CHAIN_INFO.CREATED_ON.getcolNo()]).getTime()) ;
									
									gt.setTxTimeUTC(java.sql.Timestamp.valueOf(row[COLUMN_CHAIN_INFO.CREATED_ON.getcolNo()])); // this adds long as 1540453699310
									gt.setDescription(row[COLUMN_CHAIN_INFO.CREATED_TX_DESC.getcolNo()]);
									gt.setTxId(Integer.parseInt(row[COLUMN_CHAIN_INFO.CREATED_TX_ID.getcolNo()]));
									gt.setUserId(Integer.parseInt(row[COLUMN_CHAIN_INFO.CREATED_TX_USER_ID.getcolNo()]));
									gt.setUserEmail(row[COLUMN_CHAIN_INFO.CREATED_TX_USER_EMAIL.getcolNo()]);
									gt.setComment(row[COLUMN_CHAIN_INFO.CREATED_TX_COMMENT.getcolNo()]);
									gts.add(gt);
								}
							}
						    
						    if (row[COLUMN_CHAIN_INFO.IS_ACTIVE.getcolNo()].equalsIgnoreCase("0"))
							{
								if ( !TxIds.contains(Integer.parseInt(row[COLUMN_CHAIN_INFO.DELETED_TX_ID.getcolNo()])) )
								{
									System.out.println("Columnchain : Adding Deleted txId:" +  row[COLUMN_CHAIN_INFO.DELETED_TX_ID.getcolNo()]);

									TxIds.add(Integer.parseInt(row[COLUMN_CHAIN_INFO.DELETED_TX_ID.getcolNo()])) ;

									gt = new Transaction();
									
									//gt.setCreatedOn(java.sql.Timestamp.valueOf(row[COLUMN_CHAIN_INFO.DELETED_ON.getcolNo()]).getTime()) ;
									gt.setTxTimeUTC(java.sql.Timestamp.valueOf(row[COLUMN_CHAIN_INFO.DELETED_ON.getcolNo()]));
									gt.setDescription(row[COLUMN_CHAIN_INFO.DELETED_TX_DESC.getcolNo()]);
									gt.setTxId(Integer.parseInt(row[COLUMN_CHAIN_INFO.DELETED_TX_ID.getcolNo()]));
									gt.setUserId(Integer.parseInt(row[COLUMN_CHAIN_INFO.DELETED_TX_USER_ID.getcolNo()]));
									gt.setUserEmail(row[COLUMN_CHAIN_INFO.DELETED_TX_USER_EMAIL.getcolNo()]);
									gt.setComment(row[COLUMN_CHAIN_INFO.DELETED_TX_COMMENT.getcolNo()]);
									gts.add(gt);
								}
							}
						}
						rs.close();			
						gc.setGridColumns(Arrcc);
						break;

					case 2:			//RowChain
						System.out.println("Processing " + rsCount + "th result");
						nCol = rs.getMetaData().getColumnCount();
						row = new String[nCol] ;
						table = new ArrayList<>();
					    ArrayList<RowChain>  Arrrc = new ArrayList<RowChain>();
					    RowChain rc;
						while (rs.next()) {

//						    System.out.println("Inside gridchainGridIdGet.......inside Case 2 (RowChain) loop ......13");
							
						    row = new String[nCol];
						    for( int iCol = 1; iCol <= nCol; iCol++ ){
						            Object obj = rs.getObject( iCol );
						            row[iCol-1] = (obj == null) ?null:obj.toString();
						    }
						    rc = new RowChain();
						    
						    if (row[ROW_CHAIN_INFO.IS_ACTIVE.getcolNo()].equalsIgnoreCase("1"))
						    {
						    	rc.setRowCreationTxId(Integer.parseInt(row[ROW_CHAIN_INFO.CREATED_TX_ID.getcolNo()]));
						    	rc.setRowDeletionTxId(-1);
						    }
						    else
						    {
						    	rc.setRowCreationTxId(Integer.parseInt(row[ROW_CHAIN_INFO.CREATED_TX_ID.getcolNo()]));
						    	rc.setRowDeletionTxId(Integer.parseInt(row[ROW_CHAIN_INFO.DELETED_TX_ID.getcolNo()]));
						    }
						    
						    rc.setGridId(Integer.parseInt(row[ROW_CHAIN_INFO.GRID_ID.getcolNo()]));
						    rc.setIsActive(row[ROW_CHAIN_INFO.IS_ACTIVE.getcolNo()].equalsIgnoreCase("1"));
						    rc.setOwnerEmail(row[ROW_CHAIN_INFO.OWNER_EMAIL.getcolNo()]);
						    rc.setOwnerUserId(Integer.parseInt(row[ROW_CHAIN_INFO.OWNER_ID.getcolNo()]));
						    rc.setRowId(Integer.parseInt(row[ROW_CHAIN_INFO.BW_ROW_ID.getcolNo()]));
						    rc.setRowSequenceNumber(Float.parseFloat(row[ROW_CHAIN_INFO.SEQUENCE_NUMBER.getcolNo()]));
							Arrrc.add(rc);
							
							//ROW_CHAIN_INFO.CREATE_TX_ID
							//ROW_CHAIN_INFO.DELETE_TX_ID
							if ( !TxIds.contains(Integer.parseInt(row[ROW_CHAIN_INFO.CREATED_TX_ID.getcolNo()])) )
							{
								System.out.println("Rowchain : Adding txId:" +  row[ROW_CHAIN_INFO.CREATED_TX_ID.getcolNo()]);

								TxIds.add(Integer.parseInt(row[ROW_CHAIN_INFO.CREATED_TX_ID.getcolNo()])) ;

								gt = new Transaction();

								//gt.setCreatedOn(java.sql.Timestamp.valueOf(row[ROW_CHAIN_INFO.CREATED_ON.getcolNo()]).getTime()) ;
								gt.setTxTimeUTC(java.sql.Timestamp.valueOf(row[ROW_CHAIN_INFO.CREATED_ON.getcolNo()]));
								gt.setDescription(row[ROW_CHAIN_INFO.CREATED_DESCRIPTION.getcolNo()]);
								gt.setTxId(Integer.parseInt(row[ROW_CHAIN_INFO.CREATED_TX_ID.getcolNo()]));
								gt.setUserId(Integer.parseInt(row[ROW_CHAIN_INFO.CREATED_USER_ID.getcolNo()]));
								gt.setUserEmail(row[ROW_CHAIN_INFO.CREATED_USER_EMAIL.getcolNo()]);
								gt.setComment(row[ROW_CHAIN_INFO.CREATED_COMMENT.getcolNo()]);
								gts.add(gt);								
							}
							
							if (row[ROW_CHAIN_INFO.IS_ACTIVE.getcolNo()].equalsIgnoreCase("0"))
							{
								if ( !TxIds.contains(Integer.parseInt(row[ROW_CHAIN_INFO.DELETED_TX_ID.getcolNo()])) )
								{
									System.out.println("Rowchain : Adding Deleted txId:" +  row[ROW_CHAIN_INFO.DELETED_TX_ID.getcolNo()]);

									TxIds.add(Integer.parseInt(row[ROW_CHAIN_INFO.DELETED_TX_ID.getcolNo()])) ;

									gt = new Transaction();

									//gt.setCreatedOn(java.sql.Timestamp.valueOf(row[ROW_CHAIN_INFO.DELETED_ON.getcolNo()]).getTime()) ;
									gt.setTxTimeUTC(java.sql.Timestamp.valueOf(row[ROW_CHAIN_INFO.DELETED_ON.getcolNo()]));
									gt.setDescription(row[ROW_CHAIN_INFO.DELETED_TX_DESCRIPTION.getcolNo()]);
									gt.setTxId(Integer.parseInt(row[ROW_CHAIN_INFO.DELETED_TX_ID.getcolNo()]));
									gt.setUserId(Integer.parseInt(row[ROW_CHAIN_INFO.DELETED_TX_USER_ID.getcolNo()]));
									gt.setUserEmail(row[ROW_CHAIN_INFO.DELETED_TX_USER_EMAIL.getcolNo()]);
									gt.setComment(row[ROW_CHAIN_INFO.DELETED_TX_COMMENT.getcolNo()]);
									gts.add(gt);									
								}
							}
						}
						rs.close();			
						gc.setGridRows(Arrrc);
						break;

					case 3:			//CellChain
						System.out.println("Processing " + rsCount + "th result");
						nCol = rs.getMetaData().getColumnCount();
						row = new String[nCol] ;
						table = new ArrayList<>();
					    Arrclc = new ArrayList<CellChain>();
					    CellChain clc;
						while (rs.next()) {

//						    System.out.println("Inside gridchainGridIdGet.......inside Case 3 (CellChain) loop ......14");
							
							row = new String[nCol];
						    for( int iCol = 1; iCol <= nCol; iCol++ ){
						            Object obj = rs.getObject( iCol );
						            row[iCol-1] = (obj == null) ?null:obj.toString();
						    }
						    clc = new CellChain();
						    clc.setCellId(Integer.parseInt(row[CELL_CHAIN_INFO.BW_CELL_ID.getcolNo()]));
						    clc.setCellType(row[CELL_CHAIN_INFO.CELL_TYPE.getcolNo()]);
						    clc.setColumnId(Integer.parseInt(row[CELL_CHAIN_INFO.BW_COLUMN_ID.getcolNo()]));
						    //clc.setgridId(Integer.parseInt(row[CELL_CHAIN_INFO.GRID_ID.getcolNo()]));
						    clc.setIsActive(row[CELL_CHAIN_INFO.IS_ACTIVE.getcolNo()].equalsIgnoreCase("1"));
						    clc.setRowId(Integer.parseInt(row[CELL_CHAIN_INFO.BW_ROW_ID.getcolNo()]));
						    //clc.setCellHistory();			// Set after processing StringValues
						    //clc.setCellStatus();			// Set after processing CellStatus
							Arrclc.add( clc);
							cellIds.add(Integer.parseInt(row[CELL_CHAIN_INFO.BW_CELL_ID.getcolNo()]));
							
							//CELL_CHAIN_INFO.TX_ID
							if ( !TxIds.contains(Integer.parseInt(row[CELL_CHAIN_INFO.TX_ID.getcolNo()])) )
							{
								System.out.println("Cellchain : Adding txId:" +  row[CELL_CHAIN_INFO.TX_ID.getcolNo()]);

								TxIds.add(Integer.parseInt(row[CELL_CHAIN_INFO.TX_ID.getcolNo()])) ;
								
								gt = new Transaction();
								
								//gt.setCreatedOn(java.sql.Timestamp.valueOf(row[CELL_CHAIN_INFO.TX_CREATED_ON.getcolNo()]).getTime()) ;
								gt.setTxTimeUTC(java.sql.Timestamp.valueOf(row[CELL_CHAIN_INFO.TX_CREATED_ON.getcolNo()]));
								gt.setDescription(row[CELL_CHAIN_INFO.TX_DESCRIPTION.getcolNo()]);
								gt.setTxId(Integer.parseInt(row[CELL_CHAIN_INFO.TX_ID.getcolNo()]));
								gt.setUserId(Integer.parseInt(row[CELL_CHAIN_INFO.TX_USER_ID.getcolNo()]));
								gt.setUserEmail(row[CELL_CHAIN_INFO.TX_USER_EMAIL.getcolNo()]);
								gt.setComment(row[CELL_CHAIN_INFO.TX_COMMENT.getcolNo()]);
								gts.add(gt);								
							}
						}
						rs.close();			
						gc.setGridCells(Arrclc);
						break;

					case 4:			//StringValues
						System.out.println("Processing " + rsCount + "th result");
						nCol = rs.getMetaData().getColumnCount();
						row = new String[nCol] ;
						table = new ArrayList<>();
						
						ArrayList<CellTransaction> Arrct = new ArrayList<CellTransaction>();
						CellTransaction ct = new CellTransaction();
						
					    //ArrayList<StringValues>  Arrsv = new ArrayList<StringValues>();
					    //StringValues sv;
					    int currCellId = -1;
					    boolean blnStringValuesFound = false;
					    
						while (rs.next()) {
							
//						    System.out.println("Inside gridchainGridIdGet.......inside Case 4 (StringValues) loop ......15");
							
							blnStringValuesFound = true;
						    row = new String[nCol];
						    for( int iCol = 1; iCol <= nCol; iCol++ ){
						            Object obj = rs.getObject( iCol );
						            row[iCol-1] = (obj == null) ?null:obj.toString();
						    }
						
						    if (currCellId != Integer.parseInt(row[STRING_VALUES.BW_CELL_ID.getcolNo()]))
						    {
						    	if (currCellId != -1)
						    	{
						    		//Set Arrsv to CellChain object of the CellId
						    		cellIndex = cellIds.indexOf(currCellId);
						    		currCC = Arrclc.get(cellIndex);
						    		currCC.setCellHistory(Arrct);
						    		Arrclc.set(cellIndex, currCC);
						    	}
						    	//Setting currCellId to new one. Resetting Arrsv for new cellId
					    		currCellId = Integer.parseInt(row[STRING_VALUES.BW_CELL_ID.getcolNo()]);
					    		Arrct = new ArrayList<CellTransaction>();
						    }
						    ct = new CellTransaction();
						    ct.setCellId(Integer.parseInt(row[STRING_VALUES.BW_CELL_ID.getcolNo()]));
						    ct.setFormulaId( (row[STRING_VALUES.FORMULA_ID.getcolNo()]==null)?null: Integer.parseInt(row[STRING_VALUES.FORMULA_ID.getcolNo()] ));
						    ct.setStringValue(row[STRING_VALUES.STRING_VALUE.getcolNo()]);
						    ct.setStringValueId(Integer.parseInt(row[STRING_VALUES.BW_STRING_VALUE_ID.getcolNo()]));
						    ct.setTxId(Integer.parseInt(row[STRING_VALUES.TX_ID.getcolNo()]));
						    
						    Arrct.add(ct);
						    
						    //STRING_VALUES.TX_ID
							if ( !TxIds.contains(Integer.parseInt(row[STRING_VALUES.TX_ID.getcolNo()])) )
							{
								System.out.println("StringValues : Adding txId:" +  row[STRING_VALUES.TX_ID.getcolNo()]);

								TxIds.add(Integer.parseInt(row[STRING_VALUES.TX_ID.getcolNo()])) ;
								
								gt = new Transaction();

								//gt.setCreatedOn(java.sql.Timestamp.valueOf(row[STRING_VALUES.TX_CREATED_ON.getcolNo()]).getTime()) ;
								gt.setTxTimeUTC(java.sql.Timestamp.valueOf(row[STRING_VALUES.TX_CREATED_ON.getcolNo()]));
								gt.setDescription(row[STRING_VALUES.TX_DESCRIPTION.getcolNo()]);
								gt.setTxId(Integer.parseInt(row[STRING_VALUES.TX_ID.getcolNo()]));
								gt.setUserId(Integer.parseInt(row[STRING_VALUES.TX_CREATED_USER_ID.getcolNo()]));
								gt.setUserEmail(row[STRING_VALUES.TX_CREATED_USER_EMAIL.getcolNo()]);
								gt.setComment(row[STRING_VALUES.TX_COMMENT.getcolNo()]);
								gts.add(gt);								
							}
						}
						
						if (blnStringValuesFound)
						{
							//For last cellId records 
				    		cellIndex = cellIds.indexOf(currCellId);
				    		currCC = Arrclc.get(cellIndex);
				    		currCC.setCellHistory(Arrct);
				    		Arrclc.set(cellIndex, currCC);
						}
			    		rs.close();			
						gc.setGridCells(Arrclc);
						break;
					
					case 5:			//CellStatus
						System.out.println("Processing " + rsCount + "th result");
						nCol = rs.getMetaData().getColumnCount();
						row = new String[nCol] ;
						table = new ArrayList<>();
					    ArrayList<CellStatus>  Arrcs = new ArrayList<CellStatus>();
					    CellStatus cls;
					    currCellId = -1;
					    boolean blnCellStatusFound = false;
						while (rs.next()) {
							
//						    System.out.println("Inside gridchainGridIdGet.......inside Case 5 (CellStatus) loop ......16");
							
							blnCellStatusFound = true;
							row = new String[nCol];
						    for( int iCol = 1; iCol <= nCol; iCol++ ){
						            Object obj = rs.getObject( iCol );
						            row[iCol-1] = (obj == null) ?null:obj.toString();
						    }
						    
						    if (currCellId != Integer.parseInt(row[CELL_STATUS.BW_CELL_ID.getcolNo()]))
						    {
						    	if (currCellId != -1)
						    	{
						    		//Set Arrsv to CellChain object of the CellId
						    		cellIndex = cellIds.indexOf(currCellId);
						    		currCC = Arrclc.get(cellIndex);
						    		currCC.setCellStatus(Arrcs);
						    		Arrclc.set(cellIndex, currCC);
						    	}
						    	//Setting currCellId to new one. Resetting Arrsv for new cellId
					    		currCellId = Integer.parseInt(row[CELL_STATUS.BW_CELL_ID.getcolNo()]);
					    		Arrcs = new ArrayList<CellStatus>();
						    }

						    cls = new CellStatus();
						    cls.setCellId(Integer.parseInt(row[CELL_STATUS.BW_CELL_ID.getcolNo()]));
						    cls.setCellStatusId(Integer.parseInt(row[CELL_STATUS.BW_CELL_STATUS_ID.getcolNo()]));
						    //(row[STRING_VALUES.FORMULA_ID.getcolNo()]==null)?null: Integer.parseInt(row[STRING_VALUES.FORMULA_ID.getcolNo()] ));						    
						    
						    System.out.println("row[CELL_STATUS.IS_ACTIVE.getcolNo()] ------->>>"  + row[CELL_STATUS.IS_ACTIVE.getcolNo()]);
						    cls.setIsActive( (row[CELL_STATUS.IS_ACTIVE.getcolNo()].equals("true"))? true: false);
						    cls.setTxId(Integer.parseInt(row[CELL_STATUS.TX_ID.getcolNo()]));
						    
						    Arrcs.add(cls);
						    
						    //CELL_STATUS.TX_ID
							if ( !TxIds.contains(Integer.parseInt(row[CELL_STATUS.TX_ID.getcolNo()])) )
							{
								System.out.println("CellStatus : Adding txId:" +  row[CELL_STATUS.TX_ID.getcolNo()]);

								TxIds.add(Integer.parseInt(row[CELL_STATUS.TX_ID.getcolNo()])) ;
								
								gt = new Transaction();

								//gt.setCreatedOn(java.sql.Timestamp.valueOf(row[CELL_STATUS.TX_CREATED_ON.getcolNo()]).getTime()) ;
								gt.setTxTimeUTC(java.sql.Timestamp.valueOf(row[CELL_STATUS.TX_CREATED_ON.getcolNo()]));
								gt.setDescription(row[CELL_STATUS.TX_DESCRIPTION.getcolNo()]);
								gt.setTxId(Integer.parseInt(row[CELL_STATUS.TX_ID.getcolNo()]));
								gt.setUserId(Integer.parseInt(row[CELL_STATUS.TX_CREATED_USER_ID.getcolNo()]));
								gt.setUserEmail(row[CELL_STATUS.TX_CREATED_USER_EMAIL.getcolNo()]);
								gt.setComment(row[CELL_STATUS.TX_COMMENT.getcolNo()]);
								gts.add(gt);								
							}
						}
						
						if (blnCellStatusFound)
						{
							//for last cellId record
				    		cellIndex = cellIds.indexOf(currCellId);
				    		currCC = Arrclc.get(cellIndex);
				    		currCC.setCellStatus(Arrcs);
				    		Arrclc.set(cellIndex, currCC);
						}
	
			    		rs.close();			
						gc.setGridCells(Arrclc);
						break;

					case 6:			//FormualValues
						System.out.println("Processing " + rsCount + "th result FormualValues");
						nCol = rs.getMetaData().getColumnCount();
						row = new String[nCol] ;
						table = new ArrayList<>();
					    ArrayList<FormulaValue>  Arrfmlas = new ArrayList<FormulaValue>();
					    FormulaValue fmla;
						while (rs.next()) {
							
//						    System.out.println("Inside gridchainGridIdGet.......inside Case 6 (FormualValues) loop ......17");
							
						    row = new String[nCol];
						    for( int iCol = 1; iCol <= nCol; iCol++ ){
						            Object obj = rs.getObject( iCol );
						            row[iCol-1] = (obj == null) ?null:obj.toString();
						    }

						    fmla = new FormulaValue();
						    fmla.setFormulaId(Integer.parseInt(row[FORMULA_VALUE.FORMULA_ID.getcolNo()]));
						    fmla.setFormula(row[FORMULA_VALUE.FORMULA.getcolNo()]);
						    fmla.setFormulaIndex(row[FORMULA_VALUE.FORMULA_INDEX.getcolNo()]);
						    fmla.setTxId(Integer.parseInt(row[FORMULA_VALUE.TX_ID.getcolNo()]));
						    
						    Arrfmlas.add(fmla);
						    
						    //FORMULA_VALUE.TX_ID
							if ( !TxIds.contains(Integer.parseInt(row[FORMULA_VALUE.TX_ID.getcolNo()])) )
							{
								System.out.println("FormulaValues : Adding txId:" +  row[FORMULA_VALUE.TX_ID.getcolNo()]);

								TxIds.add(Integer.parseInt(row[FORMULA_VALUE.TX_ID.getcolNo()])) ;
								
								gt = new Transaction();

								//gt.setCreatedOn(java.sql.Timestamp.valueOf(row[FORMULA_VALUE.TX_CREATED_ON.getcolNo()]).getTime()) ;
								gt.setTxTimeUTC(java.sql.Timestamp.valueOf(row[FORMULA_VALUE.TX_CREATED_ON.getcolNo()]));
								gt.setDescription(row[FORMULA_VALUE.TX_DESCRIPTION.getcolNo()]);
								gt.setTxId(Integer.parseInt(row[FORMULA_VALUE.TX_ID.getcolNo()]));
								gt.setUserId(Integer.parseInt(row[FORMULA_VALUE.TX_CREATED_USER_ID.getcolNo()]));
								gt.setUserEmail(row[FORMULA_VALUE.TX_CREATED_USER_EMAIL.getcolNo()]);
								gt.setComment(row[FORMULA_VALUE.TX_COMMENT.getcolNo()]);
								gts.add(gt);								
							}

						    
						}
						rs.close();			
						gc.setGridFormulas(Arrfmlas);
						break;
					}
					rsCount++;
					rs.close();
					System.out.println("rsCount ----------" + rsCount);
				}
//			    System.out.println("Inside gridchainGridIdGet getMoreResults()....... before  ......18");
				results = preparedstatement.getMoreResults(); 
//			    System.out.println("Inside gridchainGridIdGet getMoreResults()....... after   ......19");
		    } while (results);
		    gc.setGridTransactions(gts);

//		    System.out.println("Inside gridchainGridIdGet before preparedstatement.close() ......20");
			rs.close();
		    preparedstatement.close();

//		    System.out.println("Inside gridchainGridIdGet after preparedstatement.close() ......20");
		    
		}
		catch( SQLException sql1 )
		{
			System.out.println("@#%#$^%$^&%&%&%&%..............SQLException occured .....");
			System.out.println("Message : " + sql1.getMessage() + "ErrorCode : " + sql1.getErrorCode()  + "SQLState : " +  sql1.getSQLState() );
		}
		finally
		{
//			System.out.println("@#%#$^%$^&%&%&%&%..............inside finally() of gridchainGridIdGet() ");
		}
//		System.out.println("Inside GridchainManagement.java-> gridchainGridIdGet ----- gc:" + gc);
		return gc;
	}


    //@GET
    //@Path("/{gridId}/transactions")

	
	public static ArrayList<io.swagger.model.GridTransaction>  gridchainGridIdTransactionsGet(Integer gridId,  String reportType,  Long startDate, Long endDate, Long localTimeAfter111970, Integer startTxId, Integer endTxId,  Integer importTxId, String viewPref, ArrayList<ErrorRequestObject> ErrResps, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode) 
    {

		ArrayList<io.swagger.model.GridTransaction> txs = new ArrayList<io.swagger.model.GridTransaction>();
		io.swagger.model.GridTransaction tx ;

		/*    	long difference_in_MiliSec;

		Calendar cal_GMT = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		long server_Millis = cal_GMT.getTimeInMillis();
		difference_in_MiliSec = local_offset - server_Millis;
		System.out.println("Local Server (gmt) in miliSeconds is " + server_Millis );
		System.out.println("The difference in Server and Clietnis " + (local_offset - server_Millis ));
  */  	
    	// TODO Auto-generated method stub
		ErrorRequestObject erb;
		
		// get the connection
    	Connection connection = null;

		int nhId = -1;
		int memberId = -1;
		
		connection = bwcon.getConnection();
		memberId = memberNh.get(0);
		nhId = memberNh.get(1);
		int userId = bwcon.getUserId();
		//String viewPref = "LATEST";

	    com.boardwalk.database.Transaction ts, t;
	    
		//long endDate = startTime - difference_in_MiliSec;
		//long startDate = Long.parseLong(endTime) - difference_in_MiliSec;
		try
		{

			TableInfo tinfo = TableManager.getTableInfo(connection, userId, gridId);
			
//			System.out.println("tinfo : " + tinfo.getTableId() );
			
			if (tinfo == null)
			{
	        	erb = new ErrorRequestObject();
	        	erb.setError("GridId not found.");
	        	erb.setPath("GridchainManagement.gridchainGridIdTransactionsGet:: TableManager.getTableInfo returns Null");
				erb.setProposedSolution("Grid Details for GridId not found. You must provide an existing GridId.");
	        	ErrResps.add(erb);
				statusCode.add(404);			//404: GridId not found
				return txs;
			}
			
			Hashtable<?, ?> transactionList = null; ;

			if (reportType.toUpperCase().equals("DURATION"))
			{
				transactionList = TableManager.getTransactionList(connection,
					      gridId,
					      -1,
					      -1,
					      startDate,
					      endDate,
					      userId,
					      nhId,
					      viewPref,
					      true);
			}
			else if (reportType.toUpperCase().equals("AFTERIMPORT"))
			{
				transactionList = TableManager.getTransactionListAfterImport(connection,
					      gridId.intValue(),
					      (int) importTxId,
					      userId,
					      nhId,
					      viewPref);
			}
			else if (reportType.toUpperCase().equals("BETWEENTXS"))
			{
				transactionList = TableManager.getTransactionList(connection,
					      gridId.intValue(),
					      startTxId,
					      endTxId,
					      -1,
					      -1,
					      userId,
					      nhId,
					      viewPref,
					      true);
			}

			System.out.println("Number of transactions = " + transactionList.size());

			Vector<?> tvec = new Vector<Object>(transactionList.keySet());
			//Collections.sort(tvec);
		    Iterator<?> i = tvec.iterator();
		    String descr;
	    
		    Date longDate ;
		    while (i.hasNext())
			{
		    	Integer tid = (Integer)i.next();
		    	System.out.println("TransactionList..ID : " + tid);

			    Vector<?>  vt = (Vector<?>)transactionList.get(tid);
			    t = (com.boardwalk.database.Transaction)vt.elementAt(0);
			    Iterator<?> j = vt.iterator();
			    String checkImage = "";

			    tx = new io.swagger.model.GridTransaction();
			    
			    tx.setTxId(tid);
				tx.setBaselineAdded(false);
				tx.setCellUpdated(false);
				tx.setColumnAdded(false);
				tx.setFormulaUpdated(false);
				tx.setRowAdded(false);
				tx.setRowDeleted(false);

				tx.setComment(t.getComment());
				
				SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss a");
				//Calendar gmtCal = Calendar.getInstance();
				
				Calendar utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
				utcCal.setTimeInMillis(t.getCreatedOnTime());

				System.out.println("utcCal.getTime() :" + utcCal.getTime());
				
				// remove later
				//long server_Millis = utcCal.getTimeInMillis();			//ON UTC
				//long difference_in_MiliSec = (localTimeAfter111970 * 1000) - server_Millis;	//This is Offset of Local machine. i.e. India is +5:30 GMT in milliseconds.
				//System.out.println("server_Millis : " + server_Millis);
				//System.out.println("localTimeAfter111970 : " + localTimeAfter111970 * 1000);
				//System.out.println("difference_in_MiliSec : " + difference_in_MiliSec);
				// remove later
				
				//t.getCreatedOnTime();
				
				tx.setTxCreationTimeUTC(utcCal.getTime());
				//tx.setTxCreationTimeUTC( t.getCreatedOnTime());
				//tx.setTxTimeUTC(java.sql.Timestamp.valueOf(row[ROW_CHAIN_INFO.CREATED_ON.getcolNo()]));
				//java.sql.Timestamp.valueOf()
				
				tx.setTxCreatedByEmail(t.getCreatedByUserAddress());

//				SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss a");

//				Calendar gmtCal = Calendar.getInstance();
//				gmtCal.setTimeInMillis(t.getCreatedOnTime());
				
//				java.util.Date  theLocalServerConversionDate =  gmtCal.getTime();
//			    String strTheLocalServerConversionDate = sdfDate.format(theLocalServerConversionDate);
//				System.out.println("Setting strTheLocalServerConversionDate with Format ^&^&^&^&^&^&^&^&^&^ : " + strTheLocalServerConversionDate);
				
//				tx.setCreatedOnTime(sdfDate.format(theLocalServerConversionDate));
				
//				Calendar gmtaCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
				//after considering Client's locale difference to get GMT Time
//				gmtaCal.setTimeInMillis(t.getCreatedOnTime() - difference_in_MiliSec);
//				java.util.Date  theGMTConversionDate =  gmtaCal.getTime();
//			    String strTheGMTConversionDate = sdfDate.format(theGMTConversionDate);
//				System.out.println("Setting strTheGMTConversionDate with Format ^&^&^&^&^&^&^&^&^&^ : " + strTheGMTConversionDate);
				
//				tx.setCreatedOnTimeGMT(sdfDate.format(theGMTConversionDate));

				
			    while (j.hasNext())
			    {
			    	ts = (com.boardwalk.database.Transaction)j.next();
			    	descr = ts.getDescription();
			    	//System.out.println("descr=" + descr);
			    	if (descr.toUpperCase().startsWith("ROWADD"))
			    		tx.setRowAdded(true);
			    	else if (descr.toUpperCase().startsWith("ROWDEL"))
			    		tx.setRowDeleted(true);
			    	else if (descr.toUpperCase().startsWith("COLADD"))
						tx.setColumnAdded(true);
			    	else if (descr.toUpperCase().startsWith("CELLUPD"))
						tx.setCellUpdated(true);
			    	else if (descr.toUpperCase().startsWith("FRMUPD"))
						tx.setFormulaUpdated(true);
			    	else if (descr.toUpperCase().startsWith("BLNADD"))
						tx.setBaselineAdded(true);
			    }
				txs.add(tx);			    	
				System.out.println("End of vt.iterator");
			}
			System.out.println("End of tvec.iterator");
			statusCode.add(200);			//200 : Success. Returns txs
	    	return txs;

		}
		catch (SQLException sql)
		{
			sql.printStackTrace();
    		System.out.println("SQLException in GridManagement.gridGridIdTransactionsGet::TableManager.getTransactionList OR TableManager.getTransactionListAfterImport");
			erb = new ErrorRequestObject();
			erb.setError("SQLException:" + sql.getErrorCode() + ", Cause:"+ sql.getMessage());
			erb.setPath("GridManagement.gridGridIdTransactionsGet::TableManager.getTransactionList OR TableManager.getTransactionListAfterImport");
			erb.setProposedSolution("Contact System Administrator");
			ErrResps.add(erb);
			statusCode.add(500);			//500 : Server Error. SQLException thrown fromTableManager.getTransactionList OR TableManager.getTransactionListAfterImport
			return txs;
		}
		catch (SystemException s)
		{
			System.out.println("SystemException in GridManagement.gridGridIdTransactionsGet::TableManager.getTableInfo");
			s.printStackTrace();
			erb = new ErrorRequestObject();
			erb.setError("SystemException: " + s.getErrorMessage());
			erb.setPath("GridManagement.gridGridIdTransactionsGet::TableManager.getTableInfo");
			erb.setProposedSolution(s.getPotentialSolution());
			ErrResps.add(erb);
			statusCode.add(500);			//500 : Server Error. SystemException thrown from GridManagement.gridGridIdTransactionsGet::TableManager.getTableInfo
			return txs;
		}
		finally
		{
		  try
		  {
			if ( connection != null )
				connection.close();
		  }
		  catch ( SQLException sql )
		  {
			sql.printStackTrace();
		  }
		}
    }
	

}
