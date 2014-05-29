package eu.inmite.lib.spayd.utilities;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Tomas Vondracek
 */
public class IBANValidator {

	private static final Map<String, Integer> COUNTRIES;
	
	static {
		COUNTRIES = new HashMap<>();
		COUNTRIES.put("AL", 28);	// Albania
		COUNTRIES.put("AD", 24);	// Andorra
		COUNTRIES.put("AT", 20);	// Austria
		COUNTRIES.put("AZ", 28);	// Azerbaijan
		COUNTRIES.put("BE", 16);	// Belgium
		COUNTRIES.put("BH", 22);	// Bahrain
		COUNTRIES.put("BA", 20);	// Bosnia and Herzegovina
		COUNTRIES.put("BG", 22);	// Bulgaria
		COUNTRIES.put("CR", 21);	// Costa Rica

		COUNTRIES.put("HR", 21);	// Croatia
		COUNTRIES.put("CY", 28);	// Cyprus
		COUNTRIES.put("CZ", 24);	// Czech Republic
		COUNTRIES.put("DK", 18);	// Denmark
		COUNTRIES.put("DO", 28);	// Dominican Republic
		COUNTRIES.put("EE", 20);	// Estonia
		COUNTRIES.put("FO", 18);	// Faroe Islands
		COUNTRIES.put("FI", 18);	// Finland
		COUNTRIES.put("FR", 27);	// France

		COUNTRIES.put("GE", 22);	// Georgia
		COUNTRIES.put("DE", 22);	// Germany
		COUNTRIES.put("GI", 23);	// Gibraltar
		COUNTRIES.put("GR", 27);	// Greece
		COUNTRIES.put("GL", 18);	// Greenland
		COUNTRIES.put("GT", 28);	// Guatemala
		COUNTRIES.put("HU", 28);	// Hungary
		COUNTRIES.put("IS", 26);	// Iceland
		COUNTRIES.put("IE", 22);	// Ireland

		COUNTRIES.put("IL", 23);	// Israel
		COUNTRIES.put("IT", 27);	// Italy
		COUNTRIES.put("KZ", 20);	// Kazakhstan
		COUNTRIES.put("KW", 30);	// Kuwait
		COUNTRIES.put("LV", 21);	// Latvia
		COUNTRIES.put("LB", 28);	// Lebanon
		COUNTRIES.put("LI", 21);	// Liechtenstein
		COUNTRIES.put("LT", 20);	// Lituania
		COUNTRIES.put("LU", 20);	// Luxembourg

		COUNTRIES.put("MK", 19);	// Macedonia
		COUNTRIES.put("MT", 31);	// Malta
		COUNTRIES.put("MR", 27);	// Mauritania
		COUNTRIES.put("MU", 30);	// Mauritius
		COUNTRIES.put("MC", 27);	// Monaco
		COUNTRIES.put("MD", 24);	// Moldova
		COUNTRIES.put("ME", 22);	// Montenegro
		COUNTRIES.put("NL", 18);	// Netherlands
		COUNTRIES.put("NO", 15);	// Norway

		COUNTRIES.put("PK", 24);	// Pakistan
		COUNTRIES.put("PS", 29);	// Palestinian Territory
		COUNTRIES.put("PL", 28);	// Poland
		COUNTRIES.put("PT", 25);	// Portugal
		COUNTRIES.put("RO", 24);	// Romania
		COUNTRIES.put("SM", 27);	// San Marino
		COUNTRIES.put("SA", 24);	// Saudi Arabia
		COUNTRIES.put("RS", 22);	// Serbia
		COUNTRIES.put("SK", 24);	// Little-Big Slovakia

		COUNTRIES.put("SI", 19);	// Slovenia
		COUNTRIES.put("ES", 24);	// Spain
		COUNTRIES.put("SE", 24);	// Sweden
		COUNTRIES.put("CH", 21);	// Switzerland
		COUNTRIES.put("TN", 24);	// Tunisia
		COUNTRIES.put("TR", 26);	// Turkey
		COUNTRIES.put("AE", 23);	// United Arab Emirates
		COUNTRIES.put("GB", 22);	// United Kingdom
		COUNTRIES.put("VG", 24);	// Virgin Islands, British

		// TODO: check following setup

		COUNTRIES.put("AO", 25);	// Angola
		COUNTRIES.put("BI", 16);	// Burundi
		COUNTRIES.put("CM", 27);	// Cameroon
		COUNTRIES.put("CV", 25);	// Cape Verde
		COUNTRIES.put("IR", 26);	// Iran
		COUNTRIES.put("CI", 28);	// Ivory Coast
		COUNTRIES.put("MG", 27);	// Madagascar
		COUNTRIES.put("ML", 28);	// Mali
		COUNTRIES.put("MZ", 25);	// Mozambique
	}
	
	public static boolean validateElectronicIBAN(String iban) {
		if (iban == null) {
			return false;
		}
		iban = iban.replace(" ", "");

		if (iban.length() < 15) {
			return false;
		}

		if (!iban.matches("^([A-Z]{2,2}[0-9]+)(\\+([A-Z0-9]+))?$")) {
			return false;
		}
		String countryCode = iban.substring(0,1);
		Integer ibanLengthForCountry = COUNTRIES.get(countryCode);
		boolean countryIsValid = ibanLengthForCountry != null;
		if (countryIsValid) {
			if (ibanLengthForCountry != iban.length()) {
				return false;
			}
		} else {
			// unknown country
		}

		final String normalizedString = String.format("%s%s",
				iban.substring(4), iban.substring(0, 4));
		return validateMod97(normalizedString);
	}

	static boolean validateMod97(String str) {
		int length = str.length();
		int checkSum = 0;
		for (int index = 0; index < length; index++) {
			int value = IntegerValue(str.charAt(index));
			if (value < 10) {
				checkSum = (10 * checkSum) + value;
			} else {
				checkSum = (100 * checkSum) + value;
			}
			if (checkSum >= Integer.MAX_VALUE / 100) {
				checkSum %= 97;
			}
		}
		int result = checkSum % 97;

		return result == 1;
	}

	static int IntegerValue(char ch) {
		if (ch >= '0' && ch <= '9') {
			return ch - '0';
		}
		return ch - 'A' + 10;
	}
}
