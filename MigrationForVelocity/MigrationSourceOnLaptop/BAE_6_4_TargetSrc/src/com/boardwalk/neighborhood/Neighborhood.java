// Decompiled by Decafe PRO - Java Decompiler
// Classes: 1   Methods: 12   Fields: 7

package com.boardwalk.neighborhood;
import java.util.*;

public class Neighborhood
{
    int m_id;
    int m_levels;
    int m_level0id;
    int m_level1id;
    int m_level2id;
    int m_level3id;
    boolean m_secure;
    String m_name;
    Vector m_relations;
    int m_managedby;

    public Neighborhood(int id, int level, int level0Id, int level1Id, int level2Id, int level3Id, boolean isSecure, String a_name)
    {
        m_id = id;
        m_levels = level;
        m_level0id = level0Id;
        m_level1id = level1Id;
        m_level2id = level2Id;
        m_level3id = level3Id;
        m_secure = isSecure;
        m_name = a_name;
        m_relations = null;
    }

    public Neighborhood()
    {
    }
    public void setId(int id)
    {
      m_id = id;
    }
    public void setRelations(Vector relations)
   {
     m_relations = relations;
   }

    public int getLevel0Id()
    {
        return m_level0id;
    }

    public int getLevel1Id()
    {
        return m_level1id;
    }

    public int getLevel2Id()
    {
        return m_level2id;
    }

    public int getLevel3Id()
    {
        return m_level3id;
    }

    public int getId()
    {
        return m_id;
    }

    public int getLevels()
    {
        return m_levels;
    }

    public boolean isSecure()
    {
        return m_secure;
    }

    public boolean isPeer(Neighborhood neighborhood)
    {
        if(neighborhood.getLevels() == m_levels)
            switch(m_levels)
            {
            case 0: // '\0'
                return false;

            case 1: // '\001'
                if(neighborhood.getLevel0Id() == m_level0id)
                    return true;
                // fall through

            case 2: // '\002'
                if(neighborhood.getLevel1Id() == m_level2id)
                    return true;
                // fall through

            default:
                return false;
            }
        else
            return false;
    }

    public boolean isParent(Neighborhood neighborhood)
    {
        if(m_levels == 0)
            return false;
        int i = neighborhood.getLevels();
        if(m_levels <= i)
            return false;
        switch(m_levels)
        {
        default:
            break;

        case 1: // '\001'
            if(i == 0 && neighborhood.getLevel0Id() == m_level0id)
                return true;
            break;

        case 2: // '\002'
            if(i == 1 && neighborhood.getLevel1Id() == m_level1id)
                return true;
            break;
        }
        return false;
    }

    public boolean isChild(Neighborhood neighborhood)
    {
        if(m_levels == 2)
            return false;
        int i = neighborhood.getLevels();
        if(m_levels <= i)
            return false;
        switch(m_levels)
        {
        default:
            break;

        case 0: // '\0'
            if(i == 1 && neighborhood.getLevel0Id() == m_level0id)
                return true;
            break;

        case 1: // '\001'
            if(i == 2 && neighborhood.getLevel1Id() == m_level1id)
                return true;
            break;
        }
        return false;
    }

    public String getName()
    {
        return m_name;
    }
    public Vector getRelations()
    {
        return m_relations;
    }

    public void setManagedby(int managedby)
	{
	  m_managedby = managedby;
    }

    public int getManagedby()
	{
		 return m_managedby;
    }

}
