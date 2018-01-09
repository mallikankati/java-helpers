package com.helpers;

import java.io.File;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CommonUtil {

	private static final Logger logger = Logger.getLogger(CommonUtil.class
			.getName());

	// private static final String EMOJI_REGEX =
	// "[\uD83C-\uDBFF\uDC00-\uDFFF]";// "[^\\x00-\\x7F]";
	private static final String EMOJI_REGEX = "[\uD83C\uDF00-\uD83D\uDDFF]|[\uD83D\uDE00-\uD83D\uDE4F]|[\uD83D\uDE80-\uD83D\uDEFF]|[\u2600-\u26FF]|[\u2700-\u27BF]";

	public static final String REGEX = "[.`'%&^;:*$#|,~\\\\\"@()\\[\\]{}<>\\-?!+\\t\\r\\n\\n\b/]";

	private static Pattern CSVPATTERN = Pattern
			.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");

	private static final Pattern REMOVE_HTML_TAGS = Pattern.compile("<.+?>");

	private static final String UTF8_BOM_1 = "\uFEFF";
	private static final String UTF8_BOM_2 = "\\U+FEFF";

	public static String getFileExtension(String fileName) {
		String fileExt = null;
		int index = fileName.lastIndexOf(".");
		fileExt = fileName.substring(index + 1);
		return fileExt;
	}

	public static String getFileDir(String fileName) {
		String fileDir = null;
		int index = fileName.lastIndexOf("/");
		if (index > 0) {
			fileDir = fileName.substring(0, index);
		}
		return fileDir;
	}

	public static boolean isVideoFile(String fileUrl) {
		boolean status = false;
		if (fileUrl != null && fileUrl.trim().length() > 0) {
			String fileExt = getFileExtension(fileUrl);
			if ("mov".equalsIgnoreCase(fileExt)
					|| "mp4".equalsIgnoreCase(fileExt)
					|| "flv".equalsIgnoreCase(fileExt)
					|| "3gp".equalsIgnoreCase(fileExt)
					|| "ogv".equalsIgnoreCase(fileExt)) {
				status = true;
			}
		}
		return status;
	}

	public static String generatePrefix() {
		UUID uuid = UUID.randomUUID();
		String temp = uuid.toString().replace("-", "");
		int code = temp.hashCode();
		String str = "" + code;
		str = str.replace("-", "").trim();
		return str;
	}

	public static String generateRandomStr() {
		UUID uuid = UUID.randomUUID();
		String temp = uuid.toString().replace("-", "");
		return temp;
	}

	public static String getFileName(String fileUrl) {
		String fileName = null;
		int index = fileUrl.lastIndexOf("/");
		fileName = fileUrl.substring(index + 1);
		return fileName;
	}

	public static boolean isFileExists(String fileName) {
		File file = new File(fileName);
		return file.exists();
	}

	public static void writeToFile(InputStream is, String destinationDir,
			String fileName) {
		File fileDir = new File(destinationDir);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		File to = new File(destinationDir + "/" + fileName);
		try {
			Files.copy(is, to.toPath());
		} catch (Exception e) {
			logger.log(Level.INFO, e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	public static String createDirectory(String destinationDir) {
		String name = null;
		File fileDir = new File(destinationDir);
		if (!fileDir.exists()) {
			fileDir.mkdirs();
		}
		name = fileDir.getAbsolutePath();
		return name;
	}

	public static boolean isNumeric(String str) {
		boolean status = true;
		if (str != null && str.trim().length() > 0) {
			for (char c : str.toCharArray()) {
				if (!Character.isDigit(c)) {
					status = false;
					break;
				}
			}
		} else {
			status = false;
		}
		return status;
	}

	public static String replaceSpecialCharacter(String tweetText) {
		String tweet = tweetText.replaceAll(REGEX, "");
		return tweet;
	}

	public static String replaceSpecialCharacterWithChar(String tweetText,
			String replaceChar) {
		String tweet = tweetText.replaceAll(REGEX, replaceChar);
		String text = tweet.trim().replaceAll("\\s+", replaceChar);
		return text;
	}

	public static String replaceEmojis(String text) {

		StringBuffer sb = new StringBuffer();
		if (text != null && text.trim().length() > 0) {
			// text = EmojiParser.parseToUnicode(text);
			String[] splits = text.trim().split(" ");
			int count = 0;
			for (String s : splits) {
				if (count > 0) {
					sb.append(" ");
				}
				sb.append(s.trim().replaceAll(EMOJI_REGEX, ""));
				count++;
			}
		} else {
			sb.append(text);
		}
		return sb.toString();
		// return text;
	}

	public static String replaceUrls(String text) {
		String tempText = text
				.replaceAll(
						"((https?|ftp|gopher|telnet|file|Unsure|http):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)",
						"");
		return tempText;
	}

	public static String replaceAllSpecialChars(String text) {
		String tempText = replaceUrls(text);
		tempText = removeUTF8BOM(tempText);
		tempText = replaceEmojis(tempText);
		tempText = replaceSpecialCharacter(tempText);
		return tempText;
	}

	public static String cleanInvalidXmlChars(String text) {
		StringBuffer out = new StringBuffer();

		if (text == null || ("".equals(text)))
			return ""; // vacancy test.
		for (int i = 0; i < text.length(); i++) {
			char current = text.charAt(i);
			if ((current == 0x9) || (current == 0xA) || (current == 0xD)
					|| ((current >= 0x20) && (current <= 0xD7FF))
					|| ((current >= 0xE000) && (current <= 0xFFFD))
					|| ((current >= 0x10000) && (current <= 0x10FFFF)))
				out.append(current);
		}
		return out.toString();
	}

	public static double cosineSimilarity(double[] val1, double[] val2) {
		double cosine = 0.0;
		double multi = multiply(val1, val2);
		cosine = multi / (sqrtOfMatrix(val1) * sqrtOfMatrix(val2));
		return cosine;
	}

	public static double sqrtOfMatrix(double[] values) {
		double res = 0.0;
		for (int i = 0; i < values.length; i++) {
			res += values[i] * values[i];
		}
		res = Math.sqrt(res);

		return res;
	}

	public static double multiply(double[] val1, double[] val2) {
		double res = 0.0;
		if (val1.length == val2.length) {
			for (int i = 0; i < val1.length; i++) {
				res += val1[i] * val2[i];
			}
		} else {
			logger.info("Two matrix with different size");
		}
		return res;
	}

	// TODO: Tokens are coming with comma , need to fix that .
	public static List<String> csvStringToTokens(CharSequence csvString) {
		Matcher matcher = CSVPATTERN.matcher(csvString);
		List<String> tokens = new ArrayList<>();
		while (matcher.find()) {
			String value = matcher.group();
			int length = value.length();
			String lastChar = value.substring(length - 1, length);
			if (",".equalsIgnoreCase(lastChar)) {
				int index = value.lastIndexOf(",");
				if (index > -1) {
					value = value.substring(0, index).trim();
				}
			}

			tokens.add(value);
		}
		return tokens;
	}

	public static boolean isBigram(String word) {
		return (word != null && (word.contains(" ") || word.contains("_")));
	}

	public static String rollverFileName(String fileName, long rollSize) {
		String derivedFileName = null;
		File file = new File(fileName);
		long size = file.length();
		if (size >= rollSize) {
			derivedFileName = deriveFileName(fileName);
		}
		return derivedFileName;
	}

	private static String deriveFileName(String fileName) {
		String derivedFileName = null;
		String dir = fileName.substring(0, fileName.lastIndexOf("/"));
		String tempFileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		int counter = 1;
		// boolean status = false;
		try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths
				.get(dir))) {
			for (Path path : stream) {
				if (path.toString().contains(tempFileName)) {
					String pathFile = path.toString();
					String suffix = pathFile.substring(pathFile
							.lastIndexOf(".") + 1);
					int tempCounter = 0;
					try {
						tempCounter = Integer.parseInt(suffix) + 1;
						// status = true;
					} catch (Exception ignore) {
					}
					if (tempCounter > counter) {
						counter = tempCounter;
					}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		derivedFileName = fileName + "." + counter;

		return derivedFileName;
	}

	public static String urlEncode(String text, String encode) {
		String str = null;
		try {
			if (encode == null) {
				encode = "UTF-8";
			}
			str = URLEncoder.encode(text, encode);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return str;
	}

	public static String urlDecode(String text, String encode) {
		String str = null;
		try {
			if (encode == null) {
				encode = "UTF-8";
			}
			str = URLDecoder.decode(text, encode);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return str;
	}

	public static <T> List<List<T>> splitList(List<T> features, int splitSize) {
		List<List<T>> result = new ArrayList<>();
		if (features != null && features.size() > splitSize) {
			int fromIndex = 0;
			int toIndex = splitSize;
			while (toIndex < features.size()) {
				List<T> subList = features.subList(fromIndex, toIndex);
				result.add(subList);
				fromIndex = toIndex;
				toIndex += toIndex;
				if (toIndex >= features.size()) {
					subList = features.subList(fromIndex, features.size());
					result.add(subList);
					fromIndex = toIndex;
					toIndex += toIndex;
				}
			}
		} else {
			result.add(features);
		}
		return result;
	}

	public static String convertImageToBase64Encode(String imageFileName) {
		String result = null;
		try {
			Path path = Paths.get(imageFileName);
			byte[] imageInBytes = Files.readAllBytes(path);
			result = CryptUtil.base64EncodeStr(imageInBytes);
		} catch (Exception e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}
		return result;
	}

	public static long getFileSize(String fileName) {
		long size = 0;
		try {
			size = Files.size(Paths.get(fileName));
		} catch (Exception e) {
			logger.log(Level.INFO, e.getMessage(), e);
		}
		return size;
	}

	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static <T> void verifyTransitivity(Collection<T> elements,
			Comparator<T> comparator) {
		for (T first : elements) {
			for (T second : elements) {
				int result1 = comparator.compare(first, second);
				int result2 = comparator.compare(second, first);
				if (result1 != -result2) {
					// Uncomment the following line to step through the failed
					// case
					// comparator.compare(first, second);
					throw new AssertionError("compare(" + first + ", " + second
							+ ") == " + result1
							+ " but swapping the parameters returns " + result2);
				}
			}
		}
		for (T first : elements) {
			for (T second : elements) {
				int firstGreaterThanSecond = comparator.compare(first, second);
				if (firstGreaterThanSecond <= 0)
					continue;
				for (T third : elements) {
					int secondGreaterThanThird = comparator.compare(second,
							third);
					if (secondGreaterThanThird <= 0)
						continue;
					int firstGreaterThanThird = comparator
							.compare(first, third);
					if (firstGreaterThanThird <= 0) {
						// Uncomment the following line to step through the
						// failed case
						// comparator.compare(first, third);
						throw new AssertionError("compare(" + first + ", "
								+ second + ") > 0, " + "compare(" + second
								+ ", " + third + ") > 0, but compare(" + first
								+ ", " + third + ") == "
								+ firstGreaterThanThird);
					}
				}
			}
		}
	}

	public static String removeHtmlTags(String string) {
		if (string == null || string.length() == 0) {
			return string;
		}
		Matcher m = REMOVE_HTML_TAGS.matcher(string);
		return m.replaceAll("");
	}

	public static boolean isAccentCharsExist(String str) {
		String nfdNormalizedString = Normalizer.normalize(str,
				Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).find();
	}

	public static String deAccent(String str) {
		String nfdNormalizedString = Normalizer.normalize(str,
				Normalizer.Form.NFD);
		Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
		return pattern.matcher(nfdNormalizedString).replaceAll("");
	}

	public static String removeUTF8BOM(String str) {
		if (str.startsWith(UTF8_BOM_1)) {
			str = str.substring(1);
		} else if (str.startsWith(UTF8_BOM_2)) {
			str = str.substring(UTF8_BOM_2.length());
		} else if (str.endsWith(UTF8_BOM_1)) {
			str = str.substring(0, str.length() - 1);
		} else if (str.endsWith(UTF8_BOM_2)) {
			str = str.substring(0, str.length() - UTF8_BOM_2.length());
		}
		return str;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getValueFromMap(String key, Map<String, Object> map,
			Class<T> clazz) {
		return (T) map.get(key);
	}

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getValueAsMap(String key,
			Map<String, Object> details) {
		Map<String, Object> data = new HashMap<>();
		if (details.containsKey(key)) {
			data = (Map<String, Object>) details.get(key);
		}
		return data;
	}

	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getValueAsList(String key,
			Map<String, Object> details) {
		List<Map<String, Object>> data = new ArrayList<>();
		if (details.containsKey(key)) {
			data = (List<Map<String, Object>>) details.get(key);
		}
		return data;
	}
}
