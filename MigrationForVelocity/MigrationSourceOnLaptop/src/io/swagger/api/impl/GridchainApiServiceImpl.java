package io.swagger.api.impl;

import io.swagger.api.*;
import io.swagger.model.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import io.swagger.api.NotFoundException;
import boardwalk.connection.BoardwalkConnection;
import boardwalk.rest.GridManagement;
import boardwalk.rest.GridchainManagement;
import boardwalk.rest.bwAuthorization;

import javax.validation.constraints.NotNull;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

public class GridchainApiServiceImpl extends GridchainApiService {
    @Override

    public Response gridchainGridIdGet(Integer gridId, @NotNull String viewPref, @NotNull String chainWindow, String activityPeriod, Long startDate, Long endDate, Long localTimeAfter111970, Integer startTxId, Integer endTxId, String filter,SecurityContext securityContext, String authBase64String) throws NotFoundException {
    
        // do some magic!
    	StringBuffer invReqMsg = new StringBuffer(500);

    	BoardwalkConnection bwcon = null;
		ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();
    	
    	ArrayList <ErrorRequestObject> erbs  = new ArrayList<ErrorRequestObject>();
    	ErrorRequestObject erb;

    	ArrayList <RequestErrorInfo> reqeis = new ArrayList<RequestErrorInfo>();
    	RequestErrorInfo reqei = new RequestErrorInfo();

    	ArrayList <ResponseErrorInfo> reseis = new ArrayList<ResponseErrorInfo>();
    	ResponseErrorInfo resei ;

    	GridChain gc;
    	
 		if (authBase64String == null)
		{	
 			erbs  = new ArrayList<ErrorRequestObject>();
 			
			erb = new ErrorRequestObject(); 
			erb.setError("Missing Authorization in Header"); 
			erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);

			return Response.status(401).entity(erbs).build();		//401: Missing Authorization
			
/*			invReqMsg.append("Authorization in Header not Found. ");
			reqei = new RequestErrorInfo();
			reqei.setErrorMessage("Authorization in Header not Found");
			reqei.setErrorDetails( erbs);
			reqeis.add(reqei);  */		
		}
		else
		{
			ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
	    	//Connection connection = null;
			
			bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
			if (!ErrResps.isEmpty())
			{
    			return Response.status(401).entity(ErrResps).build();		//401: Authorization Failed
    			
/*				reqei = new RequestErrorInfo();
				reqei.setErrorMessage("Authentication Failed");
				reqei.setErrorDetails( ErrResps);
				reqeis.add(reqei);
				return Response.status(401).entity(reqeis).build();		//401: Authorization Failed  */			
    		}
		} 		

		if (gridId <= 0)	
		{	
			erbs  = new ArrayList<ErrorRequestObject>();
			
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("gridId"); 
			erb.setProposedSolution("You must enter an Existing Grid ID. It should be a Positive Number.");
			erbs.add(erb);

/*			invReqMsg.append("Negative GridId. ");
			
			reqei = new RequestErrorInfo();
			reqei.setErrorMessage("Negative GridId");
			reqei.setErrorDetails( erbs);
			reqeis.add(reqei);  */		
		}

