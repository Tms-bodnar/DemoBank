package main.java.mgbean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpSession;

import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import main.java.pojos.Accounts;
import main.java.pojos.Users;
import main.java.utils.AccountsConverter;
import main.java.utils.HttpSessionUtil;
import main.java.utils.SqlSessionFactoryUtil;
import main.java.pojos.History;

/**
 * @author Bodnár Tamás | tms.bodnar@gmail.com Az UserService ManagedBean
 *         szolgáltató végzi az adatbázis műveleteket. Be és kijelentkezteti a
 *         felhasználót, menedzseli a bejelentkezett User számláinak adatait,
 *         számlatörténeteit, átutalásait.
 * 
 */
@ManagedBean
@SessionScoped
public class UserService {

	private Users user = null;
	private Users otherUser = null;
	private List<Users> allUsers = null;
	private List<Accounts> userAllAccounts = null;
	private LinkedHashMap<String, Accounts> userAccountsNumbers = null;
	private List<Accounts> othersAccounts = null;
	private LinkedHashMap<String, Accounts> otherAccountsNumbers = null;
	private List<Accounts> allAccounts = null;
	private List<History> userHistories = null;
	private List<HistoryView> hViewList = null;
	private Accounts account;
	private String accountNumber;
	private Accounts otherAccount;
	private String otherAccountNumber;
	private Accounts usersAccount;
	private Accounts historyAccount;
	private String currency = "";
	private boolean accountsRendered = false;
	private boolean transferRendered = false;
	private boolean toAccountRendered = false;
	private boolean historyRendered = false;
	private boolean logout = false;
	private int amount;

	public UserService() {
		super();
	}

