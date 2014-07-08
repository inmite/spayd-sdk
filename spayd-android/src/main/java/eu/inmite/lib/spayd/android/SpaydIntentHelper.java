package eu.inmite.lib.spayd.android;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import eu.inmite.lib.spayd.android.adapter.SimpleIntentAdapter;
import eu.inmite.lib.spayd.android.adapter.SpaydFileAdapter;

/**
 * @author Tomas Vondracek
 */
public class SpaydIntentHelper {

	private static final ISpaydIntentAdapter[] sAdapters = new ISpaydIntentAdapter[]{
			new SpaydFileAdapter(), new SimpleIntentAdapter()
	};

	public static String getSpaydFromIntent(Intent intent, Context context) {
		String spayd = null;
		for (ISpaydIntentAdapter visitor : sAdapters) {
			spayd = visitor.getSpaydFromIntent(intent, context);

			if (! TextUtils.isEmpty(spayd)) {
				break;
			}
		}
		return spayd;
	}

	public static boolean isAnyAdapterAcceptable(Intent intent, Context context) {
		boolean isAcceptable = false;
		for (ISpaydIntentAdapter visitor : sAdapters) {
			isAcceptable = visitor.containsSpayd(intent, context);
			if (isAcceptable) {
				break;
			}
		}
		return isAcceptable;
	}
}
