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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public final class SingleDepositMode implements DepositMode {

  @Override
  public void deposit(DepositModule module, PlayerInteractEvent event, Inventory player, Inventory chest) {
    final int slot = event.getPlayer().getInventory().getHeldItemSlot();
    final ItemStack item = event.getItem().clone();

    if (!event.getPlayer().getInventory().getItem(slot).equals(event.getItem())) {
      module.getLogger().warning("Something unexpected happened: deposit item on wrong slot");
      return;
    }

    event.getPlayer().getInventory().clear(slot);
    Map<Integer, ItemStack> rejected = chest.addItem(item);

    if (rejected != null && !rejected.isEmpty()) {

      if (rejected.size() == 1) {
        event.getPlayer().getInventory().setItem(slot, rejected.get(0));
        return;
      }

      // This should not happen
      event.getPlayer().getInventory().addItem(rejected.values().toArray(new ItemStack[0]));
    }
  }
}
