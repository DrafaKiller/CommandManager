# CommandManager - Plugin Helper

PaperMC plugin helper for Minecraft.

Add this to your project and don't need to worry about managing your plugin's command anymore.
With this it's much easier. Create a SubCommand class for each sub command you want in your plugin and create a method that will be executed when the command runs.

## How to use?

When your plugin initializes, set up the command manager while also adding the sub commands, more information about each sub command can be defined in the sub command's constructor. A main "sub" command can be added to be executed when the command is used with no arguments.

Setting up the command manager:
```java
import com.drafakiller.commandmanager.CommandManager;

public final class YourPlugin extends JavaPlugin {
    public void onEnable() {
        new CommandManager(this, "your_command")
            .setMainSubCommand(new MainCommand())
            .addSubCommand(new HelpCommand())
            .addSubCommand(new AboutCommand());
	}
}
```

Each **SubCommand** can have a `name`, `aliases`, `info` and `usage`. It will only be called if the arguments of the queried command match with the name, aliases and usage.
Can also have more sub commands inside the sub commands, adding them with `addSubCommand`.

Define what arguments your sub command is expecting with `usage`. The sub command can accept predetermined arguments or more dynamical arguments.

**Keywords** will accept some arguments and check if they are valid, using them on `usage`. Official keywords will be checked automatically by the manager, but you may also add your own keywords. Unofficial keywords will accept any type of arguments that will then be processed by you with `onCommand`, adding unofficial keywords can also be important for the documentation, used on **HelpCommand**. Official keywords are `%number%`, `%decimal%` and `%player%`.

The manager already handles most default functionalities like tab completion and error message, but the sub command can decide to accept everything and handle it by making `acceptOverflows` true and not adding `usage`.

Sub command's methods:
* `onCommand`: Called when the command is executed. The sub command can decide to deny by returning false, this will make the manager automatically send an error message to the player letting them know which arguments are wrong.


* `onTabCompletion`: Called while the player is typing, the manager already automatically adds the wanted arguments defined in the usage, but the sub command can handle it and add extra tab completions.


* `onPermission`: Some sub commands might be for admins or other ranks only, every time the manager needs to display or run a sub command this method will be called. You may process the permission of a player and return false to not allow the player to see or run the command.

 
Default commands were made like **HelpCommand** and **AboutCommand**, you should add them to your command manager if you want them implemented. They can also be used as an example of a SubCommand. They can be found at `com.drafakiller.commandmanager.commands`.

SubCommand implementation:
```java
import com.drafakiller.commandmanager.SubCommand;
import com.drafakiller.commandmanager.SubCommandResult;

public class YourCommand extends SubCommand {
	public YourCommand() {
		this.name = "yoursubcommand";
		this.aliases = new String[] { "youralias1", "youralias2" };
		this.info = "Your info.";
		this.usage = new String[][] { { "your_argument1", "your_argument2" }, { "%player%" } };
		// This command can be called like: /yoursubcommand your_argument1 <player_name>
	}
	
	@Override
	public Boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, SubCommandResult result) {
		// Your code...
		return true;
	}
}
```