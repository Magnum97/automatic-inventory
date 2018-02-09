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

import com.neolumia.material.config.GsonConfig;
import com.neolumia.material.plugin.NeoJavaPlugin;
import org.bukkit.Material;

import java.util.logging.Level;

public final class AutoPlugin extends NeoJavaPlugin {

  private GsonConfig<Blacklist> blacklist;
  private AutoManager manager;

  @Override
  protected void enable() {
    blacklist = new GsonConfig<>(getRoot().resolve("blacklist.json"), Blacklist.class);
    manager = register(new AutoManager(blacklist.getConfig()));
  }

  @Override
  protected void disable() {
    if (blacklist != null) {
      try {
        blacklist.save();
      } catch (Exception ex) {
        getLogger().log(Level.WARNING, "Blacklist could not be saved", ex);
      }
    }
  }

  public GsonConfig<Blacklist> getBlacklist() {
    return blacklist;
  }

  public AutoManager getManager() {
    return manager;
  }
}
