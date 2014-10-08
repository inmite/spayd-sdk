package eu.inmite.lib.spayd.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import eu.inmite.lib.spayd.android.ISpaydIntentAdapter;
import eu.inmite.lib.spayd.android.IntentConstants;
import eu.inmite.lib.spayd.reader.SpaydReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Tomas Vondracek
 */
public class NfcIntentAdapter implements ISpaydIntentAdapter {

	private static boolean hasNfc() {
		return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1;
	}

	@Nullable
	@Override
	public String getSpaydFromIntent(@NotNull Intent intent, @NotNull Context context) {
		if (! hasNfc()) {
			return null;
		}
		return Nfc.read(intent);
	}

	@Override
	public boolean containsSpayd(@NotNull Intent intent, @NotNull Context context) {
		if (! hasNfc()) {
			return false;
		}
		final String payload = Nfc.read(intent);

		return SpaydReader.isSpayd(payload);
	}

	@Nullable
	@Override
	public String getPaymentSource() {
		return IntentConstants.SOURCE_NFC;
	}
}
