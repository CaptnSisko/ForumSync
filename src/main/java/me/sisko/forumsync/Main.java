package me.sisko.forumsync;

import com.github.gustav9797.PowerfulPermsAPI.PermissionManager;
import com.github.gustav9797.PowerfulPermsAPI.PowerfulPermsPlugin;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import me.sisko.commands.RegisterCommand;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Plugin;

public class Main extends Plugin implements net.md_5.bungee.api.plugin.Listener {
	private static Connection connection;
	private static Main plug;
	private static PermissionManager perms;
	private static Config dbConfig;

	public void onEnable() {
		getProxy().getPluginManager().registerListener(this, this);
		perms = ((PowerfulPermsPlugin) getProxy().getPluginManager().getPlugin("PowerfulPerms")).getPermissionManager();
		getProxy().getPluginManager().registerCommand(this, new RegisterCommand());
		getProxy().getPluginManager().registerCommand(this, new me.sisko.commands.PasswordCommand());

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

			getProxy().getScheduler().schedule(this, new me.sisko.sql.AsyncKeepAlive(connection), 1L,
					java.util.concurrent.TimeUnit.HOURS);
		}
	}

	@net.md_5.bungee.event.EventHandler
	public void onJoin(PostLoginEvent e) {
		ProxiedPlayer p = e.getPlayer();
		getProxy().getScheduler().runAsync(this, new me.sisko.sql.AsyncForumSync(p.getName(),
				perms.getPermissionPlayer(p.getUniqueId()).getPrimaryGroup().getName(), connection, true));
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
		return perms.getPermissionPlayer(p.getUniqueId()).getPrimaryGroup().getName();
	}
}
