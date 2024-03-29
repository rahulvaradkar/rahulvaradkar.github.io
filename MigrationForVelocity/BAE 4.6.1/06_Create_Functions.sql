/****************************************
--Replace 'BAE_4_6_TARGET' with the desired name
****************************************/
USE [BAE_4_6_TARGET]
GO
/****** Object:  UserDefinedFunction [dbo].[Validate_String]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
/*==================================================================================================*
 * Name			: 	Validate_String																	*
 * ----																								*
 * Purpose		: 	The script finds out any 'unfriendly' non keyboard characters as part of a      *
 * -------			string.                                                                         *
 * Created By	:	Venky Subramaniam				Date: 03/05/2010								*
 * ------- --																						*
 * Usage 		: 	1. SELECT	BW_ROW_ID,															*
 * -----						STRING_VALUE														*
 * 						FROM	BW_CELL																*
 *						WHERE	dbo.Validate_String(STRING_VALUE) > 0								*												*
 * =================================================================================================*
 * Dessciption								Date						Modified By					*
 * =================================================================================================*/


CREATE 	FUNCTION [dbo].[Validate_String] (@in_String VARCHAR(max)) 
		RETURNS INT 
BEGIN
	DECLARE @String_len INT
	DECLARE @Result INT
	DECLARE @Ascii_val 	INT
	DECLARE @Pos INT
	IF @in_String IS NULL
		RETURN NULL
	SET @String_len = LEN(@in_String)
	
	SET @Pos = 1
	WHILE @Pos <= @String_len 
		BEGIN
			SET @Ascii_val = ASCII(SUBSTRING(@in_String,@Pos, 1))
/*			IF 	@Ascii_val BETWEEN 48 AND 57 OR 
				@Ascii_val BETWEEN 65 AND 90 OR 
				@Ascii_val BETWEEN 97 AND 122 OR
				@Ascii_val IN (32, 39, 45, 46) */
			IF	@Ascii_val BETWEEN 32 and 126 
				BEGIN
					SET @Result = 0
					SET @Pos = @Pos + 1
				END
			ELSE
				BEGIN
					SET @Result = @Ascii_val
					BREAK
				END
		END
   RETURN @Result
   END
GO
/****** Object:  UserDefinedFunction [dbo].[ParseString_UDF]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[ParseString_UDF]
(
          @stringToParse VARCHAR(8000)  
        , @delimiter     CHAR(1)
)
RETURNS @parsedString TABLE (stringValue VARCHAR(128))
AS
/*********************************************************************************
    Name:      ParseString_UDF
 
    Purpose:    This function parses string input using a variable delimiter.
 
    Notes:      Two common delimiter values are space (' ') and comma (',')
*********************************************************************************
Usage: 		
    SELECT *
    FROM ParseString_UDF(<string>, <delimiter>);
 
Test Cases:
 
    1.  multiple strings separated by space
        SELECT * FROM dbo.ParseString_UDF('  aaa  bbb  ccc ', ' ');
 
    2.  multiple strings separated by comma
        SELECT * FROM dbo.ParseString_UDF(',aaa,bbb,,,ccc,', ',');
*********************************************************************************/
BEGIN
 
    /* Declare variables */
    DECLARE @trimmedString  VARCHAR(8000);
 
    /* Trim the string input in case the user entered extra spaces */
    SET @trimmedString = LTRIM(RTRIM(@stringToParse));
 
    /* Create a recursive CTE to break down the string for the need */
    WITH parseCTE (StartPos, EndPos)
    AS
    (
        SELECT 	1 AS StartPos,
				CHARINDEX(@delimiter, @trimmedString + @delimiter) AS EndPos
        UNION ALL
        SELECT 	EndPos + 1 AS StartPos,
				CHARINDEX(@delimiter, @trimmedString + @delimiter , EndPos + 1) AS EndPos
        FROM 	parseCTE
        WHERE 	CHARINDEX(@delimiter, @trimmedString + @delimiter, EndPos + 1) <> 0
    )
 
    /* Inserting the results into a table */  
    INSERT 	INTO @parsedString
    SELECT 	SUBSTRING(@trimmedString, StartPos, EndPos - StartPos)
    FROM 	parseCTE
    WHERE 	LEN(LTRIM(RTRIM(SUBSTRING(@trimmedString, StartPos, EndPos - StartPos)))) > 0
    OPTION 	(MaxRecursion 8000);
 
    RETURN;   
END
GO
/****** Object:  UserDefinedFunction [dbo].[ParseIDList]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS OFF
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[ParseIDList]
		(
			@IDLIST NVARCHAR(2048)
		)
		RETURNS 
		@PARSEDLIST TABLE
		(
			ID INT
		)
		AS
		BEGIN
			DECLARE @ID VARCHAR(10), @POS INT
			SET 	@IDLIST = LTRIM(RTRIM(@IDLIST))+ ','
			SET 	@POS = CHARINDEX(',', @IDLIST, 1)
			IF REPLACE(@IDLIST, ',', '') <> ''
				BEGIN
					WHILE @POS > 0
						BEGIN
							SET @ID = LTRIM(RTRIM(LEFT(@IDLIST, @POS - 1)))
							IF @ID <> ''
								BEGIN
									INSERT INTO @PARSEDLIST 
									(
										ID
									) 
									VALUES 
									(
										CAST(@ID AS INT)
									) --USE APPROPRIATE CONVERSION
								END
							SET 	@IDLIST = RIGHT(@IDLIST, LEN(@IDLIST) - @POS)
							SET 	@POS = CHARINDEX(',', @IDLIST, 1)
						END
				END	
			RETURN
		END
GO
/****** Object:  UserDefinedFunction [dbo].[parse_comma_delimited_integer]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS OFF
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[parse_comma_delimited_integer]
		(
			@LIST    	VARCHAR(8000), 
			@DELIMITER 	VARCHAR(10) = ',
			'
		)

		-- TABLE VARIABLE THAT WILL CONTAIN VALUES
		RETURNS @TABLEVALUES TABLE 
		(
			ITEM INT
		)
		AS
		BEGIN 
			DECLARE @ITEM VARCHAR(255)
		 
			/* LOOP OVER THE COMMADELIMITED LIST */
			WHILE (DATALENGTH(@LIST) > 0)
				BEGIN 
					IF CHARINDEX(@DELIMITER,@LIST) > 0
						BEGIN
							SELECT @ITEM = SUBSTRING(@LIST,1,(CHARINDEX(@DELIMITER, @LIST)-1))
							SELECT @LIST =	SUBSTRING(@LIST,(CHARINDEX(@DELIMITER, @LIST) +
							DATALENGTH(@DELIMITER)),DATALENGTH(@LIST))
						END
					ELSE
						BEGIN
							SELECT @ITEM = @LIST
							SELECT @LIST = NULL
						END
		 
					-- INSERT EACH ITEM INTO TEMP TABLE
					INSERT @TABLEVALUES 
					(
						ITEM
					)
					SELECT ITEM = CONVERT(INT, @ITEM) 
				END
		RETURN
		END
GO
/****** Object:  UserDefinedFunction [dbo].[parse_comma_delimited]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS OFF
GO
SET QUOTED_IDENTIFIER ON
GO
--SELECT * FROM [parse_comma_delimited_integerG2]('A,B,B',',')



CREATE FUNCTION [dbo].[parse_comma_delimited](
    @list    VARCHAR(max), 
    @delimiter VARCHAR(10) = ',')

-- table variable that will contain values
RETURNS @tablevalues TABLE (
	ID INT IDENTITY(1,1) PRIMARY KEY,  --Added by Sridevi on 20190904 to fix the Issue Id: 17333
    item VARCHAR(2048)
)
AS
BEGIN 
  DECLARE @item VARCHAR(2048)

  /* Loop over the commadelimited list */
  WHILE (DATALENGTH(@list) > 0)
    BEGIN 
      IF CHARINDEX(@delimiter,@list) > 0
        BEGIN
          SELECT @item =
             SUBSTRING(@list,1,(CHARINDEX(@delimiter, @list)-1))
          SELECT @list =
                SUBSTRING(@list,(CHARINDEX(@delimiter, @list) +
                DATALENGTH(@delimiter)),DATALENGTH(@list))
        END

     ELSE
        BEGIN
          SELECT @item = @list
          SELECT @list = NULL
        END

   -- Insert each item into temp table
    INSERT @tablevalues (item)
    SELECT item = @item
   END

