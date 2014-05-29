package eu.inmite.lib.spayd.model;

/**
 * @author Tomas Vondracek
 */
public enum SpaydNotificationChannel {

	None,
	Phone,
	Email;

	public static SpaydNotificationChannel fromSpayd(String spaydUnit) {
		if ("E".equals(spaydUnit)) {
			return Email;
		} else if ("P".equals(spaydUnit)) {
			return Phone;
		}
		return None;
	}

}
