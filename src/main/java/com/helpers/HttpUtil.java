package com.helpers;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * Http helper to make http calls to external systems
 * 
 * @author mallik
 * 
 */
public final class HttpUtil {

	private static final Logger logger = Logger.getLogger(HttpUtil.class
			.getName());

	public enum Method {
		GET, POST, HEAD, DELETE, PUT
	};

	public static String executeGet(String url, String queryString) {
		return execute(Method.GET, url, queryString, null, false, null);
	}

	public static String executeGet(String url, String queryString,
			boolean printheaders) {
		return execute(Method.GET, url, queryString, null, printheaders, null);
	}

	public static String executeGet(String url, String queryString,
			Map<String, String> customHeaders) {
		return execute(Method.GET, url, queryString, customHeaders, false, null);
	}

	public static String executeGet(String url, Map<String, String> payload,
			Map<String, String> customHeaders) {
		String queryString = null;
		if (payload != null && !payload.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			int size = payload.size();
			int index = 0;
			for (Map.Entry<String, String> entry : payload.entrySet()) {
				sb.append(entry.getKey()).append("=").append(entry.getValue());
				if (index < (size - 1)) {
					sb.append("&");
				}
				index++;
			}
			queryString = sb.toString();
		}
		return execute(Method.GET, url, queryString, customHeaders, false, null);
	}

	public static String executeGet(String url, Map<String, String> payload,
			Map<String, String> customHeaders, boolean printheaders,
			StatusContentHandler handler) {
		String queryString = null;
		if (payload != null && !payload.isEmpty()) {
			StringBuffer sb = new StringBuffer();
			int size = payload.size();
			int index = 0;
			for (Map.Entry<String, String> entry : payload.entrySet()) {
				sb.append(entry.getKey()).append("=").append(entry.getValue());
				if (index < (size - 1)) {
					sb.append("&");
				}
				index++;
			}
			queryString = sb.toString();
		}
		return execute(Method.GET, url, queryString, customHeaders,
				printheaders, handler);
	}

	public static String executePost(String url, String payload) {
		return execute(Method.POST, url, payload, null, false, null);
	}

	public static String executePost(String url, String payload,
			boolean printheaders) {
		return execute(Method.POST, url, payload, null, printheaders, null);
	}

	public static String executePost(String url, String payload,
			boolean printheaders, StatusContentHandler handler) {
		return execute(Method.POST, url, payload, null, printheaders, handler);
	}

	public static String executePost(String url, String payload,
			Map<String, String> customHeaders, boolean printHeaders) {
		return execute(Method.POST, url, payload, customHeaders, printHeaders,
				null);
	}

	public static String executePost(String url, String payload,
			Map<String, String> customHeaders) {
		return execute(Method.POST, url, payload, customHeaders, false, null);
	}