RETURN
END
GO
/****** Object:  UserDefinedFunction [dbo].[isReallyNumeric]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[isReallyNumeric]  
(  
    @num VARCHAR(64)  
)  
RETURNS BIT  
BEGIN  
    IF LEFT(@num, 1) = '-'  
        SET @num = SUBSTRING(@num, 2, LEN(@num))  
 
    DECLARE @pos INT  
 
    SET @pos = 1 + LEN(@num) - CHARINDEX('.', REVERSE(@num))  
 
    RETURN CASE  
    WHEN PATINDEX('%[^0-9.-]%', @num) = 0  
        AND @num NOT IN ('.', '-', '+', '^') 
        AND LEN(@num)>0  
        AND @num NOT LIKE '%-%' 
        AND  
        (  
            ((@pos = LEN(@num)+1)  
            OR @pos = CHARINDEX('.', @num))  
        )  
    THEN  
        1  
    ELSE  
    0  
    END  
END
GO
/****** Object:  UserDefinedFunction [dbo].[BW_GetConsolidationUserSet]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS OFF
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[BW_GetConsolidationUserSet] 
		( 
			@NH_ID 				INT, 
			@VIEW_PREFERENCE 	VARCHAR(256) 
		)
		RETURNS @ACCESSIBLE_USERS TABLE
		(
			USERID     INT
		)
		AS
		BEGIN
		/*
			PUBLIC STATIC FINAL STRING LATEST = "LATEST";
			PUBLIC STATIC FINAL STRING MY_ROWS = "MY_ROWS";
			PUBLIC STATIC FINAL STRING LATEST_ROWS_OF_ALL_USERS_IN_ANY_NH = "LATEST_ROWS_OF_ALL_USERS_IN_ANY_NH";
			PUBLIC STATIC FINAL STRING LATEST_ROWS_OF_ALL_USERS_IN_MY_NH = "LATEST_ROWS_OF_ALL_USERS_IN_MY_NH";
			PUBLIC STATIC FINAL STRING LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_IMM_CHD = "LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_IMM_CHD";
			PUBLIC STATIC FINAL STRING LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_ALL_CHD = "LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_ALL_CHD";
			PUBLIC STATIC FINAL STRING LOOKUP = "LOOKUP";
			PUBLIC STATIC FINAL STRING DESIGN = "DESIGN";
		*/
		IF 	@VIEW_PREFERENCE = 'LATEST_ROWS_OF_ALL_USERS_IN_ANY_NH'    
		OR 	@VIEW_PREFERENCE ='LATEST_ROWS_OF_ALL_USERS_IN_MY_NH'
			BEGIN
				INSERT 		@ACCESSIBLE_USERS
				SELECT 		BW_USER.ID
				FROM 		BW_USER, 
							BW_MEMBER,
							BW_NH
				WHERE		BW_NH.ID = @NH_ID
				AND			BW_NH.ID = BW_MEMBER.NEIGHBORHOOD_ID
				AND     	BW_MEMBER.USER_ID = BW_USER.ID
				ORDER BY 	BW_USER.EMAIL_ADDRESS
				RETURN
			END
			DECLARE @NH_LEVEL AS INTEGER
			DECLARE @LEVEL_0_ID AS INTEGER
			DECLARE @LEVEL_1_ID AS INTEGER
			DECLARE @LEVEL_2_ID AS INTEGER
			DECLARE @LEVEL_3_ID AS INTEGER
			SELECT 	@NH_LEVEL = BW_NH.NEIGHBORHOOD_LEVEL,
					@LEVEL_0_ID = BW_NH.LEVEL_0_ID,
					@LEVEL_1_ID = BW_NH.LEVEL_1_ID,
					@LEVEL_2_ID = BW_NH.LEVEL_2_ID,
					@LEVEL_3_ID = BW_NH.LEVEL_3_ID
			FROM 	BW_NH
			WHERE 	BW_NH.ID  = @NH_ID
		IF 	@VIEW_PREFERENCE = 'LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_ALL_CHD'
			BEGIN
				IF @NH_LEVEL = 0
					BEGIN
					INSERT 		@ACCESSIBLE_USERS
					SELECT 		BW_USER.ID
					FROM 		BW_USER,
								BW_MEMBER,
								BW_NH
					WHERE 		BW_USER.ID = BW_MEMBER.USER_ID
					AND 		BW_MEMBER.NEIGHBORHOOD_ID  = BW_NH.ID
					AND     	BW_NH.LEVEL_0_ID = @LEVEL_0_ID
					ORDER BY  	BW_USER.EMAIL_ADDRESS
				END
		IF @NH_LEVEL = 1
			BEGIN
				INSERT 		@ACCESSIBLE_USERS
				SELECT 		BW_USER.ID
				FROM 		BW_USER,
							BW_MEMBER,
							BW_NH
				WHERE 		BW_USER.ID = BW_MEMBER.USER_ID
				AND 		BW_MEMBER.NEIGHBORHOOD_ID  = BW_NH.ID
				AND     	BW_NH.LEVEL_1_ID = @LEVEL_1_ID
				ORDER BY  	BW_USER.EMAIL_ADDRESS
			END
		IF @NH_LEVEL = 2
			BEGIN
				INSERT 		@ACCESSIBLE_USERS
				SELECT 		BW_USER.ID
				FROM 		BW_USER,
							BW_MEMBER,
							BW_NH
				WHERE 		BW_USER.ID = BW_MEMBER.USER_ID
				AND 		BW_MEMBER.NEIGHBORHOOD_ID  = BW_NH.ID
				AND     	BW_NH.LEVEL_2_ID = @LEVEL_2_ID
				ORDER BY  	BW_USER.EMAIL_ADDRESS
			END
			IF @NH_LEVEL = 3
				BEGIN
					INSERT 		@ACCESSIBLE_USERS
					SELECT 		BW_USER.ID
					FROM 		BW_USER,
								BW_MEMBER,
								BW_NH
					WHERE 		BW_USER.ID = BW_MEMBER.USER_ID
					AND 		BW_MEMBER.NEIGHBORHOOD_ID  = BW_NH.ID
					AND     	BW_NH.LEVEL_3_ID = @LEVEL_3_ID
					ORDER BY  	BW_USER.EMAIL_ADDRESS
				END
			RETURN
		END
		IF 	@VIEW_PREFERENCE ='LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_IMM_CHD'
			BEGIN
				IF @NH_LEVEL = 0
					BEGIN
						INSERT 		@ACCESSIBLE_USERS
						SELECT 		BW_USER.ID
						FROM 		BW_USER,
									BW_MEMBER,
									BW_NH
						WHERE 		BW_USER.ID = BW_MEMBER.USER_ID
						AND 		BW_MEMBER.NEIGHBORHOOD_ID  = BW_NH.ID
						AND     	BW_NH.LEVEL_0_ID = @LEVEL_0_ID
						AND     	BW_NH.NEIGHBORHOOD_LEVEL < 2
						ORDER BY  	BW_USER.EMAIL_ADDRESS
					END
				IF @NH_LEVEL = 1
					BEGIN
						INSERT 		@ACCESSIBLE_USERS
						SELECT 		BW_USER.ID
						FROM 		BW_USER,
									BW_MEMBER,
									BW_NH
						WHERE 		BW_USER.ID = BW_MEMBER.USER_ID
						AND 		BW_MEMBER.NEIGHBORHOOD_ID  = BW_NH.ID
						AND      	BW_NH.LEVEL_1_ID = @LEVEL_1_ID
						AND      	BW_NH.NEIGHBORHOOD_LEVEL < 3
						ORDER BY  	BW_USER.EMAIL_ADDRESS
					END
				IF @NH_LEVEL = 2
					BEGIN
						INSERT 		@ACCESSIBLE_USERS
						SELECT 		BW_USER.ID
						FROM 		BW_USER,
									BW_MEMBER,
									BW_NH
						WHERE 		BW_USER.ID = BW_MEMBER.USER_ID
						AND 		BW_MEMBER.NEIGHBORHOOD_ID  = BW_NH.ID
						AND 		BW_MEMBER.NEIGHBORHOOD_ID  = BW_NH.ID
						AND     	BW_NH.LEVEL_2_ID = @LEVEL_2_ID
						ORDER BY  	BW_USER.EMAIL_ADDRESS
					END
				IF @NH_LEVEL = 3
					BEGIN
						INSERT 		@ACCESSIBLE_USERS
						SELECT 		BW_USER.ID
						FROM 		BW_USER,
									BW_MEMBER,
									BW_NH
						WHERE 		BW_USER.ID = BW_MEMBER.USER_ID
						AND 		BW_MEMBER.NEIGHBORHOOD_ID  = BW_NH.ID
						AND     	BW_NH.LEVEL_3_ID = @LEVEL_3_ID
						ORDER BY  	BW_USER.EMAIL_ADDRESS
					END
				RETURN
		END
		RETURN
	END
