package me.itsadrift.regionaccess.utils;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.internal.platform.WorldGuardPlatform;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;

public class WGUtils {

    public static boolean regionExists(World world, String regionName) {
        WorldGuardPlatform worldGuardPlatform = WorldGuard.getInstance().getPlatform();

        RegionContainer rgContainer = worldGuardPlatform.getRegionContainer();
        RegionManager regionManager = rgContainer.get(BukkitAdapter.adapt(world));

        return regionManager.getRegion(regionName) != null;
    }

    public static Set<ProtectedRegion> getRegions(Location location) {
        WorldGuardPlatform worldGuardPlatform = WorldGuard.getInstance().getPlatform();

        RegionContainer rgContainer = worldGuardPlatform.getRegionContainer();
        RegionQuery buildQuery = rgContainer.createQuery();
        com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(location);

        ApplicableRegionSet set = buildQuery.getApplicableRegions(loc);

        return (set.getRegions());
    }

}
