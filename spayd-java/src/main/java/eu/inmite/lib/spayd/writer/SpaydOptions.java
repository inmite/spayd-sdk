package eu.inmite.lib.spayd.writer;

import eu.inmite.lib.spayd.model.BankAccount;
import eu.inmite.lib.spayd.model.SpaydNotificationChannel;
import eu.inmite.lib.spayd.writer.impl.CzechSpaydOptions;
import eu.inmite.lib.spayd.writer.impl.DefaultSpaydOptions;
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
public abstract class SpaydOptions<T extends SpaydOptions<T>> {

	public static DefaultSpaydOptions createDefault() {
		return DefaultSpaydOptions.create("1.0", null);
	}

	public static DefaultSpaydOptions createDefault(String version) {
		return DefaultSpaydOptions.create(version, null);
	}

	public static DefaultSpaydOptions createDefault(String version, TimeZone timeZone) {
		return DefaultSpaydOptions.create(version, timeZone);
	}

	public static CzechSpaydOptions createCzech() {
		return CzechSpaydOptions.create("1.0", null);
	}

	public static CzechSpaydOptions createCzech(String version) {
		return CzechSpaydOptions.create(version, null);
	}

	public static CzechSpaydOptions createCzech(String version, TimeZone timeZone) {
		return CzechSpaydOptions.create(version, timeZone);
	}

	private final String mVersion;
	private final Map<String, String> mAttributes = new LinkedHashMap<>();
	protected final TimeZone mTimeZone;

	public SpaydOptions(final String version, final TimeZone timeZone) {
		mVersion = version;
		mTimeZone = timeZone;
	}

	protected abstract T self();

	String getVersion() {
		return mVersion;
	}

	Map<String, String> getAttributes() {
		return mAttributes;
	}

	public T withAttribute(final boolean encode, String key, String value) {
		final String spaydUnit = encode ? percentEncode(value ) : value;
		mAttributes.put(key, spaydUnit);
		return self();
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

	private static String formatAccount(BankAccount account) {
		if (account.getBic() != null) {
			return account.getIban().concat("+").concat(account.getBic());
		} else {
			return account.getIban();
		}
	}

	/// attributes ///

	public T withAccount(final @NotNull BankAccount account) {
		return withAttribute(false, "ACC", formatAccount(account));
	}

	public T withAlternateAccounts(final Collection<BankAccount> alternateAccounts) {
		final StringBuilder builder = new StringBuilder();
		for (BankAccount account : alternateAccounts) {
			if (builder.length() > 0) {
				builder.append(",");
			}
			builder.append(formatAccount(account));
		}
		return withAttribute(false, "ALT-ACC", builder.toString());
	}

	public T withAmount(final BigDecimal amount) {
		if (amount == null) {
			return self();
		}
		return withAttribute(false, "AM", amount.toString());
	}

	public T withCurrencyCode(final String currencyCode) {
		return withAttribute(false, "CC", currencyCode);
	}

	public T withIdentifierForReceiver(final String identifierForReceiver) {
		return withAttribute(false, "RF", identifierForReceiver);
	}

	public T withReceiversName(final String receiversName) {
		return withAttribute(true, "RN", receiversName);
	}

	public T withDueDate(final Date dueDate) {
		try {
			return withAttribute(false, "DT", formatSpaydDate(dueDate, mTimeZone));
		} catch (ParseException e) {
			throw new SpaydWriterException("failed to format due date", e);
		}
	}

	public T withPaymentType(final String paymentType) {
		return withAttribute(false, "PT", paymentType);
	}

	public T withMessageForReceiver(final String messageForReceiver) {
		return withAttribute(true, "MSG", messageForReceiver);
	}

	public T withNotificationChannel(final SpaydNotificationChannel notificationChannel) {
		return withAttribute(false, "NT", notificationChannel.getSpaydUnit());
	}

	public T withNotificationAddress(final String notificationAddress) {
		return withAttribute(true, "NTA", notificationAddress);
	}
}
