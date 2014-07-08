package eu.inmite.lib.spayd.writer;

import eu.inmite.lib.spayd.model.BankAccount;
import eu.inmite.lib.spayd.model.SpaydNotificationChannel;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Tomas Vondracek
 */
public class SpaydWriterTest {

	SpaydWriter mWriter;

	@Before
	public void setUp() throws Exception {
		mWriter = SpaydWriter.create();
	}

	private void assertResult(final WriterResult result, final String expectedSpayd) {
		assertNotNull(result);
		assertTrue(result.isSuccess());
		assertEquals(expectedSpayd, result.getSpayd());
	}

	@Test
	public void testIban() throws Exception {
		final WriterResult result = mWriter.write(SpaydOptions.createDefault()
				.withAccount(new BankAccount("CZ5855000000001265098001")));

		assertResult(result, "SPD*1.0*ACC:CZ5855000000001265098001");
	}

	@Test
	public void testIbanBIC() throws Exception {
		final WriterResult result = mWriter.write(SpaydOptions.createDefault()
				.withAccount(new BankAccount("CZ5855000000001265098001", "RZBCCZPP")));

		assertResult(result, "SPD*1.0*ACC:CZ5855000000001265098001+RZBCCZPP");
	}

	@Test
	public void testAttributes() throws Exception {
		final WriterResult result = mWriter.write(SpaydOptions.createDefault()
				.withAccount(new BankAccount("CZ5855000000001265098001"))
				.withAmount(new BigDecimal("100.55"))
				.withCurrencyCode("USD")
				.withDueDate(new Date(1402804800000L))
				.withIdentifierForReceiver("2323")
				.withMessageForReceiver("Say hello to QR payment")
				.withNotificationChannel(SpaydNotificationChannel.Email)
				.withNotificationAddress("qr@payment.cz")
				.withPaymentType("SPD")
				.withReceiversName("Tomas"));
		assertResult(result, "SPD*1.0*ACC:CZ5855000000001265098001*AM:100.55*CC:USD*DT:20140615" +
				"*RF:2323*MSG:Say+hello+to+QR+payment*NT:E*NTA:qr%40payment.cz*PT:SPD*RN:Tomas");
	}
}
