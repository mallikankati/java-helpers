package com.helpers;

import java.util.logging.Logger;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;


public final class HttpClientFactory {

	private static final Logger logger = Logger
			.getLogger(HttpClientFactory.class.getName());

	// private static CloseableHttpClient client;

	private static PoolingHttpClientConnectionManager connectionPool;

	private static RequestConfig config;
	static {
		try {
			connectionPool = new PoolingHttpClientConnectionManager();
			connectionPool.setDefaultMaxPerRoute(15);
			connectionPool.setMaxTotal(500);

			config = RequestConfig.custom().setSocketTimeout(90000)
					.setConnectTimeout(90000).setRedirectsEnabled(true).build();

			/*
			 * client =
			 * HttpClients.custom().setConnectionManager(connectionPool)
			 * .setConnectionManagerShared(true)
			 * .setDefaultRequestConfig(config).build();
			 */
		} catch (Exception e) {
			logger.info(e.getMessage());
		}
	}

	public static CloseableHttpClient getHttpClient() {
		CloseableHttpClient client = HttpClients.custom()
				.setConnectionManager(connectionPool)
				.setConnectionManagerShared(true)
				.setDefaultRequestConfig(config).build();
		if (client != null) {
			return client;
		}
		throw new RuntimeException("HttpClient is not initialized properly");
	}
}
