package me.weyzohorth.UndeadsNight;

public class Wait
{
	static public boolean wait(int time)
	{
		try
		{
			Thread.sleep(time);
			return true;
		}
		catch (InterruptedException err)
		{
			err.printStackTrace();
			return false;
		}
	}
}
