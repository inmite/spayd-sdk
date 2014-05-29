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

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Tomas Vondracek
 */
public class CzechSpaydValidator extends DefaultSpaydValidator {

	private static final Set<String> ALLOWED_CZECH_KEYS = new HashSet<>();

	static {
		ALLOWED_CZECH_KEYS.add("X-VS");
		ALLOWED_CZECH_KEYS.add("X-KS");
		ALLOWED_CZECH_KEYS.add("X-SS");
	}

	private static final int MAX_SYMBOL_LENGTH = 10;
	private static final String ERROR_SYMBOL_LENGTH = "ERROR_SYMBOL_LENGTH";

	@NotNull
	@Override
	public Collection<SpaydValidationError> validateAttributes(final Map<String, String> attributes) {
		Collection<SpaydValidationError> errors = super.validateAttributes(attributes);

		for (Map.Entry<String, String> entry : attributes.entrySet()) {
			final String key = entry.getKey();

			if (ALLOWED_CZECH_KEYS.contains(key)) {
				final String value = entry.getValue();
				if (value.length() > MAX_SYMBOL_LENGTH) {
					final SpaydValidationError error = new SpaydValidationError(ERROR_SYMBOL_LENGTH, "symbol must have at most " + MAX_SYMBOL_LENGTH + " characters");
					errors.add(error);
				}
			}
		}

		return errors;
	}
}
