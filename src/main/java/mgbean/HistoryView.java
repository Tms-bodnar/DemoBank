package main.java.mgbean;

import main.java.pojos.Accounts;
/**
*
* @author Bodnár Tamás <tms.bodnar@gmail.com> | www.kalandlabor.hu 
* A HistoryView a számlatörténetek megjelenítésének leegyszerűsítésére szolgáló osztály
* Példánya a History osztályban tárolt tranzakció forrás és célszámla tulajdonosának nevét, számlaszámokat, pénznemet és összeget tárol,
* valamint a tranzakció típusát(jóváírás-terhelés)
* 
*/
public class HistoryView {

	private Accounts fromAccount;
	private String fromName;
	private Accounts toAccount;
	private String toName;
	private String currency;
	private boolean creaditLoad;
	private int amount;

	public HistoryView() {
	}

	public HistoryView(Accounts fromAccount, Accounts toAccount, String currency, boolean creaditLoad, int amount) {
		super();
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.currency = currency;
		this.creaditLoad = creaditLoad;
		this.amount = amount;
	}

	public Accounts getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(Accounts fromAccount) {
		this.fromAccount = fromAccount;
	}

	public Accounts getToAccount() {
		return toAccount;
	}

	public void setToAccount(Accounts toAccount) {
		this.toAccount = toAccount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public boolean isCreaditLoad() {
		return creaditLoad;
	}

	public void setCreaditLoad(boolean creaditLoad) {
		this.creaditLoad = creaditLoad;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getToName() {
		return toName;
	}

	public void setToName(String toName) {
		this.toName = toName;
	}

}