GO
/****** Object:  UserDefinedFunction [dbo].[BW_GetConsolidationRowSetActive]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS OFF
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[BW_GetConsolidationRowSetActive] 
		(
			@TBL_ID 			INT, 
			@USER_ID 			INT, 
			@NH_ID 				INT, 
			@VIEW_PREFERENCE 	VARCHAR(256) 
		)
		RETURNS @ACCESSIBLE_ROWS TABLE
		(
			ROWID     INT NOT NULL PRIMARY KEY
		)
		AS
		BEGIN
		DECLARE @HIERARCHY_USERS TABLE
		(
			USERID INT
		)
		IF 	@VIEW_PREFERENCE = 'LATEST' 
		OR  @VIEW_PREFERENCE = 'LOOKUP' 
		OR 	@VIEW_PREFERENCE ='DESIGN'
			BEGIN
				INSERT 	@ACCESSIBLE_ROWS
				SELECT 	ID
				FROM 	BW_ROW
				WHERE 	BW_ROW.BW_TBL_ID = @TBL_ID
				AND 	BW_ROW.IS_ACTIVE = 1
				RETURN
			END
		ELSE
			BEGIN
				IF @VIEW_PREFERENCE = 'MY_ROWS'
					BEGIN
						INSERT 	@ACCESSIBLE_ROWS
						SELECT 	ID
						FROM 	BW_ROW
						WHERE 	BW_ROW.BW_TBL_ID = @TBL_ID
						AND 	BW_ROW.IS_ACTIVE = 1
						AND		BW_ROW.OWNER_ID =  @USER_ID
						RETURN
					END
				ELSE
					BEGIN
						INSERT  @HIERARCHY_USERS
						SELECT 	*
						FROM 	BW_GetConsolidationUserSet( @NH_ID, @VIEW_PREFERENCE)
						INSERT 	@ACCESSIBLE_ROWS
						SELECT 	BW_ROW.ID
						FROM 	BW_ROW,@HIERARCHY_USERS HUSRS
						WHERE 	BW_ROW.BW_TBL_ID = @TBL_ID
						AND 	BW_ROW.IS_ACTIVE = 1
						AND		BW_ROW.OWNER_ID = HUSRS.USERID
					END
			END
		RETURN
		END
GO
/****** Object:  UserDefinedFunction [dbo].[BW_GetConsolidationRowSet]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS OFF
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[BW_GetConsolidationRowSet] 
		(
			@TBL_ID 			INT, 
			@USER_ID 			INT, 
			@NH_ID 				INT, 
			@VIEW_PREFERENCE 	VARCHAR(256) 
		)
		RETURNS @ACCESSIBLE_ROWS TABLE
		(
			ROWID     	INT NOT NULL PRIMARY KEY,
			TX_ID     	INT,
			OWNER_ID  	INT,
			OWNER_TID   INT
		)
		AS
		BEGIN
			DECLARE @HIERARCHY_USERS TABLE
			(			
				USERID INT
			)
			/*
				PUBLIC STATIC FINAL STRING LATEST = "LATEST";
				PUBLIC STATIC FINAL STRING MY_ROWS = "MY_ROWS";
				PUBLIC STATIC FINAL STRING LATEST_ROWS_OF_ALL_USERS_IN_ANY_NH = "LATEST_ROWS_OF_ALL_USERS_IN_ANY_NH";
				PUBLIC STATIC FINAL STRING LATEST_ROWS_OF_ALL_USERS_IN_MY_NH = "LATEST_ROWS_OF_ALL_USERS_IN_MY_NH";
				PUBLIC STATIC FINAL STRING LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_IMM_CHD = "LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_IMM_CHD";
				PUBLIC STATIC FINAL STRING LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_ALL_CHD = "LATEST_ROWS_OF_ALL_USERS_IN_MY_NH_AND_ALL_CHD";
				PUBLIC STATIC FINAL STRING LOOKUP = "LOOKUP";
				PUBLIC STATIC FINAL STRING DESIGN = "DESIGN";
				STRING Q1 = "SELECT ID, TX_ID, OWNER_ID, OWNER_TID FROM BW_ROW WHERE BW_ROW.BW_TBL_ID = ? AND BW_ROW.IS_ACTIVE = 1";
			*/
			IF 	@VIEW_PREFERENCE = 'LATEST' 
			OR  @VIEW_PREFERENCE = 'LOOKUP' 
			OR 	@VIEW_PREFERENCE ='DESIGN'
				BEGIN
					INSERT 	@ACCESSIBLE_ROWS
					SELECT 	ID, 
							TX_ID, 
							OWNER_ID, 
							OWNER_TID
					FROM 	BW_ROW
					WHERE 	BW_ROW.BW_TBL_ID = @TBL_ID
					/*AND BW_ROW.IS_ACTIVE = 1*/
					RETURN
				END
			ELSE
				BEGIN
					IF @VIEW_PREFERENCE = 'MY_ROWS'
						BEGIN
							INSERT 	@ACCESSIBLE_ROWS
							SELECT 	ID, 
									TX_ID, 
									OWNER_ID, 
									OWNER_TID
							FROM 	BW_ROW
							WHERE 	BW_ROW.BW_TBL_ID = @TBL_ID
							/*AND 	BW_ROW.IS_ACTIVE = 1*/
							AND		BW_ROW.OWNER_ID =  @USER_ID
							RETURN
						END
					ELSE
						BEGIN
							INSERT  @HIERARCHY_USERS
							SELECT 	*
							FROM 	BW_GetConsolidationUserSet( @NH_ID, @VIEW_PREFERENCE)
							INSERT 	@ACCESSIBLE_ROWS
							SELECT 	BW_ROW.ID, 
									BW_ROW.TX_ID, 
									BW_ROW.OWNER_ID, 
									BW_ROW.OWNER_TID
							FROM 	BW_ROW,@HIERARCHY_USERS HUSRS
							WHERE 	BW_ROW.BW_TBL_ID = @TBL_ID
							/*AND 	BW_ROW.IS_ACTIVE = 1*/
							AND		BW_ROW.OWNER_ID = HUSRS.USERID
						END
				END
				RETURN
		END
