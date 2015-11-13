package com.jadg.mydiabetes.middleHealth.utils;

import com.jadg.mydiabetes.ui.listAdapters.GlycemiaDataBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Utils
{
	public static Date getDateFromTimestamp(long timeInMilliseconds)
	{
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(timeInMilliseconds);
		return calendar.getTime();
	}

	public static List<Long> getTimestampsFromDateFormat(List<GlycemiaDataBinding> values, String dateFormat)
	{
		List<Long> timestamps = new ArrayList<>();

		DateFormat convertFromStringToDate = new SimpleDateFormat(dateFormat);
		try
		{
			for (GlycemiaDataBinding value : values)
			{
				String dateString = value.getDate() + " " + value.getTime();

				long timestamp = convertFromStringToDate.parse(dateString).getTime();
				timestamps.add(timestamp);
			}
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}

		return timestamps;
	}
}
