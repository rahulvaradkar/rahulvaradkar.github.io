package com.boardwalk.model;

import java.util.ArrayList;

import io.swagger.model.Column;

public interface IColumnDal
{
    public interface IColumnFilter { boolean filter(Column c); }

    ArrayList<Column> getColumns(int tableId, IColumnFilter filter) throws Exception;
}
