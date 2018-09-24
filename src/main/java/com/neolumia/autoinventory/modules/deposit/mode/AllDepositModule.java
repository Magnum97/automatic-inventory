/*
 * This file is part of AutomaticInventory, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2017-2018 Neolumia
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.neolumia.autoinventory.modules.deposit.mode;

import com.neolumia.autoinventory.modules.deposit.DepositMode;
import com.neolumia.autoinventory.modules.deposit.DepositModule;
import org.bukkit.Material;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

//TODO This is completely untested
public final class AllDepositModule implements DepositMode {

  @Override
  public void deposit(DepositModule module, PlayerInteractEvent event, Inventory player, Inventory chest) {
    int amount = 0;
    List<ItemStack> list = new LinkedList<>();
    for (int i = 9; i < 36; i++) {
      final ItemStack item = player.getItem(i);
      if (item != null && item.getType() != Material.AIR) {
        player.clear(i);
        list.add(item);
        amount += item.getAmount();
      }
    }
    Map<Integer, ItemStack> rejected = chest.addItem(list.toArray(new ItemStack[list.size()]));
    if (rejected != null && !rejected.isEmpty()) {
      player.addItem(rejected.values().toArray(new ItemStack[rejected.size()]));
    }
  }
}
