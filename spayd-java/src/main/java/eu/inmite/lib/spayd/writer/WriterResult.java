package eu.inmite.lib.spayd.writer;

import eu.inmite.lib.spayd.model.SpaydValidationError;

import java.util.Collection;

/**
 * @author Tomas Vondracek
 */
public class WriterResult {

	private final String mSpayd;
	private final Collection<SpaydValidationError> mErrors;

	public static WriterResult success(final String spayd) {
		return new WriterResult(spayd, null);
	}

	public static WriterResult fail(final Collection<SpaydValidationError> errors) {
		return new WriterResult(null, errors);
	}

	private WriterResult(final String spayd, final Collection<SpaydValidationError> errors) {
		mSpayd = spayd;
		mErrors = errors;
	}

	public boolean isSuccess() {
		return mSpayd != null;
	}

	public boolean hasErrors() {
		return mErrors != null && mErrors.size() > 0;
	}

	public String getSpayd() {
		return mSpayd;
	}

	public Collection<SpaydValidationError> getErrors() {
		return mErrors;
	}

	@Override
	public String toString() {
		return "WriterResult{" +
				"mSpayd='" + mSpayd + '\'' +
				", mErrors=" + mErrors +
				'}';
	}
}
