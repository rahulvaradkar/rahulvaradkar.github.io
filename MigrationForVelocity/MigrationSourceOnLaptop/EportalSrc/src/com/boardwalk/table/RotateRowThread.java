package com.boardwalk.table;

import java.util.*;

public class RotateRowThread implements Runnable
{
	Thread m_thread;
	String m_threadName;
	Vector m_cellsForRow;
	int m_tableId;

	public RotateRowThread(String a_threadName, int a_tableId, Vector a_cellsForRow)
	{
		m_threadName = a_threadName;
		m_tableId = a_tableId;
		m_thread = new Thread(this, a_threadName);
		m_thread.start();

	}

	public void run()
	{
		try
		{
			for (int i = 0; i < m_cellsForRow.size(); i++)
			{
				System.out.println("Thread Name = " + m_threadName);
			}

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		System.out.println(m_threadName + " exiting ");
	}

	public Thread getThread()
	{
		return m_thread;
	}

}