package com.boardwalk.query;

import java.util.*;
import java.io.*;
import java.text.*;

import com.boardwalk.table.*;


public class DeltaValue
{


	public int result = -10;
	public String difference = "";
	double dvOld;
	double dvNew;

	public DeltaValue( int m_result, String m_difference, double oldValue, double newValue )
	{
		result = m_result;
		difference = m_difference;
		dvOld = oldValue;
		dvNew = newValue;
	}
	public DeltaValue( int m_result, String m_difference)
	{
		result = m_result;
		difference = m_difference;
		dvOld = 0;
		dvNew = 0;
	}

	public double getOldDblValue()
	{
		return dvOld;
	}

	public double getNewDblValue()
	{
		return dvNew;
	}

};