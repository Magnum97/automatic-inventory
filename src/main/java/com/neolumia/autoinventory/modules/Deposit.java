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

package com.neolumia.autoinventory.modules;

import com.neolumia.autoinventory.AutoPlugin;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import java.util.Map;

public final class Deposit extends Module {

  public Deposit(AutoPlugin plugin) {
    super(plugin);
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
      final Inventory inventory = ((InventoryHolder) event.getClickedBlock().getState()).getInventory();

      // Check if the player can open the chest
      if (event.getClickedBlock().getRelative(BlockFace.UP).getType().isOccluding()) {
        return;
      }
      if (getPlugin().call(new FakePlayerInteractEvent(event)).isCancelled()) {
        return;
      }

      // Deposit
      Modes.ALL.deposit(event, event.getPlayer().getInventory(), inventory);
    }
  }

  private boolean isChest(Block block) {
    return block != null && block.getState() instanceof InventoryHolder;
  }

  public final class FakePlayerInteractEvent extends PlayerInteractEvent {

    FakePlayerInteractEvent(PlayerInteractEvent event) {
      super(
        event.getPlayer(), Action.RIGHT_CLICK_BLOCK,
        event.getItem(), event.getClickedBlock(),
        event.getBlockFace(), event.getHand()
      );
    }
  }

  public interface Mode {

    void deposit(PlayerInteractEvent event, Inventory player, Inventory chest);
  }

  public enum Modes implements Mode {

    OFF {
      @Override
      public void deposit(PlayerInteractEvent event, Inventory player, Inventory chest) {}
    },

    SINGLE {
      @Override
      public void deposit(PlayerInteractEvent event, Inventory player, Inventory chest) {
        final int slot = event.getPlayer().getInventory().getHeldItemSlot();
        final int amount = event.getItem().getAmount();

        event.getPlayer().getInventory().clear(slot);

        final Map<Integer, ItemStack> rejected = chest.addItem(event.getItem());
        if (rejected != null && !rejected.isEmpty()) {

          final ItemStack item = rejected.get(0);
          final int transferred = amount - item.getAmount();

          if (rejected.size() == 1) {
            event.getPlayer().getInventory().setItem(slot, rejected.get(0));
            return;
          }

          // This should not happen
          event.getPlayer().getInventory().addItem(rejected.values().toArray(new ItemStack[rejected.size()]));
        }
      }
    },

    ALL {
      @Override
      public void deposit(PlayerInteractEvent event, Inventory player, Inventory chest) {
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
  }
}
