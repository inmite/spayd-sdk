package eu.inmite.lib.spayd.reader;

import eu.inmite.lib.spayd.model.Frequency;
import eu.inmite.lib.spayd.model.Payment;
import eu.inmite.lib.spayd.reader.impl.DefaultCroppingPostProcessor;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author Tomas Vondracek
 */
public class SpaydReaderStandingOrderTest {

	private SpaydReader<Payment> mSpayd;

	@Before
	public void setUp() throws Exception {
		mSpayd = SpaydReader.from(SpaydConfig.defaultConfig(new DefaultCroppingPostProcessor()));
	}

	private static void assertResult(final ReaderResult<Payment> result) {
		assertNotNull("result is null", result);
		assertNotNull("payment is null with errors " + result.getErrors(), result.getPayment());
		assertNull(result.getErrors());
		assertTrue(result.isSuccess());
		assertTrue(result.isValid());
	}

	private static void assertOneError(final ReaderResult<Payment> result) {
		assertFalse(result.isValid());
		assertNotNull(result.getErrors());
		assertEquals(1, result.getErrors().size());
	}

	@Test
	public void testNotStandingOrder() throws Exception {
		ReaderResult<Payment> result = mSpayd.readFromSpayd("SPD*2.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK");
		assertFalse(result.getPayment().isStandingOrder());

		result = mSpayd.readFromSpayd("SPD*2.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK*DL:20161007");
		assertFalse(result.getPayment().isStandingOrder());
	}


	@Test
	public void testFrequency() throws Exception {
		final ReaderResult<Payment> result = mSpayd.readFromSpayd("SPD*2.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK*FRQ:1M");
		assertResult(result);

		final Payment payment = result.getPayment();
		assertTrue(payment.isStandingOrder());
		assertEquals(Frequency.FRQ_1M, payment.getFrequency());
		assertNull(payment.getLastDate());
		assertNull(payment.getDueDate());
	}

	@Test
	public void testDates() throws Exception {
		final ReaderResult<Payment> result = mSpayd.readFromSpayd("SPD*2.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK*FRQ:1M*DL:20161007*DT:20141007");
		assertResult(result);

		final Payment payment = result.getPayment();
		assertTrue(payment.isStandingOrder());

		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		Date lastDate = format.parse("20161007");
		Date dueDate = format.parse("20141007");
		assertEquals(lastDate, payment.getLastDate());
		assertEquals(dueDate, payment.getDueDate());
	}

	@Test
	public void testWrongValues() throws Exception {
		ReaderResult<Payment> result = mSpayd.readFromSpayd("SPD*2.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK*FRQ:11M");
		assertOneError(result);
		result = mSpayd.readFromSpayd("SPD*2.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK*FRQ:1M*DL:201299990");
		assertOneError(result);
		result = mSpayd.readFromSpayd("SPD*2.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK*FRQ:1M*DT:201299990");
		assertOneError(result);

	}
}