		if (chainWindow == null)
		{
			erb = new ErrorRequestObject();
			erb.setError("chainWindow is missing in GET Request");
			erb.setPath("chainWindow");
			erb.setProposedSolution("chainWindow is mandetory. Valid View values are [ LATESTTX | ALLTX | ASOFTX | BETWEENTX ]");
			erbs.add(erb);
		}
		else if (chainWindow.trim().equals("LATESTTX") || 
				chainWindow.trim().equals("ALLTX") || 
				chainWindow.trim().equals("ASOFTX") || 
				chainWindow.trim().equals("BETWEENTX"))
		{    			
			System.out.println("chainWindow : " + chainWindow);

			if (chainWindow.equals("LATESTTX") || chainWindow.equals("ALLTX"))
			{
				startTxId = -1;
				endTxId = -1;
			}
			
			if (chainWindow.equals("ASOFTX"))
			{
				startTxId = -1;
				if (endTxId == null)
				{
					erb = new ErrorRequestObject();
					erb.setError("endTxId is missing in GET Request");
					erb.setPath("endTxId");
					erb.setProposedSolution("endTxId is mandetory when chainWindow is ASOFTX.");
					erbs.add(erb);
				}				
				else if (endTxId <= 0)	
				{	
					erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("endTxId"); 
					erb.setProposedSolution("You must enter an Existing endTxId. It should be a Positive Number.");
					erbs.add(erb);
				}
			}
			if (chainWindow.equals("BETWEENTX"))
			{
				if (startTxId == null)
				{
					erb = new ErrorRequestObject();
					erb.setError("startTxId is missing in GET Request");
					erb.setPath("startTxId");
					erb.setProposedSolution("startTxId is mandetory when chainWindow is BETWEENTX.");
					erbs.add(erb);
				}				
				else if (startTxId <= 0)	
				{	
					erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("startTxId"); 
					erb.setProposedSolution("You must enter an Existing startTxId. It should be a Positive Number.");
					erbs.add(erb);
				}

				if (endTxId == null)
				{
					erb = new ErrorRequestObject();
					erb.setError("endTxId is missing in GET Request");
					erb.setPath("endTxId");
					erb.setProposedSolution("endTxId is mandetory when chainWindow is BETWEENTX.");
					erbs.add(erb);
				}				
				else if (endTxId <= 0)	
				{	
					erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("endTxId"); 
					erb.setProposedSolution("You must enter an Existing endTxId. It should be a Positive Number.");
					erbs.add(erb);
				}

				if (endTxId != null && endTxId != null)
				{
					if (endTxId < startTxId)	
					{	
						erb = new ErrorRequestObject(); erb.setError("endTxId < startTxId"); erb.setPath("endTxId,  startTxId"); 
						erb.setProposedSolution("You must enter an endTxId > startTxId.");
						erbs.add(erb);
					}
				}
			}
				
		}		
		else 
		{
			erb = new ErrorRequestObject();
			erb.setError("Invalid chainWindow in GET Request");
			erb.setPath("chainWindow");
			erb.setProposedSolution("chainWindow is mandetory. Valid chainWindow values are [ LATESTTX | ALLTX | ASOFTX | BETWEENTX ]");
			erbs.add(erb);
		}
		
