package com.helpers;

import java.net.URLDecoder;
import java.security.Provider;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.junit.Test;

public class TestCryptUtil {

	private static final Logger logger = Logger.getLogger(TestCryptUtil.class
			.getName());

	// @Test
	public void testDecrypt() throws Exception {
		String token = "WpSeZDPOA9d8cVA9OeXPrjIJk96UnRUddcAZ%2BuoPSYRrM40X8aTCgPXoqxJ6%2FAeDnuQzLnhjV4Psh8avjgUcyeS0HKOPc4UBilj34pQmlyBEgKmEvy8jvr0FCGumiWE%2BDhJYRVv%2BQJ%2F63XSGTj8G4AcnDjertu%2F0DM7w7PL%2BCWWefpUhwTnru%2B5hffQi84jaZz7A90iLobpQgFI%2FKtWMMD4evFqzElMSA%2Bzwi5hcgGuMMtFAqD%2FcX5Fs3SP7iwJVYIM502kWQhijSw2AFsS9sg%3D%3D";
		token = URLDecoder.decode(token, "UTF-8");
		logger.info(token);
		// token =
		// "EAAWF4JyvAYkBAJA7cy6SJMqowJNUJyKiYN3A4gmxCyLS2rkXhT5s6JDZCR3dAs6RoyHOjZBNRaXh0ggEvrX1alTTfZBtaLcXdX4gBoVxlw6M4ZBQ8QBKYwzS6g3STVFLaZAx59GwpMHhRMZBIjQSSWjL9hTJqlMjLPk8HDISxOjgZDZD";
		token = CryptUtil.decrypt(token);
		logger.info(token);
	}

	// @Test
	public void testUrlDecode() {
		String url = "%7B%22type%22%3A%22fbsolo%22%2C%22description%22%3A%22asdf%22%2C%22pageId%22%3A%22782507861794205%22%7D=";
		String text = CryptUtil.urlDecode(url, "UTF-8");
		logger.info(text);
	}

	// @Test
	public void testEncryptDecrypt() {
		for (Provider provider : Security.getProviders())
			logger.info(provider + "");
		String xxx = "Hello World";
		String encrypt = CryptUtil.encrypt(xxx);
		logger.info(encrypt);
		logger.info(CryptUtil.decrypt(encrypt));
	}

	// @Test
	public void testFilter() {
		List<String> ignoreUris = new ArrayList<>();
		ignoreUris.add("/");
		ignoreUris.add("/twitterlogin");
		ignoreUris.add("/twittercallback");
		ignoreUris.add("/kafka");
		boolean status = isIgnoredUri(ignoreUris, "/test");
		logger.info(status + "");
	}

	public boolean isIgnoredUri(List<String> ignoreUris, String requestUri) {
		boolean status = false;
		for (String uri : ignoreUris) {
			if (uri.equalsIgnoreCase(requestUri)
					|| (!uri.equalsIgnoreCase("/") && requestUri
							.startsWith(uri))) {
				status = true;
				break;
			}
		}
		return status;
	}

	@Test
	public void testCursor() {
		String after = "WTI5dGJXVnVkRjlqZAFhKemIzSTZANak13TVRZAek16UXdPREUyT1RjM09qRTBPVGN3TkRZAM09Eaz0ZD";
		String temp = CryptUtil.base64DecodeStr(after, "UTF-8");
		logger.info(temp);
		temp = CryptUtil.base64DecodeStr(temp, "UTF-8");
		logger.info(temp);
	}
}
