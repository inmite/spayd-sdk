package eu.inmite.lib.spayd.model;

import org.jetbrains.annotations.Nullable;

/**
 * @author Tomas Vondracek
 */
public enum SpaydNotificationChannel {

	None(null),
	Phone("P"),
	Email("E");

	private final String mSpaydUnit;

	SpaydNotificationChannel(final String spaydUnit) {
		mSpaydUnit = spaydUnit;
	}

	public static SpaydNotificationChannel fromSpayd(String spaydUnit) {
		if (Email.mSpaydUnit.equals(spaydUnit)) {
			return Email;
		} else if (Phone.mSpaydUnit.equals(spaydUnit)) {
			return Phone;
		}
		return None;
	}

	@Nullable
	public String getSpaydUnit() {
		return mSpaydUnit;
	}
}
