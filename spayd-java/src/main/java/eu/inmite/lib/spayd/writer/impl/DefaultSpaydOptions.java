package eu.inmite.lib.spayd.writer.impl;

import eu.inmite.lib.spayd.writer.SpaydOptions;

import java.util.TimeZone;

/**
 * @author Tomas Vondracek
 */
public class DefaultSpaydOptions extends SpaydOptions<DefaultSpaydOptions> {

	public static DefaultSpaydOptions create() {
		return new DefaultSpaydOptions("1.0", null);
	}

	public static DefaultSpaydOptions create(String version) {
		return new DefaultSpaydOptions(version, null);
	}

	public static DefaultSpaydOptions create(String version, TimeZone timeZone) {
		return new DefaultSpaydOptions(version, timeZone);
	}

	protected DefaultSpaydOptions(final String version, final TimeZone timeZone) {
		super(version, timeZone);
	}

	@Override
	protected DefaultSpaydOptions self() {
		return this;
	}
}
