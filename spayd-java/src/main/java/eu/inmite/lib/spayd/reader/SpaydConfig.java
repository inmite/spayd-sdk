package eu.inmite.lib.spayd.reader;

import eu.inmite.lib.spayd.model.CzechPayment;
import eu.inmite.lib.spayd.model.Payment;
import eu.inmite.lib.spayd.reader.impl.CzechPaymentBuilder;
import eu.inmite.lib.spayd.reader.impl.CzechSpaydValidator;
import eu.inmite.lib.spayd.reader.impl.DefaultPaymentBuilder;
import eu.inmite.lib.spayd.reader.impl.DefaultSpaydValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.TimeZone;

/**
* @author Tomas Vondracek
*/
public class SpaydConfig<T extends Payment> {

	private final @NotNull TimeZone mTimeZone;
	private final @NotNull IPaymentBuilder<T> mBuilder;
	private final @NotNull ISpaydValidator mValidator;

	private final @Nullable ISpaydPostProcessor mPostProcessor;

	public static <P extends Payment> ConfigurationOptions<P> prepare(TimeZone timeZone) {
		return new ConfigurationOptions<>(timeZone);
	}

	public static <P extends Payment> ConfigurationOptions<P> prepareWithDefaultTimeZone() {
		return new ConfigurationOptions<>(TimeZone.getDefault());
	}

	public static <P extends Payment> SpaydConfig<P> defaultConfig() {
		return new SpaydConfig<>(TimeZone.getDefault(), (IPaymentBuilder<P>) new DefaultPaymentBuilder(), new DefaultSpaydValidator());
	}

	public static <P extends Payment> SpaydConfig<P> defaultConfig(final TimeZone timeZone) {
		return new SpaydConfig<>(timeZone, (IPaymentBuilder<P>) new DefaultPaymentBuilder(), new DefaultSpaydValidator());
	}

	public static <P extends Payment> SpaydConfig<P> defaultConfig(final ISpaydPostProcessor processor) {
		return new SpaydConfig<>(TimeZone.getDefault(), (IPaymentBuilder<P>) new DefaultPaymentBuilder(), new DefaultSpaydValidator(), processor);
	}

	public static SpaydConfig<CzechPayment> czechConfig() {
		return new SpaydConfig<>(TimeZone.getDefault(), new CzechPaymentBuilder(), new CzechSpaydValidator());
	}
	public static SpaydConfig<CzechPayment> czechConfig(final @NotNull TimeZone timeZone) {
		return new SpaydConfig<>(timeZone, new CzechPaymentBuilder(), new CzechSpaydValidator());
	}

	SpaydConfig(@NotNull final TimeZone timeZone,
	            @NotNull final IPaymentBuilder<T> builder,
	            @NotNull final ISpaydValidator validator) {
		this(timeZone, builder, validator, null);
	}

	SpaydConfig(@NotNull final TimeZone timeZone,
	            @NotNull final IPaymentBuilder<T> builder,
	            @NotNull final ISpaydValidator validator,
	            @Nullable final ISpaydPostProcessor processor) {
		mTimeZone = timeZone;
		mBuilder = builder;
		mValidator = validator;
		mPostProcessor = processor;
	}

	@NotNull
	public TimeZone getTimeZone() {
		return mTimeZone;
	}

	@NotNull
	public IPaymentBuilder<T> getBuilder() {
		return mBuilder;
	}

	@NotNull
	public ISpaydValidator getValidator() {
		return mValidator;
	}

	@Nullable
	public ISpaydPostProcessor getPostProcessor() {
		return mPostProcessor;
	}

	static class ConfigurationOptions<V extends Payment> {

		private final TimeZone mTimeZone;
		private IPaymentBuilder<V> mBuilder;
		private ISpaydValidator mValidator;
		private ISpaydPostProcessor mProcessor;

		ConfigurationOptions(final TimeZone timeZone) {
			mTimeZone = timeZone;
		}

		public ConfigurationOptions<V> withPaymentBuilder(@NotNull IPaymentBuilder<V> builder) {
			mBuilder = builder;
			return this;
		}

		public ConfigurationOptions<V> withValidator(@NotNull ISpaydValidator validator) {
			mValidator = validator;
			return this;
		}

		public ConfigurationOptions<V> withPostProcess(ISpaydPostProcessor processor) {
			mProcessor = processor;
			return this;
		}

		public SpaydConfig<V> build() {
			if (mBuilder == null) {
				mBuilder = (IPaymentBuilder<V>) new DefaultPaymentBuilder();
			}
			if (mValidator == null) {
				mValidator = new DefaultSpaydValidator();
			}
			return new SpaydConfig<>(mTimeZone, mBuilder, mValidator, mProcessor);
		}
	}
}
