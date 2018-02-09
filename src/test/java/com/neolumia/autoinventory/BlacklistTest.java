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

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public final class BlacklistTest {

  private Blacklist blacklist;

  @Before
  public void up() {
    blacklist = new Blacklist();
  }

  @After
  public void down() {
    blacklist = null;
  }

  @Test
  public void conditionMaterial() {

    final Blacklist.Item dirt = new Blacklist.Item();
    dirt.put(Blacklist.ConditionType.MATERIAL, "DIRT");

    final Blacklist.Item diamond = new Blacklist.Item();
    diamond.put(Blacklist.ConditionType.MATERIAL, "DIAMOND");

    assertTrue(dirt.applies(new ItemStack(Material.DIRT)));
    assertTrue(diamond.applies(new ItemStack(Material.DIAMOND)));
    assertFalse(dirt.applies(new ItemStack(Material.GOLD_NUGGET)));

    blacklist.add(dirt);
    blacklist.add(diamond);

    assertTrue(blacklist.contains(new ItemStack(Material.DIRT)));
    assertTrue(blacklist.contains(new ItemStack(Material.DIAMOND)));
    assertFalse(blacklist.contains(new ItemStack(Material.GOLD_NUGGET)));
  }

  @Test
  public void conditionAmount() {

    final Blacklist.Item item = new Blacklist.Item();
    item.put(Blacklist.ConditionType.AMOUNT, "1337");

    assertTrue(item.applies(new ItemStack(Material.DIRT, 1337)));
    assertFalse(item.applies(new ItemStack(Material.DIRT)));

    blacklist.add(item);

    assertTrue(blacklist.contains(new ItemStack(Material.DIRT, 1337)));
    assertFalse(blacklist.contains(new ItemStack(Material.DIRT)));
  }

  @Test
  public void conditionMixed() {

    // Gold Nugget with amount 4
    final Blacklist.Item nugget = new Blacklist.Item();
    nugget.put(Blacklist.ConditionType.MATERIAL, "GOLD_NUGGET");
    nugget.put(Blacklist.ConditionType.AMOUNT, "4");

    blacklist.add(nugget);
    assertTrue(blacklist.contains(new ItemStack(Material.GOLD_NUGGET, 4)));
    assertFalse(blacklist.contains(new ItemStack(Material.GOLD_NUGGET)));
    assertFalse(blacklist.contains(new ItemStack(Material.DIAMOND, 4)));
  }
}
