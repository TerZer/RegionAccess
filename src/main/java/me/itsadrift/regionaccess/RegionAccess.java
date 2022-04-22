package me.itsadrift.regionaccess;

import me.itsadrift.regionaccess.manager.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class RegionAccess extends JavaPlugin implements Listener {

    private RegionManager regionManager;

    @Override
    public void onEnable() {
        // Plugin startup logic

        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        Settings.reloadMessages(this);

        regionManager = new RegionManager(this);
        regionManager.load();

        getCommand("regionaccess").setExecutor(new RegionAccessCommand(this));
        Bukkit.getPluginManager().registerEvents(new RegionAccessListener(this), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public RegionManager getRegionManager() {
        return regionManager;
    }
}
