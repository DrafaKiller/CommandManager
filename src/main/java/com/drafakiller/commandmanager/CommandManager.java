package com.drafakiller.commandmanager;

import com.drafakiller.commandmanager.commands.AboutCommand;
import com.drafakiller.commandmanager.commands.HelpCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A PaperMC plugin implementation, used to easily manage commands while developing.
 * <p>
 * Manages executed commands, providing a automatic search of the exact sub command to run and respective tab completion without having to worry internally.
 * <p>
 * Initialize a new CommandManager and add to the list all the SubCommands with the functionalities needed for the plugin, onCommand and onTabComplete if needed.
 * <p><br>
 *     Step 1: Initialize a new {@link CommandManager} on onEnable of the plugin, giving the plugin and the command name.
 *     <p>
 *     Step 2: Add {@link SubCommand}s to the list of the command manager using {@code .addSubCommand()}. Can also add a main SubCommand to be executed by default when no argument using {@code .addMainSubCommand()}.
 * <p><br>
 * Also see: {@link SubCommand} and {@link SubCommandResult}.
 * <p>
 * Pre-made commands: {@link HelpCommand} and {@link AboutCommand}.
 */

public class CommandManager implements TabExecutor {
	
	protected final Plugin plugin;
	
	/**
	 * Default {@link SubCommand} to run when the command is called with no arguments.
	 */
	public SubCommand main_subcommand;
	
	/**
	 * List of {@link SubCommand}s that will be interpreted when onCommand and onTabComplete.
	 */
	public final ArrayList<SubCommand> subcommands = new ArrayList<>();
	
	public final PluginCommand command;
	
	/**
	 * Keywords are used to automatically check if the arguments requested by the {@link SubCommand} are valid.
	 * <p>
	 * They are also used to show the user what the argument should be, being displayed in the help of the command.
	 * <p>
	 * Keywords List:<br>
	 *     - %number% checks if the argument is a number, intager.<br>
	 *     - %decimal% checks if the argument is a decimal number.<br>
	 *     - %player% checks if the argument is a valid player.<br>
	 */
	public static final String[] argumentKeywords = new String[] { "%number%", "%decimal%", "%player%" };
	
	public CommandManager(Plugin plugin, String command) {
		this.plugin = plugin;
		
		this.command = this.plugin.getServer().getPluginCommand(command);
		if (this.command != null) {
			this.command.setAliases(Arrays.asList("bb", "test123"));
			this.command.setExecutor(this);
		}
	}
	
	/**
	 * Adds a {@link SubCommand} to the list of sub commands also implementing the manager into the sub command, and returns itself for chaining.
	 *
	 * @param subcommand {@link SubCommand} to be added
	 * @return this object, for chaining
	 */
	public @NotNull CommandManager addSubCommand(@NotNull SubCommand subcommand) {
		subcommand.manager = this;
		subcommands.add(subcommand);
		return this;
	}
	
	/**
	 * Sets the default {@link SubCommand} to run when the command is called with no arguments also implementing the manager into the sub command, and returns itself for chaining.
	 *
	 * @param subcommand default {@link SubCommand} to run
	 * @return this object, for chaining
	 */
	public @NotNull CommandManager setMainSubCommand(@NotNull SubCommand subcommand) {
		subcommand.manager = this;
		main_subcommand = subcommand;
		return this;
	}
	
	/**
	 * Searches for a {@link SubCommand} with the given name or alias, added to the sub command list. Returns the sub command, otherwise null.
	 *
	 * @param name name or alias of the subcommand
	 * @return sub command found, otherwise null
	 */
	public @Nullable SubCommand getSubCommand(String name) {
		for (SubCommand subcommand : subcommands) {
			if (subcommand.name.equals(name) || Arrays.asList(subcommand.aliases).contains(name)) {
				return subcommand;
			}
		}
		
		return null;
	}
	
