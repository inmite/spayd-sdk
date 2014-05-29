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
	private final Throwable mThrowable;

	public static <T extends Payment> ReaderResult<T> exception(Throwable t) {
		return new ReaderResult<>(false, null, null, t);
	}

	public static <T extends Payment> ReaderResult<T> fail() {
		return fail((Collection<SpaydValidationError>) null);
	}

	public static <T extends Payment> ReaderResult<T> fail(final Collection<SpaydValidationError> errors) {
		return new ReaderResult<>(false, errors, null, null);
	}

	public static <T extends Payment> ReaderResult<T> success(final T payment) {
		return new ReaderResult<>(true, null, payment, null);
	}

	private ReaderResult(final boolean isValid, final Collection<SpaydValidationError> errors, final T payment, final Throwable throwable) {
		mIsValid = isValid;
		mErrors = errors;
		mPayment = payment;
		mThrowable = throwable;
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

	public Throwable getThrowable() {
		return mThrowable;
	}

	@Override
	public String toString() {
		return "ReaderResult{" +
				"mIsValid=" + mIsValid +
				", mErrors=" + mErrors +
				", mPayment=" + mPayment +
				", mThrowable=" + mThrowable +
				'}';
	}
}
