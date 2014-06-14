package eu.inmite.lib.spayd.reader;

import eu.inmite.lib.spayd.model.BankAccount;
import eu.inmite.lib.spayd.model.Payment;
import eu.inmite.lib.spayd.model.SpaydNotificationChannel;
import eu.inmite.lib.spayd.reader.impl.DefaultCroppingPostProcessor;
import eu.inmite.lib.spayd.utilities.FormattingUtils;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * @author Tomas Vondracek
 */
public class SpaydReaderTest {

	static final String validCode1 = "SPD*1.0*ACC:CZ5855000000001265098001*AM:480.50*CC:CZK*RF:1234567890123456*X-VS:1234567890*DT:20120524*MSG:PLATBA ZA ZBOZI";
	static final String validCode2 = "SPD*1.0*ACC:CZ5855000000001265098001+RZBCCZPP*AM:480.50*CC:CZK*RF:1234567890123456*X-VS:1234567890*DT:20120524*MSG:PLATBA ZA ZBOZI";
	static final String validCode3 = "SPD*1.0*ACC:CZ5855000000001265098001+RZBCCZPP*AM:480.50*CC:CZK*" +
			"RF:1234567890123456*X-VS:1234567890*X-SS:123*X-KS:890*" +
			"DT:20120524*MSG:PLATBA ZA ZBOZI*PT:STD*" +
			"NT:P*NTA:608328707*RN:Tomas";

	static final String invalidCode1 = "SSS*1.0*ACC:CZ5855000000001265098001+RZBCCZPP*AM:480.50*CC:CZK*RF:1234567890123456*X-VS:1234567890*DT:20120524*MSG:PLATBA ZA ZBOZI";
	static final String invalidCode2 = "no spayd in here";

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

	@Test
	public void testIsSpayd() throws Exception {
		assertTrue(SpaydReader.isSpayd(validCode1));
		assertTrue(SpaydReader.isSpayd(validCode2));
		assertTrue(SpaydReader.isSpayd(validCode3));

		assertFalse(SpaydReader.isSpayd(invalidCode1));
		assertFalse(SpaydReader.isSpayd(invalidCode2));
	}

	@Test
	public void testSpaydRead() throws Exception {
		final ReaderResult<Payment> result1 = mSpayd.readFromSpayd(validCode1);
		assertResult(result1);
		final ReaderResult<Payment> result2 = mSpayd.readFromSpayd(validCode2);
		assertResult(result2);
		final ReaderResult<Payment> result3 = mSpayd.readFromSpayd(validCode3);
		assertResult(result3);
	}

	@Test
	public void testReadAccountWithIBAN() throws Exception {
		final ReaderResult<Payment> result = mSpayd.readFromSpayd(validCode1);
		assertResult(result);

		Payment payment = result.getPayment();
		final BankAccount account = payment.getAccount();
		assertNotNull(account);
		assertEquals("CZ5855000000001265098001", account.getIban());
	}

	@Test
	public void testReadAccountWithIBAN_BIC() throws Exception {
		final ReaderResult<Payment> result = mSpayd.readFromSpayd(validCode2);
		assertResult(result);

		Payment payment = result.getPayment();
		final BankAccount account = payment.getAccount();
		assertNotNull(account);
		assertEquals("CZ5855000000001265098001", account.getIban());
		assertEquals("RZBCCZPP", account.getBic());
	}

	@Test
	public void testReadSpaydAttributes() throws Exception {
		final ReaderResult<Payment> result = mSpayd.readFromSpayd(validCode3);
		assertResult(result);

		Payment payment = result.getPayment();
		assertEquals(new BigDecimal("480.50"), payment.getAmount());
		assertEquals("CZK", payment.getCurrencyCode());
		assertEquals("1234567890123456", payment.getIdentifierForReceiver());
		assertEquals("PLATBA ZA ZBOZI", payment.getMessageForReceiver());
		assertEquals(SpaydNotificationChannel.Phone, payment.getNotificationChannel());
		assertEquals("608328707", payment.getNotificationAddress());
		assertEquals("STD", payment.getPaymentType());
		assertEquals("Tomas", payment.getReceiversName());
		assertEquals(FormattingUtils.parseSpaydDate("20120524", null), payment.getDueDate());
		assertEquals("1.0", payment.getVersion());
	}

