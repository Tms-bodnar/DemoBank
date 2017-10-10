package main.java.pojos;

import java.io.Serializable;

/**
*
* @author Bodnár Tamás <tms.bodnar@gmail.com> | www.kalandlabor.hu 
* A History osztály példánya a számlatörténetek adatait tárolja
*/


public class History implements Serializable {

	private static final long serialVersionUID = -4518963807625352157L;
	private int id;
	private int fromAccount;
	private int toAccount;
	private int amount;

	public History() {
		super();
	}

	public History(int fromAccount, int toAccount, int amount) {
		super();
		this.fromAccount = fromAccount;
		this.toAccount = toAccount;
		this.amount = amount;
	}

	public int getFromAccount() {
		return fromAccount;
	}

	public void setFromAccount(int fromAccount) {
		this.fromAccount = fromAccount;
	}

	public int getToAccount() {
		return toAccount;
	}

	public void setToAccount(int toAccount) {
		this.toAccount = toAccount;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getId() {
		return id;
	}

	@Override
	public String toString() {
		return "History [id=" + id + ", fromAccount=" + fromAccount + ", toAccount=" + toAccount + ", amount=" + amount
				+ "]";
	}

}
