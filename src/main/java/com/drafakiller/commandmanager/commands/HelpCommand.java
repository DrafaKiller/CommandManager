package com.drafakiller.commandmanager.commands;

import com.drafakiller.commandmanager.CommandManager;
import com.drafakiller.commandmanager.SubCommand;
import com.drafakiller.commandmanager.SubCommandResult;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentBuilder;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends SubCommand {
	
	protected final int defaultPage = 1;
	protected final int pageLimit = 5;
	protected TextColor defaultColor;
	
	protected void setup() {
		this.name = "help";
		this.aliases = new String[] { "?" };
		this.info = "Shows all info about the plugin.";
		this.usage = new String[][] { { "%page%", "%command%" } };
		this.defaultColor = NamedTextColor.DARK_AQUA;
	}
	
	public HelpCommand() {
		this.setup();
	}
	
	public HelpCommand(TextColor defaultColor) {
		this.setup();
		if (defaultColor != null) {
			this.defaultColor = defaultColor;
		}
	}
	
	protected boolean showHelp(Player player, int page, int limit) {
		if (this.manager.command != null) {
			String commandName = this.manager.command.getName();
			ArrayList<SubCommand> permittedSubCommands = this.manager.getPermittedSubCommands(player);
			
			TextComponent.Builder message = Component.text()
				.append(
					Component.text()
						.append(Component.newline())
						.append(Component.text("["))
						.append(Component.text(this.getPlugin().getName(), null, TextDecoration.BOLD))
						.append(Component.space())
						.append(Component.text("- Help]"))
						.color(this.defaultColor)
				).append(Component.newline());
			
			if (this.manager.command != null && this.manager.command.getDescription().length() > 0) {
				message
					.append(Component.text(this.manager.command.getDescription()))
					.append(Component.newline())
					.append(Component.newline());
			}
			
			message
				.append(Component.text("All available commands:"))
				.append(Component.newline());
			
			if (permittedSubCommands.size() > 0) {
				int maxPage = (int) Math.ceil((double) permittedSubCommands.size() / limit);
				if (page > maxPage) {
					page = maxPage;
				}
				if (page < 1) {
					page = 1;
				}
				
				for (int i = limit * (page - 1); i < permittedSubCommands.size() && i < limit * page; i++) {
					SubCommand subcommand = permittedSubCommands.get(i);
					if (subcommand.name.length() > 0) {
						if (subcommand.info != null && subcommand.info.length() > 0) {
							String title = " /" + commandName + " " + subcommand.name + ":";
							message.append(
								Component.text(title, this.defaultColor)
									.hoverEvent(HoverEvent.showText(
										Component.text()
											.append(Component.text("Click here to see more information about "))
											.append(Component.text("/" + commandName + " " + subcommand.name, this.defaultColor, TextDecoration.ITALIC))
									))
									.clickEvent(ClickEvent.runCommand("/" + commandName + " help " + subcommand.name))
							)
								.append(Component.space())
								.append(Component.text(CommandManager.textLimit(subcommand.info, 60 - title.length()), NamedTextColor.GRAY, TextDecoration.ITALIC));
						} else {
							String title = " /" + commandName + " " + subcommand.name;
							message.append(
								Component.text(title, this.defaultColor)
									.hoverEvent(HoverEvent.showText(
										Component.text()
											.append(Component.text("Click here to see more information about "))
											.append(Component.text("/" + commandName + " " + subcommand.name, this.defaultColor, TextDecoration.ITALIC))
									))
									.clickEvent(ClickEvent.runCommand("/" + commandName + " help " + subcommand.name))
							);
						}
						if (i + 1 < permittedSubCommands.size() && i + 1 < limit * page) {
							message.append(Component.newline());
						}
					}
				}
				
				if (page > 1 || page < maxPage) {
					message
						.append(Component.newline())
						.append(Component.newline())
						.append(Component.text(" Page " + page + " of " + maxPage + " - ", this.defaultColor));
				}
				
				if (page > 1) {
					message.append(
						Component.text("Previous", this.defaultColor, TextDecoration.BOLD)
							.hoverEvent(HoverEvent.showText(
								Component.text()
									.append(Component.text("Click here to "))
									.append(Component.text("/" + commandName + " help " + (page - 1), this.defaultColor, TextDecoration.BOLD))
							))
							.clickEvent(ClickEvent.runCommand("/" + commandName + " help " + (page - 1)))
					);
				}
				
				if (page > 1 && page < maxPage) {
					message.append(Component.text(" / ", this.defaultColor));
				}
				
				if (page < maxPage) {
					message.append(
						Component.text("Next", this.defaultColor, TextDecoration.BOLD)
							.hoverEvent(HoverEvent.showText(
								Component.text()
									.append(Component.text("Click here to "))
									.append(Component.text("/" + commandName + " help " + (page + 1), this.defaultColor, TextDecoration.BOLD))
							))
							.clickEvent(ClickEvent.runCommand("/" + commandName + " help " + (page + 1)))
					);
				}
				
				if (page > 1 || page < maxPage) {
					message.append(Component.text(" page", this.defaultColor));
				}
			} else {
				message.append(Component.text("  - No commands to show...", null, TextDecoration.ITALIC));
			}
			
			message.append(Component.newline());
			
			player.sendMessage(message);
			return true;
		}
		return false;
	}
	
	protected boolean showHelp(Player player, String command) {
		SubCommand subcommand = this.manager.getSubCommand(command);
		if (subcommand != null && subcommand.senderHasPermission(player)) {
			if (this.manager.command != null) {
				String commandName = this.manager.command.getName();
				TextComponent.Builder message = Component.text()
					.append(
						Component.text()
							.append(Component.newline())
							.append(Component.text("["))
							.append(Component.text(this.getPlugin().getName(), null, TextDecoration.BOLD))
							.append(Component.space())
							.append(Component.text("- Command]"))
							.color(this.defaultColor)
							.hoverEvent(HoverEvent.showText(
								Component.text()
									.append(Component.text("Click here to check "))
									.append(Component.text(this.getPlugin().getName(), this.defaultColor, TextDecoration.BOLD))
									.append(Component.text("'s command list"))
							))
							.clickEvent(ClickEvent.runCommand("/" + commandName + " help"))
					).append(Component.newline());
				
				if (subcommand.name != null && subcommand.name.length() > 0) {
					message.append(Component.text(" Command: "))
						.append(
							Component.text(subcommand.name, this.defaultColor, TextDecoration.ITALIC)
								.hoverEvent(HoverEvent.showText(
									Component.text()
										.append(Component.text("Click here to run the command "))
										.append(Component.text("/" + commandName + " " + subcommand.name, this.defaultColor, TextDecoration.ITALIC))
								))
								.clickEvent(ClickEvent.runCommand("/" + commandName + " " + subcommand.name))
						).append(Component.newline());
				}
				
				if (subcommand.aliases != null && subcommand.aliases.length > 0) {
					message.append(Component.text(" Aliases:"));
					boolean first = true;
					for (String alias : subcommand.aliases) {
						if (first) {
							first = false;
						} else {
							message.append(Component.text(","));
						}
						
						message
							.append(Component.space())
							.append(Component.text(alias, this.defaultColor, TextDecoration.ITALIC));
					}
					message.append(Component.newline());
				}
				
				if (subcommand.info != null && subcommand.info.length() > 0) {
					message
						.append(Component.text(" Description: "))
						.append(Component.text(subcommand.info, NamedTextColor.GRAY, TextDecoration.ITALIC))
						.append(Component.newline());
				}
				
				if (subcommand.usage != null && subcommand.usage.length > 0) {
					StringBuilder usageTemplate = new StringBuilder("/" + commandName + " " + subcommand.name);
					
					for (int i = 0; i < subcommand.usage.length; i++) {
						usageTemplate.append(" <argument ").append(i + 1).append(">");
					}
					
					message
						.append(Component.newline())
						.append(Component.text(" Usage of "))
						.append(
							Component.text(usageTemplate.toString(), this.defaultColor, TextDecoration.ITALIC)
								.clickEvent(ClickEvent.suggestCommand("/" + commandName + " " + subcommand.name + " "))
						)
						.append(Component.text(":"))
						.append(Component.newline());
					
					if (subcommand.getPermittedSubCommands(player).size() > 0) {
						message.append(Component.text("   Sub commands:", null, TextDecoration.BOLD));
						boolean first = true;
						for (SubCommand sub_subcommand : subcommand.getPermittedSubCommands(player)) {
							if (sub_subcommand.name.length() > 0) {
								if (first) {
									first = false;
								} else {
									message.append(Component.text(","));
								}
								message.append(Component.space());
								message.append(Component.text(sub_subcommand.name, this.defaultColor, TextDecoration.ITALIC));
							}
						}
						message.append(Component.newline());
					}
					
					for (int i = 0; i < subcommand.usage.length; i++) {
						String[] usages = subcommand.usage[i];
						message.append(Component.text("   Argument " + (i + 1) + ":", null, TextDecoration.BOLD));
						boolean first = true;
						for (String usage : usages) {
							if (first) {
								first = false;
							} else {
								message.append(Component.text(" /"));
							}
							message.append(Component.space());
							if (CommandManager.isArgumentKeyword(usage)) {
								usage = "<" + usage.replace("%", "") + ">";
								message.append(Component.text(usage, null, TextDecoration.ITALIC));
							} else {
								message.append(Component.text(usage));
							}
						}
						message.append(Component.newline());
					}
				} else {
					message
						.append(Component.newline())
						.append(Component.text(" Usage: "))
						.append(
							Component.text("/" + commandName + " " + subcommand.name, this.defaultColor, TextDecoration.ITALIC)
								.clickEvent(ClickEvent.suggestCommand("/" + commandName + " " + subcommand.name))
						)
						.append(Component.newline());
				}
				
				player.sendMessage(message);
				return true;
			}
		}
		return false;
	}
	
	@Override
	public Boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, SubCommandResult result) {
		String currentArgument = result.getCurrentArgument();
		if (result.isUsage) {
			if (currentArgument != null && CommandManager.isStringInteger(currentArgument)) {
				return showHelp((Player) sender, Integer.parseInt(currentArgument), pageLimit);
			} else {
				return showHelp((Player) sender, currentArgument);
			}
		} else {
			if (sender instanceof Player) {
				return showHelp((Player) sender, defaultPage, pageLimit);
			}
		}
		
		return true;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, SubCommandResult result) {
		List<String> options = new ArrayList<>();
		
		for (SubCommand subcommand : this.manager.getPermittedSubCommands(sender)) {
			if (subcommand.name.length() > 0) {
				options.add(subcommand.name);
			}
		}
		
		return options;
	}
}
