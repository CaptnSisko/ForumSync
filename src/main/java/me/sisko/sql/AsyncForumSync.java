package me.sisko.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.sisko.forumsync.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AsyncForumSync implements Runnable {
	private ProxiedPlayer player;
	private Connection local;
	private Connection web;
	private boolean verbose;

	public AsyncForumSync(ProxiedPlayer player, Connection local, Connection web, boolean verbose) {
		this.player = player;
		this.local = local;
		this.web = web;
		this.verbose = verbose;
	}

	public void run() {
		try {
			Statement local = this.local.createStatement();
			Statement web = this.web.createStatement();

			ResultSet result = local
					.executeQuery("SELECT * FROM forum_users WHERE uuid='" + player.getUniqueId().toString() + "';");
			if (result.next()) {
				int id = result.getInt("id");
				String name = result.getString("name");
				String group = Main.getPrimaryGroup(player);
				if ((group.equals("Guest")) || (group.equals("default"))) {
					if (verbose)
						Main.getPlugin().getLogger().info("Setting " + player.getName()
								+ "'s group to user because they have a forum account and are Guest");
					// BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(),"PowerfulPerms
					// user " + name + " group set User");
					Main.setGroup(player, "user");
				}
				if (!player.getName().equalsIgnoreCase(name)) {
					web.executeUpdate("UPDATE phpbb_users SET username='" + player.getName() + "', username_clean='"
							+ player.getName().toLowerCase() + "' WHERE user_id=" + id);
					local.executeUpdate("UPDATE forum_users SET name='" + player.getName() + "' WHERE id=" + id);
					player.sendMessage(new TextComponent(ChatColor.GREEN + "It appears you have changed your name from "
							+ name + " to " + player.getName()));
					player.sendMessage(new TextComponent(ChatColor.GREEN
							+ "You forum account's username has been changed, so you will now lonin with "
							+ player.getName() + " as your username."));
				}
				int groupNumber = getGroupNumber(group);
				int rankNumber = getRankNumber(group);
				if ((groupNumber > 0) && (rankNumber > 0)) {
					web.executeUpdate(
							"UPDATE `phpbb_users` SET `group_id` = " + groupNumber + " WHERE `user_id` = " + id + ";");
					web.executeUpdate(
							"UPDATE `phpbb_users` SET `user_rank` = " + rankNumber + " WHERE `user_id` = " + id + ";");
					web.executeUpdate("UPDATE `phpbb_users` SET `user_permissions` = '' WHERE `user_id` = " + id + ";");
					web.executeUpdate("UPDATE `phpbb_user_group` SET `group_id`= " + groupNumber + " WHERE `user_id` = "
							+ id + ";");
					if (verbose)
						Main.getPlugin().getLogger().info("Setting " + name + "'s group to " + group + " (rankNumber="
								+ rankNumber + " groupNumber=" + groupNumber);
				} else {
					Main.getPlugin().getLogger().warning("Could not find group and rank number for " + group);
				}
			} else {
				if (verbose)
					Main.getPlugin().getLogger().info("Could not find forum account for " + player.getName());

				result = web.executeQuery("SELECT * FROM phpbb_users WHERE username='" + player.getName() + "';");
				if (result.next()) {
					Main.getPlugin().getLogger().info("Creating entry in local database for " + player.getName()
							+ " because they have a forum account");
					local.executeUpdate("INSERT INTO forum_users VALUES (" + result.getInt("user_id") + ", '"
							+ player.getName() + "', '" + player.getUniqueId() + "');");
				} else if (Main.isUser(player)) {
					player.sendMessage(new TextComponent(ChatColor.RED
							+ "Looks like you either don't have a forum account or your forum account does not match your username."
							+ " There may have been an error in your registration, or you may have changed your username before ForumSync was updated to support username changes."
							+ " Please use /register to create a new forum account."));
					Main.removeUser(player);
				}
			}

			// Statement sta = web.createStatement();
			// ResultSet r = sta.executeQuery("SELECT * FROM `phpbb_users` WHERE
			// `username`='" + name + "';");
			// if (r.next()) {
			// String id = r.getString("user_id");
			// if ((group.equals("Guest")) || (group.equals("default"))) {
			// if (verbose)
			// Main.getPlugin().getLogger().info(
			// "Setting " + name + "'s group to user because they have a forum account and
			// are Guest");
			// //
			// BungeeCord.getInstance().getPluginManager().dispatchCommand(BungeeCord.getInstance().getConsole(),"PowerfulPerms
			// // user " + name + " group set User");
			// Jedis j = new Jedis();
			// j.publish("minecraft.console.hub.in", "lp user " + name + " permission set
			// group.user");
			// j.close();
			// }
			// int groupNumber = getGroupNumber(group);
			// int rankNumber = getRankNumber(group);
			// if ((groupNumber > 0) && (rankNumber > 0)) {
			// sta.executeUpdate(
			// "UPDATE `phpbb_users` SET `group_id` = " + groupNumber + " WHERE `user_id` =
			// " + id + ";");
			// sta.executeUpdate(
			// "UPDATE `phpbb_users` SET `user_rank` = " + rankNumber + " WHERE `user_id` =
			// " + id + ";");
			// sta.executeUpdate("UPDATE `phpbb_users` SET `user_permissions` = '' WHERE
			// `user_id` = " + id + ";");
			// sta.executeUpdate("UPDATE `phpbb_user_group` SET `group_id`= " + groupNumber
			// + " WHERE `user_id` = "
			// + id + ";");
			// if (verbose)
			// Main.getPlugin().getLogger().info("Setting " + name + "'s group to " + group
			// + " (rankNumber="
			// + rankNumber + " groupNumber=" + groupNumber);
			// } else {
			// Main.getPlugin().getLogger().warning("Could not find group and rank number
			// for " + group);
			// }
			// } else if (verbose) {
			// Main.getPlugin().getLogger().info("Could not find forum account for " +
			// name);
			// }
		} catch (SQLException e) {
			Main.getPlugin().getLogger().warning("Error syncing account for " + player.getName() + "!");
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
