package me.itsadrift.regionaccess;

import com.google.common.base.Joiner;
import com.zachsthings.libcomponents.config.Setting;
import me.itsadrift.regionaccess.manager.AccessRegion;
import me.itsadrift.regionaccess.utils.HexUtils;
import me.itsadrift.regionaccess.utils.WGUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class RegionAccessCommand implements CommandExecutor, TabCompleter {

    private RegionAccess main;
    public RegionAccessCommand(RegionAccess main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            Settings.reloadMessages(main);
            sender.sendMessage(Settings.CONFIG_RELOADED);
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("/regionaccess can only be used by players!");
            return false;
        }
        Player player = (Player) sender;

        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "help":
                    if (player.hasPermission("regionaccess.help")) {
                        player.sendMessage(Settings.HELP);
                    } else {
                        player.sendMessage(Settings.NO_PERMISSION);
                    }
                    break;
                case "info":
                    if(args.length == 2) {
                        info(player, args[1]);
                    } else {
                        player.sendMessage(Settings.INVALID_ARGUMENTS);
                    }
                    break;
                case "set":
                    if (args.length == 3) {
                        set(player, args[1], args[2]);
                    } else {
                        player.sendMessage(Settings.INVALID_ARGUMENTS);
                    }
                    break;
                case "remove":
                    if (args.length == 2) {
                        remove(player, args[1]);
                    } else {
                        player.sendMessage(Settings.INVALID_ARGUMENTS);
                    }
                    break;
                case "edit":
                    if (args.length > 3) {
                        if (main.getRegionManager().isAccessRegion(args[1])) {
                            if (equalsAny(args[2], "denyMessage", "dm", "deny")) { // Deny Message
                                denyMessage(player, args[1], args);
                            } else if (equalsAny(args[2], "memberBypass", "mb", "bypass")) { // Member Bypass
                                memberBypass(player, args[1], args[3]);
                            } else {
                                player.sendMessage(Settings.INVALID_ARGUMENTS);
                            }
                        } else {
                            player.sendMessage(Settings.INVALID_REGION.replace("{REGION}", args[1]));
                        }
                    } else {
                        player.sendMessage(Settings.INVALID_ARGUMENTS);
                    }
                    break;
                default:
                    player.sendMessage(Settings.INVALID_ARGUMENTS);
                    break;
            }
        } else {
            player.sendMessage(Settings.INVALID_ARGUMENTS);
        }


        return false;
    }

    public void info(Player player, String regionName) {
        if (!player.hasPermission("regionaccess.info")) {
            player.sendMessage(Settings.NO_PERMISSION);
            return;
        }

        if (!main.getRegionManager().isAccessRegion(regionName)) {
            player.sendMessage(Settings.INVALID_REGION.replace("{REGION}", regionName));
            return;
        }
        AccessRegion accessRegion = main.getRegionManager().getRegion(regionName);

        String denyMessage = "N/A";
        if (!accessRegion.getDenyMessage().equals(Settings.DEFAULT_ACCESS_DENIED))
            denyMessage = accessRegion.getDenyMessage();

        player.sendMessage(HexUtils.colour(Settings.REGION_INFO
                .replace("{REGION}", regionName)
                .replace("{PERMISSION}", accessRegion.getPermission())
                .replace("{DENY_MESSAGE}", denyMessage)
                .replace("{MEMBER_BYPASS}", "" + accessRegion.isMemberBypass())));
    }

    public void set(Player player, String regionName, String permission) {
        if (!player.hasPermission("regionaccess.edit.set")) {
            player.sendMessage(Settings.NO_PERMISSION);
            return;
        }

        // Make sure that WorldGuard actually has a region 'regionName'
        if (!WGUtils.regionExists(player.getWorld(), regionName)) {
            player.sendMessage(Settings.INVALID_REGION);
        }

        main.getRegionManager().createRegion(regionName.toLowerCase(), permission);
        player.sendMessage(Settings.REGION_SET.replace("{REGION}", regionName).replace("{PERMISSION}", permission));
    }

    public void remove(Player player, String regionName) {
        if (!player.hasPermission("regionaccess.edit.remove")) {
            player.sendMessage(Settings.NO_PERMISSION);
            return;
        }

        main.getRegionManager().remove(regionName);
        player.sendMessage(Settings.REGION_REMOVED.replace("{REGION}", regionName));
    }

    public void denyMessage(Player player, String regionName, String[] args) {
        if (!player.hasPermission("regionaccess.edit.denyMessage")) {
            player.sendMessage(Settings.NO_PERMISSION);
            return;
        }

        // Construct the message based off of the 3rd argument to the last one.
        StringBuilder buffer = new StringBuilder();
        for(int i = 3; i < args.length; i++)
        {
            buffer.append(' ').append(args[i]);
        }
        String s = buffer.toString();
        s = s.replaceFirst(" ", "");

        main.getRegionManager().setDenyMessage(regionName, s);

        if (s.equals("disable")) {
            player.sendMessage(Settings.DENY_MESSAGE_DISABLED.replace("{REGION}", regionName));
        } else {
            player.sendMessage(HexUtils.colour(Settings.REGION_EDIT.replace("{SETTING}", "Deny Message").replace("{VALUE}", s)));
        }
    }

    public void memberBypass(Player player, String regionName, String bypass) {
        if (!player.hasPermission("regionaccess.edit.memberBypass")) {
            player.sendMessage(Settings.NO_PERMISSION);
            return;
        }

        // Because I'm using Boolean.parseBoolean, which returns false if a non-boolean string is inputted. You can type in 'ya mum gay' & it will work
        boolean b = Boolean.parseBoolean(bypass);
        main.getRegionManager().setMemberBypass(regionName, b);
        player.sendMessage(Settings.REGION_EDIT.replace("{SETTING}", "Member Bypass").replace("{VALUE}", String.valueOf(b)));
    }

    private boolean equalsAny(String s, String... s1) {
        for (String s2 : s1) {
            if (s2.equalsIgnoreCase(s)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {
        List<String> results = new ArrayList<>();

        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("help", "reload", "info", "set", "edit", "remove"), new ArrayList<>());
        } else if (args.length == 2) {
            if (equalsAny(args[0], "info", "set", "edit", "remove")) {
                return StringUtil.copyPartialMatches(args[1], main.getRegionManager().getAllNames(), new ArrayList<>());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("set")) {
                results.add("<permission>");
            } else if (args[0].equalsIgnoreCase("edit")) {
                return StringUtil.copyPartialMatches(args[2], Arrays.asList("denyMessage", "memberBypass"), new ArrayList<>());
            }
        } else if (args.length == 4) {
            if (equalsAny(args[2], "denyMessage", "deny", "dm", "message", "msg")) {
                results.add("<Message...>");
            } else if (equalsAny(args[2], "memberBypass", "mb", "bypass")) {
                return StringUtil.copyPartialMatches(args[3], Arrays.asList("true", "false"), new ArrayList<>());
            }
        }

        return results;
    }
}
