package com.boardwalk.dal;

import java.sql.Connection;

import com.boardwalk.model.IDalWrapper;
import com.boardwalk.model.ICellDal;
import com.boardwalk.model.IColumnDal;

public class DalWrapper
    implements IDalWrapper
{
    private Connection conn = null;

    public DalWrapper(Connection c)
    {
        this.conn = c;
    }

    @Override
    public ICellDal getCellDal() {
        return new CellDal(this.conn);
    }

    public IColumnDal getColumnDal() {
        return new ColumnDal(this.conn);
    }
}
