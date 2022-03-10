/**********************************************
--Create the login 'BOARDWALK_APPLICATION_USER'
**********************************************/
USE master
GO

IF 	NOT EXISTS 
(
	SELECT 	* 
	FROM 	master.dbo.syslogins 
	WHERE 	loginname = N'BOARDWALK_APPLICATION_USER'
)
BEGIN
	DECLARE @LOGINDB	VARCHAR(132)
	DECLARE @LOGINLANG	VARCHAR(132) 

	SELECT 	@LOGINDB	= N'MASTER', 
			@LOGINLANG	= N'US_ENGLISH'
	
	EXEC 	sp_addlogin 
			N'BOARDWALK_APPLICATION_USER', 
			'BOARDWALK_APPLICATION_USER', 
			@LOGINDB, 
			@LOGINLANG
END
GO

EXEC	sp_addsrvrolemember 
		N'BOARDWALK_APPLICATION_USER', 
		sysadmin
GO

EXEC	sp_addsrvrolemember 
		N'BOARDWALK_APPLICATION_USER', 
		securityadmin
GO

EXEC	sp_addsrvrolemember 
		N'BOARDWALK_APPLICATION_USER', 
		serveradmin
GO

EXEC	sp_addsrvrolemember	
		N'BOARDWALK_APPLICATION_USER', 
		setupadmin
GO

EXEC	sp_addsrvrolemember 
		N'BOARDWALK_APPLICATION_USER', 
		processadmin
GO

EXEC	sp_addsrvrolemember 
		N'BOARDWALK_APPLICATION_USER', 
		diskadmin
GO

EXEC	sp_addsrvrolemember 
		N'BOARDWALK_APPLICATION_USER', 
		dbcreator
GO