	public static String execute(Method method, String url, String payload,
			Map<String, String> customHeaders, boolean printHeaders,
			StatusContentHandler contentHandler) {
		StringBuffer sb = new StringBuffer();
		// TODO: Need to parameterize these values
		RequestConfig config = RequestConfig.custom().setSocketTimeout(30000)
				.setConnectTimeout(30000).setRedirectsEnabled(true).build();
		// CloseableHttpClient client = HttpClients.createDefault();

		CloseableHttpClient client = HttpClientFactory.getHttpClient();

		HttpRequestBase request = null;
		try {
			if (Method.GET.equals(method)) {
				if (payload != null && payload.trim().length() > 0) {
					url = url + "?" + payload;
				}
				request = new HttpGet(url);
				// TODO: Need to add more methods
			} else if (Method.POST.equals(method)) {
				HttpPost req = new HttpPost(url);
				if (payload != null && payload.trim().length() > 0) {
					req.setEntity(new StringEntity(payload, "UTF-8"));
				}
				request = req;
			} else if (Method.PUT.equals(method)) {
				HttpPut req = new HttpPut(url);
				if (payload != null && payload.trim().length() > 0) {
					req.setEntity(new StringEntity(payload, "UTF-8"));
				}
				request = req;
			}
			if (Method.DELETE.equals(method)) {
				HttpDelete req = new HttpDelete(url);
				request = req;
			}
			request.setConfig(config);
			if (customHeaders != null && !customHeaders.isEmpty()) {
				for (Map.Entry<String, String> entry : customHeaders.entrySet()) {
					request.setHeader(entry.getKey(), entry.getValue());
				}
			}

			CloseableHttpResponse response = client.execute(request);
			StatusLine statusLine = response.getStatusLine();
			logger.info(method + ", " + response.getStatusLine() + ", URI:"
					+ request.getURI());
			Header contentLengthHeader = null;
			if (printHeaders) {
				for (Header header : response.getAllHeaders()) {
					logger.info(header.toString());
					if (header.getName().equalsIgnoreCase("Content-Length")
							|| header.getName().equalsIgnoreCase(
									"content-length")) {
						contentLengthHeader = header;
					}
				}
			}
			if (contentHandler == null) {
				if (statusLine.getStatusCode() == 200
						|| contentLengthHeader != null) {
					HttpEntity entity = response.getEntity();
					try (BufferedReader br = new BufferedReader(
							new InputStreamReader(entity.getContent()))) {
						String line = null;
						while ((line = br.readLine()) != null) {
							sb.append(line);
						}
						EntityUtils.consume(entity);
					} finally {
						response.close();
						client.close();
					}
				} else {
					HttpEntity entity = response.getEntity();
					if (entity.getContent().available() > 0) {
						try (BufferedReader br = new BufferedReader(
								new InputStreamReader(entity.getContent()))) {
							String line = null;
							while ((line = br.readLine()) != null) {
								sb.append(line);
							}
							EntityUtils.consume(entity);
						} finally {
							response.close();
							client.close();
						}
					} else {
						try {
							EntityUtils.consume(entity);
						} finally {
							response.close();
							client.close();
						}
					}
					logger.info(sb.toString());
				}
			} else {
				try {
					HttpEntity entity = response.getEntity();
					contentHandler.handle(statusLine.getStatusCode(),
							entity.getContent());
					EntityUtils.consume(entity);
				} finally {
					response.close();
					client.close();
				}
			}
		} catch (Exception e) {
			logger.log(Level.INFO, e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}

		return sb.toString();
	}

	@SuppressWarnings("deprecation")
	public static String executeMultipart(String url,
			Map<String, String> payload, File file) {
		StringBuffer sb = new StringBuffer();
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost req = new HttpPost(url);
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		try {
			if (payload != null && !payload.isEmpty()) {
				for (Map.Entry<String, String> entry : payload.entrySet()) {
					builder = builder.addPart(entry.getKey(), new StringBody(
							entry.getValue()));
				}
			}
			builder.addPart("file", new FileBody(file));
			req.setEntity(builder.build());
			CloseableHttpResponse response = client.execute(req);
			StatusLine statusLine = response.getStatusLine();
			logger.info(response.getStatusLine().toString());
			Header contentLengthHeader = null;
			for (Header header : response.getAllHeaders()) {
				logger.info(header.toString());
				if (header.getName().equalsIgnoreCase("Content-Length")) {
					contentLengthHeader = header;
				}
			}
			if (contentLengthHeader != null
					&& contentLengthHeader.getValue() != null
					&& !"0".equalsIgnoreCase(contentLengthHeader.getValue())
					&& statusLine.getStatusCode() == 200) {
				HttpEntity entity = response.getEntity();
				try (BufferedReader br = new BufferedReader(
						new InputStreamReader(entity.getContent()))) {
					String line = null;
					while ((line = br.readLine()) != null) {
						sb.append(line);
					}
					EntityUtils.consume(entity);
				} finally {
					response.close();
				}
			}

		} catch (Exception e) {
			logger.log(Level.INFO, e.getMessage(), e);
			throw new RuntimeException(e);
		}

		return sb.toString();
	}

	public interface StatusContentHandler {
		public void handle(int statusCode, InputStream stream);
	}

	public static void downloadFile(String url, String queryString,
			String directory, String fileName) {
		byte[] imageData = executeRawGet(url, queryString, true);
		if (imageData != null && imageData.length > 0) {
			try {
				File dir = new File(directory);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				File file = new File(fileName);
				BufferedOutputStream bw = new BufferedOutputStream(
						new FileOutputStream(file));
				bw.write(imageData);
				bw.close();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	}

	public static byte[] executeRawGet(String url, String queryString,
			boolean printHeaders) {
		byte[] result = null;
		// TODO: Need to parameterize these values
		RequestConfig config = RequestConfig.custom().setSocketTimeout(120000)
				.setConnectTimeout(120000).setRedirectsEnabled(true).build();
		CloseableHttpClient client = HttpClients.createDefault();

		HttpRequestBase request = null;
		try {
			if (queryString != null && queryString.trim().length() > 0) {
				url = url + "?" + queryString;
			}
			request = new HttpGet(url);
			request.setConfig(config);
			CloseableHttpResponse response = client.execute(request);
			StatusLine statusLine = response.getStatusLine();
			logger.info(response.getStatusLine() + ", URI:" + request.getURI());
			Header contentLengthHeader = null;
			if (printHeaders) {
				for (Header header : response.getAllHeaders()) {
					logger.info(header.toString());
					if (header.getName().equalsIgnoreCase("Content-Length")) {
						contentLengthHeader = header;
					}
				}
			}
			if (statusLine.getStatusCode() == 200
					|| contentLengthHeader != null) {
				HttpEntity entity = response.getEntity();
				try (BufferedInputStream bis = new BufferedInputStream(
						(entity.getContent()))) {
					ByteArrayOutputStream outputRaw = new ByteArrayOutputStream();
					byte[] data = new byte[10 * 1024 * 1024];
					int n = 0;
					while ((n = bis.read(data)) != -1) {
						outputRaw.write(data, 0, n);
					}
					result = outputRaw.toByteArray();
					EntityUtils.consume(entity);
				} finally {
					response.close();
				}
			}
		} catch (Exception e) {
			logger.log(Level.INFO, e.getMessage(), e);
			throw new RuntimeException(e.getMessage());
		}

		return result;
	}
}
