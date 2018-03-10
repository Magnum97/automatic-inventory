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

import com.neolumia.autoinventory.modules.Deposit;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import org.bukkit.event.inventory.InventoryType;
import java.util.HashMap;
import java.util.Map;

@ConfigSerializable
public final class AutoConfig {

  @Setting("debug")
  public boolean debug = false;

  @Setting(value = "sort-enabled", comment = "On what inventory types should AutoSort be allowed?")
  public Map<InventoryType, Boolean> sortEnabled = new HashMap<>();

  @Setting(value = "sort-default", comment = "On what inventory types should AutoSort be enabled by default?")
  public Map<InventoryType, Boolean> sortDefault = new HashMap<>();

  @Setting(value = "deposit-enabled", comment = "Should QuickDeposit be allowed on this server?")
  public Map<Deposit.Modes, Boolean> depositEnabled = new HashMap<>();

  @Setting(value = "deposit-default", comment = "What is the default QuickDeposit mode? (SINGLE/ALL)")
  public Deposit.Modes depositDefault = Deposit.Modes.SINGLE;

  @Setting(value = "refill-enabled", comment = "Should AutoRefill be allowed on this server? (true/false)")
  public boolean refillEnabled = true;

  @Setting(value = "refill-default", comment = "Should AutoRefill be enabled by default? (true/false)")
  public boolean refillDefault = true;

  public AutoConfig() {
    sortEnabled.putIfAbsent(InventoryType.CHEST, true);
    sortEnabled.putIfAbsent(InventoryType.SHULKER_BOX, true);
    sortEnabled.putIfAbsent(InventoryType.PLAYER, true);
    sortDefault.putIfAbsent(InventoryType.CHEST, true);
    sortDefault.putIfAbsent(InventoryType.SHULKER_BOX, true);
    sortDefault.putIfAbsent(InventoryType.PLAYER, true);
    depositEnabled.putIfAbsent(Deposit.Modes.SINGLE, true);
    depositEnabled.putIfAbsent(Deposit.Modes.ALL, true);
  }

  public boolean isSortingEnabled(InventoryType type) {
    return sortEnabled.containsKey(type) && sortEnabled.get(type);
  }
}
