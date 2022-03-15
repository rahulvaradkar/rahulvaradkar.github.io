/*
 * AggregationDefinition.java
 *
 * Created on Jan 2, 2009
 */

package com.boardwalk.table;

import java.util.*;

public class DisAggregationDefinition
{
	public String sourceTable;
	public String sourceFilter;
	public String[] sourceSpecifiedKeyColumns;
	public String[] sourceOtherKeyColumns;
	public String targetTable;
	public String targetFilter;
	public String[] targetKeyColumns;
	public String distributionTable;
	public String distributionFilter;
	public String[] targetColumns;
}
