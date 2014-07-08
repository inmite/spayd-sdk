package eu.inmite.lib.spayd.android.adapter;

import android.content.Context;
import android.content.Intent;
import eu.inmite.lib.spayd.android.Constants;
import eu.inmite.lib.spayd.android.ISpaydIntentAdapter;
import eu.inmite.lib.spayd.reader.SpaydReader;

/**
 * @author Tomas Vondracek
 */
public class SimpleIntentAdapter implements ISpaydIntentAdapter {

	@Override
	public String getSpaydFromIntent(Intent intent, Context context) {
		if (intent == null) {
			return null;
		}

		if (containsSpayd(intent, context)) {
			final String payload = intent.getStringExtra(Constants.INTENT_EXTRA_SPAYD);

			if (SpaydReader.isSpayd(payload)) {
				return payload;
			}
		}
		return null;
	}

	@Override
	public boolean containsSpayd(Intent intent, Context context) {
		if (intent != null && intent.getExtras() != null && intent.getExtras().containsKey(Constants.INTENT_EXTRA_SPAYD)) {
			final String payload = intent.getStringExtra(Constants.INTENT_EXTRA_SPAYD);

			return SpaydReader.isSpayd(payload);
		}
		return false;
	}
}
