package me.weyzohorth.UndeadsNight;

public class Type<T> implements IType
{
	public enum etype
	{
		BOOL,
		INT,
		DOUBLE,
		LONG,
		UNKNOWN
	}
	private Object value;
	private etype type;
	
	Type()
	{
		type = etype.UNKNOWN;
	}
	
	Type(Object val)
	{
		set(val);
	}
	
	public void set(Object val)
	{
		if (val.getClass() == Boolean.class)
			type = etype.BOOL;
		else if (val.getClass() == Integer.class)
			type = etype.INT;
		else if (val.getClass() == Double.class)
			type = etype.DOUBLE;
		else if (val.getClass() == Long.class)
			type = etype.LONG;
		else
			type = etype.UNKNOWN;
		value = val;
	}
	
	public Object get()
	{
		return value;
	}
	
	public etype type()
	{
		return type;
	}
}