	@Test
	public void testAltAccounts() throws Exception {
		final String validCodeAltAccounts = "SPD*1.0*ACC:CZ5855000000001265098001*ALT-ACC:" +
				"CZ3208000000000000007894," +
				"CZ0908000000000353497163," +
				"AT736000000002386492";
		final ReaderResult<Payment> result = mSpayd.readFromSpayd(validCodeAltAccounts);
		assertResult(result);

		List<BankAccount> accounts = new ArrayList(result.getPayment().getAlternateAccounts());

		assertNotNull(accounts);
		assertEquals(3, accounts.size());

		for (BankAccount account : accounts) {
			assertNotNull(account);
			assertNotNull(account.getIban());
			assertNull(account.getBic());
		}
		assertEquals("CZ3208000000000000007894", accounts.get(0).getIban());
		assertEquals("CZ0908000000000353497163", accounts.get(1).getIban());
		assertEquals("AT736000000002386492", accounts.get(2).getIban());
	}

	@Test
	public void testInvalidSpaydVersion() throws Exception {
		final String codeInvalidVersion = "SPD*2.0*ACC:CZ5855000000001265098001+RZBCCZPP*AM:480.50*CC:CZK*RF:1234567890123456*X-VS:1234567890*DT:20120524*MSG:PLATBA ZA ZBOZI";
		final ReaderResult<Payment> result = mSpayd.readFromSpayd(codeInvalidVersion);

		assertNotNull(result);
		assertFalse(result.isValid());
		assertNotNull(result.getErrors());
		assertEquals(1, result.getErrors().size());
	}


	@Test
	public void testWrongValues() {
		ReaderResult<Payment> pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098021");
		assertFalse("IBAN validation is wrong", pay.isValid());

		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*");
		assertTrue("IBAN validation is wrong. Asterisk at the end is optional but valid.", pay.isValid());
//		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*AM:*RN:1");
//		assertTrue("Empty value must be ignored", pay.isValid());   // TODO not sure about this

//		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*AM:*");
//		assertTrue("Empty value must be ignored", pay.isValid());

//		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*AM:");
//		assertTrue("Empty value must be ignored", pay.isValid());

		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*AM:123,0");
		assertFalse("AM validation is wrong", pay.isValid());

		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*AM:123.00");
		assertTrue("AM without currency must be allowed", pay.isValid());
//		assertTrue("Substituted currency code is wrong", pay.getPayment().getCurrencyCode().equals("CZK"));

		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*CC:CZ");
		assertFalse("CC validation is wrong", pay.isValid());
		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*CC:CZKK");
		assertFalse("CC validation is wrong", pay.isValid());

		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*RF:uydfuy38478743");
		assertFalse("RF validation is wrong", pay.isValid());
		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*RF:12345678901234560");
		assertFalse("RF validation is wrong", pay.isValid());

		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*RN:123456789 123456789 123456789 12345xxxxxxxxxxx");
		assertTrue("RN validation is wrong", pay.isValid());
		assertTrue("RN should be cropped to maximum length", pay.getPayment().getReceiversName().equals("123456789 123456789 123456789 12345"));

		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*MSG:123456789 123456789 123456789 123456789 123456789 123456789 xxxx");
		assertTrue("MSG validation is wrong", pay.isValid());
		assertTrue("MSG should be cropped to maximum length", pay.getPayment().getMessageForReceiver().equals("123456789 123456789 123456789 123456789 123456789 123456789 "));

		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*DT:2012 10 10");
		assertFalse("DT validation is wrong", pay.isValid());
		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*DT:201299990");
		assertFalse("DT validation is wrong", pay.isValid());

		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*PT:2222");
		assertFalse("PT validation is wrong", pay.isValid());

		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*NT:X");
		assertFalse("NT validation is wrong", pay.isValid());
		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*NT:P");
		assertTrue("NT validation is wrong", pay.isValid());
		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*NT:E");
		assertTrue("NT validation is wrong", pay.isValid());

		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*WRONG:XXX");
		assertFalse("Known tags validation is wrong", pay.isValid());
		pay = mSpayd.readFromSpayd("SPD*1.0*ACC:CZ5855000000001265098001*X-UNKNOWN:XXX");
		assertTrue("X-TAG validation is wrong", pay.isValid());
	}

	@Test
	public void testPerformance() throws Exception {
		final String spayd = "SPD*1.0*ACC:CZ5855000000001265098001*AM:123.00*MSG:123456789 123456789 123456789 123456789 123456789 123456789*CC:CZK*RF:1234567890123456";

		final long start = System.currentTimeMillis();
		int attempt = 0;
		final int loops = 1000;
		while (attempt < loops) {
			mSpayd.readFromSpayd(spayd);
			attempt++;
		}
		final long time = System.currentTimeMillis() - start;

		System.out.println("time for " + loops + " loops [ms]: " + time);
	}
}
