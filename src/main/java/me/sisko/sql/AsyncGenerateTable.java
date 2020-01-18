package me.sisko.sql;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import me.sisko.forumsync.Main;
import me.sisko.util.UUIDFetcher;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;

public class AsyncGenerateTable implements Runnable {
	private Connection local;
	private Connection web;
	private LuckPerms perms;

	public AsyncGenerateTable(Connection local, Connection web, LuckPerms perms) {
		this.local = local;
		this.web = web;
		this.perms = perms;
	}

	@Override
	public void run() {
		Main.getPlugin().getLogger().info("Generating local uuid table...");
		try {
			Statement local = this.local.createStatement();
			Statement web = this.web.createStatement();

			// Get a list of user ids on the web
			HashMap<Integer, String> users = new HashMap<Integer, String>();
			ResultSet result = web.executeQuery("SELECT user_id, username, user_avatar_type FROM phpbb_users;");
			while (result.next()) {
				// verify a user is not a bot using the avatar
				if (result.getString("user_avatar_type")
						.equalsIgnoreCase("sitesplat.minecraftavatarminotar.minecraftminotar")) {
					users.put(result.getInt("user_id"), result.getString("username"));
				}
			}

			// Get a list of user ids already in the database and remove them from the table
			// creation
			result = local.executeQuery("SELECT id FROM forum_users;");
			while (result.next()) {
				users.remove(result.getInt(1));
			}

			// Now it's time to get the uuids for everyone
			HashMap<String, String> uuids = new HashMap<String, String>();
			ArrayList<String> skipNames = new ArrayList<String>();
			for (String name : users.values()) {
				String uuid = "";
				User u = perms.getUserManager().getUser(name);
				if (u == null) {
					Main.getPlugin().getLogger().info("Doing manual lookup for user " + name);
					uuid = UUIDFetcher.getUUID(name);
					if (uuid == null) {
						skipNames.add(name);
					} else {
						uuid = new StringBuilder(uuid).insert(8, '-').insert(13, '-').insert(18, '-').insert(23, '-')
								.toString();
					}
				} else {
					uuid = u.getUniqueId().toString();
				}
				uuids.put(name, uuid);
			}

			// Finally, dump it all into the table
			for (int id : users.keySet()) {
				if (!skipNames.contains(users.get(id))) {
					local.executeUpdate("INSERT INTO forum_users VALUES (" + id + ",'" + users.get(id) + "','"
							+ uuids.get(users.get(id)) + "');");
					Main.getPlugin().getLogger().info("Added " + users.get(id) + " to the database (id=" + id
							+ ", uuid=" + uuids.get(users.get(id)) + ")");
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
