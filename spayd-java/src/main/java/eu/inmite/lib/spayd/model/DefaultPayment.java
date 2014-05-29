package eu.inmite.lib.spayd.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TimeZone;

/**
 * @author Tomas Vondracek
 */
public class DefaultPayment extends Payment<BankAccount> {

	public DefaultPayment(Map<String, String> attributes, final TimeZone timeZone, final String version) {
		super(attributes, timeZone, version);
	}

	@Override
	protected BankAccount createAccount(@NotNull String iban, @Nullable String bic) {
		return new BankAccount(iban, bic);
	}

}
