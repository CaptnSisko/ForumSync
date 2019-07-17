package me.sisko.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import me.sisko.forumsync.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AsyncChangePassword implements Runnable {
	//private static final String CHANGEPASSWORDTEMPLATE = "UPDATE `phpbb_users` SET `user_password` = MD5('%PASSWORD%') WHERE `phpbb_users`.`username` = '%USERNAME%';";
	private static final String CHANGEPASSWORDTEMPLATE = "UPDATE `phpbb_users` SET `user_password` = MD5(?) WHERE `phpbb_users`.`username` = ?;";
	private String name;
	private ProxiedPlayer p;
	private String pass;
	private Connection conn;
	// private boolean verbose;

	public AsyncChangePassword(ProxiedPlayer p, String pass, Connection conn, boolean verbose) {
		name = p.getName();
		this.p = p;
		this.pass = pass;
		this.conn = conn;
		// this.verbose = verbose;
	}

	public void run() {
		try {
			PreparedStatement sta = conn.prepareStatement(CHANGEPASSWORDTEMPLATE);
//			String password = CHANGEPASSWORDTEMPLATE;
//			password = password.replace("%USERNAME%", name);
//			password = password.replace("%PASSWORD%", pass);
			sta.setString(1, pass);
			sta.setString(2, name);
			sta.execute();
			p.sendMessage(new TextComponent(ChatColor.GREEN + "Your password has been changed!"));
			Main.getPlugin().getLogger().info("Changed password for " + name);
		} catch (SQLException e) {
			p.sendMessage(new TextComponent(ChatColor.RED + "Error changing your password (does your account exist?)"));
			Main.getPlugin().getLogger().warning("Error changing password for " + name + "!");
		}
	}
}
