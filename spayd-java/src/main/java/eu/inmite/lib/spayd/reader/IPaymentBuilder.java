package eu.inmite.lib.spayd.reader;

import eu.inmite.lib.spayd.model.Payment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.TimeZone;

/**
 * @author Tomas Vondracek
 */
public interface IPaymentBuilder<T extends Payment> {

	T buildPaymentFromSpaydAttributes(@NotNull final Map<String, String> attributes, @Nullable final TimeZone timeZone, @NotNull final String version);

}