GO
/****** Object:  UserDefinedFunction [dbo].[BW_GetColumnAccessNew]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS OFF
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[BW_GetColumnAccessNew] 
		( 
			@TABLE_ID 	INT, 
			@USER_ID 	INT,  
			@MEMBER_ID 	INT 
		) 

		RETURNS @COLUMNACCESS TABLE 
		( 
			COLID     			INT, 
			ACCESS_ 			INT, 
			PREV_ACCESS 		INT, 
			ACCESS_TID 			INT, 
			SEQUENCE_NUMBER 	FLOAT 
		) 
		AS 
			BEGIN 
				DECLARE @MAX_COL_ACCESS TABLE 
				( 
					ID INT, 
					ACCESS_  INT, 
					PREV_ACCESS INT, 
					TID INT 
				) 
				INSERT 	INTO @MAX_COL_ACCESS 
				SELECT  ID,  
						MAX(ACCESS_) AS ACCESS_, 
						MAX(PREV_ACCESS) AS PREV_ACCESS, 
						MAX(TID) 
				FROM 
				( 
				-- DEFINED ACCESS 
					SELECT 	BW_COLUMN.ID ,   
							BW_COL_ACCESS.ACCESS_, 
							BW_COL_ACCESS.PREV_ACCESS, 
							BW_COL_ACCESS.TID 
					FROM    BW_COL_ACCESS, 
							BW_COLUMN, 
							BW_NH_REL, 
							BW_TBL, 
							BW_MEMBER 
					WHERE   BW_COL_ACCESS.COL_ID = BW_COLUMN.ID 
					AND     BW_COLUMN.BW_TBL_ID = BW_TBL.ID 
					AND     BW_TBL.ID = @TABLE_ID 
					AND     BW_COL_ACCESS.REL = BW_NH_REL.REL 
					AND     BW_NH_REL.TARGET_NH_ID = BW_MEMBER.NEIGHBORHOOD_ID 
					AND     BW_NH_REL.NEIGHBORHOOD_ID = BW_TBL.NEIGHBORHOOD_ID 
					AND     BW_MEMBER.ID = @MEMBER_ID 
					UNION ALL 
					SELECT 	BW_COLUMN.ID,   
							BW_COL_ACCESS.ACCESS_, 
							BW_COL_ACCESS.PREV_ACCESS, 
							BW_COL_ACCESS.TID 
					FROM    BW_COL_ACCESS, BW_COLUMN 
					WHERE 	BW_COL_ACCESS.COL_ID = BW_COLUMN.ID 
					AND     BW_COLUMN.BW_TBL_ID = @TABLE_ID 
					AND     BW_COL_ACCESS.REL = 'PUBLIC' 
					UNION 
					SELECT 	BW_COLUMN.ID,   
							BW_COL_ACCESS.ACCESS_, 
							BW_COL_ACCESS.PREV_ACCESS, 
							BW_COL_ACCESS.TID 
					FROM    BW_COL_ACCESS, 
							BW_COLUMN, 
							BW_TBL, 
							BW_TXS 
					WHERE 	BW_COL_ACCESS.COL_ID = BW_COLUMN.ID 
					AND     BW_COLUMN.BW_TBL_ID = BW_TBL.ID 
					AND     BW_COL_ACCESS.REL = 'CREATOR' 
					AND     BW_TBL.TX_ID = BW_TXS.TX_ID 
					AND     BW_TXS.CREATED_BY= @USER_ID 
					AND     BW_TBL.ID = @TABLE_ID 
				) 	AS COLACCESS 
					GROUP BY ID 
				--DEFAULT PUBLIC ACCESS (WRITE) FOR COLUMNS NOT IN THE ACCESS TABLE 
					INSERT 	INTO @MAX_COL_ACCESS 
					SELECT  BW_COLUMN.ID,   
							2 AS ACCESS_, 
							2 AS PREV_ACCESS, 
							-1 AS TID 
					FROM    BW_COLUMN 
					WHERE   BW_COLUMN.BW_TBL_ID = @TABLE_ID 
					AND     BW_COLUMN.ID NOT IN 
							(	
								SELECT 	DISTINCT ID 
								FROM 	@MAX_COL_ACCESS
							) 

				--DEFAULT PUBLIC ACCESS (WRITE) FOR COLUMNS IN THE ACCESS TABLE 
				UPDATE 	@MAX_COL_ACCESS 
				SET 	ACCESS_ = 2 
				FROM 	@MAX_COL_ACCESS MCA 
				WHERE 	MCA.ID NOT IN 
				( 
					SELECT  BW_COLUMN.ID 
					FROM    BW_COLUMN, 
							BW_COL_ACCESS 
					WHERE   BW_COLUMN.ID = BW_COL_ACCESS.COL_ID 
					AND     BW_COLUMN.BW_TBL_ID = @TABLE_ID 
					AND     BW_COL_ACCESS.REL = 'PUBLIC' 
				) 
				AND     MCA.ACCESS_ < 2 
				--DEFAULT CREATOR ACCESS (WRITE) FOR COLUMNS IN THE ACCESS TABLE 
				UPDATE 	@MAX_COL_ACCESS 
				SET 	ACCESS_ = 2 
				FROM 	BW_TBL, 
						BW_COLUMN, 
						BW_TXS, 
						@MAX_COL_ACCESS MCA 
				WHERE 	MCA.ID NOT IN 
				( 
					SELECT  BW_COLUMN.ID 
					FROM 	BW_COLUMN, 
							BW_COL_ACCESS 
					WHERE 	BW_COLUMN.BW_TBL_ID = @TABLE_ID 
					AND     BW_COL_ACCESS.COL_ID = BW_COLUMN.ID 
					AND     BW_COL_ACCESS.REL = 'CREATOR' 
				) 
				AND     MCA.ID = BW_COLUMN.ID 
				AND     BW_COLUMN.BW_TBL_ID = BW_TBL.ID 
				AND     BW_TBL.TX_ID = BW_TXS.TX_ID 
				AND     BW_TXS.CREATED_BY= @USER_ID 
				AND     BW_TBL.ID = @TABLE_ID 
				AND     MCA.ACCESS_ < 2 
				INSERT  INTO @COLUMNACCESS 
				SELECT 	BW_COLUMN.ID AS COLID, 
						MCA.ACCESS_, 
						MCA.PREV_ACCESS, 
						MCA.TID, 
						BW_COLUMN.SEQUENCE_NUMBER 
				FROM 	BW_COLUMN, 
						@MAX_COL_ACCESS MCA 
				WHERE 	BW_COLUMN.ID = MCA.ID 
				AND     MCA.ACCESS_ > 0 
			RETURN 
		END
GO
/****** Object:  UserDefinedFunction [dbo].[BW_GetColumnAccess]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS OFF
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[BW_GetColumnAccess] 
		( 
			@TABLE_ID 	INT, 
			@USER_ID 	INT,  
			@MEMBER_ID 	INT 
		) 

		RETURNS @COLUMNACCESS TABLE 
		( 
			COLID     		INT, 
			ACCESS_ 		INT, 
			PREV_ACCESS 	INT, 
			ACCESS_TID 		INT, 
			SEQUENCE_NUMBER FLOAT 
		) 

		AS 
		BEGIN 
			DECLARE @MAX_COL_ACCESS TABLE 
			( 
				ID 			INT, 
				ACCESS_  	INT, 
				PREV_ACCESS INT, 
				TID 		INT 
			) 

			INSERT 	INTO @MAX_COL_ACCESS 
			SELECT  ID,  
					MAX(ACCESS_) AS ACCESS_, 
					MAX(PREV_ACCESS) AS PREV_ACCESS, MAX(TID) 
			FROM 
			( 
			-- DEFINED ACCESS 
				SELECT 	BW_COLUMN.ID ,   
						BW_COL_ACCESS.ACCESS_, 
						BW_COL_ACCESS.PREV_ACCESS, 
						BW_COL_ACCESS.TID 
				FROM    BW_COL_ACCESS, 
						BW_COLUMN, 
						BW_NH_REL, 
						BW_TBL, 
						BW_MEMBER 
				WHERE   BW_COL_ACCESS.COL_ID = BW_COLUMN.ID 
				AND     BW_COLUMN.BW_TBL_ID = BW_TBL.ID 
				AND     BW_TBL.ID = @TABLE_ID 
				AND     BW_COLUMN.IS_ACTIVE = 1 
				AND     BW_COL_ACCESS.REL = BW_NH_REL.REL 
				AND     BW_NH_REL.TARGET_NH_ID = BW_MEMBER.NEIGHBORHOOD_ID 
				AND     BW_NH_REL.NEIGHBORHOOD_ID = BW_TBL.NEIGHBORHOOD_ID 
				AND     BW_MEMBER.ID = @MEMBER_ID 
				UNION ALL 
				SELECT 	BW_COLUMN.ID,   
						BW_COL_ACCESS.ACCESS_, 
						BW_COL_ACCESS.PREV_ACCESS, 
						BW_COL_ACCESS.TID 
				FROM    BW_COL_ACCESS, BW_COLUMN 
				WHERE 	BW_COL_ACCESS.COL_ID = BW_COLUMN.ID 
				AND     BW_COLUMN.BW_TBL_ID = @TABLE_ID 
				AND     BW_COL_ACCESS.REL = 'PUBLIC' 
				AND     BW_COLUMN.IS_ACTIVE = 1 
				UNION 
				SELECT 	BW_COLUMN.ID,   
						BW_COL_ACCESS.ACCESS_, 
						BW_COL_ACCESS.PREV_ACCESS, 
						BW_COL_ACCESS.TID 
				FROM    BW_COL_ACCESS, BW_COLUMN, BW_TBL, BW_TXS 
				WHERE 	BW_COL_ACCESS.COL_ID = BW_COLUMN.ID 
				AND     BW_COLUMN.BW_TBL_ID = BW_TBL.ID 
				AND     BW_COL_ACCESS.REL = 'CREATOR' 
				AND     BW_TBL.TX_ID = BW_TXS.TX_ID 
				AND     BW_TXS.CREATED_BY= @USER_ID 
				AND     BW_TBL.ID = @TABLE_ID 
				AND     BW_COLUMN.IS_ACTIVE = 1 
			) 	AS COLACCESS 
				GROUP BY ID 
			--DEFAULT PUBLIC ACCESS (WRITE) FOR COLUMNS NOT IN THE ACCESS TABLE 
			INSERT 	INTO @MAX_COL_ACCESS 
			SELECT  BW_COLUMN.ID,   
					2 AS ACCESS_, 
					2 AS PREV_ACCESS, 
					-1 AS TID 
			FROM    BW_COLUMN 
			WHERE   BW_COLUMN.BW_TBL_ID = @TABLE_ID 
			AND     BW_COLUMN.IS_ACTIVE = 1 
			AND     BW_COLUMN.ID NOT IN (SELECT DISTINCT ID FROM @MAX_COL_ACCESS) 
			
			INSERT  INTO @COLUMNACCESS 
			SELECT 	BW_COLUMN.ID AS COLID, 
					MCA.ACCESS_, 
					MCA.PREV_ACCESS, 
					MCA.TID, 
					BW_COLUMN.SEQUENCE_NUMBER 
			FROM 	BW_COLUMN, 
					@MAX_COL_ACCESS MCA 
			WHERE 	BW_COLUMN.ID = MCA.ID 
			AND     MCA.ACCESS_ > 0 
		RETURN 
	END
GO
/****** Object:  UserDefinedFunction [dbo].[GET_KEY_FROM_KEYSTORE]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date, ,>
-- Description:	<Description, ,>
-- =============================================
CREATE FUNCTION [dbo].[GET_KEY_FROM_KEYSTORE]
(
	@KEY_CUBOID INT,
	@DATA_CUBOID INT,
	@KEY_NAME NVARCHAR(1000)
)
RETURNS NVARCHAR(MAX)
AS
BEGIN


--------------------------------------GET PTN KEYS SRC---ADDED ASHISH------------------------

DECLARE @PTN_KEY_CELL_K INTEGER 
DECLARE @PTN_KEY_CELL_C INTEGER 
/* 
SELECT @PTN_KEY_CELL_K = PTN_KEY FROM BW_PTN_CUBOID_TO_PTN_MAP, BW_PTN_ELEMENTS 
WHERE BW_TBL_ID = @KEY_CUBOID 
AND ELEMENT_TYPE_ID = BW_PTN_ELEMENTS.TYPE_ID
AND TYPE_NAME = 'BW_CELL'

--SELECT @PTN_KEY_CELL_C = PTN_KEY FROM BW_PTN_CUBOID_TO_PTN_MAP, BW_PTN_ELEMENTS 
--WHERE BW_TBL_ID = @DATA_CUBOID 
--AND ELEMENT_TYPE_ID = BW_PTN_ELEMENTS.TYPE_ID
--AND TYPE_NAME = 'BW_CELL'

*/
-----------------------------------

	-- Declare the return variable here
	DECLARE @KEY NVARCHAR(MAX)

	--SELECT @KEY = KEYVALUE.STRING_VALUE
	--	FROM	BW_CELL AS KEYNAME, 
	--		BW_CELL AS CUBOID,
	--		BW_CELL AS KEYVALUE, 
	--		BW_COLUMN AS KEYNAMECOL, 
	--		BW_COLUMN AS CUBOIDCOL,
	--		BW_COLUMN AS KEYVALCOL
	--	WHERE KEYNAME.STRING_VALUE =   @KEY_NAME
	--	AND   KEYNAME.BW_COLUMN_ID = KEYNAMECOL.ID 
	--	AND   KEYNAMECOL.NAME =  'KEY_NAME'		
	--	AND   CUBOID.STRING_VALUE =CAST( @DATA_CUBOID AS NVARCHAR(100))
	--	AND   CUBOID.BW_COLUMN_ID = CUBOIDCOL.ID 
	--	AND   CUBOIDCOL.NAME =  'NAME'		
	--	AND   KEYVALUE.BW_COLUMN_ID = KEYVALCOL.ID 
	--	AND   KEYVALCOL.NAME =  'KEY'		
	--	AND KEYNAME.BW_ROW_ID = CUBOID.BW_ROW_ID
	--	AND KEYVALUE.BW_ROW_ID = KEYNAME.BW_ROW_ID
	--	AND CUBOIDCOL.BW_TBL_ID = KEYNAMECOL.BW_TBL_ID	
	--	AND KEYNAMECOL.BW_TBL_ID = KEYVALCOL.BW_TBL_ID
	--	AND KEYVALCOL.BW_TBL_ID = @KEY_CUBOID

	-- Return the result of the function
	--Updated By Sohum2 (Reason : There is an change in NAME column of key store and name of the key store is always uniq.) 
	SELECT @KEY = KEYVALUE.STRING_VALUE
		FROM	BW_CELL AS KEYNAME, 
		--	BW_CELL AS CUBOID,
			BW_CELL AS KEYVALUE, 
			BW_COLUMN AS KEYNAMECOL, 
		--	BW_COLUMN AS CUBOIDCOL,
			BW_COLUMN AS KEYVALCOL
		WHERE KEYNAME.STRING_VALUE =   @KEY_NAME
		AND   KEYNAME.BW_COLUMN_ID = KEYNAMECOL.ID 
		AND   KEYNAMECOL.NAME =  'KeyName'		
	--	AND   CUBOID.STRING_VALUE =CAST( @DATA_CUBOID AS NVARCHAR(100))
