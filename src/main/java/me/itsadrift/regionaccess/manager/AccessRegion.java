package me.itsadrift.regionaccess.manager;

import me.itsadrift.regionaccess.RegionAccess;
import me.itsadrift.regionaccess.Settings;

public class AccessRegion {

    private String regionName;
    private String permission;
    private String denyMessage;
    private boolean memberBypass;

    public AccessRegion(String regionName, String permission, String denyMessage, boolean memberBypass) {
        this.regionName = regionName;
        this.permission = permission;
        this.denyMessage = denyMessage;
        this.memberBypass = memberBypass;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getPermission() {
        return permission;
    }

    public String getDenyMessage() {
        return denyMessage;
    }

    public boolean isMemberBypass() {
        return memberBypass;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public void setDenyMessage(String denyMessage) {
        this.denyMessage = denyMessage;
    }

    public void setMemberBypass(boolean memberBypass) {
        this.memberBypass = memberBypass;
    }

    public void save(RegionAccess regionAccess) {
        String path = "data." + regionName + ".";

        regionAccess.getConfig().set(path + "permission", getPermission());
        if (!getDenyMessage().equals(Settings.DEFAULT_ACCESS_DENIED))
            regionAccess.getConfig().set(path + "denyMessage", getDenyMessage());
        regionAccess.getConfig().set(path + "memberBypass", isMemberBypass());

        regionAccess.saveConfig();
    }
}
