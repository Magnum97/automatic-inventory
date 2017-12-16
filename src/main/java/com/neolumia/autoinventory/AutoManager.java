package com.neolumia.autoinventory;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public final class AutoManager implements Listener {

  private static final int PLAYER_INVENTORY_START = 9;
  private static final int PLAYER_INVENTORY_END = 36;

  @EventHandler(priority = EventPriority.HIGH)
  public void onClose(InventoryCloseEvent event) {
    ifPlayerInventory(event.getView().getBottomInventory(), inventory -> {
      sort(inventory, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END);
    });
  }

  private void sort(Inventory inventory, int from, int to) {
    final List<ItemStack> contents = copyOf(inventory, from, to);
    contents.removeIf(Objects::isNull);
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
        inventory.clear(i + from);
        continue;
      }
      inventory.setItem(i + from, contents.get(i));
    }
  }

  private List<ItemStack> copyOf(Inventory inventory, int from, int to) {
    return new LinkedList<>(Arrays.asList(Arrays.copyOfRange(inventory.getContents(), from, to)));
  }

  private boolean canMerge(ItemStack to, ItemStack from) {
    return to.isSimilar(from) && to.getAmount() < from.getMaxStackSize();
  }

  private void ifPlayerInventory(Inventory inventory, Consumer<Inventory> consumer) {
    if (inventory != null && inventory.getHolder() instanceof Player) {
      if (inventory.getType() == InventoryType.PLAYER) {
        consumer.accept(inventory);
      }
    }
  }
}
