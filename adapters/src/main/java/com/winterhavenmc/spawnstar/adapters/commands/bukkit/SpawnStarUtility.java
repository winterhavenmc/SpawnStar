/*
 * Copyright (c) 2022-2025 Tim Savage.
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

package com.winterhavenmc.spawnstar.adapters.commands.bukkit;

import com.winterhavenmc.library.messagebuilder.MessageBuilder;
import com.winterhavenmc.library.messagebuilder.models.keys.ItemKey;
import com.winterhavenmc.library.messagebuilder.models.keys.ValidItemKey;
import org.bukkit.inventory.ItemStack;

import java.util.*;


/**
 * Factory class for creating and testing SpawnStar item stacks
 */
public final class SpawnStarUtility
{
	public static final String ITEM_KEY = "SPAWNSTAR";
	private final MessageBuilder messageBuilder;


	public SpawnStarUtility(final MessageBuilder messageBuilder)
	{
		this.messageBuilder = messageBuilder;
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


		ValidItemKey validItemKey = ItemKey.of(ITEM_KEY).isValid().orElseThrow();
		Optional<ItemStack> itemStack = messageBuilder.items().createItem(validItemKey);
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
