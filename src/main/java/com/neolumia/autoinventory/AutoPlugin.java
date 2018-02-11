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

import com.neolumia.autoinventory.blacklist.BlacklistHandler;
import com.neolumia.autoinventory.modules.Sorting;
import com.neolumia.material.plugin.NeoJavaPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.logging.Level;

public final class AutoPlugin extends NeoJavaPlugin {

  private BlacklistHandler blacklist;
  private Sorting sorting;

  @Override
  protected void enable() {
    blacklist = register(new BlacklistHandler(this));
    sorting = register(new Sorting(this));
  }

  @Override
  protected void disable() {
    if (blacklist != null) {
      try {
        blacklist.saveBlacklist();
      } catch (Exception ex) {
        getLogger().log(Level.WARNING, "Blacklist could not be saved", ex);
      }
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (command.getName().equalsIgnoreCase(getName())) {

      if (args.length == 0) {
        getServer().dispatchCommand(sender, "version " + getName());
        return true;
      }

      if (args[0].equalsIgnoreCase("blacklist")) {
        if (!(sender instanceof Player)) {
          sender.sendMessage(ChatColor.RED + "This command is only for players.");
          return true;
        }
        blacklist.onCommand(sender, command, label, Arrays.copyOfRange(args, 1, args.length));
        return true;
      }

      sender.sendMessage("/autoinv (help:blacklist)");
      return true;
    }
    return false;
  }

  public Sorting getSorting() {
    return sorting;
  }

  public BlacklistHandler getBlacklistHandler() {
    return blacklist;
  }
}
