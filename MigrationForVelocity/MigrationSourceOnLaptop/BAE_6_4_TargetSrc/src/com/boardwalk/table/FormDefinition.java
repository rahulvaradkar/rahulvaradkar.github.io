package com.boardwalk.table;

import java.util.*;
import java.io.*;

public class FormDefinition
{
	double height;
	double width;
	String title;
	String instr;
	int maxSectionsAtLevel;
	Hashtable sectionByLevel; // level x <list>section obj


	public FormDefinition ()
	{
		sectionByLevel = new Hashtable();
		maxSectionsAtLevel = 1;

	}

	public void setHeight( double h)
	{
		height = h;
	}

	public double getHeight ()
	{
		return height;
	}
	public void setWidth( double w)
	{
		width = w;
	}

	public double getWidth ()
	{
		return width;
	}
	public void setTitle( String t)
	{
		title = t;
	}

	public String getTitle ()
	{
		return title;
	}

	public void setInstructions(String i)
	{
		instr = i;
	}

	public String getInstructions()
	{
		return instr;
	}

	public void setSections(Hashtable sec)
	{
		sectionByLevel = sec;
	}

	public Hashtable getSections()
	{
		return sectionByLevel;
	}


	public int getMaxSectionsAtLevel()
	{
		return maxSectionsAtLevel;
	}


};