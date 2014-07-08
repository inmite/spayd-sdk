package eu.inmite.lib.spayd.android;

import android.content.Context;
import android.content.Intent;

/**
 * @author Tomas Vondracek
 */
public interface ISpaydIntentAdapter {

	public String getSpaydFromIntent(Intent intent, Context context);

	public boolean containsSpayd(Intent intent, Context context);
}
