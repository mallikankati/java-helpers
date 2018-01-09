package com.helpers;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public final class CryptUtil {

	private static byte[] passPhrase = new byte[] { 'B', 'B', 'O', 'X', 'b',
			'b', 'o', 'x', 'A', 'E', 'I', 'O', 'U', '1', '2', '3' };

	private static Cipher getECipher() {
		Cipher ecipher = null;
		try {
			Key skeySpec = new SecretKeySpec(passPhrase, "AES");
			ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			byte[] iv = new byte[ecipher.getBlockSize()];

			IvParameterSpec ivParams = new IvParameterSpec(iv);
			ecipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParams);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return ecipher;
	}

	private static Cipher getDCipher() {
		Cipher dcipher = null;
		try {
			Key key = new SecretKeySpec(passPhrase, "AES");
			dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
			// the block size (in bytes), or 0 if the underlying algorithm is
			// not a block cipher
			byte[] ivByte = new byte[dcipher.getBlockSize()];
			// This class specifies an initialization vector (IV). Examples
			// which use
			// IVs are ciphers in feedback mode, e.g., DES in CBC mode and RSA
			// ciphers with OAEP encoding operation.
			IvParameterSpec ivParamsSpec = new IvParameterSpec(ivByte);
			dcipher.init(Cipher.DECRYPT_MODE, key, ivParamsSpec);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return dcipher;
	}

	public static String encrypt(String str) {
		try {
			// Encode the string into bytes using utf-8
			byte[] utf8 = str.getBytes("UTF-8");
			Cipher ecipher = getECipher();
			// Encrypt
			byte[] enc = ecipher.doFinal(utf8);

			// Encode bytes to base64 to get a string
			byte[] encodedBytes = base64Encode(enc);
			return new String(encodedBytes, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static String decrypt(String str) {
		try {
			// Decode base64 to get bytes
			byte[] dec = base64Decode(str.getBytes("UTF-8"));
			Cipher dcipher = getDCipher();
			// Decrypt
			byte[] utf8 = dcipher.doFinal(dec);

			// Decode using utf-8
			return new String(utf8, "UTF-8");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
	}

	public static byte[] base64Encode(byte[] bytes) {
		byte[] temp = Base64.encodeBase64(bytes);
		return temp;
	}

	public static byte[] base64Decode(byte[] base64Data) {
		byte[] bytes = Base64.decodeBase64(base64Data);
		return bytes;
	}

	public static String base64EncodeStr(byte[] bytes) {
		byte[] temp = Base64.encodeBase64(bytes);
		String str = null;
		try {
			str = new String(temp, "UTF-8");
		} catch (Exception ignore) {
			throw new RuntimeException(ignore);
		}
		return str;
	}

	public static String base64EncodeStr(String source, String charset) {
		String str = null;
		try {
			byte[] temp = Base64.encodeBase64(source.getBytes(charset));
			str = new String(temp, charset);
		} catch (Exception ignore) {
			throw new RuntimeException(ignore);
		}
		return str;
	}

	public static String base64DecodeStr(byte[] base64Data) {
		byte[] bytes = Base64.decodeBase64(base64Data);
		String str = null;
		try {
			str = new String(bytes, "UTF-8");
		} catch (Exception ignore) {
		}
		return str;
	}

	public static String base64DecodeStr(String source, String charset) {
		String str = null;
		try {
			byte[] bytes = Base64.decodeBase64(source.getBytes(charset));
			str = new String(bytes, "UTF-8");
		} catch (Exception ignore) {
		}
		return str;
	}

	public static String urlEncode(String source, String charset) {
		String str = null;
		try {
			if (source != null && source.length() > 0) {
				str = URLEncoder.encode(source, charset);
			}
		} catch (Exception ignore) {
			throw new RuntimeException(ignore);
		}
		return str;
	}

	public static String urlDecode(String source, String charset) {
		String str = null;
		try {
			if (source != null && source.length() > 0) {
				str = URLDecoder.decode(source, charset);
			}
		} catch (Exception ignore) {
			throw new RuntimeException(ignore);
		}
		return str;
	}

	public static String md5Digest(String source) {
		try {
			return md5Digest(source.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

	public static String md5Digest(byte[] bytes) {
		String md5 = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(bytes);

			byte byteData[] = md.digest();

			// convert the byte to hex format method 1
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < byteData.length; i++) {
				sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			md5 = sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return md5;
	}
}
