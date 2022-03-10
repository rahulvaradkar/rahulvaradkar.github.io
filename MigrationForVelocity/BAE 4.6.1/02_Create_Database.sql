/****************************************
--Replace 'BAE_4_6' with the desired name
****************************************/
USE [master]
GO

CREATE DATABASE [BAE_4_6_TARGET]
ON  PRIMARY 
( 
	NAME		= N'BAE_4_6_TARGET', 
	FILENAME	= N'E:\Database\BAE_4_6_TARGET.mdf' , 
	SIZE		= 8192KB , 
	MAXSIZE		= UNLIMITED, 
	FILEGROWTH	= 1024MB )
LOG ON 
( 
	NAME		= N'BAE_4_6_TARGET_log', 
	FILENAME	= N'E:\Database\BAE_4_6_TARGET_log.ldf' , 
	SIZE		= 4096KB , 
	MAXSIZE		= UNLIMITED, 
	FILEGROWTH	= 10%
)
GO

IF (1 = FULLTEXTSERVICEPROPERTY('ISFULLTEXTINSTALLED'))
BEGIN
	EXEC	[BAE_4_6].[DBO].[SP_FULLTEXT_DATABASE] 
			@ACTION = 'ENABLE'
END
GO

ALTER	DATABASE [BAE_4_6] 
SET		ANSI_NULL_DEFAULT OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		ANSI_NULLS OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		ANSI_PADDING OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		ANSI_WARNINGS OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		ARITHABORT OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		AUTO_CLOSE OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		AUTO_CREATE_STATISTICS ON 
GO

ALTER	DATABASE [BAE_4_6] 
SET		AUTO_SHRINK OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		AUTO_UPDATE_STATISTICS ON 
GO

ALTER	DATABASE [BAE_4_6] 
SET		CURSOR_CLOSE_ON_COMMIT OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		CURSOR_DEFAULT  GLOBAL 
GO

ALTER	DATABASE [BAE_4_6] 
SET		CONCAT_NULL_YIELDS_NULL OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		NUMERIC_ROUNDABORT OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		QUOTED_IDENTIFIER OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		RECURSIVE_TRIGGERS OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		DISABLE_BROKER 
GO

ALTER	DATABASE [BAE_4_6] 
SET		AUTO_UPDATE_STATISTICS_ASYNC OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		DATE_CORRELATION_OPTIMIZATION OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		TRUSTWORTHY OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		ALLOW_SNAPSHOT_ISOLATION OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		PARAMETERIZATION SIMPLE 
GO

ALTER	DATABASE [BAE_4_6] 
SET		READ_COMMITTED_SNAPSHOT OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		HONOR_BROKER_PRIORITY OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		READ_WRITE 
GO

ALTER	DATABASE [BAE_4_6] 
SET		RECOVERY SIMPLE 
GO

ALTER	DATABASE [BAE_4_6] 
SET		MULTI_USER 
GO

ALTER	DATABASE [BAE_4_6] 
SET		PAGE_VERIFY TORN_PAGE_DETECTION  
GO

ALTER	DATABASE [BAE_4_6] 
SET		DB_CHAINING OFF 
GO

ALTER	DATABASE [BAE_4_6] 
SET		READ_WRITE 
GO


/*********************************************
--Create the user 'BOARDWALK_APPLICATION_USER'
*********************************************/
USE [BAE_4_6]
GO

IF NOT EXISTS 
(
	SELECT	* 
	FROM	dbo.sysusers 
	WHERE	name = N'BOARDWALK_APPLICATION_USER' 
	AND		uid < 16382
)
BEGIN
	--CREATE	USER [BOARDWALK_APPLICATION_USER] 
	--FOR		LOGIN [BOARDWALK_APPLICATION_USER] 
	--WITH	DEFAULT_SCHEMA=[BOARDWALK_APPLICATION_USER]

	--CREATE SCHEMA 
	--[BOARDWALK_APPLICATION_USER] 
	--AUTHORIZATION [BOARDWALK_APPLICATION_USER]

	EXEC	sp_grantdbaccess 
			N'BOARDWALK_APPLICATION_USER', 
			N'BOARDWALK_APPLICATION_USER'
END
GO

EXEC	sp_addrolemember 
		N'db_datareader', 
		N'BOARDWALK_APPLICATION_USER'
GO

EXEC	sp_addrolemember 
		N'db_datawriter', 
		N'BOARDWALK_APPLICATION_USER'
GO

DECLARE @PROC	VARCHAR(100)
DECLARE @STMT	NVARCHAR(200)

DECLARE ALL_OBJECTS_CRS CURSOR FOR 
SELECT	name 
FROM	sysobjects 
WHERE	type = 'P'

OPEN ALL_OBJECTS_CRS
FETCH NEXT FROM ALL_OBJECTS_CRS INTO @PROC
WHILE @@FETCH_STATUS=0 
BEGIN
	set		@stmt = 'grant execute on '+@proc+' to BOARDWALK_APPLICATION_USER'

	EXEC	SP_EXECUTESQL 
			@STMT
	
	PRINT @STMT
	
	FETCH NEXT FROM ALL_OBJECTS_CRS INTO @PROC
END

CLOSE ALL_OBJECTS_CRS
DEALLOCATE ALL_OBJECTS_CRS
