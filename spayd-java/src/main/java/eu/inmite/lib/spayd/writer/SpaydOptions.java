package eu.inmite.lib.spayd.writer;

import eu.inmite.lib.spayd.model.BankAccount;
import eu.inmite.lib.spayd.model.SpaydNotificationChannel;
import org.jetbrains.annotations.NotNull;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;

import static eu.inmite.lib.spayd.utilities.FormattingUtils.formatSpaydDate;

/**
 * @author Tomas Vondracek
 */
public class SpaydOptions {

	protected final String mVersion;
	protected final TimeZone mTimeZone;

	private final Map<String, String> mAttributes = new LinkedHashMap<>();

	public static SpaydOptions create() {
		return new SpaydOptions("1.0", null);
	}

	public static SpaydOptions create(String version) {
		return new SpaydOptions(version, null);
	}

	public static SpaydOptions create(String version, TimeZone timeZone) {
		return new SpaydOptions(version, timeZone);
	}

	private SpaydOptions(final String version, final TimeZone timeZone) {
		mVersion = version;
		mTimeZone = timeZone;
	}

	String getVersion() {
		return mVersion;
	}

	Map<String, String> getAttributes() {
		return mAttributes;
	}

	public SpaydOptions withAttribute(final boolean encode, String key, String value) {
		final String spaydUnit = encode ? percentEncode(value ) : value;
		mAttributes.put(key, spaydUnit);
		return this;
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

	private String formatAccount(BankAccount account) {
		if (account.getBic() != null) {
			return account.getIban().concat("+").concat(account.getBic());
		} else {
			return account.getIban();
		}
	}

	public SpaydOptions withAccount(final @NotNull BankAccount account) {
		return withAttribute(false, "ACC", formatAccount(account));
	}

	public SpaydOptions withAlternateAccounts(final Collection<BankAccount> alternateAccounts) {
		final StringBuilder builder = new StringBuilder();
		for (BankAccount account : alternateAccounts) {
			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(formatAccount(account));
		}
		return withAttribute(false, "ALT-ACC", builder.toString());
	}

	public SpaydOptions withAmount(final BigDecimal amount) {
		if (amount == null) {
			return this;
		}
		return withAttribute(false, "AM", amount.toString());
	}

	public SpaydOptions withCurrencyCode(final String currencyCode) {
		return withAttribute(false, "CC", currencyCode);
	}

	public SpaydOptions withIdentifierForReceiver(final String identifierForReceiver) {
		return withAttribute(false, "RF", identifierForReceiver);
	}

	public SpaydOptions withReceiversName(final String receiversName) {
		return withAttribute(true, "RN", receiversName);
	}

	public SpaydOptions withDueDate(final Date dueDate) {
		try {
			return withAttribute(false, "DT", formatSpaydDate(dueDate, mTimeZone));
		} catch (ParseException e) {
			throw new SpaydWriterException("failed to format due date", e);
		}
	}

	public SpaydOptions withPaymentType(final String paymentType) {
		return withAttribute(false, "PT", paymentType);
	}

	public SpaydOptions withMessageForReceiver(final String messageForReceiver) {
		return withAttribute(true, "MSG", messageForReceiver);
	}

	public SpaydOptions withNotificationChannel(final SpaydNotificationChannel notificationChannel) {
		return withAttribute(false, "NT", notificationChannel.getSpaydUnit());
	}

	public SpaydOptions withNotificationAddress(final String notificationAddress) {
		return withAttribute(true, "NTA", notificationAddress);
	}
}
