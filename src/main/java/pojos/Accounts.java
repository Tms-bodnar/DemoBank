package main.java.pojos;

import java.io.Serializable;

/**
*
* @author Bodnár Tamás <tms.bodnar@gmail.com> | www.kalandlabor.hu 
* Az Accounts osztály példánya a számlák adatait tárolja
*/

public class Accounts implements Serializable {

	private static final long serialVersionUID = -2931083328107258316L;
	private int id;
	private String accountNumber;
	private String currency;
	private int balance;
	private int userId;

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public Accounts() {
		super();
	}

	public Accounts(int id) {
		super();
		this.id = id;
	}

	public Accounts(String accountNumber) {
		super();
		this.accountNumber = accountNumber;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public int getBalance() {
		return balance;
	}

	public void setBalance(int balance) {
		this.balance = balance;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "" + id;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Accounts && this.id == ((Accounts) obj).id) {
			return true;
		} else {
			return false;
		}

	}
}
