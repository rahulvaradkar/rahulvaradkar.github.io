package com.boardwalk.dal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import com.boardwalk.model.IColumnDal;

import io.swagger.model.Column;

public class ColumnDal implements IColumnDal {

    private Connection conn = null;
    
    public ColumnDal(Connection c) {
        this.conn = c;
    }

    @Override
    public ArrayList<Column> getColumns(int tableId, IColumnFilter filter) throws Exception {
        ResultSet rs = null;
        PreparedStatement ps = null;
        String query = "select bwcol.ID, bwcol.NAME, bwcol.BW_TBL_ID, bwcol.SEQUENCE_NUMBER, bwcol.TX_ID from BW_COLUMN bwcol where bwcol.BW_TBL_ID = ? ";
        ArrayList<Column> result = new ArrayList<>();
        try {
            ps = this.conn.prepareStatement(query);
            ps.setInt(1, tableId);
            
            rs = ps.executeQuery();
            while (rs.next()) {
                Column c = new Column().id(rs.getInt(1)).name(rs.getString(2)).seqNo(rs.getFloat(4)).tid(rs.getInt(5));
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
