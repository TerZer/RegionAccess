package me.itsadrift.regionaccess;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.itsadrift.regionaccess.manager.AccessRegion;
import me.itsadrift.regionaccess.utils.HexUtils;
import me.itsadrift.regionaccess.utils.Pair;
import me.itsadrift.regionaccess.utils.WGUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

public class RegionAccessListener implements Listener {

    private RegionAccess main;
    public RegionAccessListener(RegionAccess main) {
        this.main = main;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getPlayer().hasPermission("regionaccess.bypass"))
            return;

        if (e.getTo().getBlockX() == e.getFrom().getBlockX()
                && e.getTo().getBlockZ() == e.getFrom().getBlockZ()
                && e.getTo().getBlockY() == e.getFrom().getBlockY())
            return; // has not moved a full block

        if (!regionExistsAt(e.getTo()))
            return;

        Pair<Boolean, AccessRegion> result = canAccessLocation(e.getPlayer(), e.getTo());
        if (!result.getKey()) {
            e.setCancelled(true);

            // Creates a Vector (which can be used as a direction) of point A, to point B
            Vector v = e.getTo().getBlock().getLocation().subtract(e.getFrom().getBlock().getLocation()).toVector();
            e.getPlayer().setVelocity(v.multiply(-0.5).setY(0.25)); // Bounce the player back

            if (!result.getValue().getDenyMessage().equals("disable"))
                e.getPlayer().sendMessage(HexUtils.colour(result.getValue().getDenyMessage()));
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        if (e.getPlayer().hasPermission("regionaccess.bypass"))
            return;

        if (!regionExistsAt(e.getTo()))
            return;

        Pair<Boolean, AccessRegion> result = canAccessLocation(e.getPlayer(), e.getTo());
        if (!result.getKey()) {
            e.setCancelled(true);

            if (!result.getValue().getDenyMessage().equals("disable"))
                e.getPlayer().sendMessage(HexUtils.colour(result.getValue().getDenyMessage()));
        }
    }

    private boolean regionExistsAt(Location location) {
        return WGUtils.getRegions(location).size() > 0;
    }

    private Pair<Boolean, AccessRegion> canAccessLocation(Player player, Location location) {
        for (ProtectedRegion pr : WGUtils.getRegions(location)) {
            AccessRegion region = main.getRegionManager().getRegion(pr.getId());
            if (region == null)
                continue;

            return new Pair<>(main.getRegionManager().canAccess(player, region, pr), region);
        }

        return new Pair<>(false, null);
    }

}
