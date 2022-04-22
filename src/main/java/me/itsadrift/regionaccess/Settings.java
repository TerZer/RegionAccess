package me.itsadrift.regionaccess;

import me.itsadrift.regionaccess.utils.HexUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

public class Settings {

    public static boolean DEFAULT_MEMBER_ACCESS = true;
    public static String DEFAULT_ACCESS_DENIED = "&c[RegionAccess] &7You do not have permission to enter this region!";

    public static String CONFIG_RELOADED = "&c[RegionAccess] &7Config reloaded.";

    public static String NO_PERMISSION = "&c[RegionAccess] &7No Permission!";
    public static String INVALID_ARGUMENTS = "&c[RegionAccess] &7Invalid Arguments. See &c/regionaccess help";
    public static String INVALID_REGION = "&c[RegionAccess] &7No region found with the name &c{REGION}";

    public static String REGION_INFO = "----------[&c&l {REGION} Info &8&l]---------- \n &cPermission: &7{PERMISSION} \n &cCustom message: &7{DENY_MESSAGE} \n &cMember Bypass: &7{MEMBER_BYPASS}";

    public static String REGION_SET = "&c[RegionAccess] &7You have set the permission &c{PERMISSION} &7for the region &c{REGION}";
    public static String REGION_REMOVED = "&c[RegionAccess] &7You have removed the permission for the region &c{REGION}";
    public static String REGION_EDIT = "&c[RegionAccess] &7You have set &c{SETTING} &7to &c{VALUE}";

    public static String DENY_MESSAGE_DISABLED = "&c[RegionAccess] &7You have &cdisabled &7the deny message for &c{REGION}";

    public static void reloadMessages(RegionAccess regionAccess) {
        FileConfiguration config = regionAccess.getConfig();

        DEFAULT_MEMBER_ACCESS = config.getBoolean("defaultMemberAccess");
        DEFAULT_ACCESS_DENIED = HexUtils.colour(config.getString("defaultAccessDenied"));

        CONFIG_RELOADED = HexUtils.colour(config.getString("messages.configReloaded"));

        NO_PERMISSION = HexUtils.colour(config.getString("messages.noPermission"));
        INVALID_ARGUMENTS = HexUtils.colour(config.getString("messages.invalidArguments"));
        INVALID_REGION = HexUtils.colour(config.getString("messages.invalidRegion"));

        REGION_INFO = HexUtils.colour(config.getString("messages.regionInfo"));

        REGION_SET = HexUtils.colour(config.getString("messages.regionSet"));
        REGION_REMOVED = HexUtils.colour(config.getString("messages.regionRemoved"));
        REGION_EDIT = HexUtils.colour(config.getString("messages.regionEdit"));

        DENY_MESSAGE_DISABLED = HexUtils.colour(config.getString("messages.denyMessageDisabled"));
    }

    public static String[] HELP = new String[]{
            HexUtils.colour("&8&l----------[&c&l RegionAccess Help &8&l]----------"),
            HexUtils.colour("&c/regionaccess help &8- &7Displays this message"),
            HexUtils.colour("&c/regionaccess reload &8- &7Reload the config file"),
            HexUtils.colour("&c/regionaccess set <region> <permission> &8- &7Sets an access region"),
            HexUtils.colour("&c/regionaccess remove <region> &8- &7Removes an access region"),
            HexUtils.colour("&c/regionaccess edit <region> denyMessage <Message...>&8- &7Sets a custom deny message. Type &cdisable &7to disable the message."),
            HexUtils.colour("&c/regionaccess edit <region> memberBypass <true/false> &8- &7Allows (or disallows) members of the region to enter regardless of their permissions. DEFAULT: &ctrue"),
    };
}
