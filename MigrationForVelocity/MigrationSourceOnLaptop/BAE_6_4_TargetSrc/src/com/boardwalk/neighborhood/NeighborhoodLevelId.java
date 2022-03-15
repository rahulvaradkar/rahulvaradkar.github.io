// Decompiled by Decafe PRO - Java Decompiler
// Classes: 1   Methods: 12   Fields: 7

package com.boardwalk.neighborhood;

public class NeighborhoodLevelId extends  NeighborhoodId
{
    int m_level_id;
    int m_level;

    public NeighborhoodLevelId(int id, String a_name, int level_id, int level)
    {
       super( id, a_name);
       m_level_id = level_id;
       m_level = level;
    }


    public int getLevelId()
    {
        return m_level_id;
    }

    public int getLevel()
    {
        return m_level;
    }
}
