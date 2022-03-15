/*
 * AggregationDefinition.java
 *
 * Created on May 22, 2007
 */

package com.boardwalk.table;

import java.util.*;

public class AggregationDefinition
{
	public String sourceTable;
	public String sourceFilter;
	public String[] targetColumns;
	public String[] sourceColumns;
	public String operator;
	public String[] groupByColumns;
	public String type;
	public String targetTable;
	public String targetFilter;
	public String Transform;
}
