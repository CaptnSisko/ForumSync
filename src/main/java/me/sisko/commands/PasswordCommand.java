package me.sisko.commands;

import java.util.ArrayList;
import me.sisko.forumsync.Main;
import me.sisko.forumsync.PasswordCreator;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class PasswordCommand extends Command {
	public PasswordCommand() {
		super("changepassword");
	}

	private static ArrayList<PasswordCreator> passwords = new ArrayList<PasswordCreator>();

	@Override
	public void execute(CommandSender sender, String[] args) {
		if ((sender instanceof ProxiedPlayer)) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			if (args.length == 1) {
				for (int i = 0; i < passwords.size(); i++) {
					if (((PasswordCreator) passwords.get(i)).getName().equals(p.getName()))
						passwords.remove(i);
				}
				passwords.add(new PasswordCreator(p.getName(), args[0]));
				p.sendMessage(new TextComponent(
						ChatColor.AQUA + "Confirm your password with /changepassword confirm <password>"));
			} else if (args.length == 2) {
				if (args[0].equals("confirm")) {
					boolean foundName = false;
					for (int i = 0; i < passwords.size(); i++) {
						if (passwords.get(i).getName().equals(p.getName())) {
							foundName = true;
							if (passwords.get(i).getPass().equals(args[1])) {
								Main.getPlugin().getProxy().getScheduler().runAsync(Main.getPlugin(),
										new me.sisko.sql.AsyncChangePassword(p,
												passwords.get(i).getPass(), Main.getConnection(),
												Main.getConfig().getVerbose()));
								passwords.remove(i);
								return;
							}
						}
					}
					if (foundName) {
						p.sendMessage(new TextComponent(ChatColor.RED + "Passwords do not match"));
					} else {
						p.sendMessage(new TextComponent(
								ChatColor.RED + "You must change your password before confirming it"));
					}
				} else {
					p.sendMessage(new TextComponent(ChatColor.RED + "Usage: /changepassword <newpassword>"));
				}
			} else {
				p.sendMessage(new TextComponent(ChatColor.RED + "Usage: /changepassword <newpassword>"));
			}
		} else {
			Main.getPlugin().getLogger().info("You can't do that from console!");
		}
	}
}
