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
import com.neolumia.autoinventory.SortModes;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Sorting extends Module {

  private static final int PLAYER_INVENTORY_START = 9;
  private static final int PLAYER_INVENTORY_END = 36;
  private static final String PERMISSION = "automaticinventory.sort";

  public Sorting(AutoPlugin plugin) {
    super(plugin);
  }

  @EventHandler(priority = EventPriority.HIGH)
  public void onClose(InventoryCloseEvent event) {
    if (!(event.getInventory().getHolder() instanceof Player)) {
      return;
    }
    if (event.getInventory().getType() != InventoryType.PLAYER) {
      return;
    }
    if (canSort((Player) event.getPlayer(), event.getInventory().getType())) {
      sort(event.getInventory(), PLAYER_INVENTORY_START, PLAYER_INVENTORY_END);
    }
  }

  private void sort(Inventory inventory, int from, int to) {
    final Map<Integer, ItemStack> blacklisted = new HashMap<>();
    final List<ItemStack> contents = copyOf(inventory, from, to);
    for (int i = 0; i < contents.size(); i++) {
      ItemStack item = contents.get(i);
      if (item != null && getBlacklist().contains(item)) {
        blacklisted.put(i, item);
      }
    }
    contents.removeIf(Objects::isNull);
    contents.removeIf(i -> getBlacklist().contains(i));
    contents.sort(SortModes.DEFAULT);
    ItemStack previous = null;
    final Iterator<ItemStack> iterator = contents.iterator();
    while (iterator.hasNext()) {
      final ItemStack item = iterator.next();
      if (previous != null && canMerge(previous, item)) {
        int count = Math.min(item.getAmount(), previous.getMaxStackSize() - previous.getAmount());
        if (count > 0) {
          previous.setAmount(previous.getAmount() + count);
          item.setAmount(item.getAmount() - count);
          if (item.getAmount() <= 0) {
            iterator.remove();
            continue;
          }
        }
      }
      previous = item;
    }
    for (int i = 0; i < to; i++) {
      if (i >= contents.size()) {
        contents.add(null);
      }
    }
    for (Map.Entry<Integer, ItemStack> item : blacklisted.entrySet()) {
      contents.set(item.getKey(), item.getValue());
    }
    for (int i = 0; i < to; i++) {
      inventory.setItem(i + from, contents.get(i));
    }
  }

  private List<ItemStack> copyOf(Inventory inventory, int from, int to) {
    return new LinkedList<>(Arrays.asList(Arrays.copyOfRange(inventory.getContents(), from, to)));
  }

  private boolean canSort(Player player, InventoryType type) {
    return (player.hasPermission(PERMISSION) || player.hasPermission(PERMISSION + "." + type.name())) && getConfig().isSortingEnabled(type);
  }

  private boolean canMerge(ItemStack to, ItemStack from) {
    return !getBlacklist().contains(to) && !getBlacklist().contains(from) && to.isSimilar(from) && to.getAmount() < from.getMaxStackSize();
  }
}
