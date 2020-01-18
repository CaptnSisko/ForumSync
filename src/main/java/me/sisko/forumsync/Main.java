package me.sisko.forumsync;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import me.sisko.commands.GenerateForumTableCommand;
import me.sisko.commands.PasswordCommand;
import me.sisko.commands.RegisterCommand;
import me.sisko.sql.AsyncForumSync;
import me.sisko.sql.AsyncKeepAlive;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class Main extends Plugin implements Listener {
	private static Connection connection;
	private static Connection localConnection;
	private static Main plug;
	private static LuckPerms perms;
	private static Config dbConfig;

	public void onEnable() {
		getProxy().getPluginManager().registerListener(this, this);
		getProxy().getPluginManager().registerCommand(this, new RegisterCommand());
		getProxy().getPluginManager().registerCommand(this, new PasswordCommand());
		getProxy().getPluginManager().registerCommand(this, new GenerateForumTableCommand());

		perms = LuckPermsProvider.get();

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
				localConnection = DriverManager.getConnection(
						"jdbc:mysql://" + dbConfig.getLocalIp() + ":" + dbConfig.getLocalPort() + "/" + dbConfig.getLocalName()
								+ "?autoReconnect=true&verifyServerCertificate=false&useSSL=true",
						dbConfig.getLocalUser(), dbConfig.getLocalPass());
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		getProxy().getScheduler().schedule(this, new AsyncKeepAlive(connection), 5l, TimeUnit.MINUTES);
	}

	@EventHandler
	public void onJoin(PostLoginEvent e) {
		ProxiedPlayer p = e.getPlayer();
		getProxy().getScheduler().runAsync(this,
				new AsyncForumSync(p, localConnection, connection, true));
	}

	public static Connection getConnection() {
		return connection;
	}
	
	public static Connection getLocalConnection() {
		return localConnection;
	}

	public static LuckPerms getPerms() {
		return perms;
	}
	
	public static Main getPlugin() {
		return plug;
	}

	public static Config getConfig() {
		return dbConfig;
	}

	public static String getPrimaryGroup(ProxiedPlayer p) {
		return perms.getUserManager().getUser(p.getUniqueId()).getPrimaryGroup();
	}

	// best to only call this async
	public static void setGroup(ProxiedPlayer p, String group) {
		User u = perms.getUserManager().getUser(p.getUniqueId());
		Node n = Node.builder("group." + group).build();
		u.getNodes().add(n);
		perms.getUserManager().saveUser(u).join();
		perms.getMessagingService().ifPresent(service -> service.pushUserUpdate(u));
	}

	// best to only call async
	public static boolean isUser(ProxiedPlayer p) {
		User u = perms.getUserManager().getUser(p.getUniqueId());
		Node n = Node.builder("group.user").build();
		return u.getNodes().contains(n);
	}

	// best to only call async
	public static void removeUser(ProxiedPlayer p) {
		User u = perms.getUserManager().getUser(p.getUniqueId());
		Node n = Node.builder("group.user").build();
		u.getNodes().remove(n);
		perms.getUserManager().saveUser(u).join();
		perms.getMessagingService().ifPresent(service -> service.pushUserUpdate(u));
	}
}
