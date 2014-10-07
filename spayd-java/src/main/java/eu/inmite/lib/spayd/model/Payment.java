package eu.inmite.lib.spayd.model;

import eu.inmite.lib.spayd.reader.SpaydReaderException;
import eu.inmite.lib.spayd.utilities.FormattingUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

/**
 * @author Tomas Vondracek
 */
public abstract class Payment<T extends BankAccount> {

	/**
	 *  Version property keeps version of SmartPayment. If not set during the string building then default value is used.
	 */
	protected final String version;
	/**
	 * Primary account
	 */
	protected final T account;
	/**
	 * Contains array of alternate accounts (SmartPaymentAccount objects)
	 */
	protected final Collection<T> alternateAccounts;
	/**
	 * AM
	 */
	protected final BigDecimal amount;
	/**
	 * CC
	 */
	protected final String currencyCode;
	/**
	 * "RF" attribute
 	 */
	protected final String identifierForReceiver;
	/**
	 * "RN" attribute
	 */
	protected final String receiversName;
	/**
	 * "DT" attribute
	 */
	protected final Date dueDate;
	/**
	 * "DL" attribute, only applicable with valid {@link #frequency}
	 */
	protected final Date lastDate;

	/** "FRQ" attribute - frequency for standing order */
	protected final Frequency frequency;

	/**
	 * "PT" attribute
	 */
	protected final String paymentType;
	/**
	 * "MSG" attribute
	 */
	protected final String messageForReceiver;
	/**
	 * "NT" attribute
	 */
	protected final SpaydNotificationChannel notificationChannel;
	/**
	 * "NTA" attribute
	 */
	protected final String notificationAddress;

	public Payment(final Map<String, String> attrs, final TimeZone timeZone, final String version) {
		this.version = version;
		messageForReceiver = attrs.get("MSG");
		receiversName = attrs.get("RN");
		if (attrs.containsKey("AM")) {
			amount = new BigDecimal(attrs.get("AM"));
		} else {
			amount = null;
		}
		identifierForReceiver = attrs.get("RF");
		notificationChannel = SpaydNotificationChannel.fromSpayd(attrs.get("NT"));
		notificationAddress = attrs.get("NTA");
		paymentType = attrs.get("PT");
		currencyCode = attrs.get("CC");

		final String ibanAndBic = attrs.get("ACC");
		account = accountFromSpayd(ibanAndBic);
		if (attrs.containsKey("ALT-ACC")) {
			final String[] ibans = attrs.get("ALT-ACC").split(",");

			alternateAccounts = new ArrayList<>(ibans.length);
			for (String altIban : ibans) {
				final T altAccount = accountFromSpayd(altIban);
				if (altAccount != null) {
					alternateAccounts.add(altAccount);
				}
			}
		} else {
			alternateAccounts = Collections.emptyList();
		}

		final String dateValue = attrs.get("DT");
		try {
			dueDate = FormattingUtils.parseSpaydDate(dateValue, timeZone);
		} catch (ParseException e) {
			throw new SpaydReaderException("failed to parse date " + dateValue, e);
		}

		final String lastDateValue = attrs.get("DL");
		try {
			lastDate = FormattingUtils.parseSpaydDate(lastDateValue, timeZone);
		} catch (ParseException e) {
			throw new SpaydReaderException("failed to parse last date" + lastDateValue, e);
		}

		frequency = Frequency.fromSpaydFrequency(attrs.get("FRQ"));
	}

	protected abstract T createAccount(@NotNull String iban, @Nullable String bic);

	private T accountFromSpayd(final String ibanAndBic) {
		if (ibanAndBic == null) {
			return null;
		}

		final String[] parts = ibanAndBic.split("\\+");
		if (parts.length == 0) {
			return null;
		}
		final String iban = parts[0];
		final String bic = parts.length > 1 ? parts[1] : null;

		return createAccount(iban, bic);
	}

	public String getVersion() {
		return version;
	}

	public T getAccount() {
		return account;
	}

	public Collection<T> getAlternateAccounts() {
		return alternateAccounts;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public String getCurrencyCode() {
		return currencyCode;
	}

	public String getIdentifierForReceiver() {
		return identifierForReceiver;
	}

	public String getReceiversName() {
		return receiversName;
	}

	public Date getDueDate() {
		return dueDate;
	}

	public String getPaymentType() {
		return paymentType;
	}

	public String getMessageForReceiver() {
		return messageForReceiver;
	}

	public SpaydNotificationChannel getNotificationChannel() {
		return notificationChannel;
	}

	public String getNotificationAddress() {
		return notificationAddress;
	}

	/** Last date for standing order, see also {@link #getFrequency()} */
	public Date getLastDate() {
		return lastDate;
	}

	/** standing order frequency */
	public Frequency getFrequency() {
		return frequency;
	}

	public boolean isStandingOrder() {
		return frequency != null;
	}

	@Override
	public String toString() {
		return "Payment{" +
				"version='" + version + '\'' +
				", account=" + account +
				", alternateAccounts=" + alternateAccounts +
				", amount=" + amount +
				", currencyCode='" + currencyCode + '\'' +
				", identifierForReceiver='" + identifierForReceiver + '\'' +
				", receiversName='" + receiversName + '\'' +
				", dueDate=" + dueDate +
				", lastDate=" + lastDate +
				", frequency=" + frequency +
				", paymentType='" + paymentType + '\'' +
				", messageForReceiver='" + messageForReceiver + '\'' +
				", notificationChannel=" + notificationChannel +
				", notificationAddress='" + notificationAddress + '\'' +
				'}';
	}
}
