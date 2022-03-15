package com.boardwalk.model;

import java.util.ArrayList;

import io.swagger.model.Cell;

public interface ICellDal
{
    public interface ICellFilter { boolean filter(Cell cell); }

    ArrayList<Cell> getCellsByColumnIdsSortedByRC(int tableId, Integer[] columnIds, ICellDal.ICellFilter filter) throws Exception;
}
