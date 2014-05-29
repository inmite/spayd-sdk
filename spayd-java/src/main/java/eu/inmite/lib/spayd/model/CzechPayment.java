package eu.inmite.lib.spayd.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TimeZone;

/**
 * @author Tomas Vondracek
 */
public class CzechPayment extends Payment<CzechBankAccount> {

	private final String variableSymbol;
	private final String specificSymbol;
	private final String constantSymbol;

	private final String customIdentifier;
	private final String customUrl;

	private final Integer repeatDaysCount;

	public CzechPayment(final Map<String, String> spayd, final TimeZone timeZone, final String version) {
		super(spayd, timeZone, version);

		variableSymbol = spayd.get("X-VS");
		specificSymbol = spayd.get("X-SS");
		constantSymbol = spayd.get("X-KS");

		customIdentifier = spayd.get("X-ID");
		customUrl = spayd.get("X-URL");

		final String repeatDaysCountValue = spayd.get("X-PER");
		if (repeatDaysCountValue != null) {
			repeatDaysCount = Integer.parseInt(repeatDaysCountValue);
		} else {
			repeatDaysCount = null;
		}
	}

	@Override
	protected CzechBankAccount createAccount(@NotNull final String iban, @Nullable final String bic) {
		return new CzechBankAccount(iban, bic);
	}

	public String getVariableSymbol() {
		return variableSymbol;
	}

	public String getSpecificSymbol() {
		return specificSymbol;
	}

	public String getConstantSymbol() {
		return constantSymbol;
	}

	public String getCustomIdentifier() {
		return customIdentifier;
	}

	public String getCustomUrl() {
		return customUrl;
	}

	public Integer getRepeatDaysCount() {
		return repeatDaysCount;
	}

	@Override
	public String toString() {
		return "CzechPayment{" +
				"variableSymbol='" + variableSymbol + '\'' +
				", specificSymbol='" + specificSymbol + '\'' +
				", constantSymbol='" + constantSymbol + '\'' +
				", customIdentifier='" + customIdentifier + '\'' +
				", customUrl='" + customUrl + '\'' +
				", repeatDaysCount=" + repeatDaysCount +
				"} " + super.toString();
	}
}
