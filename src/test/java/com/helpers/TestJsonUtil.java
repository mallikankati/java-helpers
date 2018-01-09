package com.helpers;

import java.io.Serializable;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.Test;

import com.fasterxml.jackson.core.type.TypeReference;

public class TestJsonUtil {

	private static final Logger logger = Logger.getLogger(TestJsonUtil.class
			.getName());

	class JobDetails implements Serializable {
		private static final long serialVersionUID = 6645682103386594267L;

	}

	class CorpusContent implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -1501475745053038553L;
		String title;
		String type;
		String content;
	}

	// @Test
	public void testJsonUtil() {
		String jsonStr = "{\"created_at\":\"Mon May 05 17:35:54 +0000 2014\",\"id\":463371523609206784,\"id_str\":\"463371523609206784\",\"text\":\"Decanting Spout Wine Bottle  Aerator On The Go Wine Age No Drip Home Bar #ebaymobile http://t.co/Ast5bcqMai\",\"source\":\"\\u003ca href=\"http://www.apple.com/\" rel=\"nofollow\"\\u003eiOS\\u003c/a\\u003e\",\"truncated\":false,\"in_reply_to_status_id\":null,\"in_reply_to_status_id_str\":null,\"in_reply_to_user_id\":null,\"in_reply_to_user_id_str\":null,\"in_reply_to_screen_name\":null,\"user\":{\"id\":1634664511,\"id_str\":\"1634664511\",\"name\":\"lisa F\",\"screen_name\":\"successlisa2013\",\"location\":\"successlisa2013@gmail.com\",\"url\":\"http://www.ultimatecapturepages.com/lisamariarn\",\"description\":\"Have fun and create income from home. It's a Lifestyle of Freedom! You can have it all too!\",\"protected\":false,\"followers_count\":420,\"friends_count\":933,\"listed_count\":2,\"created_at\":\"Wed Jul 31 05:07:23 +0000 2013\",\"favourites_count\":8,\"utc_offset\":null,\"time_zone\":null,\"geo_enabled\":true,\"verified\":false,\"statuses_count\":1982,\"lang\":\"en\",\"contributors_enabled\":false,\"is_translator\":false,\"is_translation_enabled\":false,\"profile_background_color\":\"C0DEED\",\"profile_background_image_url\":\"http://abs.twimg.com/images/themes/theme1/bg.png\",\"profile_background_image_url_https\":\"https://abs.twimg.com/images/themes/theme1/bg.png\",\"profile_background_tile\":false,\"profile_image_url\":\"http://pbs.twimg.com/profile_images/378800000218367521/1969dc794f83aa6537146a10286c62fe_normal.jpeg\",\"profile_image_url_https\":\"https://pbs.twimg.com/profile_images/378800000218367521/1969dc794f83aa6537146a10286c62fe_normal.jpeg\",\"profile_link_color\":\"0084B4\",\"profile_sidebar_border_color\":\"C0DEED\",\"profile_sidebar_fill_color\":\"DDEEF6\",\"profile_text_color\":\"333333\",\"profile_use_background_image\":true,\"default_profile\":true,\"default_profile_image\":false,\"following\":null,\"follow_request_sent\":null,\"notifications\":null},\"geo\":null,\"coordinates\":null,\"place\":null,\"contributors\":null,\"retweet_count\":0,\"favorite_count\":0,\"entities\":{\"hashtags\":[{\"text\":\"ebaymobile\",\"indices\":[73,84]}],\"symbols\":[],\"urls\":[{\"url\":\"http://t.co/Ast5bcqMai\",\"expanded_url\":\"http://bit.ly/1lSc9lU\",\"display_url\":\"bit.ly/1lSc9lU\",\"indices\":[85,107]}],\"user_mentions\":[]},\"favorited\":false,\"retweeted\":false,\"possibly_sensitive\":false,\"filter_level\":\"medium\",\"lang\":\"en\"}";
		Map<String, Object> temp = JsonUtil.fromJson(jsonStr,
				new TypeReference<Map<String, Object>>() {
				});
		String json = JsonUtil.toJson(temp);
		logger.info(json);
	}

	// @Test
	public void testJsonUtilWithList() {
		String json = "[{\"title\":\"test\", \"type\":\"manual\", \"content\":\"This is a test\"}]";

		List<CorpusContent> contentList = Arrays.asList(JsonUtil.fromJson(json,
				CorpusContent[].class));
		CorpusContent content = contentList.get(0);
		logger.info(content.toString());
	}

	// @Test
	public void testJsonUtilToJson() {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("id", "852735478090263_852878981409246");
		map.put("message",
				"You so silly! Of course. No mommy and family without you! \u003C3 Mimi");
		map.put("like_count", 0);
		map.put("created_time", "2014-11-06T00:14:54+0000");
		Map<String, Object> from = new LinkedHashMap<>();
		from.put("category", "Public figure");
		from.put("name", "Mimi the Rescue");
		from.put("id", "331654090198407");
		map.put("from", from);
		logger.info(JsonUtil.toJson(map));
	}

	@Test
	public void testNullObj() {
		JobDetails job = new JobDetails();
		logger.info(JsonUtil.toJson(job));
	}
}
