package eu.inmite.lib.spayd.android;

import android.content.Context;
import android.content.Intent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Tomas Vondracek
 */
public interface ISpaydIntentAdapter {

	@Nullable
	public String getSpaydFromIntent(@NotNull Intent intent, @NotNull Context context);

	public boolean containsSpayd(@NotNull Intent intent, @NotNull Context context);

	@Nullable
	public String getPaymentSource();
}
