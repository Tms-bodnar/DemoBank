package main.java.mgbean;

/**
*
* @author Bodnár Tamás <tms.bodnar@gmail.com> | www.kalandlabor.hu 
* Az AmountException az Exception leszármazottja
* Akkor dobjuk, ha nincs elég egyenleg a számlán
* 
*/

public class AmountException extends Exception {

	private static final long serialVersionUID = 6531300078361708719L;

	public AmountException(String message) {
		super(message);
	}

	public AmountException(String message, Throwable throwable) {
		super(message, throwable);
	}
}
