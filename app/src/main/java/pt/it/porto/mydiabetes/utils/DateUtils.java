package pt.it.porto.mydiabetes.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
	public static final String ISO8601_FORMAT = "yyyy-MM-dd HH:mm";
	public static final String ISO8601_FORMAT_SECONDS = "yyyy-MM-dd HH:mm:ss";
	public static final String DATE_FORMAT = "yyyy-MM-dd";
	public static final String TIME_FORMAT = "HH:mm";
	public static final String TIME_FORMAT_SECONDS = "HH:mm:ss";
	public static final SimpleDateFormat iso8601Format = new SimpleDateFormat(ISO8601_FORMAT, LocaleUtils.ENGLISH_LOCALE);
	public static final SimpleDateFormat iso8601FormatSeconds = new SimpleDateFormat(ISO8601_FORMAT_SECONDS, LocaleUtils.ENGLISH_LOCALE);
	public static final SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, LocaleUtils.ENGLISH_LOCALE);
	public static final SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, LocaleUtils.ENGLISH_LOCALE);
	public static final SimpleDateFormat timeFormatSeconds = new SimpleDateFormat(TIME_FORMAT_SECONDS, LocaleUtils.ENGLISH_LOCALE);

	public static Calendar parseDateTime(String dateTime) throws ParseException {
		Calendar calendar = Calendar.getInstance();
		Date date;
		if (dateTime.length() == ISO8601_FORMAT.length()) {
			date = iso8601Format.parse(dateTime);
		} else {
			date = iso8601FormatSeconds.parse(dateTime);
		}
		calendar.setTime(date);
		return calendar;
	}

	public static String formatToDb(Calendar calendar) {
		return iso8601FormatSeconds.format(calendar.getTime());
	}


	public static String getFormattedDate(Calendar calendar) {
		return dateFormat.format(calendar.getTime());
	}

	/**
	 * //TODO change to throw exception
	 *
	 * @param date
	 * @return null if unable to parse
	 */
	public static Calendar getDateCalendar(String date) {
		Calendar calendar = Calendar.getInstance();
		try {
			calendar.setTime(dateFormat.parse(date));
			return calendar;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * //TODO change to throw exception
	 *
	 * @param date
	 * @return null if unable to parse
	 */
	public static Calendar getTimeCalendar(String date) {
		Calendar calendar = Calendar.getInstance();
		try {
			if (date.length() == TIME_FORMAT.length()) {
				calendar.setTime(timeFormat.parse(date));
			} else {
				calendar.setTime(timeFormatSeconds.parse(date));
			}
			return calendar;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getFormattedTime(Calendar calendar) {
		return timeFormat.format(calendar.getTime());
	}

	public static Calendar getDateTime(String date, String time) throws ParseException {
		return parseDateTime(date+" "+time);
	}
}
