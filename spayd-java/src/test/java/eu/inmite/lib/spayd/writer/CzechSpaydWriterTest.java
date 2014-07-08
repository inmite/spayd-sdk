package eu.inmite.lib.spayd.writer;

import eu.inmite.lib.spayd.model.BankAccount;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * @author Tomas Vondracek
 */
public class CzechSpaydWriterTest {

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
	public void testAttributes() throws Exception {
		final WriterResult result = mWriter.write(SpaydOptions.createCzech()
				.withAccount(new BankAccount("CZ5855000000001265098001"))
				.withAmount(new BigDecimal("100.55"))
				.withCurrencyCode("CZK")
				.withId("id")
				.withPer(5)
				.withVariableSymbol("1234567890")
				.withSpecificSymbol("666")
				.withConstantSymbol("5555"));

		assertResult(result, "SPD*1.0*ACC:CZ5855000000001265098001*AM:100.55*CC:CZK*X-ID:id*X-PER:5*X-VS:1234567890*X-SS:666*X-KS:5555");
	}
}
