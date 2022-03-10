package com.boardwalk.excel;

import java.util.*;
import java.io.*;
import com.boardwalk.util.*;

public class xlColumn_import
{
	int id_;
	String name_;
	float sequenceNumber_;
	int tid_;
	int access_;
	int prevAccess_;
	int accessTid_;
	int refColId_;
	int lkpColId_;
	int lkpTblId_;
	String attr_;



    public xlColumn_import(
				int id,
				String name,
				float sequenceNumber,
				int tid,
				int access,
				int prevAccess,
				int accessTid,
				int refColId,
				String attr
				)
	{
		id_ = id;
		name_ = name;
		sequenceNumber_ = sequenceNumber;
		tid_ = tid;
		access_ = access;
		prevAccess_ = prevAccess;
		accessTid_ = accessTid;
		refColId_ = refColId;
		lkpColId_ = -1;
		lkpTblId_ = -1;
		attr_ = attr;
    }
	public xlColumn_import(
			int id,
			String name,
			float sequenceNumber,
			int tid,
			int access,
			int prevAccess,
			int accessTid,
			int refColId,
			int lkpColId,
			int lkpTblId,
			String attr
			)
	{
		id_ = id;
		name_ = name;
		sequenceNumber_ = sequenceNumber;
		tid_ = tid;
		access_ = access;
		prevAccess_ = prevAccess;
		accessTid_ = accessTid;
		refColId_ = refColId;
		lkpColId_ = lkpColId;
		lkpTblId_ = lkpTblId;
		attr_ = attr;
	}
    public int getId ()
    {
		return id_;
    }

    public String getName()
    {
        return name_;
    }

	public String getAttributes()
	{
		return attr_;
	}

	public float getSequenceNumber()
	{
		return sequenceNumber_;
	}

	public int getCreationTid()
	{
		return tid_;
	}

	public int getAccess()
	{
		return access_;
	}

	public int getPrevAccess()
	{
		return prevAccess_;
	}

	public int getAccessTid()
	{
		return accessTid_;
	}

	public int getSourceColumnId()
	{
		return refColId_;
	}
	public int getLookupColumnId()
	{
		return lkpColId_;
	}
	public int getLookupTableId()
	{
		return lkpTblId_;
	}

    public boolean equals(Object c)
    {
		if (((xlColumn)c).getId() == id_)
			return true;
		else
			return false;
    }

    public int hashCode() {
	return id_;
    }

};


