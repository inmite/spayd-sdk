/**
 *  Copyright (c) 2012, SPAYD (www.spayd.org).
 */
package eu.inmite.lib.spayd.reader.impl;

import eu.inmite.lib.spayd.model.Frequency;
import eu.inmite.lib.spayd.model.SpaydValidationError;
import eu.inmite.lib.spayd.reader.ISpaydValidator;
import eu.inmite.lib.spayd.utilities.IBANValidator;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static eu.inmite.lib.spayd.model.SpaydValidationError.*;

/**
 *
 * @author petrdvorak
 */
public class DefaultSpaydValidator implements ISpaydValidator {

	private static final Set<String> ALLOWED_KEYS = new HashSet<>(12);

	private static final Set<String> SUPPORTED_VERSIONS = new HashSet<>(2);

	static {
		ALLOWED_KEYS.add("ACC");
		ALLOWED_KEYS.add("ALT-ACC");
		ALLOWED_KEYS.add("AM");
		ALLOWED_KEYS.add("CC");
		ALLOWED_KEYS.add("RF");
		ALLOWED_KEYS.add("RN");
		ALLOWED_KEYS.add("DT");
		ALLOWED_KEYS.add("FRQ");
		ALLOWED_KEYS.add("DL");
		ALLOWED_KEYS.add("PT");
		ALLOWED_KEYS.add("MSG");
		ALLOWED_KEYS.add("NT");
		ALLOWED_KEYS.add("NTA");
		ALLOWED_KEYS.add("CRC32");

		SUPPORTED_VERSIONS.add("1.0");
		SUPPORTED_VERSIONS.add("2.0");
	}

	public static final int DEFAULT_MAX_MESSAGE_LENGTH = 60;
	public static final int MAX_RF_LENGTH = 16;
	public static final int MAX_RN_LENGTH = 35;

	protected final int mMaxMessageLength;

	public DefaultSpaydValidator() {
		this(DEFAULT_MAX_MESSAGE_LENGTH);
	}

	public DefaultSpaydValidator(final int maxMessageLength) {
		mMaxMessageLength = maxMessageLength;
	}

	@NotNull
	@Override
	public Collection<SpaydValidationError> validatePaymentString(String paymentString) {
        List<SpaydValidationError> errors = new LinkedList<>();

        if (!Charset.forName("ISO-8859-1").newEncoder().canEncode(paymentString)) { // check encoding
            SpaydValidationError error = new SpaydValidationError(ERROR_INVALID_CHARSET, "Invalid charset - only ISO-8859-1 characters must be used");
            errors.add(error);
            return errors;
        }

        if (!paymentString.matches("^SPD\\*[0-9]+\\.[0-9]+\\*.*")) {
            SpaydValidationError error = new SpaydValidationError(ERROR_NOT_SPAYD, "Invalid data prefix - SPD*{$VERSION}* expected.");
            errors.add(error);
            return errors;
        }

        if (!paymentString.matches("^SPD\\*[0-9]+\\.[0-9]+(\\*[0-9A-Z $%*+-.]+:[^\\*]+)+\\*?$")) {
            SpaydValidationError error = new SpaydValidationError(ERROR_INVALID_STRUCTURE, "Payment String code didn't pass the basic regexp validation.");
            errors.add(error);
            return errors;
        }

		final String[] components = paymentString.split("\\*");

		if (components.length < 3) {
			final SpaydValidationError error = new SpaydValidationError(ERROR_NOT_SPAYD, "String doesn't contain valid SmartPayment descriptor.");
			errors.add(error);
		}
		// SPD*1.0*ACC:...
		final String version = components[1];
		if (! SUPPORTED_VERSIONS.contains(version)) {
			final SpaydValidationError error = new SpaydValidationError(ERROR_REQUEST_GENERIC, "Unknown version " + version);
			errors.add(error);
		}

		// skip the header and version => start with 2
		for (int i = 2; i < components.length; i++) {
			int index = components[i].indexOf(":");
			if (index == -1) { // missing pair between two stars ("**")
				SpaydValidationError error = new SpaydValidationError(ERROR_INVALID_STRUCTURE, "Payment String code didn't pass the basic regexp validation.");
				errors.add(error);
				break;
			}
		}

		return errors;
    }

