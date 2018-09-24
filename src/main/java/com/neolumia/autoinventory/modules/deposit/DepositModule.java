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

package com.neolumia.autoinventory.modules.deposit;

import com.neolumia.autoinventory.AutoPlugin;
import com.neolumia.autoinventory.modules.Module;
import com.neolumia.autoinventory.util.FakePlayerInteractEvent;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public final class DepositModule extends Module<DepositMode> {

  public DepositModule(AutoPlugin plugin) {
    super(plugin);
    register("single", DepositMode.SINGLE);
    register("all", DepositMode.ALL);
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onInteract(PlayerInteractEvent event) {
    if (event.isAsynchronous() || event.isCancelled() || event instanceof FakePlayerInteractEvent) {
      return;
    }
    if (event.getAction() != Action.LEFT_CLICK_BLOCK || !event.getPlayer().isSneaking() || event.getItem() == null) {
      return;
    }
    if (isChest(event.getClickedBlock())) {

      // Is the chest blocked?
      if (event.getClickedBlock().getRelative(BlockFace.UP).getType().isOccluding()) {
        return;
      }

      // Check if the player can open the chest, do not inform the player here
      final FakePlayerInteractEvent testPermission = plugin.call(new FakePlayerInteractEvent(event));
      if (testPermission.isCancelled()) {
        return;
      }

      final Inventory inventory = ((InventoryHolder) event.getClickedBlock().getState()).getInventory();

      // Deposit
      DepositMode.SINGLE.deposit(this, event, event.getPlayer().getInventory(), inventory);
    }
  }

  private static boolean isChest(Block block) {
    return block != null && block.getState() instanceof InventoryHolder;
  }
}
