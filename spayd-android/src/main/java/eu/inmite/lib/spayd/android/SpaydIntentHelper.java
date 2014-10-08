package eu.inmite.lib.spayd.android;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import eu.inmite.lib.spayd.android.adapter.NfcIntentAdapter;
import eu.inmite.lib.spayd.android.adapter.SimpleIntentAdapter;
import eu.inmite.lib.spayd.android.adapter.SpaydFileAdapter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Tomas Vondracek
 */
class SpaydIntentHelper {

	private static final ISpaydIntentAdapter[] sDefaultAdapters = new ISpaydIntentAdapter[]{
			new SpaydFileAdapter(), new SimpleIntentAdapter(), new NfcIntentAdapter()
	};

	static SpaydResult getSpaydFromIntent(@NotNull Intent intent, @NotNull Context context) {
		return getSpaydFromIntentWithAdapters(intent, context, sDefaultAdapters);
	}

	static SpaydResult getSpaydFromIntentWithAdapters(@NotNull Intent intent, @NotNull Context context, @NotNull ISpaydIntentAdapter[] adapters) {
		String spayd = null;
		String source = null;
		for (ISpaydIntentAdapter adapter : adapters) {
			spayd = adapter.getSpaydFromIntent(intent, context);

			if (! TextUtils.isEmpty(spayd)) {
				source = adapter.getPaymentSource();
				break;
			}
		}
		if (spayd != null) {
			return new SpaydResult(spayd, source);
		}
		return null;
	}

	static boolean isAnyAdapterAcceptable(Intent intent, Context context) {
		return isAnyAdapterAcceptable(intent, context, sDefaultAdapters);
	}

	static boolean isAnyAdapterAcceptable(@NotNull Intent intent, @NotNull Context context, @NotNull ISpaydIntentAdapter[] adapters) {
		boolean isAcceptable = false;
		for (ISpaydIntentAdapter visitor : adapters) {
			isAcceptable = visitor.containsSpayd(intent, context);
			if (isAcceptable) {
				break;
			}
		}
		return isAcceptable;
	}
}
