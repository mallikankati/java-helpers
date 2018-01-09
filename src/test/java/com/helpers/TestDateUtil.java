package com.helpers;

import java.math.RoundingMode;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

public class TestDateUtil {

	private static final Logger logger = Logger.getLogger(TestDateUtil.class.getName());

	// @Test
	public void testJsonUtil() {
		String json = "{\"id\": \"852735478090263_852878981409246\","
				+ "\"message\": \"You so silly! Of course. No mommy and family without you! \u003C3 Mimi\","
				+ "\"like_count\": 1,"
				+ "\"from\": { \"category\": \"Public figure\",\"name\": \"Mimi the Rescue\",\"id\": \"331654090198407\"},"
				+ "\"created_time\": \"2014-11-06T00:14:54+0000\","
				+ "\"parent\": {\"id\": \"852735478090263_852772271419917\","
				+ "\"from\": {\"id\": \"10152580352678585\",\"name\": \"Brian Thompson\"}}}";

		for (int i = 0; i < 5000; i++) {
			Map<String, Object> map = JsonUtil.fromJson(json,
					new TypeReference<Map<String, Object>>() {
					});
			if (!map.containsKey("parent")) {
				logger.info(map.get("parent") +"");
			}
		}
	}

	// @Test
	public void testReference() {
		Map<String, String> x = new HashMap<>();
		x.put("key1", "value1");
		x = checkReference(x, true);
		logger.info(x +"");

		String commentId = "1024185004273767_1024241857601415";
		commentId = commentId.substring(commentId.indexOf("_") + 1,
				commentId.length());
		logger.info(commentId);
		String dateStr = "2014-11-18T11:49:27-08:00";
		logger.info("date :"
				+ DateUtil.getTimeFromIso8601FormatWithColon(dateStr) / 1000);
	}

	private Map<String, String> checkReference(Map<String, String> test,
			boolean flag) {
		if (flag) {
			test = new HashMap<>();
			test.put("x", "y");
		}
		return test;
	}

	

	// @Test
	public void testFirstLetterCaps() {
		Set<String> set = new HashSet<>();
		set.add("Channel");
		String word = firstLetterUpper("channel");
		logger.info(word);
		logger.info(set.contains(word) +"");
	}

	private String firstLetterUpper(String word) {
		StringBuffer sb = new StringBuffer();
		sb.append((word.charAt(0) + "").toUpperCase().trim()).append(
				word.substring(1));
		return sb.toString();
	}

