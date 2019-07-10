package me.sisko.forumsync;

import net.cubespace.Yamler.Config.YamlConfig;
import net.md_5.bungee.api.plugin.Plugin;

public class Config extends YamlConfig {
	public Config(Plugin p) {
		CONFIG_FILE = new java.io.File(p.getDataFolder(), "config.yml");
		CONFIG_HEADER = new String[] { "Forumsync Configuration" };
	}

	private String ip = "localhost";
	private String name = "phpbb";
	private String user = "root";
	private String pass = "password";
	private int port = 1433;
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

	public boolean getVerbose() {
		return verbose;
	}
}
