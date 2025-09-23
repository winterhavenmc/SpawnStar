/*
 * Copyright (c) 2022 Tim Savage.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.winterhavenmc.spawnstar.util;

import com.winterhavenmc.spawnstar.SpawnStarPluginController;
import org.bukkit.inventory.ItemStack;

import java.util.*;


/**
 * Factory class for creating and testing SpawnStar item stacks
 */
public final class SpawnStarUtility
{
	private final SpawnStarPluginController plugin;


	public SpawnStarUtility(final SpawnStarPluginController plugin)
	{
		this.plugin = plugin;
	}


	/**
	 * Create a SpawnStar item stack of given quantity, with custom display name and lore
	 *
	 * @param passedQuantity number of SpawnStar items in newly created stack
	 * @return ItemStack of SpawnStar items
	 */
	public ItemStack create(final int passedQuantity)
	{
		int quantity = passedQuantity;
		quantity = Math.max(1, quantity);

		Optional<ItemStack> itemStack = plugin.messageBuilder.itemForge().createItem("SPAWNSTAR");
		if (itemStack.isPresent())
		{
			ItemStack returnItem = itemStack.get();
			quantity = Math.min(quantity, returnItem.getMaxStackSize());
			returnItem.setAmount(quantity);
			return returnItem;
		}
		else
		{
			return null;
		}
	}

}
