/**
 *  Copyright (c) 2012, SPAYD (www.spayd.org).
 */
package eu.inmite.lib.spayd.model;

import eu.inmite.lib.spayd.reader.impl.DefaultSpaydValidator;
import eu.inmite.lib.spayd.utilities.IBANUtilities;

/**
 *
 * @author petrdvorak
 */
public class CzechBankAccount extends BankAccount {

    private final String prefix;
    private final String number;
    private final String bankCode;

	public CzechBankAccount(String iban) {
		this(iban, null);
	}

	public CzechBankAccount(String iban, String bic) {
		super(iban, bic);

		this.bankCode = iban.substring(4, 8);
		this.number = iban.substring(14, 24);
		this.prefix = iban.substring(8, 14);
    }

	public CzechBankAccount(String prefix, String number, String bankCode) {
		super(IBANUtilities.computeIBANFromCzechBankAccount(prefix, number, bankCode));

        this.prefix = prefix;
        this.number = number;
        this.bankCode = bankCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getNumber() {
        return number;
    }

    public String getPrefix() {
        if (prefix == null) {
            return "000000";
        }
        return prefix;
    }

    private void validateAccountParameters(String prefix, String number, String bankCode) throws IllegalArgumentException {
        if (prefix != null) {
            for (int i = 0; i < prefix.length(); i++) {
                if (prefix.charAt(i) < 0 && prefix.charAt(i) > 9) {
                    throw new IllegalArgumentException("Czech account number (prefix) must be numeric.");
                }
            }
            if (!DefaultSpaydValidator.validateMod11(prefix)) {
                throw new IllegalArgumentException("Czech account number (prefix) must pass bank mod 11 test.");
            }
        }
        if (number != null) {
            for (int i = 0; i < number.length(); i++) {
                if (number.charAt(i) < 0 && number.charAt(i) > 9) {
                    throw new IllegalArgumentException("Czech account number (basic part) must be numeric.");
                }
            }
            if (!DefaultSpaydValidator.validateMod11(number)) {
                throw new IllegalArgumentException("Czech account number (basic part) must pass bank mod 11 test.");
            }
        }
        if (bankCode != null) {
            for (int i = 0; i < bankCode.length(); i++) {
                if (bankCode.charAt(i) < 0 && bankCode.charAt(i) > 9) {
                    throw new IllegalArgumentException("Czech account number (bank code) must be numeric.");
                }
            }
        }
    }

	@Override
	public String toString() {
		return "CzechBankAccount{" +
				"prefix='" + prefix + '\'' +
				", number='" + number + '\'' +
				", bankCode='" + bankCode + '\'' +
				"} " + super.toString();
	}
}
