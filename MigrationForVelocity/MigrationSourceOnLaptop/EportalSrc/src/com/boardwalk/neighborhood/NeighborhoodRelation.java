// Decompiled by Decafe PRO - Java Decompiler
// Classes: 1   Methods: 12   Fields: 7

package com.boardwalk.neighborhood;

public class NeighborhoodRelation
{
    int    m_id;
    int    m_nh_id;
    int    m_trg_nh_id;
    int    m_relid;
    String m_relation;

    public NeighborhoodRelation(int trg_nh_id,int relid, String relation)
    {
//        m_id = id;
 //       m_nh_id = nh_id;
        m_trg_nh_id = trg_nh_id;
        m_relid = relid;
        m_relation = relation;

    }
/*
    public int getNhId()
    {
        return m_nh_id;
    }
*/
    public int getTargetNhId()
    {
        return m_trg_nh_id;
    }

    public int getRelationId()
    {
        return m_relid;
    }

    public String getRelation()
    {
        return m_relation;
    }

}
