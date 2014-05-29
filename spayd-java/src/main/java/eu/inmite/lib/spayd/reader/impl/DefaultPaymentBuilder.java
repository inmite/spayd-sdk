package eu.inmite.lib.spayd.reader.impl;

import eu.inmite.lib.spayd.model.DefaultPayment;
import eu.inmite.lib.spayd.reader.IPaymentBuilder;
import eu.inmite.lib.spayd.model.Payment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TimeZone;

/**
 * @author Tomas Vondracek
 */
public class DefaultPaymentBuilder implements IPaymentBuilder<Payment> {

	@NotNull
	@Override
	public Payment buildPaymentFromSpaydAttributes(@NotNull final Map<String, String> attributes, @Nullable TimeZone timeZone, @NotNull final String version) {
		return new DefaultPayment(attributes, timeZone, version);
	}
}
