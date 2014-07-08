package eu.inmite.lib.spayd.android.adapter;

import android.content.Context;
import android.content.Intent;
import eu.inmite.lib.spayd.android.ISpaydIntentAdapter;

/**
 * @author Tomas Vondracek
 */
public class NfcIntentAdapter implements ISpaydIntentAdapter {

	@Override
	public String getSpaydFromIntent(Intent intent, Context context) {
		// TODO
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsSpayd(Intent intent, Context context) {
		return false;

	}
}
