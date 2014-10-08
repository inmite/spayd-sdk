package eu.inmite.lib.spayd.android.adapter;

import android.content.Context;
import android.content.Intent;
import eu.inmite.lib.spayd.android.IntentConstants;
import eu.inmite.lib.spayd.android.ISpaydIntentAdapter;
import eu.inmite.lib.spayd.reader.SpaydReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Tomas Vondracek
 */
public class SimpleIntentAdapter implements ISpaydIntentAdapter {

	@Nullable
	@Override
	public String getSpaydFromIntent(@NotNull Intent intent, @NotNull Context context) {
		if (containsSpayd(intent, context)) {
			final String payload = intent.getStringExtra(IntentConstants.EXTRA_SPAYD);

			if (SpaydReader.isSpayd(payload)) {
				return payload;
			}
		}
		return null;
	}

	@Override
	public boolean containsSpayd(@NotNull Intent intent, @NotNull Context context) {
		if (intent.getExtras() != null && intent.getExtras().containsKey(IntentConstants.EXTRA_SPAYD)) {
			final String payload = intent.getStringExtra(IntentConstants.EXTRA_SPAYD);

			return SpaydReader.isSpayd(payload);
		}
		return false;
	}

	@Nullable
	@Override
	public String getPaymentSource() {
		return IntentConstants.SOURCE_INT;
	}
}