--		AND   CUBOID.BW_COLUMN_ID = CUBOIDCOL.ID 
		--AND   CUBOIDCOL.NAME =  'NAME'		
		AND   KEYVALUE.BW_COLUMN_ID = KEYVALCOL.ID 
		AND   KEYVALCOL.NAME =  'KEY'		
		--AND KEYNAME.BW_ROW_ID = CUBOID.BW_ROW_ID
		AND KEYVALUE.BW_ROW_ID = KEYNAME.BW_ROW_ID
		--AND CUBOIDCOL.BW_TBL_ID = KEYNAMECOL.BW_TBL_ID	
		AND KEYNAMECOL.BW_TBL_ID = KEYVALCOL.BW_TBL_ID
		AND KEYVALCOL.BW_TBL_ID = @KEY_CUBOID
		AND		KEYNAME.ACTIVE			= 1 --Added by Lakshman to avoid the duplicates in case of value change
		AND		KEYVALUE.ACTIVE			= 1 --Added by Lakshman to avoid the duplicates in case of value change
		AND		KEYNAMECOL.IS_ACTIVE	= 1 --Added by Lakshman to avoid the duplicates in case of value change
		AND		KEYVALCOL.IS_ACTIVE		= 1 --Added by Lakshman to avoid the duplicates in case of value change

	RETURN @KEY
