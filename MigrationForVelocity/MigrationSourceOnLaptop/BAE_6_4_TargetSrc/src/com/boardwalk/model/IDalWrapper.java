package com.boardwalk.model;

// DalWrapper required to manage database connection object
// across all structured DAL implementations
public interface IDalWrapper
{
    ICellDal getCellDal();
    IColumnDal getColumnDal();
}