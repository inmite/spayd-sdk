package eu.inmite.lib.spayd.writer;

import eu.inmite.lib.spayd.model.SpaydValidationError;
import eu.inmite.lib.spayd.reader.ISpaydValidator;
import eu.inmite.lib.spayd.reader.impl.DefaultSpaydValidator;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

/**
 * @author Tomas Vondracek
 */
public class SpaydWriter {


	public static SpaydWriter create() {
		return new SpaydWriter(new DefaultSpaydValidator());
	}

	public static SpaydWriter create(final @NotNull ISpaydValidator validator) {
		return new SpaydWriter(validator);
	}

	private final ISpaydValidator mValidator;

	private SpaydWriter(final ISpaydValidator validator) {
		mValidator = validator;
	}

	@NotNull
	public WriterResult write(@NotNull SpaydOptions options) {
		final Map<String, String> attributes = options.getAttributes();

		final Collection<SpaydValidationError> attrErrors = mValidator.validateAttributes(attributes);
		if (attrErrors.size() > 0) {
			return WriterResult.fail(attrErrors);
		}

		final StringBuilder builder = new StringBuilder("SPD*");
		builder.append(options.mVersion);
		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			if (entry.getValue() != null) {
				builder.append("*");
				builder.append(entry.getKey());
				builder.append(":");
				builder.append(entry.getValue());
			}
		}

		final String spayd = builder.toString();
		final Collection<SpaydValidationError> spaydErrors = mValidator.validatePaymentString(spayd);
		if (spaydErrors.size() > 0) {
			return WriterResult.fail(spaydErrors);
		}

		return WriterResult.success(spayd);
	}
}