	@NotNull
	@Override
	public Collection<SpaydValidationError> validateAttributes(Map<String, String> attributes) {
		final List<SpaydValidationError> errors = new LinkedList<>();

		boolean ibanFound = false;

		for (Map.Entry<String, String> entry : attributes.entrySet()) {
		    final String key = entry.getKey();
		    final String value = entry.getValue();

		    if (!ALLOWED_KEYS.contains(key) && !key.startsWith("X-")) {
		        SpaydValidationError error = new SpaydValidationError(ERROR_INVALID_KEY_FOUND, "Unknown key detected. Use 'X-' prefix to create your own key.");
		        errors.add(error);
		        continue;
		    }

			if (key.equals("ACC")) {
				ibanFound = true;
				final String[] parts = value.split("\\+");
				if (parts.length == 0) {
					SpaydValidationError error = new SpaydValidationError(ERROR_INVALID_IBAN, "IBAN+BIC pair was not in the correct format.");
					errors.add(error);
				}
				final String iban = parts[0];
				if (!IBANValidator.validateElectronicIBAN(iban)) {
					SpaydValidationError error = new SpaydValidationError(ERROR_INVALID_IBAN, "IBAN+BIC pair was not in the correct format.");
					errors.add(error);
				}

			} else if (key.equals("ALT-ACC")) {
				ibanFound = true;
				if (!value.matches("^([A-Z]{2,2}[0-9]+)(\\+([A-Z0-9]+))?(,([A-Z]{2,2}[0-9]+)(\\+([A-Z0-9]+))?)*$")) {
					SpaydValidationError error = new SpaydValidationError(ERROR_INVALID_ALTERNATE_IBAN, "Alternate accounts are not properly formatted - should be IBAN+BIC list with items separated by ',' character.");
					errors.add(error);
				}

			} else if (key.equals("AM")) {
				if (!value.matches("^[0-9]{0,10}(\\.[0-9]{0,2})?$") || value.equals(".")) {
					SpaydValidationError error = new SpaydValidationError(ERROR_INVALID_AMOUNT, "Amount must be a number with at most 2 decimal digits.");
					errors.add(error);
				}

			} else if (key.equals("CC")) {
				try {
					Currency.getInstance(value);
				} catch (IllegalArgumentException ex) {
					SpaydValidationError error = new SpaydValidationError(ERROR_INVALID_CURRENCY, "Currency must be a valid currency from ISO 4271.");
					errors.add(error);
				}

			} else if (key.equals("RF")) {
				if (value.length() > MAX_RF_LENGTH || value.length() < 1 || !value.matches("^[0-9]+$")) {
					SpaydValidationError error = new SpaydValidationError(ERROR_INVALID_SENDERS_REFERENCE, "Senders reference must be a decimal string with length between 1 and 16 characters.");
					errors.add(error);
				}

			} else if (key.equals("RN")) {
				if (value.length() > MAX_RN_LENGTH || value.length() < 1) {
					SpaydValidationError error = new SpaydValidationError(
							ERROR_INVALID_RECIPIENT_NAME, "Recipient name must be a string with length between 1 and 40 characters.");
					errors.add(error);
				}

			} else if (key.equals("NT")) {
				if (!value.equals("E") && !value.equals("P")) {
					SpaydValidationError error = new SpaydValidationError(
							ERROR_INVALID_NOTIFICATION_TYPE, "Notification type must be 'E' (e-mail) or 'P' (phone).");
					errors.add(error);
				}

			} else if (key.equals("DT") || key.equals("DL")) {
				if (!value.matches("^[0-9]{8,8}$")) {
					SpaydValidationError error = new SpaydValidationError(
							ERROR_INVALID_DUE_DATE, "Date must be represented as a decimal string in yyyyMMdd format - " + value);
					errors.add(error);
				} else {
					SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
					Date date;
					try {
						date = df.parse(value);
						if (date == null) {
							SpaydValidationError error = new SpaydValidationError(ERROR_INVALID_DUE_DATE, "Date must be represented as a decimal string in yyyyMMdd format - " + value);
							errors.add(error);
						}
					} catch (ParseException ex) {
						SpaydValidationError error = new SpaydValidationError(ERROR_INVALID_DUE_DATE, "Date must be represented as a decimal string in yyyyMMdd format - " + value);
						errors.add(error);
					}
				}

			} else if (key.equals("PT")) {
				if (value.length() > 3 || value.length() < 1) {
					SpaydValidationError error = new SpaydValidationError(ERROR_INVALID_PAYMENT_TYPE, "Payment type must be at represented as a string with length between 1 and 3 characters.");
					errors.add(error);
				}

			} else if (key.equals("MSG")) {
				if (value.length() < 1 || (mMaxMessageLength > 0 && value.length() > mMaxMessageLength)) {
					SpaydValidationError error = new SpaydValidationError(ERROR_INVALID_MESSAGE, "Message must be at represented as a string with length between 1 and 60 characters.");
					errors.add(error);
				}
			} else if (key.equals("FRQ")) {
				if (Frequency.fromSpaydFrequency(value) == null) {
					SpaydValidationError error = new SpaydValidationError(ERROR_UNKNOWN_FREQUENCY, "Unknown frequency " + value);
					errors.add(error);
				}
			}
		}
		if (!ibanFound) {
		    SpaydValidationError error = new SpaydValidationError(ERROR_IBAN_NOT_FOUND, "You must specify an account number.");
		    errors.add(error);
		}
		return errors;
	}

	public static boolean validateMod11(String number) {
	    int weight = 1;
	    int sum = 0;
	    for (int k = number.length() - 1; k >= 0; k--) {
	        sum += (number.charAt(k) - '0') * weight;
	        weight *= 2;
	    }

		return (sum % 11) == 0;

	}

}
