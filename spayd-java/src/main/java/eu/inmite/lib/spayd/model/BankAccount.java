/**
 *  Copyright (c) 2012, SPAYD (www.spayd.org).
 */
package eu.inmite.lib.spayd.model;

/**
 *
 * @author petrdvorak
 */
public class BankAccount {
    
    protected final String iban;
    protected final String bic;

    public BankAccount(String iban) {
        this.iban = iban;
	    this.bic = null;
    }

    public BankAccount(String iban, String bic) {
        this.iban = iban;
        this.bic = bic;
    }

	public String getIban() {
		return iban;
	}

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
