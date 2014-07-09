package eu.inmite.lib.spayd.android;

import android.content.Context;
import android.content.Intent;
import eu.inmite.lib.spayd.reader.SpaydReader;

/**
 * @author Tomas Vondracek
 */
public class IntentIntegrator {

	public static boolean containsSpayd(Intent intent, Context context) {
		return SpaydIntentHelper.isAnyAdapterAcceptable(intent, context);
	}

	public static String getSpaydFromIntent(Intent intent, Context context) {
		return SpaydIntentHelper.getSpaydFromIntent(intent, context);
	}

	public static Intent createIntentForSpayd(String spayd) {
		if (! SpaydReader.isSpayd(spayd)) {
			throw new IllegalStateException("invalid spayd " + spayd);
		}

		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.putExtra(IntentConstants.EXTRA_SPAYD, spayd);
		intent.setType(IntentConstants.MIME_TYPE);

		return intent;
	}
}
