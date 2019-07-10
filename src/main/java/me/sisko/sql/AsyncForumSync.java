package me.sisko.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.sisko.forumsync.Main;
import redis.clients.jedis.Jedis;

public class AsyncForumSync implements Runnable {
	private String name;
	private String group;
	private Connection conn;
	private boolean verbose;

	public AsyncForumSync(String name, String group, Connection conn, boolean verbose) {
		this.name = name;
		this.group = group;
		this.conn = conn;
		this.verbose = verbose;
	}

	public void run() {
		try {
			Statement sta = conn.createStatement();
			ResultSet r = sta.executeQuery("SELECT * FROM `phpbb_users` WHERE `username`='" + name + "';");
			if (r.next()) {
				String id = r.getString("user_id");
				if ((group.equals("Guest")) || (group.equals("default"))) {
					if (verbose)
						Main.getPlugin().getLogger().info(
								"Setting " + name + "'s group to user because they have a forum account and are Guest");
					//BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(),"PowerfulPerms user " + name + " group set User");
					Jedis j = new Jedis();
					j.publish("minecraft.console.hub.in", "pp user " + name + " setrank user");
					j.close();
				}
				int groupNumber = getGroupNumber(group);
				int rankNumber = getRankNumber(group);
				if ((groupNumber > 0) && (rankNumber > 0)) {
					sta.executeUpdate(
							"UPDATE `phpbb_users` SET `group_id` = " + groupNumber + " WHERE `user_id` = " + id + ";");
					sta.executeUpdate(
							"UPDATE `phpbb_users` SET `user_rank` = " + rankNumber + " WHERE `user_id` = " + id + ";");
					sta.executeUpdate("UPDATE `phpbb_users` SET `user_permissions` = '' WHERE `user_id` = " + id + ";");
					sta.executeUpdate("UPDATE `phpbb_user_group` SET `group_id`= " + groupNumber + " WHERE `user_id` = "
							+ id + ";");
					if (verbose)
						Main.getPlugin().getLogger().info("Setting " + name + "'s group to " + group + " (rankNumber="
								+ rankNumber + " groupNumber=" + groupNumber);
				} else {
					Main.getPlugin().getLogger().warning("Could not find group and rank number for " + group);
				}
			} else if (verbose) {
				Main.getPlugin().getLogger().info("Could not find forum account for " + name);
			}
		} catch (SQLException e) {
			Main.getPlugin().getLogger().warning("Error syncing account for " + name + "!");
			e.printStackTrace();
		}
	}

	private static int getGroupNumber(String group) {
		if (group.equalsIgnoreCase("guest") || group.equalsIgnoreCase("default") || group.equalsIgnoreCase("user"))
			return 2;
		if (group.equalsIgnoreCase("user+"))
			return 8;
		if (group.equalsIgnoreCase("donor"))
			return 9;
		if (group.equalsIgnoreCase("donor+"))
			return 10;
		if (group.equalsIgnoreCase("patron"))
			return 16;
		if (group.equalsIgnoreCase("patron+"))
			return 17;
		if (group.equalsIgnoreCase("builder"))
			return 15;
		if (group.equalsIgnoreCase("helper"))
			return 11;
		if (group.equalsIgnoreCase("moderator"))
			return 12;
		if (group.equalsIgnoreCase("admin"))
			return 13;
		if (group.equalsIgnoreCase("owner"))
			return 14;
		return -1;
	}

	private static int getRankNumber(String group) {
		if (group.equalsIgnoreCase("guest") || group.equalsIgnoreCase("default") || group.equalsIgnoreCase("user"))
			return 10;
		if (group.equalsIgnoreCase("user+"))
			return 9;
		if (group.equalsIgnoreCase("donor"))
			return 8;
		if (group.equalsIgnoreCase("donor+"))
			return 7;
		if (group.equalsIgnoreCase("patron"))
			return 14;
		if (group.equalsIgnoreCase("patron+"))
			return 13;
		if (group.equalsIgnoreCase("builder"))
			return 6;
		if (group.equalsIgnoreCase("helper"))
			return 5;
		if (group.equalsIgnoreCase("moderator"))
			return 4;
		if (group.equalsIgnoreCase("admin"))
			return 3;
		if (group.equalsIgnoreCase("owner"))
			return 2;
		return -1;
	}
}