		if (erbs.size() == 0)
		{
	  	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
	  	 	gc = GridchainManagement.gridchainGridIdGet(gridId, chainWindow, startTxId, endTxId, ErrResps, bwcon, memberNh, statusCode);

	    	if (ErrResps.size() > 0)
	    	{
/*		    	resei = new ResponseErrorInfo();		    	
		    	resei.setErrorMessage("Errors on Server");
		    	resei.setErrorDetails(ErrResps);
		  
		    	ResponseInfo ri = new ResponseInfo();
		    	ri.setStatus("Failure");
		    	ri.setFailureDetails(reseis);
	    		return Response.status(422).entity(ri).build(); */  	

	    		int scode = statusCode.get(0);
		  	 	System.out.println("scode............" + scode);
				return Response.status(scode).entity(ErrResps).build();   	

	    	}
	    	else
	    	{
		  	 	System.out.println("scode............200");
				//System.out.println("gc -> " + gc.toString());

				return Response.status(200).entity(gc).build();
	    	}
		}
		else
		{
	  	 	System.out.println("scode............400");
	       	return Response.status(400).entity(erbs).build();	//400: Bad Request: Negative gridId, Null importTid, view, baseline, mode, mode != 1 or 0 
			
/*	    	ResponseInfo ri = new ResponseInfo();
	    	ri.setStatus("Invalid Request");
	    	ri.setMessage(invReqMsg.toString().trim());
	    	ri.setInvalidRequestDetails(reqeis);
	        return Response.status(400).entity(ri).build(); */		
	     }

    }

    @Override
    public Response gridchainGridIdPut(Integer gridId, CellBuffer cellBufferRequest, SecurityContext securityContext, String authBase64String) throws NotFoundException {
        // do some magic!

        // do some magic!

		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();
    	BoardwalkConnection bwcon = null;
		ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();

		System.out.println("Inside GridApiServiceImpl.gridPut --- gridId : " + gridId);
	    	//System.out.println("authBase64String : " + authBase64String);
			
		if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			return Response.status(401).entity(erbs).build();		//401: Missing Authorization
		}
    	else
    	{
    		ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
        	//Connection connection = null;
    		
    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
    		if (!ErrResps.isEmpty())
    		{
    			return Response.status(401).entity(ErrResps).build();		//401: Authorization Failed
    		}
    	}
			
		if (gridId <= 0)
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("gridId"); 
			erb.setProposedSolution("You must enter an Existing Grid ID. It should be a Positive Number.");
			erbs.add(erb);
		}

	   	if (erbs.size() == 0)
	   	{
	   		CellBuffer cbf;
	  	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();

	  	 	int importTxId = cellBufferRequest.getInfo().getImportTxId();
	  	 	cbf = GridManagement.gridPut(gridId, cellBufferRequest, ErrResps, authBase64String, bwcon, memberNh, statusCode);
	  	 	
	    	if (ErrResps.size() > 0)
	    	{
				int scode = statusCode.get(0);
				return Response.status(scode).entity(ErrResps).build();   	
	    	}
	    	else
	    	{
		  	 	int exportTxId = cbf.getInfo().getExportTxId();
	
				String chainWindow = "BETWEENTX";
		  	 	System.out.println("Inside GridchainApiServiceImpl.java..... ");
		  	 	System.out.println("chainWindow : " + chainWindow);
		  	 	System.out.println("importTxId : " + importTxId);
		  	 	System.out.println("exportTxId : " + exportTxId);
		  	 	System.out.println("gridId : " + gridId);

		  	 	
		  	 	System.out.println("Calling bwAuthorization.AuthenticateUser .....start ");
	    		bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
	    		if (!ErrResps.isEmpty())
	    		{
	    			return Response.status(401).entity(ErrResps).build();		//401: Authorization Failed
	    		}
		  	 	System.out.println("Calling bwAuthorization.AuthenticateUser ..... successful");
		  	 	
		  	 	GridChain gc = new GridChain();
		  	 	gc = GridchainManagement.gridchainGridIdGet(gridId, chainWindow, importTxId, exportTxId, ErrResps, bwcon, memberNh, statusCode);

		  	 	System.out.println("Inside GridchainApiServiceImpl.java -> gridchainGridIdPut() -> gc:"+gc);
		    	if (ErrResps.size() > 0)
		    	{
		    		int scode = statusCode.get(0);
			  	 	System.out.println("Inside GridchainApiServiceImpl.java -> gridchainGridIdPut() ->.....scode............" + scode);
					return Response.status(scode).entity(ErrResps).build();   	
		    	}
		    	else
		    	{
			  	 	System.out.println("Inside GridchainApiServiceImpl.java -> gridchainGridIdPut() ->.....scode............200");
					return Response.status(200).entity(gc).build();
		    	}
	    	}
	    }
	   	else
	   	{
	       	return Response.status(400).entity(erbs).build();		//400: Bad Request. Negative GridId
	   	}    	  	 	
