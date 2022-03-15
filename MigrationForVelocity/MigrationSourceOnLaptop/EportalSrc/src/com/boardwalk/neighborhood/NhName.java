/*
 * NhName.java
 *
 * Created on June 26, 2002, 8:57 AM
 */

package com.boardwalk.neighborhood;

/**
 *
 * @author  Anuradha Kulkarni
 */
public final class NhName {
    
    public Neighborhood nh = null;
    
    public String name = null;
    
    /** Creates a new instance of NhName */
    public NhName(Neighborhood n, String nme) {
        nh = n;
        name = nme;
    }
    
}
