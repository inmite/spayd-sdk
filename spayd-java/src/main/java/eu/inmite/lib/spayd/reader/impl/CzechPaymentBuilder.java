package eu.inmite.lib.spayd.reader.impl;

import eu.inmite.lib.spayd.model.CzechPayment;
import eu.inmite.lib.spayd.reader.IPaymentBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TimeZone;

/**
 * @author Tomas Vondracek
 */
public class CzechPaymentBuilder implements IPaymentBuilder<CzechPayment> {

	@Override
	public CzechPayment buildPaymentFromSpaydAttributes(@NotNull final Map<String, String> attributes, @Nullable final TimeZone timeZone, @NotNull final String version) {
		return new CzechPayment(attributes, timeZone, version);
	}
}
