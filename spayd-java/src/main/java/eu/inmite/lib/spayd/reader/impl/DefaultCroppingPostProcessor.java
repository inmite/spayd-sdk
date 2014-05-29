package eu.inmite.lib.spayd.reader.impl;

import eu.inmite.lib.spayd.reader.ISpaydPostProcessor;

import java.util.Map;

/**
 * @author Tomas Vondracek
 */
public class DefaultCroppingPostProcessor implements ISpaydPostProcessor {

	private static final int MAX_RN_LENGTH = DefaultSpaydValidator.MAX_RN_LENGTH;
	private static final int MAX_MSG_LENGTH = DefaultSpaydValidator.DEFAULT_MAX_MESSAGE_LENGTH;

	@Override
	public void processAttributes(final Map<String, String> mutableAttrs) {
		tryCropAttribute(mutableAttrs, "RN", MAX_RN_LENGTH);
		tryCropAttribute(mutableAttrs, "MSG", MAX_MSG_LENGTH);
	}

	private void tryCropAttribute(final Map<String, String> mutableAttrs, final String attrName, final int maxLength) {
		if (mutableAttrs.containsKey(attrName)) {
			final String receiverName = mutableAttrs.get(attrName);
			if (receiverName.length() > maxLength) {
				mutableAttrs.put(attrName, receiverName.substring(0, maxLength));
			}
		}
	}
}
