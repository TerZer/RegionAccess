package me.itsadrift.regionaccess.manager;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.itsadrift.regionaccess.RegionAccess;
import me.itsadrift.regionaccess.Settings;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class RegionManager {

    // regionName, Region
    private Map<String, AccessRegion> regions = new HashMap<>();

    private RegionAccess main;
    public RegionManager(RegionAccess main) {
        this.main = main;
    }

    public void load() {
        if (main.getConfig().getConfigurationSection("data") == null)
            return;

        for (String key : main.getConfig().getConfigurationSection("data").getKeys(false)) {
            String path = "data." + key + ".";

            AccessRegion region = new AccessRegion(
                    key,
                    main.getConfig().getString(path + "permission"),
                    main.getConfig().getString(path + "denyMessage", Settings.DEFAULT_ACCESS_DENIED),
                    main.getConfig().getBoolean(path + "memberBypass", Settings.DEFAULT_MEMBER_ACCESS));

            regions.put(key, region);
        }
    }

    public void createRegion(String name, String permission) {
        AccessRegion region = new AccessRegion(name, permission, Settings.DEFAULT_ACCESS_DENIED, Settings.DEFAULT_MEMBER_ACCESS);
        regions.put(name, region);
        region.save(main);
    }

    public void remove(String regionName) {
        regions.remove(regionName);
        main.getConfig().set("data." + regionName, null);
        main.saveConfig();
    }

    public void setDenyMessage(String regionName, String denyMessage) {
        regions.get(regionName).setDenyMessage(denyMessage);
        regions.get(regionName).save(main);
    }

    public void setMemberBypass(String regionName, boolean memberBypass) {
        regions.get(regionName).setMemberBypass(memberBypass);
        regions.get(regionName).save(main);
    }

    public boolean canAccess(Player player, AccessRegion region, ProtectedRegion protectedRegion) {
        LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
        if (region.isMemberBypass() && protectedRegion.isMember(localPlayer))
            return true;

        return player.hasPermission(region.getPermission());
    }

    public AccessRegion getRegion(String regionName) {
        return regions.get(regionName);
    }

    public boolean isAccessRegion(String regionName) {
        return regions.containsKey(regionName);
    }


}
