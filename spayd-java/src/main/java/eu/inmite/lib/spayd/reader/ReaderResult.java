package eu.inmite.lib.spayd.reader;

import eu.inmite.lib.spayd.model.Payment;
import eu.inmite.lib.spayd.model.SpaydValidationError;

import java.util.Collection;

/**
 * @author Tomas Vondracek
 */
public class ReaderResult<T extends Payment> {

	private final boolean mIsValid;
	private final Collection<SpaydValidationError> mErrors;
	private final T mPayment;

	static <T extends Payment> ReaderResult<T> fail() {
		return fail((Collection<SpaydValidationError>) null);
	}

	static <T extends Payment> ReaderResult<T> fail(final Collection<SpaydValidationError> errors) {
		return new ReaderResult<>(false, errors, null);
	}

	static <T extends Payment> ReaderResult<T> success(final T payment) {
		return new ReaderResult<>(true, null, payment);
	}

	private ReaderResult(final boolean isValid, final Collection<SpaydValidationError> errors, final T payment) {
		mIsValid = isValid;
		mErrors = errors;
		mPayment = payment;
	}

	public boolean isSuccess() {
		return mPayment != null;
	}

	public boolean isValid() {
		return mIsValid;
	}

	public Collection<SpaydValidationError> getErrors() {
		return mErrors;
	}

	public T getPayment() {
		return mPayment;
	}

	@Override
	public String toString() {
		return "ReaderResult{" +
				"mIsValid=" + mIsValid +
				", mErrors=" + mErrors +
				", mPayment=" + mPayment +
				'}';
	}
}
