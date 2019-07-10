package me.sisko.forumsync;

public class PasswordCreator {
	private String name;
	private String pass;

	public PasswordCreator(String name, String pass) {
		this.name = name;
		this.pass = pass;
	}

	public String getName() {
		return name;
	}

	public String getPass() {
		return pass;
	}
}