END
GO
/****** Object:  UserDefinedFunction [dbo].[BW_GetTableTransactions]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS OFF
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[BW_GetTableTransactions] 
		(
			@TABLE_ID 	INT, 
			@SDATE 		DATETIME, 
			@EDATE 		DATETIME,
			@USER_ID 	INT,
			@NH_ID 		INT,
			@VIEW_PREF 	VARCHAR(256) 
		)
		RETURNS @TRANSACTIONS TABLE
		(
			TX_ID 	INT NOT NULL,
			ACTION 	NCHAR (16) NOT NULL
		)
		AS
			BEGIN
				DECLARE @ACCESSIBLE_ROWS TABLE
				(
					ID     		INT,
					TX_ID     	INT,
					OWNER_ID    INT,
					OWNER_TID   INT
				)

				INSERT 	INTO @ACCESSIBLE_ROWS
				SELECT 	* 
				FROM 	BW_GetConsolidationRowSet 
				(
					@TABLE_ID, 
					@USER_ID, 
					@NH_ID, 
					@VIEW_PREF
				)
				INSERT 		INTO @TRANSACTIONS
				/* ROWS ADDED */
				SELECT 		BW_TXS.TX_ID,  
							ACTION='ROWADD'
				FROM 		BW_TXS,
							BW_CELL_STATUS,
							BW_CELL,
							@ACCESSIBLE_ROWS AS BWROW ,
							BW_TBL
				WHERE 		BW_CELL.BW_ROW_ID = BWROW.ID
				AND 		BW_CELL_STATUS.BW_CELL_ID = BW_CELL.ID
				AND 		BW_CELL_STATUS.TX_ID = BW_TXS.TX_ID
				AND 		BW_CELL_STATUS.ACTIVE = 1
				AND 		BW_TXS.CREATED_ON >= @SDATE
				AND 		BW_TXS.CREATED_ON <= @EDATE
				UNION
				/* ROWS DELETED */
				SELECT 		BW_TXS.TX_ID, 
							ACTION='ROWDEL'
				FROM 		BW_TXS,
							BW_CELL_STATUS,
							BW_CELL,
							@ACCESSIBLE_ROWS AS BWROW ,
							BW_TBL
				WHERE 		BW_CELL.BW_ROW_ID = BWROW.ID
				AND 		BW_CELL_STATUS.BW_CELL_ID = BW_CELL.ID
				AND 		BW_CELL_STATUS.TX_ID = BW_TXS.TX_ID
				AND 		BW_CELL_STATUS.ACTIVE = 0
				AND 		BW_TXS.CREATED_ON >= @SDATE
				AND 		BW_TXS.CREATED_ON <= @EDATE
				UNION
				/* STRING VALUE UPDATES */
				SELECT 		BW_TXS.TX_ID,  
							ACTION='CELLUPD'
				FROM 		BW_TXS,
							BW_STRING_VALUE,
							BW_CELL,
							@ACCESSIBLE_ROWS AS BWROW ,
							BW_TBL
				WHERE  		BW_CELL.BW_ROW_ID = BWROW.ID
				AND 		BW_STRING_VALUE.BW_CELL_ID = BW_CELL.ID
				AND 		BW_STRING_VALUE.TX_ID = BW_TXS.TX_ID
				AND 		BW_TXS.CREATED_ON >= @SDATE
				AND 		BW_TXS.CREATED_ON <= @EDATE
				UNION
				/* FORMULA UPDATE */
				SELECT 		BW_TXS.TX_ID,  
							ACTION='FRMUPD'
				FROM  		BW_TXS,
							BW_STRING_VALUE,
							BW_STRVAL_FORMULA,
							BW_CELL,
							@ACCESSIBLE_ROWS AS BWROW,
							BW_TBL
				WHERE  		BW_CELL.BW_ROW_ID = BWROW.ID
				AND 		BW_STRING_VALUE.BW_CELL_ID = BW_CELL.ID
				AND 		BW_STRING_VALUE.ID = BW_STRVAL_FORMULA.STRVAL_ID
				AND 		BW_STRING_VALUE.TX_ID = BW_TXS.TX_ID
				AND 		BW_TXS.CREATED_ON >= @SDATE
				AND 		BW_TXS.CREATED_ON <= @EDATE
				UNION
				SELECT 		BW_TXS.TX_ID,  
							ACTION='COLADD'
				FROM  		BW_TXS,
							BW_COLUMN,
							BW_TBL
				WHERE  		BW_COLUMN.BW_TBL_ID = BW_TBL.ID
				AND 		BW_TBL.ID = @TABLE_ID
				AND 		BW_COLUMN.TX_ID = BW_TXS.TX_ID
				AND 		BW_TXS.CREATED_ON >= @SDATE
				AND 		BW_TXS.CREATED_ON <= @EDATE
				ORDER BY 	BW_TXS.TX_ID
			RETURN
	END
GO
/****** Object:  UserDefinedFunction [dbo].[BW_GetStatusTransactions]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS OFF
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[BW_GetStatusTransactions] 
		(
			@TABLE_ID	INT, 
			@TDATE 		DATETIME 
		)
		RETURNS @TRANSACTIONS TABLE
		(
			TX_ID 	INT,
			CELL_ID INT
		)
		AS
			BEGIN
				INSERT 		INTO @TRANSACTIONS
				SELECT 		MAX(BW_TXS.TX_ID) AS TX_ID, 
							BW_CELL_STATUS.BW_CELL_ID AS CELL_ID
				FROM 		BW_TXS,
							BW_CELL_STATUS,
							BW_CELL,
							BW_ROW,
							BW_TBL
				WHERE  		BW_CELL.BW_ROW_ID = BW_ROW.ID
				AND 		BW_ROW.BW_TBL_ID = BW_TBL.ID
				AND 		BW_TBL.ID = @TABLE_ID
				AND 		BW_CELL.ID = BW_CELL_STATUS.BW_CELL_ID
				AND 		BW_CELL_STATUS.TX_ID = BW_TXS.TX_ID
				AND 		BW_TXS.CREATED_ON <= @TDATE
				GROUP BY 	BW_CELL_STATUS.BW_CELL_ID
				RETURN
			END
GO
/****** Object:  UserDefinedFunction [dbo].[BW_GetChangeTransactionsDelta]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS OFF
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[BW_GetChangeTransactionsDelta] 
		(
			@TABLE_ID 	INT, 
			@SDATE 		DATETIME, 
			@EDATE  	DATETIME 
		)
		RETURNS @TRANSACTIONS TABLE
		(
			TX_ID 	INT,
			CELL_ID INT
		)
		AS
			BEGIN
				INSERT 	INTO @TRANSACTIONS
				/* PICK CELL VERSIONS AT START TID FOR CELLS THAT CHANGED BETWEEN START_TID AND END_TID */
				SELECT 	MAX(BW_TXS.TX_ID) AS TX_ID, 
						SV2.BW_CELL_ID AS CELL_ID
				FROM
				(
					SELECT 	SV1.BW_CELL_ID, 
							SV1.TX_ID
					FROM 	BW_STRING_VALUE SV1, 
							BW_TXS TX1
					WHERE   SV1.TX_ID =  TX1.TX_ID
					AND 	TX1.CREATED_ON > @SDATE
					AND 	TX1.CREATED_ON <= @EDATE
					UNION
					SELECT 	BCS.BW_CELL_ID, 
							BCS.TX_ID
					FROM 	BW_CELL_STATUS BCS, 
							BW_TXS TX1
					WHERE   BCS.TX_ID = TX1.TX_ID
					AND 	TX1.CREATED_ON > @SDATE
					AND 	TX1.CREATED_ON <= @EDATE
				) 		CC,
						BW_TXS,
						BW_CELL,
						BW_STRING_VALUE SV2,
						BW_ROW,
						BW_TBL
				WHERE  	BW_CELL.BW_ROW_ID = BW_ROW.ID
				AND 	BW_ROW.BW_TBL_ID = BW_TBL.ID
				AND 	BW_TBL.ID = @TABLE_ID
				AND 	BW_CELL.ID = CC.BW_CELL_ID
				AND 	BW_CELL.ID = SV2.BW_CELL_ID
				AND 	SV2.TX_ID = BW_TXS.TX_ID
				AND 	BW_TXS.CREATED_ON <= @SDATE
				GROUP 	BY SV2.BW_CELL_ID
				RETURN
		END
