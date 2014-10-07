/*
 * Copyright (c) 2013, Inmite s.r.o. (www.inmite.eu).
 *
 * All rights reserved. This source code can be used only for purposes specified
 * by the given license contract signed by the rightful deputy of Inmite s.r.o.
 * This source code can be used only by the owner of the license.
 *
 * Any disputes arising in respect of this agreement (license) shall be brought
 * before the Municipal Court of Prague.
 */

package eu.inmite.lib.spayd.reader.impl;

import eu.inmite.lib.spayd.model.SpaydValidationError;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static eu.inmite.lib.spayd.model.SpaydValidationError.ERROR_INVALID_MESSAGE;

/**
 * @author Tomas Vondracek
 */
public class CzechSpaydValidator extends DefaultSpaydValidator {

	private static final Set<String> CZECH_SYMBOL_KEYS = new HashSet<>();

	static {
		CZECH_SYMBOL_KEYS.add("X-VS");
		CZECH_SYMBOL_KEYS.add("X-KS");
		CZECH_SYMBOL_KEYS.add("X-SS");
	}

	private static final int MAX_REPEAT_COUNT = 30;
	private static final int MAX_SYMBOL_LENGTH = 10;
	private static final String ERROR_SYMBOL_LENGTH = "ERROR_SYMBOL_LENGTH";
	private static final String ERROR_SYMBOL_INVALID = "ERROR_SYMBOL_INVALID";
	private static final String ERROR_REPEAT_COUNT = "ERROR_REPEAT_ATTEMPTS_COUNT";

	@NotNull
	@Override
	public Collection<SpaydValidationError> validateAttributes(final Map<String, String> attributes) {
		Collection<SpaydValidationError> errors = super.validateAttributes(attributes);

		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			final String key = entry.getKey();

			if (CZECH_SYMBOL_KEYS.contains(key)) {
				final String value = entry.getValue();
				if (value.length() > MAX_SYMBOL_LENGTH) {
					final SpaydValidationError error = new SpaydValidationError(ERROR_SYMBOL_LENGTH, "symbol must have at most " + MAX_SYMBOL_LENGTH + " characters");
					errors.add(error);
				}
				char[] valueChars = value.toCharArray();
				for (char valueChar : valueChars) {
					if (! Character.isDigit(valueChar)) {
						final SpaydValidationError error = new SpaydValidationError(ERROR_SYMBOL_INVALID, "symbol must contain numeric characters only");
						errors.add(error);
						break;
					}
				}
			} else if ("X-PER".equals(key)) {
				final String value = entry.getValue();
				final int repeatCount;
				try {
					repeatCount = Integer.parseInt(value);

					if (repeatCount < 0 || repeatCount > MAX_REPEAT_COUNT) {
						errors.add(new SpaydValidationError(ERROR_REPEAT_COUNT, "invalid attempt repeat count " + repeatCount));
					}
				} catch (NumberFormatException e) {
					errors.add(new SpaydValidationError(ERROR_REPEAT_COUNT, "invalid attempt repeat count " + e.getMessage()));
				}
			} else if (key.equals("X-SELF")) {
				final String value = entry.getValue();
				if (value.length() < 1 || (mMaxMessageLength > 0 && value.length() > mMaxMessageLength)) {
					SpaydValidationError error = new SpaydValidationError(ERROR_INVALID_MESSAGE, "Message must be at represented as a string with length between 1 and 60 characters.");
					errors.add(error);
				}
			}
		}

		return errors;
	}
}
