// Decompiled by Decafe PRO - Java Decompiler
// Classes: 1   Methods: 6   Fields: 4

package com.boardwalk.neighborhood;


public abstract class NeighborhoodLevel {
    
    int m_id;
    int m_nhid;
    String m_name;
    boolean m_isSecure;
    boolean m_isActive;
    
    public NeighborhoodLevel(String s, int id, int nhid, boolean isSecure, boolean isActive) {
        m_id = id;
        m_nhid = nhid;
        m_name = s;
        m_isSecure = isSecure;
        m_isActive = isActive;
    }
    
    public String getName() {
        return m_name;
    }
    
    public int getId() {
        return m_id;
    }
    
    public int getNhId() {
        return m_nhid;
    }
    
    public boolean isSecure() {
        return m_isSecure;
    }
    
    public boolean isActive() {
        return m_isActive;
    }
    
    public abstract int getLevel();
    
    public boolean isEquals( NeighborhoodLevel nlx ) {
        if ( m_id == nlx.getId() && m_nhid == nlx.getNhId() && m_name.equals(nlx.getName()) && m_isSecure == nlx.isSecure() && m_isActive==nlx.isActive() )
            return true;
        else
            return false;
        
        
    }
    
    public String toString() {
        
        String nh = " NeighborhoodLevel = " + getLevel() + "\n" + "Neighborhood_Level_id = " + getId() + "\n" + "Neighborhood_Id = " + getNhId() +  "\n" + "Neighborhood_Name = " + getName() +  "\n" + " isActive = " + isActive() +  "\n" + " isSecure = " + isSecure();
        return nh;
        
        
    }
    
    
}
