package com.neolumia.autoinventory;

import org.bukkit.plugin.java.JavaPlugin;

public final class AutoPlugin extends JavaPlugin {

  private final AutoManager manager = new AutoManager();

  @Override
  public void onEnable() {
    getServer().getPluginManager().registerEvents(manager, this);
    getLogger().info("Plugin enabled");
  }
}
