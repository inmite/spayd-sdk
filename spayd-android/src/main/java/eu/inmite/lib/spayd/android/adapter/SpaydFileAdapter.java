package eu.inmite.lib.spayd.android.adapter;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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

		ContentResolver cr = context.getContentResolver();
		try {
			InputStream inputStream = cr.openInputStream(intent.getData());
			String payload = streamToString(new InputStreamReader(inputStream));
			if (SpaydReader.isSpayd(payload)) {
				return payload;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	@Override
	public boolean containsSpayd(@NotNull Intent intent, @NotNull Context context) {
		if (intent.getData() == null) {
			return false;
		}
		ContentResolver cr = context.getContentResolver();
		try {
			InputStream inputStream = cr.openInputStream(intent.getData());
			String payload = streamToString(new InputStreamReader(inputStream));
			return SpaydReader.isSpayd(payload);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Nullable
	@Override
	public String getPaymentSource() {
		return IntentConstants.SOURCE_SPD;
	}

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
				try {
					isr.close();
				} catch (IOException ignored) { }
			}
			return writer.toString();
		} else {
			return "";
		}
	}

}
