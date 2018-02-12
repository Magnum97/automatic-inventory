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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.neolumia.autoinventory.AutoPlugin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemBreakEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class Refill extends Module {

  private static final int OFF_HAND_SLOT = 40;
  private static final long REFILL_DELAY = 1L;

  public Refill(AutoPlugin plugin) {
    super(plugin);
  }

  /*
   *  Some interesting facts:
   *    - Eating calls PlayerInteractEvent, but event.getItem() is null (?!)
   *    - Throwing items calls PlayerInteractEvent, but it is directly cancelled (?!)
   */

  @EventHandler(priority = EventPriority.MONITOR)
  public void onInteract(PlayerInteractEvent event) {
    if (event.isAsynchronous() || event.getItem() == null || event.getItem().getType().isEdible()) {
      return;
    }
    if (!isRightClick(event.getAction())) {
      return;
    }
    final ItemStack item = event.getItem().clone();
    final int slot = findHoldingSlot(event.getPlayer(), event.getHand());
    if (slot != -1) {
      run(() -> refill(event.getPlayer().getInventory(), item, slot), REFILL_DELAY);
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onConsume(PlayerItemConsumeEvent event) {
    System.out.println("CONSUME: Starting..");
    if (!event.isCancelled() && !event.isAsynchronous() && event.getItem().getAmount() <= 1) {
      System.out.println("CONSUME: Starting.. (2)");
      int slot = findHoldingSlot(event.getPlayer(), event.getItem());
      if (slot != -1) {
        System.out.println("CONSUME: Refilling..");
        run(() -> refill(event.getPlayer().getInventory(), event.getItem(), slot), null);
      }
    }
  }

  @EventHandler(priority = EventPriority.MONITOR)
  public void onItemBreak(PlayerItemBreakEvent event) {
    if (!event.isAsynchronous()) {
      System.out.println("BREAK: Starting..");
      final ItemStack item = event.getBrokenItem().clone();
      int slot = findHoldingSlot(event.getPlayer(), event.getBrokenItem());
      if (slot != -1) {
        System.out.println("BREAK: Refilling..");
        run(() -> refill(event.getPlayer().getInventory(), item, slot), null);
      }
    }
  }

  @SuppressWarnings("deprecation")
  private void refill(Inventory inventory, ItemStack item, int slot) {
    checkNotNull(item, "Item cannot be null");
    checkArgument(slot >= 0 && slot < inventory.getSize(), "Invalid slot provided: " + slot);
    if (inventory.getItem(slot) != null) {
      System.out.println("REFILL: Still there");
      return;
    }
    ItemStack best = null;
    Integer bestSlot = null;
    int bestPoints = -1;
    for (int i = 0; i < inventory.getSize(); i++) {
      final ItemStack search = inventory.getItem(i);
      if (i == slot || search == null || search.getType() == Material.AIR) {
        continue;
      }
      final int points = shouldBeBest(item, search);
      System.out.println("This: " + points + ", best: " + bestPoints);
      if (points > 0 && points >= bestPoints) {
        if (points == bestPoints && search.getAmount() < best.getAmount()) {
          continue;
        }
        System.out.println("Switching..");
        best = search;
        bestSlot = i;
        bestPoints = points;
      }
    }
    if (best != null) {
      System.out.println("Refilled.");
      inventory.clear(bestSlot);
      inventory.setItem(slot, best);
    } else {
      System.out.println("REFILL: Nothing found, aborting");
    }
  }

  private int findHoldingSlot(Player player, EquipmentSlot slot) {
    if (slot == EquipmentSlot.HAND) {
      return player.getInventory().getHeldItemSlot();
    }
    if (slot == EquipmentSlot.OFF_HAND) {
      return OFF_HAND_SLOT;
    }
    return -1;
  }

  private int findHoldingSlot(Player player, ItemStack item) {
    if (player.getInventory().getItemInMainHand().equals(item)) {
      return player.getInventory().getHeldItemSlot();
    }
    if (player.getInventory().getItemInOffHand().equals(item)) {
      return OFF_HAND_SLOT;
    }
    return -1;
  }

  private boolean isRightClick(Action action) {
    return action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK;
  }

  private void run(Runnable runnable, Long delay) {
    if (delay == null) {
      getPlugin().getServer().getScheduler().runTask(getPlugin(), runnable);
    } else {
      getPlugin().getServer().getScheduler().runTaskLater(getPlugin(), runnable, delay);
    }
  }

  @SuppressWarnings("deprecation")
  private int shouldBeBest(ItemStack current, ItemStack item2) {
    int points = 0;
    if (current.getType() == item2.getType()) {
      points++;
      if (current.getData().getData() == item2.getData().getData()) {
        points++;
        if (current.getMaxStackSize() == item2.getMaxStackSize()) {
          points++;
        }
      }
    }
    return points;
  }
}
