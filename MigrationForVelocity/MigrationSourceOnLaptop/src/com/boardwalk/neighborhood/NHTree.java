package com.boardwalk.neighborhood;

import java.util.*;

public class NHTree {
    Neighborhood nh = new Neighborhood();
    Vector children = new Vector();
    String name = new String();

    public NHTree (Neighborhood m, Vector mch, String nme){
		nh = m;
		children = mch;
        name = nme;
    }

    public Neighborhood getNeighborhood() {
	return nh;
    }

    public Vector getChildren() {
	return children;
    }

    public String getName () {
        return name;
    }

}




