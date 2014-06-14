/**
 *  Copyright (c) 2012, SPAYD (www.spayd.org).
 */
package eu.inmite.lib.spayd.model;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author petrdvorak
 */
public class BankAccount {
    
    protected final @NotNull String iban;
    protected final @Nullable String bic;

    public BankAccount(@NotNull String iban) {
        this.iban = iban;
	    this.bic = null;
    }

    public BankAccount(@NotNull String iban, @Nullable String bic) {
        this.iban = iban;
        this.bic = bic;
    }

	@NotNull
	public String getIban() {
		return iban;
	}

	@Nullable
	public String getBic() {
		return bic;
	}

	@Override
	public String toString() {
		return "BankAccount{" +
				"iban='" + iban + '\'' +
				", bic='" + bic + '\'' +
				'}';
	}
}
