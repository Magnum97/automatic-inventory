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

package com.neolumia.autoinventory.blacklist;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalCause;
import com.google.common.cache.RemovalListener;
import com.neolumia.autoinventory.AutoPlugin;
import com.neolumia.material.config.GsonConfig;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

public final class BlacklistHandler implements Listener, CommandExecutor {

  private final Cache<UUID, List<Conditions>> adding = CacheBuilder.newBuilder()
    .removalListener((RemovalListener<UUID, List<Conditions>>) n -> {
      if (n.getCause() == RemovalCause.EXPIRED) {
        final Player player = Bukkit.getPlayer(n.getKey());
        if (player != null) {
          player.sendMessage(ChatColor.RED + "Time expired. Please use '/autoinv blacklist' again.");
        }
      }
    }).expireAfterWrite(15, SECONDS).build();

  private GsonConfig<Blacklist> blacklist;

  public BlacklistHandler(AutoPlugin plugin) {
    blacklist = new GsonConfig<>(plugin.getRoot().resolve("blacklist.json"), Blacklist.class);
  }

  @EventHandler(priority = EventPriority.HIGHEST)
  public void onClick(InventoryClickEvent event) {
    final List<Conditions> conditions = adding.getIfPresent(event.getWhoClicked().getUniqueId());
    if (conditions != null) {
      blacklist.getConfig().add(new BlacklistItem(conditions.stream().collect(
        Collectors.toMap(Function.identity(), (c) -> c.retrieve(event.getCurrentItem())))));
      adding.invalidate(event.getWhoClicked().getUniqueId());
      event.getWhoClicked().sendMessage(ChatColor.GREEN + "Item has been added.");
    }
  }

  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    if (args.length > 0) {

      if (args[0].equalsIgnoreCase("add")) {
        final List<Conditions> conditions = new ArrayList<>(3);
        for (String condition : Arrays.copyOfRange(args, 1, args.length)) {
          try {
            conditions.add(Conditions.valueOf(condition.toUpperCase()));
          } catch (Exception ex) {
            sender.sendMessage("Error: " + condition + " is not a valid condition");
            return true;
          }
        }
        if (conditions.isEmpty()) {
          sender.sendMessage(ChatColor.RED + "Error: You have to provide at least one condition.");
          return true;
        }
        adding.put(((Player) sender).getUniqueId(), conditions);
        sender.sendMessage(ChatColor.GREEN + "Please click the item you want to add.");
        return true;
      }

      if (args[0].equalsIgnoreCase("cancel")) {
        adding.invalidate(((Player) sender).getUniqueId());
        return true;
      }
    }
    sender.sendMessage(ChatColor.RED + "Syntax: /autoinv blacklist <add:cancel> [material, amount, name]");
    return true;
  }

  public void saveBlacklist() throws IOException, ObjectMappingException {
    blacklist.save();
  }

  public Blacklist getBlacklist() {
    return blacklist.getConfig();
  }
}
