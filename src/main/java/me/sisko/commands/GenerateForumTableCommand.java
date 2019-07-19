package me.sisko.commands;

import me.sisko.forumsync.Main;
import me.sisko.sql.AsyncGenerateTable;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GenerateForumTableCommand extends Command {
	public GenerateForumTableCommand() {
		super("generateforum");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if ((sender instanceof ProxiedPlayer)) {
			ProxiedPlayer p = (ProxiedPlayer) sender;
			p.sendMessage(new TextComponent(ChatColor.RED + "You don't have permission to do that!"));
		} else {
			Main.getPlugin().getProxy().getScheduler().runAsync(Main.getPlugin(), new AsyncGenerateTable(Main.getLocalConnection(), Main.getConnection(), Main.getPerms()));
		}
	}
}
