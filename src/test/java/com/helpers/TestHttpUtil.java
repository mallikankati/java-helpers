package com.helpers;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.junit.Test;

public class TestHttpUtil {

	private static final Logger logger = Logger.getLogger(TestHttpUtil.class.getName());

	// @Test
	public void testHttpGet() {
		// String url =
		// "http://www.broadwayworld.com/bwwtv/article/VIDEO-Phil-Hanley-Performs-Stand-Up-on-LATE-NIGHT-WITH-SETH-MEYERS-20150501";
		String url = "http://www.celebdirtylaundry.com/2015/the-young-and-the-restless-spoilers-yr-jack-fights-for-his-life-chelsea-victoria-fight-dylan-saves-sharon/";
		Map<String, String> customHeaders = new HashMap<>();
		customHeaders.put("User-Agent",
				"Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5)");
		String response = HttpUtil.executeGet(url, "", customHeaders);
		logger.info(response);
	}

	//@Test
	public void testPhraseServer() throws Exception {
		final String url = "http://fs-20.bumebox.com:9090/phrase?comment=Why%20do%20you%20keep%20Raquel%20around%3F%3F%3F%20She%27s%20not%20to%20bright...%20sorry&feature=shes";
		ExecutorService service = Executors.newFixedThreadPool(5);
		for (int task = 0; task < 10; task++) {
			service.execute(createRunnable(task, url));
		}
		service.awaitTermination(50, TimeUnit.SECONDS);
	}
	
	@Test
	public void testSolrTopicsAPI(){
		String url = "http://api.bumebox.com:8983/solr/1000064/select";
		StringBuffer querySb = new StringBuffer();
		querySb.append("q=tweets_topics.*&wt=json&rows=500&DocumentAssigner.minClusterSize=1");
		querySb.append("&LingoClusteringAlgorithm.phraseLengthPenaltyStart=3&LingoClusteringAlgorithm.phraseLengthPenaltyStop=4");
		querySb.append("&LingoClusteringAlgorithm.desiredClusterCountBase=15&LingoClusteringAlgorithm.clusterMergingThreshold=0.18");
		querySb.append("&LingoClusteringAlgorithm.phraseLabelBoost=0.40&CompleteLabelFilter.labelOverrideThreshold=0.45");

        logger.info(HttpUtil.executeGet(url, querySb.toString()));
	}

	private Runnable createRunnable(final int task, final String url) {
		Runnable r = new Runnable() {
			@Override
			public void run() {
				String response = HttpUtil.executeGet(url, null);
				logger.info("Task :" + task + ", response:" + response);
			}

		};
		return r;
	}
}
