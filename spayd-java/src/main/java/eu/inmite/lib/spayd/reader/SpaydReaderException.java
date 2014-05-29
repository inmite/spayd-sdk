package eu.inmite.lib.spayd.reader;

/**
 * @author Tomas Vondracek
 */
public class SpaydReaderException extends RuntimeException {

	public SpaydReaderException() {
	}

	public SpaydReaderException(final String message) {
		super(message);
	}

	public SpaydReaderException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public SpaydReaderException(final Throwable cause) {
		super(cause);
	}

	public SpaydReaderException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
