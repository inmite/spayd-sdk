package eu.inmite.lib.spayd.android;

import android.content.Context;
import android.content.Intent;
import eu.inmite.lib.spayd.reader.SpaydReader;
import org.jetbrains.annotations.Nullable;

/**
 * @author Tomas Vondracek
 */
public class IntentIntegrator {

	public static boolean containsSpayd(Intent intent, Context context) {
		return containsSpayd(intent, context,(ISpaydIntentAdapter[]) null);
	}

	public static boolean containsSpayd(Intent intent, Context context, final ISpaydIntentAdapter... customAdapters) {
		if (context == null) {
			throw new IllegalArgumentException("null context");
		}
		if (intent == null) {
			return false;
		}

		if (customAdapters != null && customAdapters.length > 0) {
			final boolean acceptable = SpaydIntentHelper.isAnyAdapterAcceptable(intent, context, customAdapters);
			if (acceptable) {
				return true;
			}
		}

		return SpaydIntentHelper.isAnyAdapterAcceptable(intent, context);
	}

	@Nullable
	public static SpaydResult getSpaydAndSourceFromIntent(Intent intent, Context context) {
		return getSpaydAndSourceFromIntent(intent, context, (ISpaydIntentAdapter[]) null);
	}

	@Nullable
	public static SpaydResult getSpaydAndSourceFromIntent(Intent intent, Context context, ISpaydIntentAdapter... customAdapters) {
		if (context == null) {
			throw new IllegalArgumentException("null context");
		}
		if (intent == null) {
			return null;
		}
		if (customAdapters != null && customAdapters.length > 0) {
			final SpaydResult result = SpaydIntentHelper.getSpaydFromIntentWithAdapters(intent, context, customAdapters);
			if (result != null) {
				return result;
			}
		}
		return SpaydIntentHelper.getSpaydFromIntent(intent, context);
	}

	@Nullable
	public static String getSpaydFromIntent(Intent intent, Context context) {
		final SpaydResult result = getSpaydAndSourceFromIntent(intent, context);

		return result != null ? result.getSpayd() : null;
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
