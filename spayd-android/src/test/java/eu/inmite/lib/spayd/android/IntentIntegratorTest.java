package eu.inmite.lib.spayd.android;

import android.content.Intent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class IntentIntegratorTest {

	@Test
	public void testContainsSpayd() throws Exception {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setType(Constants.INTENT_MIME_TYPE);
		assertFalse(IntentIntegrator.containsSpayd(intent, Robolectric.application));

		intent.putExtra(Constants.INTENT_EXTRA_SPAYD, "not a spayd");
		assertFalse(IntentIntegrator.containsSpayd(intent, Robolectric.application));

		intent.putExtra(Constants.INTENT_EXTRA_SPAYD, "SPD*1.0*ACC:CZ5855000000001265098001*");
		assertTrue(IntentIntegrator.containsSpayd(intent, Robolectric.application));
	}

	@Test
	public void testGetSpaydFromIntent() throws Exception {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setType(Constants.INTENT_MIME_TYPE);
		final String spayd = "SPD*1.0*ACC:CZ5855000000001265098001*";
		intent.putExtra(Constants.INTENT_EXTRA_SPAYD, spayd);

		final String spaydFromIntent = IntentIntegrator.getSpaydFromIntent(intent, Robolectric.application);
		assertEquals(spayd, spaydFromIntent);
	}

	@Test
	public void testCreateIntentForSpayd() throws Exception {
		final String spayd = "SPD*1.0*ACC:CZ5855000000001265098001*";
		final Intent intent = IntentIntegrator.createIntentForSpayd(spayd);

		assertEquals(Intent.ACTION_VIEW, intent.getAction());
		assertEquals(Constants.INTENT_MIME_TYPE, intent.getType());
		assertNotNull(intent.getExtras());
		assertTrue(intent.getExtras().containsKey(Constants.INTENT_EXTRA_SPAYD));
		assertEquals(spayd, intent.getStringExtra(Constants.INTENT_EXTRA_SPAYD));
	}
}