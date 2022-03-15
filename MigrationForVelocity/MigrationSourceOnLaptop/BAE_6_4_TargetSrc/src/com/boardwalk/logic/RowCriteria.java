package com.boardwalk.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import com.boardwalk.model.ICellDal;
import com.boardwalk.model.IColumnDal;
import com.boardwalk.model.IDalWrapper;
import com.boardwalk.model.IRowCriteria;
import com.boardwalk.table.FilterCritereon;

import io.swagger.model.Cell;
import io.swagger.model.Column;

public class RowCriteria implements IRowCriteria {

    IDalWrapper dw = null;
    ArrayList<Integer> resultList = null;

    public RowCriteria(IDalWrapper d, ArrayList<Integer> rList) {
        dw = d;
        this.resultList = rList;
    }

    // get list of columns from filter criteria
    // retrieve columns, map column names to column ids
    // retrieve cells, order by row-id + column-id
    // compare with hashmap of filter criteria
    // store matching rows, discard/ ignore unmatched rows
    // construct query for accessible row temp table and return
    @Override
    public String getAccessibleRowSet(HashMap criteria, int a_table_id, boolean active,
            String resultType) {

        HashMap<String, Column> mapColumnsByNameUpper = new HashMap<>(); //Modified by Rushabh on 20191015 to fix the Issue Id: 17359
        HashMap<Integer, Column> mapColumnsById = new HashMap<>();
        FilterCritereon[][] fcList = new FilterCritereon[criteria.size()][];
        HashMap<Integer, Object> accessibleRowIdsList = new HashMap<>();
        StringBuffer result = new StringBuffer();

        try {
            // retrieve columns, map column names to column ids
            IColumnDal dalCol = dw.getColumnDal();
            dalCol.getColumns(a_table_id, (c) -> {
                mapColumnsByNameUpper.put(c.getName().toUpperCase(), c); //Modified by Rushabh on 20191015 to fix the Issue Id: 17359
                return false;
            });

            // get list of columns from filter criteria
            int ctr = 0;
            for (Object objList : criteria.values()) {
                ArrayList listFc = (ArrayList)objList;
                FilterCritereon[] arrFc = (FilterCritereon[])listFc.toArray(new FilterCritereon[listFc.size()]);
                fcList[ctr] = arrFc;
                ctr++;

                for (int inner = 0; inner < arrFc.length; inner++) {
                    FilterCritereon fc = arrFc[inner];
                    fc.settrgtColumnId(mapColumnsByNameUpper.get(fc.getColumnName().toUpperCase()).getId()); //Modified by Rushabh on 20191015 to fix the Issue Id: 17359
                    fc.prepare();
                    mapColumnsById.put(fc.gettrgtColumnId(), null);
                }
            }

            // retrieve cells, order by row-id + column-id
            ICellDal dalCell = dw.getCellDal();
            SingleRowData current = new SingleRowData();
            dalCell.getCellsByColumnIdsSortedByRC(a_table_id, mapColumnsById.keySet().toArray(new Integer[mapColumnsById.size()]), (cell) -> {
                if (current.RowId != cell.getRowId()) {
                    processRowForFilterCriteria(accessibleRowIdsList, fcList, current);
                    current.Reset(cell.getRowId());
                }
                current.mapCells.put(cell.getColId(), cell);
                return false;
            });
            processRowForFilterCriteria(accessibleRowIdsList, fcList, current);

            // generate query
			if (ResultTypeTable.equals(resultType)) {
                result.append(" CREATE TABLE #ACCESSIBLE_ROWS (ROWID  INT  PRIMARY KEY NOT NULL) ");
                if (accessibleRowIdsList.size() > 0) {
                    Iterator<Integer> iter = accessibleRowIdsList.keySet().iterator();

                    // generate inserts in sets of 1000 records
                    while (iter.hasNext()) {
                        int setCtr = 1;
                        Integer nextInt = iter.next();
                        if (this.resultList != null) {
                            this.resultList.add(nextInt);
                        }
                        result.append(" INSERT INTO #ACCESSIBLE_ROWS(ROWID) VALUES (");
                        result.append(Integer.toString(nextInt));
                        result.append(")");

                        while (iter.hasNext() && setCtr < 1000) {
                            nextInt = iter.next();
                            if (this.resultList != null) {
                                this.resultList.add(nextInt);
                            }
                            result.append(", (");
                            result.append(Integer.toString(nextInt));
                            result.append(")");
                            setCtr++;
                        }
                    }
                }
            } else {
                // QUERY GENERATION NOT REQUIRED FOR RESULT-TYPE-RESULTSET
                if (accessibleRowIdsList.size() > 0) {
                    Iterator<Integer> iter = accessibleRowIdsList.keySet().iterator();
                    Integer nextInt = iter.next();
                    if (this.resultList != null) {
                        this.resultList.add(nextInt);
                    }
                    while (iter.hasNext()) {
                        nextInt = iter.next();
                        if (this.resultList != null) {
                            this.resultList.add(nextInt);
                        }
                    }
                }
            }
        } catch (Exception exc) {
            System.out.println("Error in AccessibleRowSet: " + exc.getMessage());
            exc.printStackTrace();
        }

        return result.toString();
    }

    private class SingleRowData {
        public int RowId = -1;
        public HashMap<Integer, Cell> mapCells = new HashMap<>();

        public void Reset(int newRowId) {
            this.RowId = newRowId;
            mapCells.clear();
        }
    }

    private void processRowForFilterCriteria(HashMap<Integer, Object> accessibleRowIdsList, FilterCritereon[][] fcList,
            SingleRowData row) {
        boolean match = false; // if the boolean is initialized to true, add else clause below
        
        // iterate and process all FcSets till you find a match
        if (row.RowId != -1)
        {
            for (int outer = 0; outer < fcList.length && !match; outer++) {
                FilterCritereon[] fcSet = fcList[outer];
                boolean fcMatch = true;
                for (int inner = 0; inner < fcSet.length && fcMatch; inner++) {
                    FilterCritereon currFc = fcSet[inner];
                    Cell cell = row.mapCells.get(currFc.gettrgtColumnId());
                    fcMatch = fcMatch && currFc.processCell(cell == null ? null : cell.getCellValue());
                }
                match = match || fcMatch;
            }
        }

        if (match) {
            accessibleRowIdsList.put(row.RowId, null);
        }
    }
}