GO
/****** Object:  UserDefinedFunction [dbo].[BW_GetChangeTransactions]    Script Date: 09/29/2016 04:44:13 ******/
SET ANSI_NULLS OFF
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[BW_GetChangeTransactions] 
		(
			@TABLE_ID 	INT, 
			@TDATE  	DATETIME 
		)
		RETURNS @TRANSACTIONS TABLE
		(
			TX_ID INT,
			CELL_ID INT
		)
		AS
			BEGIN
				INSERT 		INTO @TRANSACTIONS
				SELECT 		MAX(BW_TXS.TX_ID) AS TX_ID, 
							BW_STRING_VALUE.BW_CELL_ID AS CELL_ID
				FROM  		BW_TXS,
							BW_STRING_VALUE,
							BW_CELL,
							BW_ROW,
							BW_TBL
				WHERE  		BW_CELL.BW_ROW_ID = BW_ROW.ID
				AND 		BW_ROW.BW_TBL_ID = BW_TBL.ID
				AND 		BW_TBL.ID = @TABLE_ID
				AND 		BW_CELL.ID = BW_STRING_VALUE.BW_CELL_ID
				AND 		BW_STRING_VALUE.TX_ID = BW_TXS.TX_ID
				AND 		BW_TXS.CREATED_ON <= @TDATE
				GROUP BY 	BW_STRING_VALUE.BW_CELL_ID
				RETURN
			END
GO
/****** Object:  UserDefinedFunction [dbo].[BW_GET_ID_FROM_NEIGHBORHOOD_HIERARCHY]    Script Date: 5/30/2019 8:21:17 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[BW_GET_ID_FROM_NEIGHBORHOOD_HIERARCHY]
(
	@NEIGHBORHOOD_HIERARCHY VARCHAR(512)
)
RETURNS INT
AS
BEGIN
/*****************************************************************************************************************************
STORED FUNCTION :	BW_GET_ID_FROM_NEIGHBORHOOD_HIERARCHY
----------------
DESCRIPTION		:	This function is created to get Neighborhood Id from Neighborhood Hierarchy
-----------			
           
CREATED BY		:	Lakshman									DATE: Jan 24, 2019
----------
CALLS			:	
-----         		
CALLED BY		:	NONE
---------
DEPENDENCIES	:	NONE
------------
PARAMETER(S) 	:	@NEIGHBORHOOD_HIERARCHY	-- NEIGHBORHOOD HIERARCHY
------------		

USAGE			:	SELECT DBO.BW_GET_ID_FROM_NEIGHBORHOOD_HIERARCHY('ROOT|TEST_DELETE_L1|TEST_DELETE_L2|ABC')
-----
SPECIAL NOTES	:	
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
HISTORY:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
DESCRIPTION                              MODIFIED BY                                            DATE
-----------                              -------- --                                            ----
===================================================================================================================

******************************************************************************************************************** */

	--DECLARE @NEIGHBORHOOD_HIERARCHY VARCHAR(512) = 'ROOT|TEST_DELETE_L1|TEST_DELETE_L2|ABC'

	DECLARE @NH_ID		INT
	DECLARE @NH0_ID		INT
	DECLARE @NH1_ID		INT
	DECLARE @NH2_ID		INT
	DECLARE @NH3_ID		INT

	DECLARE @NH0_NAME	VARCHAR(256)
	DECLARE @NH1_NAME	VARCHAR(256)
	DECLARE @NH2_NAME	VARCHAR(256)
	DECLARE @NH3_NAME	VARCHAR(256)

	------------------------------------------
	--PROCESSING SOURCE NEIGHBORHOOD HIERARCHY
	------------------------------------------
	--Remove extra characters at the end of @NEIGHBORHOOD_HIERARCHY
	SELECT	@NEIGHBORHOOD_HIERARCHY	= LTRIM(RTRIM(@NEIGHBORHOOD_HIERARCHY))

	WHILE	RIGHT(@NEIGHBORHOOD_HIERARCHY,1) = '|'
		SELECT	@NEIGHBORHOOD_HIERARCHY = LEFT(@NEIGHBORHOOD_HIERARCHY,DATALENGTH(@NEIGHBORHOOD_HIERARCHY)-1)
	--SELECT @NEIGHBORHOOD_HIERARCHY

	--Extract NH0 from Source Neighborhood Hierarchy
	IF	CHARINDEX('|',@NEIGHBORHOOD_HIERARCHY) = 0
	BEGIN
		SELECT	@NH0_NAME = @NEIGHBORHOOD_HIERARCHY
	END
	ELSE
	BEGIN
		SELECT	@NH0_NAME = LEFT(@NEIGHBORHOOD_HIERARCHY,CHARINDEX('|',@NEIGHBORHOOD_HIERARCHY)-1)
		SELECT	@NEIGHBORHOOD_HIERARCHY = SUBSTRING(@NEIGHBORHOOD_HIERARCHY,CHARINDEX('|',@NEIGHBORHOOD_HIERARCHY)+1,DATALENGTH(@NEIGHBORHOOD_HIERARCHY))

		--Extract NH1 from Source Neighborhood Hierarchy
		IF	CHARINDEX('|',@NEIGHBORHOOD_HIERARCHY) = 0
		BEGIN
			SELECT	@NH1_NAME = @NEIGHBORHOOD_HIERARCHY
		END
		ELSE
		BEGIN
			SELECT	@NH1_NAME = LEFT(@NEIGHBORHOOD_HIERARCHY,CHARINDEX('|',@NEIGHBORHOOD_HIERARCHY)-1)
			SELECT	@NEIGHBORHOOD_HIERARCHY = SUBSTRING(@NEIGHBORHOOD_HIERARCHY,CHARINDEX('|',@NEIGHBORHOOD_HIERARCHY)+1,DATALENGTH(@NEIGHBORHOOD_HIERARCHY))

			--Extract NH2 from Source Neighborhood Hierarchy
			IF	CHARINDEX('|',@NEIGHBORHOOD_HIERARCHY) = 0
			BEGIN
				SELECT	@NH2_NAME = @NEIGHBORHOOD_HIERARCHY
			END
			ELSE
			BEGIN
				SELECT	@NH2_NAME = LEFT(@NEIGHBORHOOD_HIERARCHY,CHARINDEX('|',@NEIGHBORHOOD_HIERARCHY)-1)

				--Extract NH3 from Source Neighborhood Hierarchy
				SELECT	@NH3_NAME = SUBSTRING(@NEIGHBORHOOD_HIERARCHY,CHARINDEX('|',@NEIGHBORHOOD_HIERARCHY)+1,DATALENGTH(@NEIGHBORHOOD_HIERARCHY))
			END
		END
	END
	--SELECT @NH0_NAME AS SRC_NH0, @NH1_NAME AS SRC_NH1, @NH2_NAME AS SRC_NH2, @NH3_NAME AS SRC_NH3

	------------------------------------------------------
	--Get the Neighborhood Id from the processed hierarchy
	------------------------------------------------------
	--Get the Level 0 Id of NH0
	SELECT	@NH0_ID				= LEVEL_0_ID
	FROM	BW_NH
	WHERE	[NAME]				= @NH0_NAME
	AND		NEIGHBORHOOD_LEVEL	= 0
	AND		IS_ACTIVE			= 1
	--SELECT @NH0_ID

	IF	@NH0_ID IS NOT NULL
	BEGIN
		--if given hierarchy is of Level 0 Neighborhood
		IF	@NH1_NAME IS NULL
		BEGIN
			SELECT	@NH_ID	= NEIGHBORHOOD_ID
			FROM	BW_NH_LEVEL_0
			WHERE	ID		= @NH0_ID
			--SELECT @NH_ID
		END
		ELSE
		BEGIN
			--Get the Level 1 Id of NH1
			SELECT	@NH1_ID				= LEVEL_1_ID
			FROM	BW_NH
			WHERE	[NAME]				= @NH1_NAME
			AND		NEIGHBORHOOD_LEVEL	= 1
			AND		IS_ACTIVE			= 1
			AND		LEVEL_0_ID			= @NH0_ID
			--SELECT @NH1_ID

			IF	@NH1_ID IS NOT NULL
			BEGIN
				--if given hierarchy is of Level 1 Neighborhood
				IF	@NH2_NAME IS NULL
				BEGIN
					SELECT	@NH_ID	= NEIGHBORHOOD_ID
					FROM	BW_NH_LEVEL_1
					WHERE	ID		= @NH1_ID
					--SELECT @NH_ID
				END
				ELSE
				BEGIN
					--Get the Level 2 Id of NH2
					SELECT	@NH2_ID				= LEVEL_2_ID
					FROM	BW_NH
					WHERE	[NAME]				= @NH2_NAME
					AND		NEIGHBORHOOD_LEVEL	= 2
					AND		IS_ACTIVE			= 1
					AND		LEVEL_1_ID			= @NH1_ID
					--SELECT @NH2_ID

					IF	@NH2_ID IS NOT NULL
					BEGIN
						--if given hierarchy is of Level 2 Neighborhood
						IF	@NH3_NAME IS NULL
						BEGIN
							SELECT	@NH_ID	= NEIGHBORHOOD_ID
							FROM	BW_NH_LEVEL_2
							WHERE	ID		= @NH2_ID
							--SELECT @NH_ID
						END
						ELSE
						BEGIN
							--Get the Level 3 Id of NH3
							SELECT	@NH3_ID				= LEVEL_3_ID
							FROM	BW_NH
							WHERE	[NAME]				= @NH3_NAME
							AND		NEIGHBORHOOD_LEVEL	= 3
							AND		IS_ACTIVE			= 1
							AND		LEVEL_2_ID			= @NH2_ID
							--SELECT @NH3_ID

							IF	@NH3_ID IS NOT NULL
							BEGIN
								SELECT	@NH_ID	= NEIGHBORHOOD_ID
								FROM	BW_NH_LEVEL_3
								WHERE	ID		= @NH3_ID
								--SELECT @NH_ID
							END
						END
					END
				END
			END
		END
	END
	--SELECT @NH_ID AS NEIGHBORHOOD_ID

	RETURN @NH_ID
