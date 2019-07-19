package me.sisko.forumsync;

import java.io.File;

import net.cubespace.Yamler.Config.YamlConfig;
import net.md_5.bungee.api.plugin.Plugin;

public class Config extends YamlConfig {
	public Config(Plugin p) {
		CONFIG_FILE = new File(p.getDataFolder(), "config.yml");
		CONFIG_HEADER = new String[] { "Forumsync Configuration" };
	}

	private String ip = "localhost";
	private String name = "phpbb";
	private String user = "root";
	private String pass = "password";
	private int port = 1433;
	private String local_ip = "localhost";
	private String local_name = "database";
	private String local_user = "user";
	private String local_pass = "pass";
	private int local_port = 1433;
	private boolean verbose = true;

	public String getIp() {
		return ip;
	}

	public String getName() {
		return name;
	}

	public String getUser() {
		return user;
	}

	public String getPass() {
		return pass;
	}

	public int getPort() {
		return port;
	}

	public String getLocalIp() {
		return local_ip;
	}

	public String getLocalName() {
		return local_name;
	}

	public String getLocalUser() {
		return local_user;
	}

	public String getLocalPass() {
		return local_pass;
	}

	public int getLocalPort() {
		return local_port;
	}

	public boolean getVerbose() {
		return verbose;
	}
}
