package me.sisko.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import me.sisko.forumsync.Main;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AsyncCreateAccount implements Runnable {
	// private static final String CREATEACCOUNTTEMPLATE = "INSERT INTO
	// `phpbb_users` (`user_id`, `user_type`, `group_id`, `user_permissions`,
	// `user_perm_from`, `user_ip`, `user_regdate`, `username`, `username_clean`,
	// `user_password`, `user_passchg`, `user_email`, `user_email_hash`,
	// `user_birthday`, `user_lastvisit`, `user_lastmark`, `user_lastpost_time`,
	// `user_lastpage`, `user_last_confirm_key`, `user_last_search`,
	// `user_warnings`, `user_last_warning`, `user_login_attempts`,
	// `user_inactive_reason`, `user_inactive_time`, `user_posts`, `user_lang`,
	// `user_timezone`, `user_dateformat`, `user_style`, `user_rank`, `user_colour`,
	// `user_new_privmsg`, `user_unread_privmsg`, `user_last_privmsg`,
	// `user_message_rules`, `user_full_folder`, `user_emailtime`,
	// `user_topic_show_days`, `user_topic_sortby_type`, `user_topic_sortby_dir`,
	// `user_post_show_days`, `user_post_sortby_type`, `user_post_sortby_dir`,
	// `user_notify`, `user_notify_pm`, `user_notify_type`, `user_allow_pm`,
	// `user_allow_viewonline`, `user_allow_viewemail`, `user_allow_massemail`,
	// `user_options`, `user_avatar`, `user_avatar_type`, `user_avatar_width`,
	// `user_avatar_height`, `user_sig`, `user_sig_bbcode_uid`,
	// `user_sig_bbcode_bitfield`, `user_jabber`, `user_actkey`, `user_newpasswd`,
	// `user_form_salt`, `user_new`, `user_reminded`, `user_reminded_time`) VALUES
	// (NULL, '0', '2', '', '0', '%IP%', '%TIME%', '%USERNAME%', '%USERNAME_CLEAN%',
	// MD5('%PASSWORD%'), '0', '%EMAIL%', '0', '', '0', '0', '0', '', '', '0', '0',
	// '0', '0', '0', '0', '0', 'en', 'America/Anguilla', 'D M d, Y g:i a', '1',
	// '0', '', '0', '0', '0', '0', '-3', '0', '0', 't', 'd', '0', 't', 'a', '0',
	// '1', '0', '1', '1', '1', '1', '230271', '[\"%USERNAME%\",\"helm\"]',
	// 'sitesplat.minecraftavatarminotar.minecraftminotar', '90', '90', '', '', '',
	// '', '', '', '', '1', '0', '0');";
	// private static final String ADDTOGROUPTEMPLATE = "INSERT INTO
	// `phpbb_user_group` (`group_id`, `user_id`, `group_leader`, `user_pending`)
	// VALUES ('2', '%USERID%', '0', '0');";
	// private static final String[] NOTIFICATIONSTEMPLATE = {
	// "INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`,
	// `method`, `notify`) VALUES ('notification.type.topic', '0', '%USER%',
	// 'notification.method.board', '1');",
	// "INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`,
	// `method`, `notify`) VALUES ('notification.type.topic', '0', '%USER%',
	// 'notification.method.email', '1');",
	// "INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`,
	// `method`, `notify`) VALUES ('notification.type.post', '0', '%USER%',
	// 'notification.method.email', '1');",
	// "INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`,
	// `method`, `notify`) VALUES ('notification.type.needs_approval', '0',
	// '%USER%', 'notification.method.board', '1');",
	// "INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`,
	// `method`, `notify`) VALUES ('notification.type.pm', '0', '%USER%',
	// 'notification.method.board', '1');",
	// "INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`,
	// `method`, `notify`) VALUES ('notification.type.pm', '0', '%USER%',
	// 'notification.method.email', '1');",
	// "INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`,
	// `method`, `notify`) VALUES ('notification.type.bookmark', '0', '%USER%',
	// 'notification.method.board', '1');",
	// "INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`,
	// `method`, `notify`) VALUES ('notification.type.post', '0', '%USER%',
	// 'notification.method.board', '1');",
	// "INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`,
	// `method`, `notify`) VALUES ('notification.type.quote', '0', '%USER%',
	// 'notification.method.board', '1');",
	// "INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`,
	// `method`, `notify`) VALUES ('notification.type.report', '0', '%USER%',
	// 'notification.method.board', '1');",
	// "INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`,
	// `method`, `notify`) VALUES ('sitesplat.bbmention.notification', '0',
	// '%USER%', 'notification.method.board', '1');" };
	private static final String CREATEACCOUNTTEMPLATE = "INSERT INTO `phpbb_users` (`user_id`, `user_type`, `group_id`, `user_permissions`, `user_perm_from`, `user_ip`, `user_regdate`, `username`, `username_clean`, `user_password`, `user_passchg`, `user_email`, `user_email_hash`, `user_birthday`, `user_lastvisit`, `user_lastmark`, `user_lastpost_time`, `user_lastpage`, `user_last_confirm_key`, `user_last_search`, `user_warnings`, `user_last_warning`, `user_login_attempts`, `user_inactive_reason`, `user_inactive_time`, `user_posts`, `user_lang`, `user_timezone`, `user_dateformat`, `user_style`, `user_rank`, `user_colour`, `user_new_privmsg`, `user_unread_privmsg`, `user_last_privmsg`, `user_message_rules`, `user_full_folder`, `user_emailtime`, `user_topic_show_days`, `user_topic_sortby_type`, `user_topic_sortby_dir`, `user_post_show_days`, `user_post_sortby_type`, `user_post_sortby_dir`, `user_notify`, `user_notify_pm`, `user_notify_type`, `user_allow_pm`, `user_allow_viewonline`, `user_allow_viewemail`, `user_allow_massemail`, `user_options`, `user_avatar`, `user_avatar_type`, `user_avatar_width`, `user_avatar_height`, `user_sig`, `user_sig_bbcode_uid`, `user_sig_bbcode_bitfield`, `user_jabber`, `user_actkey`, `user_newpasswd`, `user_form_salt`, `user_new`, `user_reminded`, `user_reminded_time`) VALUES (NULL, '0', '2', '', '0', ?, ?, ?, ?, MD5(?), '0', ?, '0', '', '0', '0', '0', '', '', '0', '0', '0', '0', '0', '0', '0', 'en', 'America/Anguilla', 'D M d, Y g:i a', '1', '0', '', '0', '0', '0', '0', '-3', '0', '0', 't', 'd', '0', 't', 'a', '0', '1', '0', '1', '1', '1', '1', '230271', '[\"?\",\"helm\"]', 'sitesplat.minecraftavatarminotar.minecraftminotar', '90', '90', '', '', '', '', '', '', '', '1', '0', '0');";
	private static final String ADDTOGROUPTEMPLATE = "INSERT INTO `phpbb_user_group` (`group_id`, `user_id`, `group_leader`, `user_pending`) VALUES ('2', ?, '0', '0');";
	private static final String[] NOTIFICATIONSTEMPLATE = {
			"INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`, `method`, `notify`) VALUES ('notification.type.topic', '0', ?, 'notification.method.board', '1');",
			"INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`, `method`, `notify`) VALUES ('notification.type.topic', '0', ?, 'notification.method.email', '1');",
			"INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`, `method`, `notify`) VALUES ('notification.type.post', '0', ?, 'notification.method.email', '1');",
			"INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`, `method`, `notify`) VALUES ('notification.type.needs_approval', '0', ?, 'notification.method.board', '1');",
			"INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`, `method`, `notify`) VALUES ('notification.type.pm', '0', ?, 'notification.method.board', '1');",
			"INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`, `method`, `notify`) VALUES ('notification.type.pm', '0', ?, 'notification.method.email', '1');",
			"INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`, `method`, `notify`) VALUES ('notification.type.bookmark', '0', ?, 'notification.method.board', '1');",
			"INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`, `method`, `notify`) VALUES ('notification.type.post', '0', ?, 'notification.method.board', '1');",
			"INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`, `method`, `notify`) VALUES ('notification.type.quote', '0', ?, 'notification.method.board', '1');",
			"INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`, `method`, `notify`) VALUES ('notification.type.report', '0', ?, 'notification.method.board', '1');",
			"INSERT INTO `phpbb_user_notifications` (`item_type`, `item_id`, `user_id`, `method`, `notify`) VALUES ('sitesplat.bbmention.notification', '0', ?, 'notification.method.board', '1');" };
	private static final String FORUMUSERSTEMPLATE = "INSERT INTO forum_users VALUES (?, ?, ?);";

	private String ip;
	private String name;
	private ProxiedPlayer p;
	private String email;
	private String pass;
	private Connection conn;
	private boolean verbose;

	public AsyncCreateAccount(String ip, ProxiedPlayer p, String email, String pass, Connection conn, boolean verbose) {
		this.ip = ip;
		name = p.getName();
		this.p = p;
		this.email = email;
		this.pass = pass;
		this.conn = conn;
		this.verbose = verbose;
	}

	public void run() {
		try {
			PreparedStatement sta = conn.prepareStatement("SELECT * FROM `phpbb_users` WHERE `username`= ?");
			sta.setString(1, name);
			ResultSet r = sta.executeQuery();
			if (r.next()) {
				p.sendMessage(new TextComponent(ChatColor.RED + "You already have an account!"));
				p.sendMessage(new TextComponent(ChatColor.RED + "Change your password with /changepassword!"));
			} else {
				sta = conn.prepareStatement(CREATEACCOUNTTEMPLATE);
				sta.setString(1, ip);
				sta.setString(2, Long.toString(System.currentTimeMillis() / 1000l));
				sta.setString(3, name);
				sta.setString(4, name.toLowerCase());
				sta.setString(5, pass);
				sta.setString(6, email);
				// account = account.replace("%IP%", ip);
				// account = account.replace("%TIME%", Long.toString(System.currentTimeMillis()
				// / 1000L));
				// account = account.replace("%USERNAME%", name);
				// account = account.replace("%USERNAME_CLEAN%", name.toLowerCase());
				// account = account.replace("%PASSWORD%", pass);
				// account = account.replace("%EMAIL%", email);
				sta.executeUpdate();
				r = sta.executeQuery("SELECT MAX(user_id) AS user_id FROM `phpbb_users`");
				r.next();
				String id = r.getString("user_id");
				if (verbose) {
					Main.getPlugin().getLogger().info("Created account for " + name + ", forum id is " + id);
				}
				sta = conn.prepareStatement(ADDTOGROUPTEMPLATE);
				sta.setString(1, id);
				sta.executeUpdate();
				if (verbose)
					Main.getPlugin().getLogger().info("Added id " + id + " to group (registered users)");
				if (verbose)
					Main.getPlugin().getLogger().info("Setting up notification settings...");
				for (String notif : NOTIFICATIONSTEMPLATE) {
					sta = conn.prepareStatement(notif);
					sta.setString(1, id);
					sta.executeUpdate();
				}
				if (verbose)
					Main.getPlugin().getLogger().info("Adding user to forum_users database");

				r = Main.getLocalConnection().createStatement()
						.executeQuery("SELECT * FROM forum_users WHERE id=" + id);
				if (r.next()) {
					Main.getLocalConnection().createStatement().executeUpdate("UPDATE forum_users SET name='"
							+ p.getName() + "', uuid='" + p.getUniqueId() + "' WHERE id=" + id);
				} else {
					sta = Main.getLocalConnection().prepareStatement(FORUMUSERSTEMPLATE);
					sta.setInt(1, Integer.parseInt(id));
					sta.setString(2, p.getName());
					sta.setString(3, p.getUniqueId().toString());
					sta.executeUpdate();
				}

				Main.getPlugin().getLogger().info("Created account for " + name);

				p.sendMessage(new TextComponent(ChatColor.GREEN + "Account created!"));
				p.sendMessage(new TextComponent(ChatColor.GREEN + "Name: " + name));
				p.sendMessage(new TextComponent(ChatColor.GREEN + "Email: " + email));
				TextComponent url = new TextComponent(ChatColor.GREEN + "Click here to open the website!");
				url.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.left4craft.org"));
				p.sendMessage(url);
				Main.setGroup(p, "user");

			}
		} catch (SQLException e) {
			Main.getPlugin().getLogger().warning("Error creating account for " + name + "!");
			e.printStackTrace();
		}
	}
}
