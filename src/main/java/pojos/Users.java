package main.java.pojos;

import java.io.Serializable;
/**
*
* @author Bodnár Tamás <tms.bodnar@gmail.com> | www.kalandlabor.hu 
* A Users osztály példánya a felhasználók adatait tárolja
*/

public class Users implements Serializable {

	private static final long serialVersionUID = 2801204018440181608L;
	private int id;
	private String name;
	private String password;

	public Users() {
		super();
	}

	public Users(int id, String name, String password) {
		super();
		this.id = id;
		this.name = name;
		this.password = password;
	}

	public Users(String name, String password) {
		super();
		this.name = name;
		this.password = password;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Users [id=" + id + ", name=" + name + ", password=" + password + "]";
	}

}
