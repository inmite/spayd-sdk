package eu.inmite.lib.spayd.reader;

import eu.inmite.lib.spayd.model.SpaydValidationError;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

/**
 * @author Tomas Vondracek
 */
public interface ISpaydValidator {

	@NotNull Collection<SpaydValidationError> validatePaymentString(String paymentString);

	@NotNull Collection<SpaydValidationError> validateAttributes(Map<String, String> attrs);
}
