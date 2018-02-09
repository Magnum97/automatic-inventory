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

package com.neolumia.autoinventory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

@ConfigSerializable
public final class Blacklist {

  @Setting(value = "items")
  private List<Item> items = Lists.newArrayList();

  public boolean contains(ItemStack item) {
    for (Item check : items) {
      if (check.applies(item)) {
        return true;
      }
    }
    return false;
  }

  public void add(Item item) {
    items.add(item);
  }

  @ConfigSerializable
  public static class Item {

    @Setting("conditions")
    private Map<ConditionType, String> conditions = Maps.newHashMap();

    boolean applies(ItemStack item) {
      for (Map.Entry<ConditionType, String> entry : conditions.entrySet()) {
        if (!doesApply(entry.getKey(), entry.getValue(), item)) {
          return false;
        }
      }
      return true;
    }

    private boolean doesApply(ConditionType type, String value, ItemStack item) {
      switch (type) {
        case NAME:
          if (!item.getItemMeta().getDisplayName().equalsIgnoreCase(value)) {
            return false;
          }
          break;
        case AMOUNT:
          if (item.getAmount() != Integer.valueOf(value)) {
            return false;
          }
          break;
        case MATERIAL:
          if (!item.getType().name().equalsIgnoreCase(value)) {
            return false;
          }
          break;
      }
      return true;
    }

    public void put(ConditionType type, String value) {
      conditions.put(type, value);
    }
  }

  public enum ConditionType {

    MATERIAL,
    AMOUNT,
    NAME
  }
}