	/**
	 * Checks if a text can be converted into a {@link Double}, returns the result.
	 *
	 * @param text text to be checked
	 * @return boolean result
	 */
	public static boolean isStringDouble(String text) {
		try {
			Double.parseDouble(text);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Checks if a text can be converted into a {@link Integer}, returns the result.
	 *
	 * @param text text to be checked
	 * @return boolean result
	 */
	public static boolean isStringInteger(String text) {
		try {
			Integer.parseInt(text);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	/**
	 * Checks if a text is valid for an argument keyword. Valid keywords must start and end with "%".
	 *
	 * @param keyword text to be checked
	 * @return boolean result
	 */
	public static boolean isArgumentKeyword(String keyword) {
		return keyword.matches("^%.*%$");
	}
	
	/**
	 * Checks if a keyword makes part of the {@link CommandManager}'s keywords.
	 *
	 * @param keyword text to be checked
	 * @return true if the keyword is a known keyword, otherwise false.
	 */
	public static boolean isOfficialArgumentKeyword(String keyword) {
		return Arrays.asList(argumentKeywords).contains(keyword);
	}
	
	/**
	 * Checks if a keyword doesn't makes part of the CommandManager's keywords.
	 *
	 * @param keyword text to be checked
	 * @return true if the keyword is a known keyword, otherwise false.
	 */
	public static boolean isUnofficialArgumentKeyword(String keyword) {
		return isArgumentKeyword(keyword) && !isOfficialArgumentKeyword(keyword);
	}
	
	/**
	 * Checks if a list contains unofficial keyword.
	 *
	 * @param array list to be checked
	 * @return true if an unofficial keyword exists, otherwise false.
	 */
	public static boolean containsUnofficialArgumentKeywords(List<String> array) {
		for (String text : array) {
			if (isUnofficialArgumentKeyword(text)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Limits a text by adding {@code ...} at the end of the text if it passed the max length.
	 * Given a limit and a min length for a word to be cut, if a word that needs to be cut doesn't meet this requirement then it won't be shown.
	 *
	 * @param text text to be modified
	 * @param limit max length of the text
	 * @param wordLimit min length of a word to be cut
	 * @return formatted text, with {@code ...} if it passed the limit
	 */
	public static String textLimit(String text, int limit, int wordLimit) {
		if (text.length() <= limit) {
			return text;
		}
		
		int lastSpace = text.lastIndexOf(" ", limit - 3);
		if ((limit - 3) - (lastSpace + 1) <= wordLimit) {
			text = text.substring(0, lastSpace);
		} else {
			text = text.substring(0, limit - 3);
		}
		
		return text.replaceAll("\\s*$", "") + "...";
	}
	
	/**
	 * Limits a text by adding {@code ...} at the end of the text if it passed the max length.
	 * Given a limit, if a word that needs to be cut is less than 5 characters long then it won't be shown.
	 *
	 * @param text text to be modified
	 * @param limit max length of the text
	 * @return formatted text, with ... if it passed the limit
	 */
	public static String textLimit(String text, int limit) {
		return textLimit(text, limit, 5);
	}
	
	/**
	 * Searches a list of {@link SubCommand}s to find and match the arguments requested.
	 * Each argument must be a sub command's name, alias or usage. If the argument is another sub command then the search will continue inside that sub command instead.
	 *
	 * Keywords are checked here.
	 *
	 * @param subcommands list of sub commands to search
	 * @param arguments list of arguments to compare
	 * @return the result of the query
	 */
	protected SubCommandResult querySubCommand(@NotNull ArrayList<SubCommand> subcommands, @NotNull String[] arguments) {
		if (arguments.length > 0) {
			for (SubCommand subcommand : subcommands) {
				if (subcommand.name.equals(arguments[0]) || Arrays.asList(subcommand.aliases).contains(arguments[0])) {
					if (arguments.length > 1) {
						if (subcommand.acceptOverflows) {
							return new SubCommandResult(subcommand, arguments, 1);
						} else {
							SubCommandResult result = this.querySubCommand(subcommand.subcommands, Arrays.copyOfRange(arguments, 1, arguments.length));
							if (result.subcommand != null) {
								return new SubCommandResult(result.subcommand, arguments, result.currentArgumentIndex + 1, result.isValid, result.isUsage, result.currentUsageIndex);
							} else {
								boolean valid = true;
								int i;
								for (i = 0; i < subcommand.usage.length && i < arguments.length - 1; i++) {
									List<String> usage = Arrays.asList(subcommand.usage[i]);
									String argument = arguments[i + 1];
									
									if (!containsUnofficialArgumentKeywords(usage) && !(
										(usage.contains(argument) && !isOfficialArgumentKeyword(argument)) ||
										(usage.contains("%number%") && isStringInteger(argument)) ||
										(usage.contains("%decimal%") && isStringDouble(argument)) ||
										(usage.contains("%player%") && Bukkit.getPlayer(argument) != null)
									)) {
										valid = false;
										break;
									}
								}
								
								if (valid && arguments.length - 1 <= subcommand.usage.length) {
									return new SubCommandResult(subcommand, arguments, i, true, true, i - 1);
								} else {
									return new SubCommandResult(subcommand, arguments, i + 1, false, true, i - (valid ? 1 : 0));
								}
							}
						}
					} else {
						return new SubCommandResult(subcommand, arguments, 0);
					}
				}
			}
		}
		return new SubCommandResult(null, arguments, 0);
	}
	
	protected void sendErrorMessage(@NotNull CommandSender sender, String[] arguments, Integer currentArgumentIndex) {
		String rightCommand = String.join(" ", Arrays.copyOfRange(arguments, 0, currentArgumentIndex));
		String wrongCommand = String.join(" ", Arrays.copyOfRange(arguments, currentArgumentIndex, arguments.length));
		if (sender instanceof Player) {
			Player player = (Player) sender;
			player.sendMessage(
				Component.text()
					.append(Component.text("[" + this.plugin.getName() + "]", NamedTextColor.RED, TextDecoration.BOLD)
						.clickEvent(ClickEvent.runCommand("/" + this.command.getName() + " help")))
					.append(Component.text(" Incorrect argument for command:", NamedTextColor.RED, TextDecoration.BOLD))
					.append(Component.newline())
					.append(Component.text()
						.append(Component.text("/" + this.plugin.getName() + " " + rightCommand + (currentArgumentIndex > 0 ? " " : ""), NamedTextColor.GRAY))
						.append(Component.text(wrongCommand, NamedTextColor.RED, TextDecoration.UNDERLINED))
						.clickEvent(ClickEvent.suggestCommand("/" + this.plugin.getName() + " " + rightCommand + (currentArgumentIndex > 0 ? " " : "") + wrongCommand)))
					.build()
			);
		}
	}
	
	/**
	 * Returns a list of all {@link SubCommand}s which are accessible by the player, server or entity.
	 * The permission is given by onPermission, implemented by the sub command.
	 *
	 * @return a list with all permitted sub commands
	 */
	public ArrayList<SubCommand> getPermittedSubCommands(CommandSender sender) {
		ArrayList<SubCommand> permittedList = new ArrayList<>();
		for (SubCommand subcommand : subcommands) {
			if (subcommand.senderHasPermission(sender)) {
				permittedList.add(subcommand);
			}
		}
		return permittedList;
	}
	
	/**
	 * Searches for the {@link SubCommand} and executes it. If no sub command was found or if the sub command denies, a error message is shown.
	 *
	 * @return always returns true to prevent the default error message
	 */
	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] arguments) {
		if (arguments.length > 0) {
			SubCommandResult result = this.querySubCommand(this.getPermittedSubCommands(sender), arguments);
			
			if (result.isValid()) {
				result.subcommand.onCommand(sender, command, label, result);
			} else {
				this.sendErrorMessage(sender, arguments, result.currentArgumentIndex);
			}
		} else if (this.main_subcommand != null) {
			this.main_subcommand.onCommand(sender, command, label, new SubCommandResult(this.main_subcommand, arguments, null));
		}
		
		return true;
	}
	
	@Override
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] arguments) {
		List<String> options = new ArrayList<>();
		
		if (arguments.length > 1) {
			SubCommandResult result = this.querySubCommand(this.getPermittedSubCommands(sender), Arrays.copyOfRange(arguments, 0, arguments.length - 1));
			
			if (result.isValid() && !(result.isUsage && !(result.currentUsageIndex + 1 <= result.subcommand.usage.length - 1))) {
				if (!result.isUsage) {
					for (SubCommand subcommand : result.subcommand.getPermittedSubCommands(sender)) {
						if (subcommand.name.length() > 0) {
							options.add(subcommand.name);
						}
					}
					
					if (result.subcommand.usage.length > 0) {
						for (String usage : result.subcommand.usage[0]) {
							if (usage.equals("%player%")) {
								for (Player player : Bukkit.getServer().getOnlinePlayers()) {
									options.add(player.getName());
								}
							} else if (!isArgumentKeyword(usage)) {
								options.add(usage);
							}
						}
					}
				} else if (result.currentUsageIndex + 1 < result.subcommand.usage.length) {
					for (String usage : result.subcommand.usage[result.currentUsageIndex + 1]) {
						if (usage.equals("%player%")) {
							for (Player player : Bukkit.getServer().getOnlinePlayers()) {
								options.add(player.getName());
							}
						} else if (!isArgumentKeyword(usage)) {
							options.add(usage);
						}
					}
				}
				
				List<String> tabResult = result.subcommand.onTabComplete(sender, command, alias, result);
				if (tabResult != null) {
					options.addAll(tabResult);
				}
			}
		} else {
			for (SubCommand subcommand : this.getPermittedSubCommands(sender)) {
				if (subcommand.name.length() > 0) {
					options.add(subcommand.name);
				}
			}
		}
		
		for (int i = options.size() - 1; i >= 0; i--) {
			if (!options.get(i).toLowerCase().contains(arguments[arguments.length - 1].toLowerCase())) {
				options.remove(i);
			}
		}
		
		return options;
	}
	
}