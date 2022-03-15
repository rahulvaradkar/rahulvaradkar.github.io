package com.boardwalk.model;

import java.util.HashMap;

public interface IRowCriteria
{
    final String ResultTypeTable = "TABLE";
    final String ResultTypeResultSet = "RESULTSET";
    String getAccessibleRowSet(HashMap criteria, int a_table_id, boolean active, String resultType);
}