	// @Test
	public void testDecimalFormat() {
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.DOWN);
		logger.info(df.format(0.9997));
		StringBuffer str = new StringBuffer();
		str.append("yyyy");
		strTest(str);
		logger.info(str.toString());
		double x = ((Double.valueOf(1) / Double.valueOf(15)) * 100);
		logger.info(x +"");
	}

	private void strTest(StringBuffer str) {
		str.delete(0, str.length());
		str.append("xxx");
	}

	// @Test
	public void testCeilAndFloor() {
		logger.info(Math.floor(5.2) +"");
		logger.info(Math.ceil(5.2) +"");
		long temp = 1401234571010L - 1401234512371L;
		logger.info(temp +"");
	}

	// @Test
	public void testTokenDecode() throws Exception {
		String st = "uOkl093d7AjY7FueV83d11Bfq0hL7DroU28Z%2B4kNZLcTB0FruPQyhOHkHeh31LMMnkPX1A23qjIFGwBRSwDGnHK05Ss3O37M%2BpUJk%2FFZgoHeKmnbrsqhuxWRcpUsFyjQDJCtxAsk1b7%2BvtuWm9ZAJsmroi1C9oKJrqh7HcgDtAwxe6pT4%2BchNWikXpsHzhJnPkYQSljARYD6CSMidv8uJ7jAbBTZ%2B%2FvijKVUa48DJgfaCRVpHhNa%2BSPrdGOG336TCzyJ26KKDJgxuvjBQrq18ZDllY9JQWjDVGC296VkZPstYUr57bCDZPM9V0amxIAt%2FQkZH5IO%2F1PgTwX6bInomw%3D%3D";
		st = URLDecoder.decode(st, "UTF-8");
		st = CryptUtil.decrypt(st);
		logger.info(st);
	}

	// @Test
	public void testTwitterDate() {
		String str = "Mon Jul 28 17:59:23 +0000 2014";
		long time = DateUtil.getTimeFromTwitterFormat(str);
		logger.info(DateUtil.getSolrFormat(time));
	}

	// @Test
	public void testStrReference() {
		String st = "Mallik";
		String st1 = "Mallik";
		logger.info(System.identityHashCode(st) +"");
		logger.info(System.identityHashCode(st1) +"");
	}

	// @Test
	public void testURIPatternMatch() {
		// String regex = "/events/(.[^/]*?)/tracker";
		String regex = "/(.[^/]*?)/posts/\\d*";
		String uri = "/events/posts/91890829018";
		logger.info(Pattern.matches(regex, uri) +"");
	}

	// @Test
	public void testUnicode() {
		String s = "\u6211\u5011\u9019\u6b21\u70ba\u4e864\u670812\u65e5\u7684\u300a\u6211\u76f8\u4fe1\uff01\u9189\u5fc3\u9f8d\u864e\u699c\u8857\u982d\u7206\u5531\u6703\u300b\u56de\u4f86\u65b0\u52a0\u5761\u4e86\uff01\u800c\u4e14\u597d\u8208\u596e\u80fd\u8207MPF\u5011\u804a\u5929\uff01\n\u5225\u5fd8\u4e864\u670811\u65e5\uff0c\u4e0b\u53481.30\uff0cMP\u9b54\u5e7b\u529b\u91cf\u8207\u5927\u5bb6\u5728Facebook Q&A\u4e0a\u4e0d\u898b\u4e0d\u6563\u54e6\uff01\u4f60\u5011\u73fe\u5728\u5c31\u53ef\u4ee5\u5728\u4e0b\u9762\u7559\u8a00\uff0c\u628a\u60f3\u554f\u7684\u554f\u984c\u5beb\u4e0b\u53bb\u54e6\uff01\n\u7531\u65bc\u6280\u8853\u6027\u554f\u984c\uff0c\u6211\u5011\u6703\u7528\u9019\u500bQ&A\uff01\u8acb\u5927\u5bb6\u8e34\u8e8d\u652f\u6301\uff01\n\nWe are back in Singapore for B'IN Together Concert Live in Singapore this Sunday 12th April and are excited to chat with our fans!\nDon\u2019t forget that we are hosting our Facebook Q&A on 11th April, 1.30pm SGT. So start posting your questions below in the Comments box.";

		logger.info(s);
	}

	// @Test
	public void testDateUtil() {
		String date = "2015-02-13T19:10:27Z";
		long test = DateUtil.getTimeFromSolrFormat(date);
		logger.info("Date : " + test);
	}

	// @Test
	public void testDates() {
		Calendar start = GregorianCalendar.getInstance();
		start.set(Calendar.YEAR, 2014);
		start.set(Calendar.MONTH, 3);
		start.set(Calendar.DAY_OF_MONTH, 29);
		start.set(Calendar.HOUR, 11);
		start.set(Calendar.MINUTE, 20);
		start.set(Calendar.AM_PM, Calendar.AM);

		logger.info("Start time:" + start.getTimeInMillis() / 1000 + ","
				+ start.getTime());

		Calendar end = GregorianCalendar.getInstance();
		end.set(Calendar.YEAR, 2014);
		end.set(Calendar.MONTH, 4);
		end.set(Calendar.DAY_OF_MONTH, 1);
		end.set(Calendar.HOUR, 0);
		end.set(Calendar.MINUTE, 40);
		end.set(Calendar.AM_PM, Calendar.PM);

		logger.info("End time:" + end.getTimeInMillis() / 1000 + ", "
				+ end.getTime());
	}

	// @Test
	public void testDateFrom() {
		logger.info(new Date(DateUtil.dateFrom("1y")) +"");
	}

	// @Test
	public void testCustomDateFrom() {
		// logger.info(new Date(DateUtil.dateFrom("1y")));
		logger.info(DateUtil.getCustomFormat("MMM-dd-yyyy hh:mm:ss a z",
				1436389871000L));
	}

	// @Test
	public void testList() {
		String testFile = "src/test/resources/5001189/Carrie_Underwood.gexf";
		logger.info(CommonUtil.rolloverFileName(testFile, 1024));
	}

	// @Test
	public void testReplaceChar() {
		logger.info(CommonUtil
				.replaceSpecialCharacter("visa\b also contains |"));
	}

	// @Test
	public void testRoundRobin() {
		int count = 0;
		for (int i = 0; i < 100; i++) {
			String msg = "Msg-" + i;
			count = Math.abs(msg.hashCode()) % 6;
			logger.info(count +"");
		}
	}

	// @Test
	public void testStringWithChars() {
		String text = "____";
		logger.info(text.matches(".*[a-zA-Z]+.*") +"");
		logger.info(DateUtil.getCustomFormat("MMM-dd-yyyy-hh:mmaaa",
				System.currentTimeMillis()));
		String path = "/mnt/bb/jettylogs/topaz.log";
		path = path.substring(path.lastIndexOf("/") + 1);
		logger.info(path);
	}

	// @Test
	public void testLogarithmic() {
		float x = 0.15f;
		int E = 5000;
		double value1 = Math.abs(1 - Math.log((double) (x + 1)
				/ (double) (E + 1)));
		double value2 = Math.abs(1 - (Math.log((double) (x + 1)) / Math
				.log((double) (E + 1))));
		logger.info("value1:" + value1 + ", value2:" + value2);

		String cursor = CryptUtil
				.base64DecodeStr(
						"WTI5dGJXVnVkRjlqZAFhKemIzSTZANVEF4TlRNNU9EWTJNekE1TURZAMk5qZAzZANVFExT1RJM05EazJPUT09",
						"UTF-8");
		logger.info(cursor);

		String test = "/lib64/libc.so.6 (0x00007fcbdb13a000)";
		test = test.substring(0, test.indexOf("("));
		test = test.substring(test.lastIndexOf("/") + 1);
		logger.info(test);
	}


	// @Test
	public void testSolrFormatWithZ() {
		String date = "2016-11-06T00:39:28.000Z";
		logger.info(DateUtil.getTimeFromSolrFormat(date) +"");
	}

	// @Test
	public void testDeAccent() {
		// logger.info(BBCommonUtil.replaceAllSpecialChars("Taggart\uFEFF"));
		// String publishedAt = "2017-02-02T23:43:51.000Z";
		// String publishedAt = "2017-02-03T00:18:51.000Z";
		String publishedAt = "2017-02-03T00:52:07.000Z";
		long publishedAtTs = DateUtil.getTimeFromSolrFormat(publishedAt);
		String publishedAtStr = DateUtil
				.getIso8601FormatWithColon(publishedAtTs);
		logger.info("publishedAt=" + publishedAt + ", publishedAtTs="
				+ publishedAtTs + ", publishedAtStr=" + publishedAtStr);
	}

	// @Test
	public void testDayStart() {
		long startTime = DateUtil.getDayStartTime();
		logger.info("startTime:" + startTime + ", str:"
				+ DateUtil.getIso8601Format(startTime));
	}

	// @Test
	public void testDateCustomFormat() {
		long ts = DateUtil.getTimeFrom("2017-05-27", "yyyy-MM-dd");
		String str = DateUtil.getIso8601Format(ts);
		logger.info("ts:" + ts + ", str:" + str);
	}

	@Test
	public void testStartAndEndtime() {
		long ts = DateUtil.getTimeFrom("2017-05-27", "yyyy-MM-dd");
		long[] temp = DateUtil.getStartAndEndTimeOfDay(ts);
		logger.info(DateUtil.getIso8601FormatWithColon(ts) + " "
				+ DateUtil.getIso8601FormatWithColon(temp[0]) + " "
				+ DateUtil.getIso8601FormatWithColon(temp[1]));
		logger.info(DateUtil.getCustomFormat("yyyy-MM-dd", System.currentTimeMillis()));
	}
}
