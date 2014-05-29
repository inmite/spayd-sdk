package eu.inmite.lib.spayd.reader;

import eu.inmite.lib.spayd.model.Payment;
import eu.inmite.lib.spayd.model.SpaydValidationError;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

/**
 * Reader for spayd strings. Initialize with {@link eu.inmite.lib.spayd.reader.SpaydConfig} and call {@link #readFromSpayd(String)} method.
 *
 * @author Tomas Vondracek
 */
public final class SpaydReader<T extends Payment> {

	protected static final Pattern mPayloadPattern = Pattern.compile("SPD\\*[0-9]\\.[0-9]\\*.+\\*?");

	private final SpaydConfig<T> mConfiguration;

	/**
	 * Check if given string is valid SPAYD payload
	 */
	public static boolean isSpayd(String content) {
		if (content == null || content.length() == 0) {
			return false;
		}

		final Matcher matcher = mPayloadPattern.matcher(content);

		return matcher.find();
	}

	public static <P extends Payment> SpaydReader<P> from(@NotNull SpaydConfig<P> configuration) {
		return new SpaydReader<>(configuration);
	}

	private SpaydReader(SpaydConfig<T> configuration) {
		mConfiguration = configuration;
	}

	/**
	 * Read Spayd string and create {@link eu.inmite.lib.spayd.model.Payment} object.
	 * @param spayd spayd string
	 *
	 * @return result with created {@link eu.inmite.lib.spayd.model.Payment} in case of success or collection of errors occurred while reading and validating the spayd
	 */
	@NotNull
	public ReaderResult<T> readFromSpayd(String spayd) {
		if (! isSpayd(spayd)) {
			return ReaderResult.fail();
		}
		final Collection<SpaydValidationError> spaydErrors = mConfiguration.getValidator().validatePaymentString(spayd);
		if (spaydErrors.size() > 0) {
			return ReaderResult.fail(spaydErrors);
		}

		final String[] contentParts = spayd.split("\\*");

		final Map<String, String> attributes = new HashMap<>();
		for (String part : contentParts) {
			final int separatorIndex = part.indexOf(":");
			if (separatorIndex < 1 || separatorIndex >= part.length()) {
				continue;
			}
			final String key = part.substring(0, separatorIndex);
			String encodedValue = part.substring(separatorIndex + 1);
			if (encodedValue.contains("+")) {
				encodedValue = encodedValue.replaceAll("\\+", "%2B");
			}
			final String value = percentDecode(encodedValue);

			attributes.put(key, value);
		}
		if (mConfiguration.getPostProcessor() != null) {
			mConfiguration.getPostProcessor().processAttributes(attributes);
		}
		final Map<String, String> unmodifiableAttributes = Collections.unmodifiableMap(attributes);
		final Collection<SpaydValidationError> attributesErrors = mConfiguration.getValidator().validateAttributes(attributes);
		if (attributesErrors.size() > 0) {
			return ReaderResult.fail(attributesErrors);
		}

		// SPD*1.0*ACC:...
		final String version = contentParts[1];
		final IPaymentBuilder<T> builder = mConfiguration.getBuilder();
		final T payment = builder.buildPaymentFromSpaydAttributes(unmodifiableAttributes, mConfiguration.getTimeZone(), version);

		return ReaderResult.success(payment);
	}


	/**
	 * Decode URL-encoded string
	 */
	private static String percentDecode(String content) {
		try {
			return URLDecoder.decode(content, "utf-8");
		} catch (UnsupportedEncodingException uee) {
			return null;
		}
	}

	/**
	 * Create URL-encoded string
	 */
	private static String percentEncode(String content) {
		try {
			return URLEncoder.encode(content, "utf-8");
		} catch (UnsupportedEncodingException uee) {
			return null;
		}
	}

	/**
	 * Check if CRC matches the rest of payload
	 *
	 * @param content
	 * @param crc
	 * @return
	 */
	private static boolean checkCRC(String content, String crc) {
		final CRC32 crc32 = new CRC32();
		crc32.update(content.getBytes());

		final String hex = Long.toHexString(crc32.getValue()).toLowerCase();

		return (crc.toLowerCase().equals(hex));
	}
}
