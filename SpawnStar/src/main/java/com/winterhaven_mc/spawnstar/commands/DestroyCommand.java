package com.winterhaven_mc.spawnstar.commands;

import com.winterhaven_mc.spawnstar.PluginMain;
import com.winterhaven_mc.spawnstar.messages.Message;
import com.winterhaven_mc.spawnstar.sounds.SoundId;
import com.winterhaven_mc.spawnstar.util.SpawnStar;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Objects;

import static com.winterhaven_mc.spawnstar.messages.Macro.ITEM_QUANTITY;
import static com.winterhaven_mc.spawnstar.messages.MessageId.*;


public class DestroyCommand extends AbstractCommand {

	private final PluginMain plugin;


	DestroyCommand(final PluginMain plugin) {
		this.plugin = Objects.requireNonNull(plugin);
		this.setName("destroy");
		this.setUsage("/spawnstar destroy");
		this.setDescription(COMMAND_HELP_DESTROY);
		this.setMaxArgs(0);
	}


	@Override
	public boolean onCommand(final CommandSender sender, final List<String> args) {

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

		// check max arguments
		if (args.size() > getMaxArgs()) {
			Message.create(sender, COMMAND_FAIL_ARGS_COUNT_OVER).send();
			plugin.soundConfig.playSound(sender, SoundId.COMMAND_FAIL);
			displayUsage(sender);
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

}
