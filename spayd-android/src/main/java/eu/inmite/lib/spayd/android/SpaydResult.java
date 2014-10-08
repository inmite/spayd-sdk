package eu.inmite.lib.spayd.android;

import android.os.Parcel;
import android.os.Parcelable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Tomas Vondracek (vondracek@avast.com)
 */
public class SpaydResult implements Parcelable {

	private final @NotNull String mSpayd;
	private final @Nullable String mSource;

	public SpaydResult(final @NotNull String spayd, final @Nullable String source) {
		mSpayd = spayd;
		mSource = source;
	}

	private SpaydResult(Parcel in) {
		mSpayd = in.readString();
		mSource = in.readString();
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.mSpayd);
		dest.writeString(this.mSource);
	}

	@NotNull
	public String getSpayd() {
		return mSpayd;
	}

	@Nullable
	public String getSource() {
		return mSource;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	public static final Parcelable.Creator<SpaydResult> CREATOR = new Parcelable.Creator<SpaydResult>() {
		public SpaydResult createFromParcel(Parcel source) {
			return new SpaydResult(source);
		}

		public SpaydResult[] newArray(int size) {
			return new SpaydResult[size];
		}
	};

	@Override
	public String toString() {
		return "SpaydResult{" +
				"mSpayd='" + mSpayd + '\'' +
				", mSource=" + mSource +
				'}';
	}
}
