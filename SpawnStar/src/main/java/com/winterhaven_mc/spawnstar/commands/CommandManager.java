package com.winterhaven_mc.spawnstar.commands;

import com.winterhaven_mc.spawnstar.PluginMain;
import com.winterhaven_mc.spawnstar.messages.Message;
import com.winterhaven_mc.spawnstar.util.SpawnStar;
import com.winterhaven_mc.spawnstar.messages.MessageId;
import com.winterhaven_mc.spawnstar.sounds.SoundId;
import com.winterhaven_mc.util.LanguageManager;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.winterhaven_mc.spawnstar.messages.Macro.*;
import static com.winterhaven_mc.spawnstar.messages.MessageId.*;


/**
 * Implements command executor and tab completer for SpawnStar commands.
 */
public final class CommandManager implements CommandExecutor, TabCompleter {

	// reference to main class
	private final PluginMain plugin;

	// constants for chat colors
	private final static ChatColor helpColor = ChatColor.YELLOW;
	private final static ChatColor usageColor = ChatColor.GOLD;

	// constant List of subcommands
	private final static List<String> SUBCOMMANDS =
			Collections.unmodifiableList(new ArrayList<>(
					Arrays.asList("give", "destroy", "status", "reload", "help")));


	/**
	 * Class constructor method for CommandManager
	 *
	 * @param plugin reference to main class
	 */
	public CommandManager(final PluginMain plugin) {

		// set reference to main class
		this.plugin = plugin;

		// register this class as command executor
		Objects.requireNonNull(plugin.getCommand("spawnstar")).setExecutor(this);

		// register this class as tab completer
		Objects.requireNonNull(plugin.getCommand("spawnstar")).setTabCompleter(this);
	}


	/**
	 * Tab completer for SpawnStar
	 */
	@Override
	public final List<String> onTabComplete(final CommandSender sender, final Command command,
											final String alias, final String[] args) {

		// initialize return list
		final List<String> returnList = new ArrayList<>();

		switch (args.length) {
			case 1: // return list of valid matching subcommands
				returnList.addAll(matchSubcommand(sender, args[0]));
				break;

			case 2: // second argument
				// if subcommand is "help", return list of matching subcommands
				if (args[0].equalsIgnoreCase("help")) {
					returnList.addAll(matchSubcommand(sender, args[1]));
				}
				// if subcommand is "give", return list of matching online players
				else if (args[0].equalsIgnoreCase("give")) {
					List<Player> matchedPlayers = plugin.getServer().matchPlayer(args[1]);
					for (Player player : matchedPlayers) {
						returnList.add(player.getName());
					}
				}
				break;

			case 3: // return some useful quantities
				returnList.add("1");
				returnList.add("2");
				returnList.add("3");
				returnList.add("5");
				returnList.add("10");
				break;
		}

		return returnList;
	}


	/**
	 * command executor method for SpawnStar
	 */
	@Override
	public final boolean onCommand(final CommandSender sender, final Command cmd,
								   final String label, final String[] args) {

		String subcommand;

		// get subcommand
		if (args.length > 0) {
			subcommand = args[0];
		}

		// if no arguments, display usage for all commands
		else {
			displayUsage(sender, "all");
			return true;
		}

		// status command
		if (subcommand.equalsIgnoreCase("status")) {
			return statusCommand(sender, args);
		}

		// reload command
		if (subcommand.equalsIgnoreCase("reload")) {
			return reloadCommand(sender, args);
		}

		// give command
		if (subcommand.equalsIgnoreCase("give")) {
			return giveCommand(sender, args);
		}

		// destroy command
		if (subcommand.equalsIgnoreCase("destroy")) {
			return destroyCommand(sender, args);
		}

		// help command
		if (subcommand.equalsIgnoreCase("help")) {
			return helpCommand(sender, args);
		}

		// send invalid command message
		Message.create(sender, COMMAND_FAIL_INVALID_COMMAND).send();

		// play command fail sound for player
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);

		// display usage for help command
		displayUsage(sender, "help");