END
GO
/****** Object:  UserDefinedFunction [dbo].[BW_GET_HIERARCHY_FROM_NEIGHBORHOOD_ID]    Script Date: 06/25/2019 02:59:09 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE FUNCTION [dbo].[BW_GET_HIERARCHY_FROM_NEIGHBORHOOD_ID]
(
	@NEIGHBORHOOD_ID INT
)
RETURNS VARCHAR(512)
AS
BEGIN
/*****************************************************************************************************************************
STORED FUNCTION :	BW_GET_HIERARCHY_FROM_NEIGHBORHOOD_ID
----------------
DESCRIPTION		:	This function is created to get Neighborhood Hierarchy from Neighborhood Id
-----------			
           
CREATED BY		:	Lakshman									DATE: Mar 19, 2019
----------
CALLS			:	
-----         		
CALLED BY		:	NONE
---------
DEPENDENCIES	:	NONE
------------
PARAMETER(S) 	:	@NEIGHBORHOOD_ID	-- NEIGHBORHOOD ID
------------		

USAGE			:	SELECT DBO.BW_GET_HIERARCHY_FROM_NEIGHBORHOOD_ID(1)
-----
SPECIAL NOTES	:	
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
HISTORY:
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
DESCRIPTION                              MODIFIED BY                                            DATE
-----------                              -------- --                                            ----
===================================================================================================================

******************************************************************************************************************** */

	--DECLARE @NEIGHBORHOOD_ID		INT = 1
	DECLARE @NEIGHBORHOOD_HIERARCHY	VARCHAR(512)

	--Get Neighborhood IDs
	DECLARE @NH0_ID		INT
	DECLARE @NH1_ID		INT
	DECLARE @NH2_ID		INT
	DECLARE @NH3_ID		INT

	SELECT	@NH0_ID		= LEVEL_0_ID,
			@NH1_ID		= LEVEL_1_ID,
			@NH2_ID		= LEVEL_2_ID,
			@NH3_ID		= LEVEL_3_ID
	FROM	BW_NH
	WHERE	ID			= @NEIGHBORHOOD_ID
	AND		IS_ACTIVE	= 1
	--SELECT @NH0_ID,@NH1_ID,@NH2_ID,@NH3_ID

	--Get Neighborhood Names
	DECLARE @NH0_NAME	VARCHAR(256)
	SELECT	@NH0_NAME	= [NAME]
	FROM	BW_NH_LEVEL_0
	WHERE	ID			= @NH0_ID
	--SELECT @NH0_NAME

	DECLARE @NH1_NAME	VARCHAR(256)
	SELECT	@NH1_NAME	= [NAME]
	FROM	BW_NH_LEVEL_1
	WHERE	ID			= @NH1_ID
	--SELECT @NH0_NAME,@NH1_NAME

	DECLARE @NH2_NAME	VARCHAR(256)
	SELECT	@NH2_NAME	= [NAME]
	FROM	BW_NH_LEVEL_2
	WHERE	ID			= @NH2_ID
	--SELECT @NH0_NAME,@NH1_NAME,@NH2_NAME

	DECLARE @NH3_NAME	VARCHAR(256)
	SELECT	@NH3_NAME	= [NAME]
	FROM	BW_NH_LEVEL_3
	WHERE	ID			= @NH3_ID
	--SELECT @NH0_NAME,@NH1_NAME,@NH2_NAME,@NH3_NAME

	--Get Neighborhood Hierarchy
	IF	@NH0_NAME IS NOT NULL
		SET @NEIGHBORHOOD_HIERARCHY = @NH0_NAME

	IF	@NH1_NAME IS NOT NULL
		SET @NEIGHBORHOOD_HIERARCHY = @NEIGHBORHOOD_HIERARCHY + '|' + @NH1_NAME

	IF	@NH2_NAME IS NOT NULL
		SET @NEIGHBORHOOD_HIERARCHY = @NEIGHBORHOOD_HIERARCHY + '|' + @NH2_NAME

	IF	@NH3_NAME IS NOT NULL
		SET @NEIGHBORHOOD_HIERARCHY = @NEIGHBORHOOD_HIERARCHY + '|' + @NH3_NAME

	RETURN @NEIGHBORHOOD_HIERARCHY
END
GO