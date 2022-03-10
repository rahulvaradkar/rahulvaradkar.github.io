package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class FormField
{
	String fieldName;
	String label;
	int level;
    Vector properties;

    public FormField () {}

    public FormField (String n)
    {
		fieldName = n;
		level = 0;
		properties = new Vector();
	}

	public String getName()
	{
		return fieldName;
	}

	public int getLevel()
	{
		return level;
	}

	public String getLabel()
	{
		return label;
	}

	public Vector getProperties()
	{
		return properties;
	}
};