//        return Response.ok().entity(new ApiResponseMessage(ApiResponseMessage.OK, "magic!")).build();
	}    
    
    public Response gridchainGridIdTransactionsGet(Integer gridId, @NotNull String viewPref, @NotNull String reportType, String activityPeriod, Long startDate, Long endDate, Long localTimeAfter111970, Integer startTxId, Integer endTxId, Integer importTxId, SecurityContext securityContext, String authBase64String) throws NotFoundException
    {

    	BoardwalkConnection bwcon = null;
		ArrayList<Integer> memberNh = new ArrayList<Integer>();
		ArrayList<Integer> statusCode = new ArrayList<Integer>();

    	ArrayList <RequestErrorInfo> reqeis = new ArrayList<RequestErrorInfo>();
    	RequestErrorInfo reqei;

    	ArrayList <ResponseErrorInfo> reseis = new ArrayList<ResponseErrorInfo>();
    	ResponseErrorInfo resei ;

		ErrorRequestObject erb;
		ArrayList <ErrorRequestObject> erbs = new ArrayList<ErrorRequestObject>();

		System.out.println("Inside GridApiServiceImpl.gridGridIdTransactionsGet --- gridId : " + gridId);
		System.out.println("Inside GridApiServiceImpl.gridGridIdTransactionsGet --- localTimeAfter111970 : " + localTimeAfter111970);
		System.out.println("Inside GridApiServiceImpl.gridGridIdTransactionsGet --- viewPref : " + viewPref);
		System.out.println("Inside GridApiServiceImpl.gridGridIdTransactionsGet --- chainWindow : " + reportType);
		System.out.println("Inside GridApiServiceImpl.gridGridIdTransactionsGet --- activityPeriod : " + activityPeriod);
		System.out.println("Inside GridApiServiceImpl.gridGridIdTransactionsGet --- startDate : " + startDate);
		System.out.println("Inside GridApiServiceImpl.gridGridIdTransactionsGet --- endDate : " + endDate);
		System.out.println("Inside GridApiServiceImpl.gridGridIdTransactionsGet --- importTid : " + importTxId);
    	System.out.println("Inside GridApiServiceImpl.gridGridIdTransactionsGet --- authBase64String : " + authBase64String);
			
    	
    	if (authBase64String == null)
		{	
			erb = new ErrorRequestObject(); erb.setError("Missing Authorization in Header"); erb.setPath("Header:Authorization"); 
			erb.setProposedSolution("Authorization Header should contain user:pwd:nhPath as Base64 string");
			erbs.add(erb);
			
			reqei = new RequestErrorInfo();
			reqei.setErrorMessage("Authorization in Header not Found");
			reqei.setErrorDetails( erbs);
			reqeis.add(reqei);
			return Response.status(401).entity(reqeis).build();		//401: Missing Authorization
		}
		else
		{
			ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
	    	//Connection connection = null;
			
			bwcon = bwAuthorization.AuthenticateUser(authBase64String, memberNh, ErrResps);
			if (!ErrResps.isEmpty())
			{
				reqei = new RequestErrorInfo();
				reqei.setErrorMessage("Authentication Failed");
				reqei.setErrorDetails( ErrResps);
				reqeis.add(reqei);
				return Response.status(401).entity(reqeis).build();		//401: Authorization Failed
			}
		}

		if (gridId <= 0)
		{	
			erb = new ErrorRequestObject(); erb.setError("IsNegative"); erb.setPath("gridId"); 
			erb.setProposedSolution("You must enter an Existing Grid ID. It should be a Positive Number.");
			erbs.add(erb);

			reqei = new RequestErrorInfo();
			reqei.setErrorMessage("Negative GridId");
			reqei.setErrorDetails( erbs);
			reqeis.add(reqei);
		}

		if (reportType == null)
		{
			erb = new ErrorRequestObject();
			erb.setError("reportType is missing in GET Request");
			erb.setPath("reportType");
			erb.setProposedSolution("reportType must be AFTERIMPORT or DURATION or BETWEENTXS");
			erbs.add(erb);
			
			reqei = new RequestErrorInfo();
			reqei.setErrorMessage("Missing reportType");
			reqei.setErrorDetails( erbs);
			reqeis.add(reqei);
		}

		if (localTimeAfter111970 == null)
		{
			erb = new ErrorRequestObject();
			erb.setError("localTimeAfter111970 is missing in GET Request");
			erb.setPath("localTimeAfter111970");
			erb.setProposedSolution("localTimeAfter111970 must be number of MilliSeconds passed after date 01-01-1970");
			erbs.add(erb);
			
			reqei = new RequestErrorInfo();
			reqei.setErrorMessage("Missing localTimeAfter111970");
			reqei.setErrorDetails( erbs);
			reqeis.add(reqei);			
		}
		else if (localTimeAfter111970 <= 0)
		{
			erb = new ErrorRequestObject();
			erb.setError("localTimeAfter111970 must be Positive Number");
			erb.setPath("localTimeAfter111970");
			erb.setProposedSolution("localTimeAfter111970 must be number of MilliSeconds passed after date 01-01-1970");
			erbs.add(erb);

			reqei = new RequestErrorInfo();
			reqei.setErrorMessage("Negative localTimeAfter111970");
			reqei.setErrorDetails( erbs);
			reqeis.add(reqei);			
		}		

		if (reqeis.size() > 0)
		{
	    	ResponseInfo ri = new ResponseInfo();
	    	ri.setStatus("Invalid Request");
	    	ri.setInvalidRequestDetails(reqeis);
	        return Response.status(400).entity(ri).build();		//400 : Invalid Request
		}
		
    	long difference_in_MiliSec;
    	long local_offset = localTimeAfter111970.longValue() ;  //ON User machine

		Calendar cal_GMT = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
		long server_Millis = cal_GMT.getTimeInMillis();			//ON GMT
		difference_in_MiliSec = local_offset - server_Millis;	//This is Offset of Local machine. i.e. India is +5:30 GMT in milliseconds.

		
		Calendar cal_Local = Calendar.getInstance();
		long server_Millis_local = cal_Local.getTimeInMillis();			//ON local
		long difference_in_MiliSec_local = local_offset - server_Millis_local;	//This is Offset of Local machine. i.e. India is +5:30 GMT in milliseconds.

		
    	System.out.println("Inside GridApiServiceImpl.gridGridIdTransactionsGet --- local_offset : " + local_offset);
    	System.out.println("Inside GridApiServiceImpl.gridGridIdTransactionsGet --- server_Millis : " + server_Millis);
    	System.out.println("Inside GridApiServiceImpl.gridGridIdTransactionsGet --- difference_in_MiliSec : " + difference_in_MiliSec);
    	System.out.println("Inside GridApiServiceImpl.gridGridIdTransactionsGet --- difference_in_MiliSec_local : " + difference_in_MiliSec_local);

		System.out.println("Local Server (gmt) in miliSeconds is " + server_Millis );
		System.out.println("The difference in Server and Clietnis " + (local_offset - server_Millis ));
		
		long actStartDate = -1;
		long actEndDate = -1;

		if (reportType.toUpperCase().equals("DURATION"))
		{
			java.util.Date d = new java.util.Date();
			
			Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
			cal.setTime(d);

			actEndDate = d.getTime();
			actStartDate = 0;

			
			if (activityPeriod.toUpperCase().equals("WEEK"))
			{
				cal.add(Calendar.DATE, -7);
				actStartDate = cal.getTime().getTime();
			}
			else if (activityPeriod.toUpperCase().equals("MONTH"))
			{
				cal.add(Calendar.MONTH, -1);
				actStartDate = cal.getTime().getTime();
			}
			else if (activityPeriod.toUpperCase().equals("QUARTER"))
			{
				cal.add(Calendar.MONTH, -3);
				actStartDate = cal.getTime().getTime();
			}
			else if (activityPeriod.toUpperCase().equals("YEAR"))
			{
				cal.add(Calendar.YEAR, -1);
				actStartDate = cal.getTime().getTime();
			}
			else if (activityPeriod.toUpperCase().equals("CUSTOM") && startDate != null && endDate != null)
			{
				//actEndDate = endDate.longValue() - difference_in_MiliSec;
				//actStartDate = startDate.longValue() - difference_in_MiliSec;
				actEndDate = endDate.longValue();
				actStartDate = startDate.longValue();
				
				if (actStartDate > actEndDate)
				{
					erb = new ErrorRequestObject();
					erb.setError("Start Date < End Date");
					erb.setPath("startDate, endDate");
					erb.setProposedSolution("Start Date must be prior to End Date.");
					erbs.add(erb);

					reqei = new RequestErrorInfo();
					reqei.setErrorMessage("Invalid Start and End Dates");
					reqei.setErrorDetails( erbs);
					reqeis.add(reqei);			
				}
			}
			else 
			{
				erb = new ErrorRequestObject();
				erb.setError("Invalid Activity Period");
				erb.setPath("activityPeriod");
				erb.setProposedSolution("The Valid ActivityPeriod must be either Week OR Month OR Quarter OR Year OR Custom. For Custom activityPeriod you must provide valid startDate and endDate.");
				erbs.add(erb);

				reqei = new RequestErrorInfo();
				reqei.setErrorMessage("Invalid Activity Period");
				reqei.setErrorDetails( erbs);
				reqeis.add(reqei);			
			}
		}
		else if (reportType.toUpperCase().equals("AFTERIMPORT"))
		{
			if (importTxId == null)
			{
				erb = new ErrorRequestObject();
				erb.setError("importTid is missing in GET Request");
				erb.setPath("importTid");
				erb.setProposedSolution("importTid is mandetory to get transaction list after Last Import. Enter importTid as -1 or Positive Tranaction Number");
				erbs.add(erb);

				reqei = new RequestErrorInfo();
				reqei.setErrorMessage("Missing importTid");
				reqei.setErrorDetails( erbs);
				reqeis.add(reqei);			
			}
		}
		
		else if (reportType.toUpperCase().equals("BETWEENTXS"))
		{
			if (startTxId == null)
			{
				erb = new ErrorRequestObject();
				erb.setError("startTxId is missing in GET Request");
				erb.setPath("startTxId");
				erb.setProposedSolution("startTxId is mandetory to get changes between 2 transactions. Enter startTxId as -1 or Positive Tranaction Number");
				erbs.add(erb);

				reqei = new RequestErrorInfo();
				reqei.setErrorMessage("Missing importTid");
				reqei.setErrorDetails( erbs);
				reqeis.add(reqei);			
			}
			if (endTxId == null)
			{
				erb = new ErrorRequestObject();
				erb.setError("endTxId is missing in GET Request");
				erb.setPath("endTxId");
				erb.setProposedSolution("endTxId is mandetory to get changes between 2 transactions. Enter endTxId as Positive Tranaction Number later to startTxId.");
				erbs.add(erb);

				reqei = new RequestErrorInfo();
				reqei.setErrorMessage("Missing importTid");
				reqei.setErrorDetails( erbs);
				reqeis.add(reqei);			
			}		

			if ( (startTxId != null) && (endTxId != null) )
			{
				if (startTxId >= endTxId)
				{
					erb = new ErrorRequestObject();
					erb.setError("startTxId must be prior to endTxId");
					erb.setPath("startTxId >= endTxId");
					erb.setProposedSolution("Start TxId must be prior to End TxId.");
					erbs.add(erb);

					reqei = new RequestErrorInfo();
					reqei.setErrorMessage("Invalid startTxId, endTxId");
					reqei.setErrorDetails( erbs);
					reqeis.add(reqei);					
				}
			}
	
		}		
		else 
		{
			erb = new ErrorRequestObject();
			erb.setError("Invalid reportType");
			erb.setPath("reportType");
			erb.setProposedSolution("reportType must be AFTERIMPORT or DURATION");
			erbs.add(erb);

			reqei = new RequestErrorInfo();
			reqei.setErrorMessage("Invalid reportType");
			reqei.setErrorDetails( erbs);
			reqeis.add(reqei);			
		}
		
		System.out.println("++++++++++++++++ actStartDate = "  + actStartDate + " actEndDate = " + actEndDate );
		
		if (reqeis.size() == 0)
		{
	  	 	ArrayList <ErrorRequestObject> ErrResps = new ArrayList<ErrorRequestObject>();
	  	 	
	  		if (reportType.toUpperCase().equals("DURATION"))
	  		{
	  			
	  		}
	  		else if (reportType.toUpperCase().equals("AFTERIMPORT"))
	  		{
	  			
	  		}
	  		else if (reportType.toUpperCase().equals("BETWEENTXS"))
	  		{
	  			
	  		}
	  		
	   		ArrayList <GridTransaction> txs;

	  	 	txs = GridchainManagement.gridchainGridIdTransactionsGet(gridId, reportType,  actStartDate, actEndDate, difference_in_MiliSec, startTxId,  endTxId,  importTxId, viewPref, ErrResps,  bwcon, memberNh, statusCode);

//	  		public ArrayList<io.swagger.model.GridTransaction>  gridchainGridIdTransactionsGet(Integer gridId,  String reportType,  Long startDate, Long endDate, Long localTimeAfter111970, Integer startTxId, Integer endTxId,  Integer importTxId, ArrayList<ErrorRequestObject> ErrResps, BoardwalkConnection bwcon, ArrayList<Integer> memberNh, ArrayList<Integer> statusCode) 

	  	 	
	  	 	//404: GridId not found
	  	 	//200 : Success. Returns txs
	  	 	//500 : Server Error. SQLException thrown fromTableManager.getTransactionList OR TableManager.getTransactionListAfterImport
	  	 	//500 : Server Error. SystemException thrown from GridManagement.gridGridIdTransactionsGet::TableManager.getTableInfo
	  	 	
	    	if (ErrResps.size() > 0)
	    	{
				int scode = statusCode.get(0);

		    	resei = new ResponseErrorInfo();		    	
		    	resei.setErrorMessage("Errors on Server");
		    	resei.setErrorDetails(ErrResps);
		    	reseis.add(resei);
		    	
		    	ResponseInfo ri = new ResponseInfo();
		    	ri.setStatus("Failure");
		    	ri.setFailureDetails(reseis);
	    		return Response.status(scode).entity(ri).build();   	
	    	}
	    	else
	    	{
	    		return Response.status(200).entity(txs).build();		//200 Success: Return transactions
	    	}
		}
		else
		{
	    	ResponseInfo ri = new ResponseInfo();
	    	ri.setStatus("Invalid Request");
	    	ri.setInvalidRequestDetails(reqeis);
	        return Response.status(400).entity(ri).build();		//400 : Invalid Request
		}
    	
    	
    	
    	//return Response.status(200).entity("magic !!").build();	
    }

}