		// return true to prevent display of bukkit usage string
		return true;
	}


	/**
	 * Display plugin settings
	 *
	 * @param sender the command sender
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean statusCommand(final CommandSender sender, final String[] args) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(args);

		// if command sender does not have permission to view status, output error message and return
		if (!sender.hasPermission("spawnstar.status")) {
			Message.create(sender, COMMAND_FAIL_STATUS_PERMISSION).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get subcommand from command arguments
		final String subcommand = args[0];

		// argument limits
		int maxArgs = 1;

		// check max arguments
		if (args.length > maxArgs) {
			Message.create(sender, MessageId.COMMAND_FAIL_ARGS_COUNT_OVER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		// output config settings
		String versionString = this.plugin.getDescription().getVersion();
		sender.sendMessage(ChatColor.DARK_AQUA + "[SpawnStar] "
				+ ChatColor.AQUA + "Version: " + ChatColor.RESET + versionString);

		if (plugin.getConfig().getBoolean("debug")) {
			sender.sendMessage(ChatColor.DARK_RED + "DEBUG: true");
		}

		sender.sendMessage(ChatColor.GREEN + "Language: "
				+ ChatColor.RESET + plugin.getConfig().getString("language"));

		sender.sendMessage(ChatColor.GREEN + "Default material: "
				+ ChatColor.RESET + plugin.getConfig().getString("item-material"));

		sender.sendMessage(ChatColor.GREEN + "Minimum distance: "
				+ ChatColor.RESET + plugin.getConfig().getInt("minimum-distance"));

		sender.sendMessage(ChatColor.GREEN + "Warmup: "
				+ ChatColor.RESET
				+ LanguageManager.getInstance().getTimeString(TimeUnit.SECONDS.toMillis(
				plugin.getConfig().getInt("teleport-warmup"))));

		sender.sendMessage(ChatColor.GREEN + "Cooldown: "
				+ ChatColor.RESET
				+ LanguageManager.getInstance().getTimeString(TimeUnit.SECONDS.toMillis(
				plugin.getConfig().getInt("teleport-cooldown"))));

		sender.sendMessage(ChatColor.GREEN
				+ "Cancel on damage/movement/interaction: " + ChatColor.RESET + "[ "
				+ plugin.getConfig().getBoolean("cancel-on-damage") + "/"
				+ plugin.getConfig().getBoolean("cancel-on-movement") + "/"
				+ plugin.getConfig().getBoolean("cancel-on-interaction") + " ]");

		sender.sendMessage(ChatColor.GREEN + "Remove from inventory: "
				+ ChatColor.RESET + plugin.getConfig().getString("remove-from-inventory"));

		sender.sendMessage(ChatColor.GREEN + "Allow in recipes: "
				+ ChatColor.RESET + plugin.getConfig().getBoolean("allow-in-recipes"));

		sender.sendMessage(ChatColor.GREEN + "Lightning: "
				+ ChatColor.RESET + plugin.getConfig().getBoolean("lightning"));

		sender.sendMessage(ChatColor.GREEN + "Enabled Words: "
				+ ChatColor.RESET + plugin.worldManager.getEnabledWorldNames().toString());

		return true;
	}


	/**
	 * Reload plugin settings
	 *
	 * @param sender the command sender
	 * @param args   the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean reloadCommand(final CommandSender sender, final String[] args) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(args);

		// if sender does not have permission to reload config, send error message and return
		if (!sender.hasPermission("spawnstar.reload")) {
			Message.create(sender, COMMAND_FAIL_RELOAD_PERMISSION).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get subcommand from command arguments
		final String subcommand = args[0];

		// argument limits
		int maxArgs = 1;

		// check max arguments
		if (args.length > maxArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_OVER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		// reinstall main configuration file if necessary
		plugin.saveDefaultConfig();

		// reload main configuration
		plugin.reloadConfig();

		// update enabledWorlds list
		plugin.worldManager.reload();

		// reload messages
		LanguageManager.reload();

		// reload sounds
		plugin.soundConfig.reload();

		// send reloaded message
		Message.create(sender, COMMAND_SUCCESS_RELOAD).send();
		return true;
	}


	/**
	 * Give target player a spawnstar item
	 *
	 * @param sender the command sender
	 * @param args   the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean giveCommand(final CommandSender sender, final String[] args) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(args);

		// if command sender does not have permission to give SpawnStars, output error message and return
		if (!sender.hasPermission("spawnstar.give")) {
			Message.create(sender, COMMAND_FAIL_GIVE_PERMISSION).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get subcommand from command arguments
		final String subcommand = args[0];

		// argument limits
		int minArgs = 2;
		int maxArgs = 3;

		// check min arguments
		if (args.length < minArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_UNDER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		// check max arguments
		if (args.length > maxArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_OVER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		String targetPlayerName = args[1];
		int quantity = 1;

		if (args.length > 2) {
			try {
				quantity = Integer.parseInt(args[2]);
			}
			catch (NumberFormatException e) {
				Message.create(sender, COMMAND_FAIL_GIVE_QUANTITY_INVALID).send();
				plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
				return true;
			}
		}

		// validate quantity (min = 1, max = configured maximum, or runtime Integer.MAX_VALUE)
		quantity = Math.max(1, quantity);
		int maxQuantity = plugin.getConfig().getInt("max-give-amount");
		if (maxQuantity < 0) {
			maxQuantity = Integer.MAX_VALUE;
		}
		quantity = Math.min(maxQuantity, quantity);

		// try to match target player name to currently online player
		Player targetPlayer = matchPlayer(sender, targetPlayerName);

		// if no match, do nothing and return (message was output by matchPlayer method)
		if (targetPlayer == null) {
			return true;
		}

		// add specified quantity of spawnstar(s) to player inventory
		HashMap<Integer, ItemStack> noFit = targetPlayer.getInventory().addItem(SpawnStar.create(quantity));

		// count items that didn't fit in inventory
		int noFitCount = 0;
		for (int index : noFit.keySet()) {
			noFitCount += noFit.get(index).getAmount();
		}

		// if remaining items equals quantity given, send player-inventory-full message and return
		if (noFitCount == quantity) {
			Message.create(sender, COMMAND_FAIL_GIVE_INVENTORY_FULL)
					.setMacro(ITEM_QUANTITY, quantity)
					.setMacro(TARGET_PLAYER, targetPlayerName)
					.send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// subtract noFitCount from quantity
		quantity -= noFitCount;

		// send both players message if not giving item to self
		if (!sender.getName().equals(targetPlayer.getName())) {

			// send message and play sound to giver
			Message.create(sender, COMMAND_SUCCESS_GIVE_SENDER)
					.setMacro(ITEM_QUANTITY, quantity)
					.setMacro(TARGET_PLAYER, targetPlayerName)
					.send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_SUCCESS_GIVE_SENDER);

			// send message to target player
			Message.create(targetPlayer, COMMAND_SUCCESS_GIVE_TARGET)
					.setMacro(ITEM_QUANTITY, quantity)
					.setMacro(TARGET_PLAYER, sender)
					.send();
		}
		else {
			// send message when giving to self
			Message.create(sender, COMMAND_SUCCESS_GIVE_SELF)
					.setMacro(ITEM_QUANTITY, quantity)
					.send();
		}

		// play sound to target player
		plugin.soundConfig.playSound(targetPlayer, SoundId.COMMAND_SUCCESS_GIVE_TARGET);
		return true;
	}


	/**
	 * Destroy command
	 *
	 * @param sender the command sender
	 * @param args   the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean destroyCommand(final CommandSender sender, final String[] args) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(args);

		// sender must be in game player
		if (!(sender instanceof Player)) {
			Message.create(sender, COMMAND_FAIL_DESTROY_CONSOLE).send();
			return true;
		}

		// if command sender does not have permission to destroy SpawnStars, output error message and return true
		if (!sender.hasPermission("spawnstar.destroy")) {
			Message.create(sender, COMMAND_FAIL_DESTROY_PERMISSION).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get subcommand from command arguments
		final String subcommand = args[0];

		// argument limits
		int maxArgs = 1;

		// check max arguments
		if (args.length > maxArgs) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_OVER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender, subcommand);
			return true;
		}

		// get in game player that issued command
		Player player = (Player) sender;

		// get item in player's hand
		ItemStack playerItem = player.getInventory().getItemInMainHand();

		// check that player held item is a spawnstar stack
		if (!SpawnStar.isItem(playerItem)) {
			Message.create(sender, COMMAND_FAIL_DESTROY_NO_MATCH).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		// get quantity of items in stack (to display in message)
		int quantity = playerItem.getAmount();

		// set quantity of items to zero
		playerItem.setAmount(0);

		// set player's item in hand to the zero quantity itemstack
		//noinspection deprecation
		player.getInventory().setItemInHand(playerItem);

		// send success message
		Message.create(sender, COMMAND_SUCCESS_DESTROY)
				.setMacro(ITEM_QUANTITY, quantity)
				.send();

		// play success sound
		plugin.soundConfig.playSound(player, SoundId.COMMAND_SUCCESS_DESTROY);

		// return true to prevent display of bukkit command usage string
		return true;
	}


	/**
	 * Display command usage
	 *
	 * @param sender        the command sender
	 * @param passedCommand the command for which to display usage string
	 */
	private void displayUsage(final CommandSender sender, final String passedCommand) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(passedCommand);

		String command = passedCommand;

		if (command.isEmpty() || command.equalsIgnoreCase("help")) {
			command = "all";
		}
		if ((command.equalsIgnoreCase("status")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("spawnstar.status")) {
			sender.sendMessage(usageColor + "/spawnstar status");
		}
		if ((command.equalsIgnoreCase("reload")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("spawnstar.reload")) {
			sender.sendMessage(usageColor + "/spawnstar reload");
		}
		if ((command.equalsIgnoreCase("give")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("spawnstar.give")) {
			sender.sendMessage(usageColor + "/spawnstar give <player> [quantity]");
		}
		if ((command.equalsIgnoreCase("destroy")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("spawnstar.destroy")) {
			sender.sendMessage(usageColor + "/spawnstar destroy");
		}
		if ((command.equalsIgnoreCase("help")
				|| command.equalsIgnoreCase("all"))
				&& sender.hasPermission("spawnstar.help")) {
			sender.sendMessage(usageColor + "/spawnstar help [command]");
		}
	}


	/**
	 * Display help message for commands
	 *
	 * @param sender the command sender
	 * @param args   the command arguments
	 * @return always returns {@code true}, to prevent display of bukkit usage message
	 */
	private boolean helpCommand(final CommandSender sender, final String[] args) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(args);

		// if command sender does not have permission to display help, output error message and return true
		if (!sender.hasPermission("spawnstar.help")) {
			Message.create(sender, COMMAND_FAIL_HELP_PERMISSION).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			return true;
		}

		String command = "help";

		if (args.length > 1) {
			command = args[1];
		}

		String helpMessage = "That is not a valid command.";

		if (command.equalsIgnoreCase("status")) {
			helpMessage = "Displays current configuration settings.";
		}
		if (command.equalsIgnoreCase("reload")) {
			helpMessage = "Reloads the configuration without needing to restart the server.";
		}
		if (command.equalsIgnoreCase("give")) {
			helpMessage = "Gives a SpawnStar to a player.";
		}
		if (command.equalsIgnoreCase("destroy")) {
			helpMessage = "Destroys the stack of SpawnStars you are holding.";
		}
		if (command.equalsIgnoreCase("help")) {
			helpMessage = "Displays help for SpawnStar commands.";
		}
		sender.sendMessage(helpColor + helpMessage);
		displayUsage(sender, command);
		return true;
	}


	/**
	 * Match online player; sends appropriate message for offline or unknown players
	 *
	 * @param sender the command sender
	 * @param targetPlayerName the player name to match
	 *
	 * @return Player - a matching player object, or null if no match
	 */
	private Player matchPlayer(final CommandSender sender, final String targetPlayerName) {

		// check for null parameters
		Objects.requireNonNull(sender);
		Objects.requireNonNull(targetPlayerName);

		Player targetPlayer;

		// check exact match first
		targetPlayer = plugin.getServer().getPlayer(targetPlayerName);

		// if no match, try substring match
		if (targetPlayer == null) {
			List<Player> playerList = plugin.getServer().matchPlayer(targetPlayerName);

			// if only one matching player, use it, otherwise send error message (no match or more than 1 match)
			if (playerList.size() == 1) {
				targetPlayer = playerList.get(0);
			}
		}

		// if match found, return target player object
		if (targetPlayer != null) {
			return targetPlayer;
		}

		// check if name matches known offline player
		HashSet<OfflinePlayer> matchedPlayers = new HashSet<>();
		for (OfflinePlayer offlinePlayer : plugin.getServer().getOfflinePlayers()) {
			if (targetPlayerName.equalsIgnoreCase(offlinePlayer.getName())) {
				matchedPlayers.add(offlinePlayer);
			}
		}
		if (matchedPlayers.isEmpty()) {
			Message.create(sender, COMMAND_FAIL_GIVE_PLAYER_NOT_FOUND).send();
		}
		else {
			Message.create(sender, COMMAND_FAIL_GIVE_PLAYER_NOT_ONLINE).send();
		}
		plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
		return null;
	}


	/**
	 * Match list of subcommands for which player has permission
	 *
	 * @param sender the command sender (player) to check for permission
	 * @param arg the partial subcommand string to match
	 * @return List of String - matching subcommands
	 */
	List<String> matchSubcommand(final CommandSender sender, final String arg) {
		List<String> returnList = new ArrayList<>();
		for (String subcommand : SUBCOMMANDS) {
			if (sender.hasPermission("spawnstar." + subcommand)
					&& subcommand.toLowerCase().startsWith(arg.toLowerCase())) {
				returnList.add(subcommand);
			}
		}
		return returnList;
	}

}
