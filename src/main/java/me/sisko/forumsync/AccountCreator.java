package me.sisko.forumsync;

public class AccountCreator {
	private String name;
	private String email;
	private String pass;

	public AccountCreator(String name, String email, String pass) {
		this.name = name;
		this.email = email;
		this.pass = pass;
	}

	public String getName() {
		return name;
	}

	public String getEmail() {
		return email;
	}

	public String getPass() {
		return pass;
	}
}