	/**
	 * Eltárolja egy listában az adatbázisból lekérdezett összes számlát. A
	 * paraméterként kapott name-password párost összehasonlítja a Users
	 * adatbáziból lekérdezett adatokkal ha van ilyen, létrehoz egy Users
	 * példányt az adott paraméterekkel. A HttpSession-ben eltárolja a
	 * username-t az AuthFilter authentikációjához egy listában tárolja a
	 * User-hez tartozó Accounts objeltumokat Hiba esetén üzeetet jelenít meg az
	 * xhtml oldalon
	 * 
	 * @param name
	 *            a username inputtext-ből beolvasott String
	 * @param password
	 *            a password inputsecre-ből beolvasott String
	 * @return String answer a válasz a kiértékelés után(index vagy userContent)
	 * 
	 * @exception PersistenceException
	 *                ha az adatbáziskapcsolat hibás
	 */
	public String loginUser(String name, String password) {

		setUser(null);
		String answer = "";

		try {
			SqlSession session = SqlSessionFactoryUtil.getSqlsessionFactory().openSession();
			allAccounts = session.selectList("main.java.pojos.Accounts.getAllAccounts");
			allUsers = session.selectList("main.java.pojos.Users.getAllUser");

			if (name.isEmpty() || password.isEmpty()) {
				FacesMessage fm = new FacesMessage("Please, fill the form!", "ERROR_MSG");
				fm.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, fm);
			} else {
				Users user1 = new Users(name, password);
				session = SqlSessionFactoryUtil.getSqlsessionFactory().openSession();

				Users loginUser = (Users) session.selectOne("main.java.pojos.Users.loginUser", user1);

				if (loginUser != null) {
					setUser(loginUser);
					getUserAllAccounts(user.getId());

					if (userAllAccounts == null) {
						answer = "index";
					} else {
						HttpSession httpSession = HttpSessionUtil.getHttpSession();
						httpSession.setAttribute("username", user.getName());
						session.commit();
						session.close();
						answer = "userContent";
					}
				} else {
					FacesMessage message = new FacesMessage("Wrong username or password!" + "\n" + "Try again!",
							"ERROR_MSG");
					message.setSeverity(FacesMessage.SEVERITY_ERROR);
					FacesContext.getCurrentInstance().addMessage(null, message);
					answer = "index";
				}
			}
		} catch (PersistenceException e) {
			FacesMessage message = new FacesMessage("Database connection problem!", "ERROR_MSG");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			setTransferRendered(false);
			setHistoryRendered(false);
			setAccountsRendered(false);
			answer = "index";
		}
		return answer;
	}

	/**
	 *
	 * @author Bodnár Tamás <tms.bodnar@gmail.com> | www.kalandlabor.hu A
	 *         'logout' commandbutton hívja, a kijelentkezés megerősítését
	 *         jeleníti meg
	 * 
	 */
	public void logoutFirst() {

		setTransferRendered(false);
		setHistoryRendered(false);
		setAccountsRendered(false);
		setLogout(true);
	}

	/**
	 * A 'yes' commandbutton hívja meg, a kijelentkezés megerősítésekor. A
	 * tárolt listákat és az userhez kapcsolódó adatokat nullázza, a
	 * megjelenítés booleanjait állítja alaphelyzetbe, a HttpSessiont
	 * érvényteleníti
	 * 
	 * @return String index A login oldalra irányít
	 */

	public String logoutSecond() {

		setAllAccounts(null);
		setUser(null);
		setUserAllAccounts(null);
		setUserAccountsNumbers(null);
		setOthersAccounts(null);
		sethViewList(null);
		setAccountsRendered(false);
		setTransferRendered(false);
		setToAccountRendered(false);
		setHistoryRendered(false);
		HttpSession httpSession = HttpSessionUtil.getHttpSession();
		httpSession.invalidate();
		return "index";
	}

	/**
	 * A paraméterként kapott id alapján lekérdezi az Accounts adatbázisból az
	 * adott id-jű Userhez tartozó Accounts listát, ezt jeleníti meg a
	 * 'accTable' datatable
	 * 
	 * @param id
	 *            a User id-je
	 *
	 * 
	 * @exception PersistenceException
	 *                adatbázis hiba esetén
	 */
	public void getUserAllAccounts(int id) {

		setUserAllAccounts(null);
		SqlSession session = SqlSessionFactoryUtil.getSqlsessionFactory().openSession();

		try {
			setUserAllAccounts(session.selectList("main.java.pojos.Accounts.getUserAllAccounts", id));

			if (!userAllAccounts.isEmpty() && userAllAccounts != null) {
				userAccountsNumbers = new LinkedHashMap<>();

				for (Accounts account : userAllAccounts) {
					userAccountsNumbers.put(account.getAccountNumber() + ", " + account.getCurrency() + ", balance: "
							+ account.getBalance(), account);
				}
			} else {
				FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "" + user.getName() + ": No accounts",
						"ERROR_MSG");
				FacesContext.getCurrentInstance().addMessage(null, msg);
				setUserAllAccounts(null);
			}
			session.commit();
			session.close();

			setAccountsRendered(true);
			setHistoryRendered(false);
			sethViewList(null);
			setOthersAccounts(null);
			setTransferRendered(false);
			setLogout(false);
		} catch (PersistenceException e) {
			FacesMessage message = new FacesMessage("Database connection problem!", "ERROR_MSG");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			setTransferRendered(false);
			setHistoryRendered(false);
			setAccountsRendered(false);
		}
	}

	/**
	 * A paraméterként kapott id alapján lekérdezi az Accounts adatbázisból az
	 * adott id-jű Userhez NEM tartozó Accounts listát
	 * 
	 * @param id
	 *            a User id-je
	 * 
	 * @exception PersistenceException
	 *                adatbázis kapcsolati hiba esetén
	 */
	public void getOtherAccounts(int id) {

		setOthersAccounts(null);
		if (user.getId() == id) {

			SqlSession session = SqlSessionFactoryUtil.getSqlsessionFactory().openSession();
			try {
				setOthersAccounts(session.selectList("main.java.pojos.Accounts.getOtherAccounts", id));

				if (!othersAccounts.isEmpty()) {
					// setOtherAccount(othersAccounts.get(0));
					// setOtherAccountNumber(othersAccounts.get(0).getAccountNumber());
				}
				setTransferRendered(true);
				setAccountsRendered(false);
				setLogout(false);
				session.commit();
				session.close();
			} catch (PersistenceException e) {
				FacesMessage message = new FacesMessage("Database connection problem!", "ERROR_MSG");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				setTransferRendered(false);
				setHistoryRendered(false);
				setAccountsRendered(false);
			}
		} else {
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Invalid user, please login!",
					"ERROR_MSG");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	/**
	 * A 'transfer' form 'userAcc'oneselectmenu elemének kijelölése estén az
	 * AccountsConverter segítségével megadja a kijelölt Accounts objektum
	 * példányát, és az adott példány pénzneme alapján a többi számla Lista
	 * ugyanolyan pénznemű elemeinek adatait egy map-ben tárolja
	 * 
	 * @param e
	 *            onclick esemény
	 */
	public void valueChanged(ValueChangeEvent e) {

		String selected = (String) e.getNewValue();
		AccountsConverter converter = new AccountsConverter();

		Accounts tempAccount = (Accounts) converter.getAsObject(FacesContext.getCurrentInstance(),
				UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()), selected);
		String currency = tempAccount.getCurrency();
		setUsersAccount(tempAccount);
		LinkedHashMap<String, Accounts> otherAccNumb = new LinkedHashMap<>();
		for (Accounts acc : othersAccounts) {
			if (acc.getCurrency().equals(currency)) {
				otherAccNumb.put(getOtherUser(acc.getUserId()).getName() + ": " + acc.getAccountNumber() + ", "
						+ acc.getCurrency(), acc);
			}
		}
		setOtherAccountsNumbers(otherAccNumb);
		setToAccountRendered(true);
	}

	/**
	 * a 'transfer' form 'otherAcc' oneselectmenü elemének kiválasztásakor az
	 * AccountsConvrter segítségével megadja a kiválasztott Accounts példányt,
	 * 
	 * @param e
	 *            onclick esemény
	 */
	public void valueChanged2(ValueChangeEvent e) {

		String sel = (String) e.getNewValue();
		AccountsConverter converter = new AccountsConverter();

		Accounts otherAcc = (Accounts) converter.getAsObject(FacesContext.getCurrentInstance(),
				UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()), sel);
		int id = otherAcc.getId();
		for (Accounts accounts : allAccounts) {
			if (accounts.getId() == id) {
				setOtherAccount(accounts);
			}
		}
	}

	/**
	 * A metódus a paraméterként kapott amount összegével teljesíti az
	 * átutalást. Leellenőrzi a bevitt adatokat, az elérhető egyenleget, Először
	 * a forrásszámla egyenlegét csökkenti az amount összegével, Majd a
	 * célszámla egyenlegét növeli az amount összegével, Végül létrehoz egy új
	 * History példányt az átutalás adataival. Az adatbázis műveleteket
	 * tranzakcióként kezeli, hiba esetén rollback-el.
	 * 
	 * @param amount
	 *            az átutalás összege
	 * 
	 * 
	 * @exception NumberFormatException
	 *                ha nem megfelelő az amount formátuma
	 * @exception AmountException
	 *                ha nem elég az egyenleg
	 * @exception PersistenceException
	 *                adatbázis kapcsolati hiba esetén
	 */
	public void doTransfer(String amount) {

		try {
			setAmount(Integer.valueOf(amount));

			if (this.amount <= 0) {
				throw new NumberFormatException("Not valid!");
			}
			if (this.amount > usersAccount.getBalance()) {
				throw new AmountException("Not enough founds!");
			}
			try {
				SqlSession session = SqlSessionFactoryUtil.getSqlsessionFactory().openSession();
				usersAccount.setBalance(usersAccount.getBalance() - this.amount);
				int fromAccOK = session.update("main.java.pojos.Accounts.updateAccount", usersAccount);

				otherAccount.setBalance(otherAccount.getBalance() + this.amount);
				int toAccOK = session.update("main.java.pojos.Accounts.updateAccount", otherAccount);

				History history = new History(usersAccount.getId(), otherAccount.getId(), this.amount);
				int historyOK = session.insert("main.java.pojos.History.insertNewHistory", history);

				if (fromAccOK > 0 && toAccOK > 0 && historyOK > 0) {
					FacesMessage msg = new FacesMessage("The transfer is done: from: " + usersAccount.getAccountNumber()
							+ "\n" + "to: " + otherAccount.getAccountNumber() + "\n" + "amount: " + this.amount + " "
							+ usersAccount.getCurrency(), "INFO");
					msg.setSeverity(FacesMessage.SEVERITY_INFO);
					FacesContext.getCurrentInstance().addMessage(null, msg);
					session.commit();

				} else {
					FacesMessage msg = new FacesMessage("Transfer ERROR: from: " + usersAccount.getAccountNumber()
							+ "\n" + "to: " + otherAccount.getAccountNumber() + "\n" + "amount: " + this.amount + ", "
							+ usersAccount.getCurrency(), "ERROR");
					msg.setSeverity(FacesMessage.SEVERITY_ERROR);
					FacesContext.getCurrentInstance().addMessage(null, msg);
					session.rollback();
				}
				session.close();
			} catch (PersistenceException e) {
				FacesMessage message = new FacesMessage("Database connection problem!", "ERROR_MSG");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				setTransferRendered(false);
				setHistoryRendered(false);
				setAccountsRendered(false);
			}
		} catch (NumberFormatException e) {
			FacesMessage msg = new FacesMessage("The amount is not valid: " + this.amount + "!" + e, "ERROR");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
		} catch (AmountException e) {
			FacesMessage msg = new FacesMessage("" + e, "ERROR");
			msg.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, msg);
			e.printStackTrace();
		}
		setTransferRendered(false);
		setHistoryRendered(false);
		setToAccountRendered(true);
	}

	/**
	 * A user id-ját paraméterként megkapva lekérdezi a Userhez tartozó History
	 * listát
	 * 
	 * @param id
	 *            a User id-ja
	 * @exception PersistenceException
	 *                adatbázis kapcsolati hiba esetén
	 */
	public void getUserHistory(int id) {
		setAccountsRendered(false);
		setTransferRendered(false);
		setHistoryRendered(true);
		setLogout(false);

		if (userAllAccounts != null) {
			for (Accounts acc : userAllAccounts) {
				if (acc.getUserId() == id) {
					setHistoryAccount(acc);
				}
			}

			SqlSession session = SqlSessionFactoryUtil.getSqlsessionFactory().openSession();
			try {
				List<History> temp = session.selectList("main.java.pojos.History.selectHistories",
						historyAccount.getId());

				setUserHistories(temp);
				session.commit();
				session.close();
			} catch (PersistenceException e) {
				FacesMessage message = new FacesMessage("Database connection problem!", "ERROR_MSG");
				message.setSeverity(FacesMessage.SEVERITY_ERROR);
				FacesContext.getCurrentInstance().addMessage(null, message);
				setTransferRendered(false);
				setHistoryRendered(false);
				setAccountsRendered(false);
			}
		}
	}

	/**
	 * A 'userAcounts' selectonemenu elemének kiválsztásakor az
	 * AccountsConverter segítségével a userHistories lista elemeiből létrehoz
	 * egy-egy HistoryView példányt a megjelenítéshez, majd ezen példányokból
	 * egy listát, amit a 'listHistory' datatable-ban jelenít meg
	 * 
	 * @param e
	 *            onclick esemény
	 * 
	 */
	public void valueChangedHistory(ValueChangeEvent e) {

		String selected = (String) e.getNewValue();
		AccountsConverter converter = new AccountsConverter();

		Accounts tempAccount = (Accounts) converter.getAsObject(FacesContext.getCurrentInstance(),
				UIComponent.getCurrentComponent(FacesContext.getCurrentInstance()), selected);
		setHistoryAccount(tempAccount);

		SqlSession session = SqlSessionFactoryUtil.getSqlsessionFactory().openSession();
		try {
			List<History> temp = session.selectList("main.java.pojos.History.selectHistories", historyAccount.getId());

			setUserHistories(temp);
			session.commit();
			session.close();
		} catch (PersistenceException ex) {
			FacesMessage message = new FacesMessage("Database connection problem!", "ERROR_MSG");
			message.setSeverity(FacesMessage.SEVERITY_ERROR);
			FacesContext.getCurrentInstance().addMessage(null, message);
			setTransferRendered(false);
			setHistoryRendered(false);
			setAccountsRendered(false);
		}
		hViewList = new ArrayList<>();
		if (!userHistories.isEmpty()) {

			for (History h : userHistories) {
				HistoryView hv = new HistoryView();

				if (h.getFromAccount() == historyAccount.getId()) {
					hv.setFromAccount(historyAccount);
					hv.setFromName(getOtherUser(historyAccount.getUserId()).getName());
					hv.setCurrency(historyAccount.getCurrency());
					hv.setCreaditLoad(true);
					hv.setToAccount(getAccount(h.getToAccount()));
					hv.setToName(getOtherUser(getAccount(h.getToAccount()).getUserId()).getName());
				}
				if (h.getToAccount() == historyAccount.getId()) {
					hv.setToAccount(historyAccount);
					hv.setToName(getOtherUser(historyAccount.getUserId()).getName());
					hv.setFromAccount(getAccount(h.getFromAccount()));
					hv.setFromName(getOtherUser(getAccount(h.getFromAccount()).getUserId()).getName());
					hv.setCurrency(historyAccount.getCurrency());
					hv.setCreaditLoad(false);
				}
				hv.setAmount(h.getAmount());
				hViewList.add(hv);
			}
		} else {
			hViewList.clear();
			FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR,
					"No history on " + historyAccount.getAccountNumber() + "!", "ERROR_MSG");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
		setHistoryRendered(true);
	}

	/**
	 * A paraméterként kapott id alapján visszaadja az adott Accounts példányt
	 * 
	 * @param id
	 * @return az adott id-jű Accounts példány
	 * 
	 */
	public Accounts getAccount(int id) {

		for (Accounts acc : allAccounts) {
			if (acc.getId() == id) {
				setAccount(acc);
			}
		}
		return account;
	}

	/**
	 * A paraméterként kapott id alapján visszaadja az adott Users példányt
	 * 
	 * @param id
	 * @return az adott id-jű Users példány
	 * 
	 */
	public Users getOtherUser(int id) {

		for (Users user : allUsers) {
			if (user.getId() == id) {
				setOtherUser(user);
			}
		}
		return otherUser;
	}

	// getter-setter

	public List<Users> getAllUsers() {
		return allUsers;
	}

	public Users getOtherUser() {
		return otherUser;
	}

	public void setOtherUser(Users otherUser) {
		this.otherUser = otherUser;
	}

	public void setAllUsers(List<Users> allUser) {
		this.allUsers = allUser;
	}

	public boolean setAccountsRendered(boolean rendered) {
		return this.accountsRendered = rendered;
	}

	public boolean isToAccountRendered() {
		return toAccountRendered;
	}

	public void setToAccountRendered(boolean toAccountRendered) {
		this.toAccountRendered = toAccountRendered;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public List<Accounts> getUserAllAccounts() {
		return userAllAccounts;
	}

	public void setUserAllAccounts(List<Accounts> allAccounts) {
		this.userAllAccounts = allAccounts;
	}

	public boolean isAccountsRendered() {
		return accountsRendered;
	}

	public boolean isTransferRendered() {
		return transferRendered;
	}

	public boolean isHistoryRendered() {
		return historyRendered;
	}

	public void setHistoryRendered(boolean historyRendered) {
		this.historyRendered = historyRendered;
	}

	public void setTransferRendered(boolean transferRendered) {
		this.transferRendered = transferRendered;
	}

	public Accounts getAccount() {
		return account;
	}

	public void setAccount(Accounts account) {
		this.account = account;
	}

	public List<Accounts> getOthersAccounts() {
		return othersAccounts;
	}

	public void setOthersAccounts(List<Accounts> othersAccounts) {
		this.othersAccounts = othersAccounts;
	}

	public List<Accounts> getAllAccounts() {
		return allAccounts;
	}

	public List<HistoryView> gethViewList() {
		return hViewList;
	}

	public void sethViewList(List<HistoryView> hViewList) {
		this.hViewList = hViewList;
	}

	public void setAllAccounts(List<Accounts> toAccounts) {
		this.allAccounts = toAccounts;
	}

	public Accounts getUsersAccount() {
		return usersAccount;
	}

	public void setUsersAccount(Accounts toAccount) {
		this.usersAccount = toAccount;
	}

	public Accounts getOtherAccount() {
		return otherAccount;
	}

	public void setOtherAccount(Accounts otherAccount) {
		this.otherAccount = otherAccount;
	}

	public LinkedHashMap<String, Accounts> getUserAccountsNumbers() {
		return userAccountsNumbers;
	}

	public void setUserAccountsNumbers(LinkedHashMap<String, Accounts> userAccountsNumbers) {
		this.userAccountsNumbers = userAccountsNumbers;
	}

	public LinkedHashMap<String, Accounts> getOtherAccountsNumbers() {
		return otherAccountsNumbers;
	}

	public void setOtherAccountsNumbers(LinkedHashMap<String, Accounts> othetAccountsNumbers) {
		this.otherAccountsNumbers = othetAccountsNumbers;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	public String getOtherAccountNumber() {
		return otherAccountNumber;
	}

	public void setOtherAccountNumber(String otherAccountNumber) {
		this.otherAccountNumber = otherAccountNumber;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public List<History> getUserHistories() {
		return userHistories;
	}

	public void setUserHistories(List<History> userHistories) {
		this.userHistories = userHistories;
	}

	public Accounts getHistoryAccount() {
		return historyAccount;
	}

	public void setHistoryAccount(Accounts historyAccount) {
		this.historyAccount = historyAccount;
	}

	public boolean isLogout() {
		return logout;
	}

	public void setLogout(boolean logout) {
		this.logout = logout;

	}

}
