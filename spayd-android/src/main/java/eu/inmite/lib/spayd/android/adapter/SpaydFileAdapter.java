package eu.inmite.lib.spayd.android.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import eu.inmite.lib.spayd.android.ISpaydIntentAdapter;
import eu.inmite.lib.spayd.android.IntentConstants;
import eu.inmite.lib.spayd.reader.SpaydReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;

/**
 * @author Tomas Vondracek
 */
public class SpaydFileAdapter implements ISpaydIntentAdapter {

	@Nullable
	@Override
	public String getSpaydFromIntent(@NotNull Intent intent, @NotNull Context context) {
		if (intent.getData() == null) {
			return null;
		}

		final String payload = readSpaydFromUri(context, intent.getData());
		if (SpaydReader.isSpayd(payload)) {
			return payload;
		}

		return null;
	}

	@Override
	public boolean containsSpayd(@NotNull Intent intent, @NotNull Context context) {
		if (intent.getData() == null) {
			return false;
		}
		final String payload = readSpaydFromUri(context, intent.getData());
		return SpaydReader.isSpayd(payload);
	}


	@Nullable
	@Override
	public String getPaymentSource() {
		return IntentConstants.SOURCE_SPD;
	}

	@Nullable
	private static String readSpaydFromUri(final @NotNull Context context, final Uri uri) {
		String payload = null;

		final ContentResolver cr = context.getContentResolver();
		InputStream inputStream = null;
		try {
			inputStream = cr.openInputStream(uri);
			payload = streamToString(new InputStreamReader(inputStream));
		} catch (IOException ignored) {
		}
		finally {
			close(inputStream);
		}
		return payload;
	}

	@NotNull
	private static String streamToString(InputStreamReader isr) throws IOException {
		if (isr != null) {
			final Writer writer = new StringWriter();

			final char[] buffer = new char[128];
			try {
				final Reader reader = new BufferedReader(isr);
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} catch (UnsupportedEncodingException e) {
				return "";
			} finally {
				close(isr);
			}
			return writer.toString();
		} else {
			return "";
		}
	}

	private static void close(final @Nullable Closeable closable) {
		if (closable != null) {
			try {
				closable.close();
			} catch (IOException ignored) { }
		}
	}

}
