package com.boardwalk.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.boardwalk.model.ICellDal;

import io.swagger.model.Cell;

public class CellDal
    implements ICellDal
{
    Connection conn = null;
    public CellDal(Connection c)
    {
        this.conn = c;
	}

	@Override
    public ArrayList<Cell> getCellsByColumnIdsSortedByRC(int tableId, Integer[] columnIds, ICellFilter filter) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        ArrayList<Cell> result = new ArrayList<>();

        StringBuffer query = new StringBuffer("select bwcll.ID, bwcll.BW_ROW_ID, bwcll.BW_COLUMN_ID, bwcll.STRING_VALUE from BW_CELL bwcll inner join BW_COLUMN bwcol on bwcol.ID = bwcll.BW_COLUMN_ID where bwcol.BW_TBL_ID = ? ");
        if (columnIds.length > 0) {
            query.append("and bwcol.ID in ( ?");
            for (int ctr = 1; ctr < columnIds.length; ctr++) {
                query.append(", ?");
            }
            query.append(") ");
        }
        query.append(" order by bwcll.BW_ROW_ID, bwcll.BW_COLUMN_ID ");
        try {
            ps = conn.prepareStatement(query.toString());
            ps.setInt(1, tableId);
            if (columnIds.length > 0) {
                ps.setInt(2, columnIds[0]);
                for (int ctr = 1; ctr < columnIds.length; ctr++) {
                    ps.setInt(ctr+2, columnIds[ctr]);
                }
            }
            rs = ps.executeQuery();

            while (rs.next()) {
                Cell c = new Cell().id(rs.getInt(1)).rowId(rs.getInt(2)).colId(rs.getInt(3)).cellValue(rs.getString(4));
                if (filter.filter(c)) {
                    result.add(c);
                }
            }
        } finally {
            rs.close();
            ps.close();
        }
        return result;
    }
}
