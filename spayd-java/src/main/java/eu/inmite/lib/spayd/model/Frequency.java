package eu.inmite.lib.spayd.model;

import org.jetbrains.annotations.Nullable;

/**
 * Standing order frequency.
 * @author Tomas Vondracek (vondracek@avast.com)
 */
public enum Frequency {
	FRQ_1D("1D"),
	FRQ_1W("1W"),
	FRQ_1M("1M"),
	FRQ_3M("3M"),
	FRQ_6M("6M"),
	FRQ_1Y("1Y");

	@Nullable
	public static Frequency fromSpaydFrequency(String name) {
		if (name == null) {
			return null;
		}
		switch (name) {
			case "1D":
				return FRQ_1D;
			case "1W":
				return FRQ_1W;
			case "1M":
				return FRQ_1M;
			case "3M":
				return FRQ_3M;
			case "6M":
				return FRQ_6M;
			case "1Y":
				return FRQ_1Y;
			default:
				return null;
		}
	}

	private final String name;

	Frequency(final String name) {
		this.name = name;
	}

	public String getFrequencyText() {
		return name;
	}


	@Override
	public String toString() {
		return "Frequency{" +
				"name='" + name + '\'' +
				"} " + super.toString();
	}
}
