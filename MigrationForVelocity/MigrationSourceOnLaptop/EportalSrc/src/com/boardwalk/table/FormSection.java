package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class FormSection
{
    String name;
    int level;
    Vector properties;
    Hashtable fieldsByLevel; // fieldname x field obj
    int maxFieldsAtLevel;

	public FormSection()
	{
		fieldsByLevel = new Hashtable();
		level = 0;
		maxFieldsAtLevel = 1;
	}

	public String getName()
	{
		return name;
	}

	public int getLevel()
	{
		return level;
	}

	public Hashtable getFields()
	{
		return fieldsByLevel;
	}

	public Vector getProperties()
	{
			return properties;
	}
	public int getMaxFieldsAtLevel()
	{
		return maxFieldsAtLevel;
	}
};