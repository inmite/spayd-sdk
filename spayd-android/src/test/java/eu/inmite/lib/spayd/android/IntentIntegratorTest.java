package eu.inmite.lib.spayd.android;

import android.content.Context;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class IntentIntegratorTest {

	private static final String SPAYD = "SPD*1.0*ACC:CZ5855000000001265098001*";

	@Test
	public void testContainsSpayd() throws Exception {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setType(IntentConstants.MIME_TYPE);
		assertFalse(IntentIntegrator.containsSpayd(intent, Robolectric.application));

		intent.putExtra(IntentConstants.EXTRA_SPAYD, "not a spayd");
		assertFalse(IntentIntegrator.containsSpayd(intent, Robolectric.application));

		intent.putExtra(IntentConstants.EXTRA_SPAYD, SPAYD);
		assertTrue(IntentIntegrator.containsSpayd(intent, Robolectric.application));
	}

	@Test
	public void testContainsSpaydWithCustomAdapter() throws Exception {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		assertTrue(IntentIntegrator.containsSpayd(intent, Robolectric.application, new CustomSpaydIntentAdapter()));
	}

	@Test
	public void testGetSpaydFromIntent() throws Exception {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setType(IntentConstants.MIME_TYPE);
		final String spayd = SPAYD;
		intent.putExtra(IntentConstants.EXTRA_SPAYD, spayd);

		final String spaydFromIntent = IntentIntegrator.getSpaydFromIntent(intent, Robolectric.application);
		assertEquals(spayd, spaydFromIntent);
	}

	@Test
	public void testGetSpaydWithSourceFromNfc() throws Exception {
		@SuppressWarnings("deprecation") final NdefRecord record = new NdefRecord(SPAYD.getBytes());
		final NdefMessage message = new NdefMessage(new NdefRecord[] {record});
		final Intent intent = new Intent(NfcAdapter.ACTION_NDEF_DISCOVERED);
		intent.putExtra(NfcAdapter.EXTRA_NDEF_MESSAGES, new Parcelable[]{message});

		final SpaydResult result = IntentIntegrator.getSpaydAndSourceFromIntent(intent, Robolectric.application);
		assertNotNull(result);
		assertEquals(SPAYD, result.getSpayd());
		assertEquals(IntentConstants.SOURCE_NFC, result.getSource());
	}

	@Test
	public void testGetSpaydWithSourceFromIntent() throws Exception {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setType(IntentConstants.MIME_TYPE);
		final String spayd = SPAYD;
		intent.putExtra(IntentConstants.EXTRA_SPAYD, spayd);

		final SpaydResult result = IntentIntegrator.getSpaydAndSourceFromIntent(intent, Robolectric.application);
		assertNotNull(result);
		assertEquals(spayd, result.getSpayd());
		assertEquals(IntentConstants.SOURCE_INT, result.getSource());
	}

	@Test
	public void testGetSpaydWithSourceFromIntentWithCustomAdapter() throws Exception {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setType(IntentConstants.MIME_TYPE);

		final SpaydResult result = IntentIntegrator.getSpaydAndSourceFromIntent(intent, Robolectric.application, new CustomSpaydIntentAdapter());
		assertNotNull(result);
		assertEquals(SPAYD, result.getSpayd());
		assertEquals("custom", result.getSource());
	}

	@Test
	public void testCreateIntentForSpayd() throws Exception {
		final String spayd = SPAYD;
		final Intent intent = IntentIntegrator.createIntentForSpayd(spayd);

		assertEquals(Intent.ACTION_VIEW, intent.getAction());
		assertEquals(IntentConstants.MIME_TYPE, intent.getType());
		assertNotNull(intent.getExtras());
		assertTrue(intent.getExtras().containsKey(IntentConstants.EXTRA_SPAYD));
		assertEquals(spayd, intent.getStringExtra(IntentConstants.EXTRA_SPAYD));
	}

	private static class CustomSpaydIntentAdapter implements ISpaydIntentAdapter {
		@Nullable
		@Override
		public String getSpaydFromIntent(@NotNull final Intent intent, @NotNull final Context context) {
			return SPAYD;
		}

		@Override
		public boolean containsSpayd(@NotNull final Intent intent, @NotNull final Context context) {
			return true;
		}

		@Nullable
		@Override
		public String getPaymentSource() {
			return "custom";
		}
	}
}