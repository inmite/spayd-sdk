package eu.inmite.lib.spayd.writer;

/**
 * @author Tomas Vondracek
 */
public class SpaydWriterException extends RuntimeException {

	public SpaydWriterException() {
	}

	public SpaydWriterException(final String message) {
		super(message);
	}

	public SpaydWriterException(final String message, final Throwable cause) {
		super(message, cause);
	}

	public SpaydWriterException(final Throwable cause) {
		super(cause);
	}

	public SpaydWriterException(final String message, final Throwable cause, final boolean enableSuppression, final boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
