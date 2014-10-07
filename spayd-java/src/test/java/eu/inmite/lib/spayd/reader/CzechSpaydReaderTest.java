package eu.inmite.lib.spayd.reader;

import eu.inmite.lib.spayd.model.CzechPayment;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author Tomas Vondracek
 */
public class CzechSpaydReaderTest {

	static final String validCode1 = "SPD*1.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK*RF:1234567890123456*X-VS:1234567890*DT:20120524*MSG:PLATBA ZA ZBOZI";
	static final String validCode2 = "SPD*1.0*ACC:CZ5855000000001265098001+RZBCCZPP*AM:480.50*CC:CZK*RF:1234567890123456*X-VS:1234567890*DT:20120524*MSG:PLATBA ZA ZBOZI";
	static final String validCode3 = "SPD*1.0*ACC:CZ5855000000001265098001+RZBCCZPP*AM:480.50*CC:CZK*" +
			"RF:1234567890123456*X-VS:1234567890*X-SS:123*X-KS:890*" +
			"DT:20120524*MSG:PLATBA ZA ZBOZI*PT:STD*" +
			"NT:P*NTA:608328707*RN:Tomas*X-URL:inmite.eu*X-ID:customId*X-PER:2";

	private SpaydReader<CzechPayment> mSpayd;

	@Before
	public void setUp() throws Exception {
		mSpayd = SpaydReader.from(SpaydConfig.czechConfig());
	}

	private static void assertResult(final ReaderResult<CzechPayment> result) {
		assertNotNull("result is null", result);
		assertNotNull("payment is null with errors " + result.getErrors(), result.getPayment());
		assertNull(result.getErrors());
		assertTrue(result.isSuccess());
		assertTrue(result.isValid());
	}

	private static void assertOneError(final ReaderResult<CzechPayment> result) {
		assertFalse(result.isValid());
		assertFalse(result.isSuccess());
		assertNotNull(result.getErrors());
		assertNotNull(result.getErrors().size() == 1);
	}

	@Test
	public void testIsSpayd() throws Exception {
		assertTrue(SpaydReader.isSpayd(validCode1));
		assertTrue(SpaydReader.isSpayd(validCode2));
		assertTrue(SpaydReader.isSpayd(validCode3));
	}

	@Test
	public void testSpaydRead() throws Exception {
		final ReaderResult<CzechPayment> result1 = mSpayd.readFromSpayd(validCode3);
		assertResult(result1);

		final CzechPayment payment = result1.getPayment();
		assertEquals("1234567890", payment.getVariableSymbol());
		assertEquals("123", payment.getSpecificSymbol());
		assertEquals("890", payment.getConstantSymbol());
		assertEquals("inmite.eu", payment.getCustomUrl());
		assertEquals("customId", payment.getCustomIdentifier());
		assertEquals(Integer.valueOf(2), payment.getRepeatDaysCount());
	}

	@Test
	public void testWrongValues() throws Exception {
		ReaderResult<CzechPayment> result = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK*X-VS:123456789012*");
		assertOneError(result);

		result = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK*X-SS:AbC*");
		assertOneError(result);
		result = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK*X-KS:dEf*");
		assertOneError(result);
		result = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK*X-SS:123:dEf*");
		assertOneError(result);
		result = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK*X-PER:2a*");
		assertOneError(result);
		result = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK*X-PER:100*");
		assertOneError(result);

		result = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK*" +
				"X-VS:123456789012*X-SS:AbC*X-KS:dEf*" +
				"NT:P*NTA:608328707*RN:Tomas*X-PER:2a");

		assertNotNull(result.getErrors());
		assertNotNull(result.getErrors().size() == 4);
	}
}
