package com.drafakiller.commandmanager.commands;

import com.drafakiller.commandmanager.SubCommand;
import com.drafakiller.commandmanager.SubCommandResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AboutCommand extends SubCommand {
	
	protected TextColor defaultColor;
	
	public void setup() {
		this.name = "about";
		this.info = "Shows more details about the plugin.";
		this.defaultColor = NamedTextColor.DARK_AQUA;
	}
	
	public AboutCommand() {
		this.setup();
	}
	
	public AboutCommand(TextColor defaultColor) {
		this.setup();
		if (defaultColor != null) {
			this.defaultColor = defaultColor;
		}
	}
	
	@Override
	public Boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, SubCommandResult result) {
		if (sender instanceof Player) {
			Player player = (Player) sender;
			PluginDescriptionFile pluginInfo = this.getPlugin().getDescription();
			
			TextComponent.Builder message = Component.text()
				.append(
					Component.text()
						.append(Component.newline())
						.append(Component.text("["))
						.append(Component.text(this.getPlugin().getName(), null, TextDecoration.BOLD))
						.append(Component.space())
						.append(Component.text("- About]"))
						.color(this.defaultColor)
				).append(Component.newline());
			
			message
				.append(Component.text(" Plugin: "))
				.append(Component.text(pluginInfo.getName(), this.defaultColor, TextDecoration.ITALIC))
				.append(Component.newline());
			
			if (pluginInfo.getVersion().length() > 0) {
				message
					.append(Component.text(" Version: "))
					.append(Component.text(pluginInfo.getVersion()))
					.append(Component.newline())
					.append(Component.newline());
			}
			
			String description = pluginInfo.getDescription();
			if (description != null && description.length() > 0) {
				message
					.append(Component.text(" Description: "))
					.append(Component.text(description, NamedTextColor.GRAY))
					.append(Component.newline())
					.append(Component.newline());
			}
			
			List<String> authors = pluginInfo.getAuthors();
			if (!authors.isEmpty()) {
				message.append(Component.text(authors.size() > 1 ? " Authors:" : " Author:"));
				boolean first = true;
				for (String author : authors) {
					if (first) {
						first = false;
					} else {
						message.append(Component.text(","));
					}
					message.append(Component.text(" " + author, NamedTextColor.DARK_AQUA));
				}
				message.append(Component.newline());
			}
			
			List<String> contributors = pluginInfo.getContributors();
			if (!contributors.isEmpty()) {
				message.append(Component.text(contributors.size() > 1 ? " Contributors:" : " Contributor:"));
				boolean first = true;
				for (String author : contributors) {
					if (first) {
						first = false;
					} else {
						message.append(Component.text(","));
					}
					message.append(Component.text(" " + author, NamedTextColor.DARK_AQUA));
				}
				message.append(Component.newline());
			}
			
			String website = pluginInfo.getWebsite();
			if (website != null && website.length() > 0) {
				if (!website.toLowerCase().matches("^https?://")) {
					website = "http://" + website;
				}
				message
					.append(Component.text(" Website: "))
					.append(
						Component.text(website, this.defaultColor, TextDecoration.ITALIC)
							.hoverEvent(HoverEvent.showText(
								Component.text()
									.append(Component.text("Click here to open the url "))
									.append(Component.text(website, this.defaultColor, TextDecoration.ITALIC))
							))
							.clickEvent(ClickEvent.openUrl(website))
					)
					.append(Component.newline());
			}
			
			player.sendMessage(message);
		}
		
		return true;
	}
}
