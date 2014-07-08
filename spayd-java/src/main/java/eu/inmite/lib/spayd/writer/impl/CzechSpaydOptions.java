package eu.inmite.lib.spayd.writer.impl;

import eu.inmite.lib.spayd.writer.SpaydOptions;

import java.util.TimeZone;

/**
 * @author Tomas Vondracek
 */
public class CzechSpaydOptions extends SpaydOptions<CzechSpaydOptions> {

	public static CzechSpaydOptions create() {
		return new CzechSpaydOptions("1.0", null);
	}

	public static CzechSpaydOptions create(String version) {
		return new CzechSpaydOptions(version, null);
	}

	public static CzechSpaydOptions create(String version, TimeZone timeZone) {
		return new CzechSpaydOptions(version, timeZone);
	}

	private CzechSpaydOptions(final String version, final TimeZone timeZone) {
		super(version, timeZone);
	}

	@Override
	protected CzechSpaydOptions self() {
		return this;
	}

	public CzechSpaydOptions withVariableSymbol(final String vs) {
		return withAttribute(false, "X-VS", vs);
	}

	public CzechSpaydOptions withSpecificSymbol(final String ss) {
		return withAttribute(false, "X-SS", ss);
	}

	public CzechSpaydOptions withConstantSymbol(final String cs) {
		return withAttribute(false, "X-KS", cs);
	}

	public CzechSpaydOptions withPer(final int days) {
		return withAttribute(false, "X-PER", String.valueOf(days));
	}

	public CzechSpaydOptions withId(final String identifier) {
		return withAttribute(false, "X-ID", identifier);
	}

	public CzechSpaydOptions withUrl(final String url) {
		return withAttribute(true, "X-URL", url);
	}
}
