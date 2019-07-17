package me.sisko.forumsync;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import me.lucko.luckperms.LuckPerms;
import me.lucko.luckperms.api.LuckPermsApi;
import me.sisko.commands.PasswordCommand;
import me.sisko.commands.RegisterCommand;
import me.sisko.sql.AsyncForumSync;
import me.sisko.sql.AsyncKeepAlive;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class Main extends Plugin implements Listener {
	private static Connection connection;
	private static Main plug;
	private static Optional<LuckPermsApi> perms;
	private static Config dbConfig;

	public void onEnable() {
		getProxy().getPluginManager().registerListener(this, this);
		getProxy().getPluginManager().registerCommand(this, new RegisterCommand());
		getProxy().getPluginManager().registerCommand(this, new PasswordCommand());

		perms = LuckPerms.getApiSafe();

		dbConfig = null;
		plug = this;
		try {
			dbConfig = new Config(this);
			dbConfig.init();
		} catch (Exception ex) {
			System.out.println("Error with forumsync configuration!");
			ex.printStackTrace();
		}
		try {
			synchronized (this) {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection(
						"jdbc:mysql://" + dbConfig.getIp() + ":" + dbConfig.getPort() + "/" + dbConfig.getName()
								+ "?autoReconnect=true&verifyServerCertificate=false&useSSL=true",
						dbConfig.getUser(), dbConfig.getPass());
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		getProxy().getScheduler().schedule(this, new AsyncKeepAlive(connection), 5l, TimeUnit.MINUTES);
	}

	@EventHandler
	public void onJoin(PostLoginEvent e) {
		ProxiedPlayer p = e.getPlayer();
		getProxy().getScheduler().runAsync(this, new AsyncForumSync(p.getName(), getPrimaryGroup(e.getPlayer()), connection, true));
	}

	public static Connection getConnection() {
		return connection;
	}

	public static Main getPlugin() {
		return plug;
	}

	public static Config getConfig() {
		return dbConfig;
	}

	public static String getPrimaryGroup(ProxiedPlayer p) {
		return perms.get().getUser(p.getUniqueId()).getPrimaryGroup();
	}
}
