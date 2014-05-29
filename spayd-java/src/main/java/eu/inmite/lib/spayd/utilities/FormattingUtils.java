package eu.inmite.lib.spayd.utilities;

import java.text.DateFormat;
import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * @author Tomas Vondracek
 */
public class FormattingUtils {

	private static DateFormat dateFormat = null;
	private static final boolean isJava1_5;

	static {
		String version = System.getProperty("java.version");
		isJava1_5 = version.startsWith("1.5");
	}

	public static Date parseSpaydDate(String date, TimeZone tz) throws ParseException {
		if (date == null) {
			return null;
		}

		if (dateFormat == null) {
			dateFormat = new SimpleDateFormat("yyyymmdd");
		}
		if (tz != null) {
			dateFormat.setTimeZone(tz);
		}
		return dateFormat.parse(date);
	}


	public static String normalizeString(String string) {
		if (isJava1_5) {
			return NormalizerCompat.normalize(string);
		} else {
			return Normalizer.normalize(string, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
		}
	}

	private static class NormalizerCompat {

		private static final String original    = "àáâäãåčçdďèéêěëíîïľňñóôöõøřšßťùúûúůüýÿž";
		private static final String translated  = "aaaaaaccddeeeeeiiilnnooooorsstuuuuuuyyz";

		public static String normalize(String text) {
			if (text == null) {
				return null;
			}
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < text.length(); i++) {
				char c = text.charAt(i);
				if (Character.isUpperCase(c)) {
					sb.append(Character.toUpperCase(normalizeChar(Character.toLowerCase(c))));
				} else {
					sb.append(normalizeChar(c));
				}
			}
			return sb.toString();
		}

		private static char normalizeChar(char c) {
			for (int i = 0; i < original.length(); i++) {
				if (c == original.charAt(i)) {
					return translated.charAt(i);
				}
			}
			return c;
		}
	}
}
