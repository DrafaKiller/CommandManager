package com.drafakiller.commandmanager;

import com.drafakiller.commandmanager.commands.AboutCommand;
import com.drafakiller.commandmanager.commands.HelpCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Object used in conjunction with {@link CommandManager}.
 * <p>
 * Define the information about the sub command and how it should be interacted with.
 * A sub command will only be called if the arguments of the queried command match with the name, aliases and usage.
 * <p>
 * A {@code usage} is what defines what can be inputted on each argument. It's a list of all arguments, where each is a list of acceptable inputs.<br>
 * For example: {@code { { "example" }, { "test1", "test2", "test3" }, { "%player%" } }}<br>
 * - This usage only allows a max of 3 argument (not required) which the first argument must be "example", the second argument must be "test1", "test2" or "test3" and the third argument must be a valid online player (using a keyword).<br>
 * - This usage will allow {@code "/<command> <subcommand> example"} and  {@code "/<command> <subcommand> example test2 <player_name>"}. It will be up to the sub command to implement internally whether accepts or denies the fact that the other arguments are missing. But it will return a chat error message of invalid argument to {@code "/<command> <subcommand> fish"} because fish was not defined as an acceptable input for the first argument.
 * <p>
 * {@code Keywords} tell the manager what to accept. Keywords known by the manager will be automatically processed, while unknown keywords will accept any input and it will be up to the sub command to accept them or not.
 * It's still important to set both known and unknown (when needed) keywords so the manager can tell the player what type of argument the sub command expects, when viewing the help list.
 * <p>
 * Known keywords list:<br>
 *     - {@code %number%} accepts a number.<br>
 *     - {@code %integer%} accepts an integer.<br>
 *     - {@code %player%} accepts a valid online player.<br>
 * <p>
 * Allow any input (and to deal them with internally) by using {@code acceptOverflows}, setting it to true will always accept any length of arguments.
 * <p>
 * While {@code onCommand} and {@code onTabComplete}:<br>
 * - Reject a command query by returning false, which will automatically display an error message for invalid argument, returning true will tell the manager that everything is okay.<br>
 * - The information of the search which resulted on the sub command is accessible with {@link SubCommandResult}, which contains the arguments and others.
 * <p>
 * Every time the manager needs to check if a player has the permission to acknowledge or run a sub command, {@code onPermission} will be called, returning true will allow and false will disallow.
 * <p>
 * Also see: {@link CommandManager} and {@link SubCommandResult}.
 * <p>
 * Pre-made commands: {@link HelpCommand} and {@link AboutCommand}.
 */
public abstract class SubCommand {
	
	public CommandManager manager;
	public final ArrayList<SubCommand> subcommands = new ArrayList<>();
	
	public String name = "";
	public String info = "";
	public String[] aliases = new String[0];
	public String[][] usage = new String[0][];
	public Boolean acceptOverflows = false;
	
	public Plugin getPlugin() {
		if (this.manager != null) {
			return this.manager.plugin;
		} else {
			return null;
		}
	}
	
	public @NotNull SubCommand addSubCommand(@NotNull SubCommand subcommand) {
		subcommand.manager = this.manager;
		subcommands.add(subcommand);
		return this;
	}
	
	public boolean senderHasPermission(CommandSender sender) {
		return onPermission(sender);
	}
	
	public ArrayList<SubCommand> getPermittedSubCommands(@NotNull CommandSender sender) {
		ArrayList<SubCommand> permittedList = new ArrayList<>();
		for (SubCommand subcommand : subcommands) {
			if (subcommand.senderHasPermission(sender)) {
				permittedList.add(subcommand);
			}
		}
		return permittedList;
	}
	
	public Boolean onPermission(CommandSender sender) {
		return true;
	}
	
	public Boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, SubCommandResult result) {
		return false;
	}
	
	public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, SubCommandResult result) {
		return null;
	}
	
}