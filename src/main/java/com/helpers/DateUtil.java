package com.helpers;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Date util abstraction to convert dates
 * 
 * @author mallik
 * 
 */
public final class DateUtil {

	public static String ISO_8601_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZ";

	public static String ISO_8601_DATE_FORMAT_WITH_COLON = "yyyy-MM-dd'T'HH:mm:ssXXX";

	public static String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

	public static String SOLR_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZ'Z'";

	public static String getIso8601Format(long datetime) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(datetime);
		DateFormat iso8601Format = new SimpleDateFormat(ISO_8601_DATE_FORMAT);
		String iso8601 = iso8601Format.format(cal.getTime());
		return iso8601;
	}

	public static String getIso8601FormatWithColon(long datetime) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(datetime);
		DateFormat iso8601FormatWithColon = new SimpleDateFormat(
				ISO_8601_DATE_FORMAT_WITH_COLON);
		String iso8601 = iso8601FormatWithColon.format(cal.getTime());
		return iso8601;
	}

	public static Date getDateFormat(long datetime) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(datetime);
		Date date = cal.getTime();
		return date;
	}

	public static long getTimeFromIso8601Format(String dateStr) {
		Date date;
		try {
			DateFormat iso8601Format = new SimpleDateFormat(
					ISO_8601_DATE_FORMAT);
			date = iso8601Format.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return date.getTime();
	}

	public static long getTimeFrom(String dateStr, String format) {
		Date date;
		try {
			DateFormat iso8601Format = new SimpleDateFormat(format);
			date = iso8601Format.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return date.getTime();
	}

	public static long getTimeFromIso8601FormatWithColon(String dateStr) {
		Date date;
		try {
			DateFormat iso8601FormatWithColon = new SimpleDateFormat(
					ISO_8601_DATE_FORMAT_WITH_COLON);
			date = iso8601FormatWithColon.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return date.getTime();
	}

	public static String getSolrFormat(long datetime) {
		DateFormat format = new SimpleDateFormat(SOLR_DATE_FORMAT);
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		String solrDate = format.format(datetime);
		return solrDate;
	}

	public static long getTimeFromTwitterFormat(String dateStr) {
		Date date;
		try {
			DateFormat twitterDateFormat = new SimpleDateFormat(
					TWITTER_DATE_FORMAT);
			date = twitterDateFormat.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return date.getTime();
	}

	public static long getTimeFromSolrFormat(String dateStr) {
		Date date;
		try {
			if (dateStr.endsWith("Z")) {
				dateStr = dateStr.replaceAll("Z", "").trim();
			}
			DateFormat solrDateFormat = new SimpleDateFormat(
					"yyyy-MM-dd'T'HH:mm:ss");
			solrDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			date = solrDateFormat.parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		return date.getTime();
	}

	/**
	 * Start and end will be in secs
	 * 
	 * @param start
	 * @param end
	 * @return
	 */
	public static boolean isCurrentTimeBetweenSec(long start, long end) {
		boolean flag = false;
		long currentSec = System.currentTimeMillis() / 1000;
		if (currentSec >= start && currentSec <= end) {
			flag = true;
		}
		return flag;
	}

	public static long convertTo(Object dateObj) {
		long timestamp = 0;
		if (dateObj != null) {
			if (dateObj instanceof Double) {
				timestamp = ((Double) dateObj).longValue();
			} else if (dateObj instanceof Integer) {
				timestamp = ((Integer) dateObj).longValue();
			} else if (dateObj instanceof Long) {
				timestamp = (Long) dateObj;
			}
		}
		return timestamp;
	}

	/**
	 * Accept time format like 1m, 2m etc., or 1y, 2y etc.,
	 * 
	 * @param time
	 * @return
	 */
	public static long dateFrom(String time) {
		Calendar cal = Calendar.getInstance();
		if (time.endsWith("m")) {
			String temp = time.replace("m", "");
			Integer month = Integer.parseInt(temp.trim());
			cal.add(Calendar.MONTH, -month);
		} else if (time.endsWith("y")) {
			String temp = time.replace("y", "");
			Integer month = Integer.parseInt(temp.trim());
			cal.add(Calendar.YEAR, -month);
			int calmonth = cal.get(Calendar.MONTH);
			cal.set(Calendar.MONTH, calmonth + 1);
			cal.set(Calendar.DAY_OF_MONTH,
					cal.getActualMinimum(Calendar.DAY_OF_MONTH));
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.HOUR_OF_DAY, 0);
		}
		return cal.getTimeInMillis();
	}

	public static String getCustomFormat(String format, long datetime) {
		DateFormat dateFormat = new SimpleDateFormat(format);
		String dateStr = dateFormat.format(datetime);
		return dateStr;
	}

	public static long getDayStartTime() {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(System.currentTimeMillis());
		cal.set(Calendar.AM_PM, Calendar.AM);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 1);
		return cal.getTimeInMillis();
	}

	public static long[] getStartAndEndTimeOfDay(long time) {
		Calendar start = Calendar.getInstance();
		start.setTimeInMillis(time);
		start.set(Calendar.AM_PM, Calendar.AM);
		start.set(Calendar.HOUR, 0);
		start.set(Calendar.MINUTE, 0);
		start.set(Calendar.SECOND, 0);
		start.set(Calendar.MILLISECOND, 0);

		Calendar end = Calendar.getInstance();
		end.setTimeInMillis(time);
		end.set(Calendar.AM_PM, Calendar.PM);
		end.set(Calendar.HOUR, 11);
		end.set(Calendar.MINUTE, 59);
		end.set(Calendar.SECOND, 59);
		end.set(Calendar.MILLISECOND, 999);

		long[] result = { start.getTimeInMillis(), end.getTimeInMillis() };
		return result;
	}
}
