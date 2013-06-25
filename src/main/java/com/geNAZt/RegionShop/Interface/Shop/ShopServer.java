package com.geNAZt.RegionShop.Interface.Shop;

import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Model.ShopRegion;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.ServerShop.Price;
import com.geNAZt.RegionShop.ServerShop.PriceStorage;
import com.geNAZt.RegionShop.Storages.ListStorage;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Util.Misc;
import com.google.common.base.CharMatcher;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.geNAZt.RegionShop.Util.Loader.loadFromJAR;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopServer extends ShopCommand {
    private CopyOnWriteArrayList<ShopCommand> loadedCommands = new CopyOnWriteArrayList<ShopCommand>();
    private String[] help = new String[]{};

    public ShopServer(Plugin plugin) {
        loadedCommands = loadFromJAR((RegionShopPlugin)plugin, "com.geNAZt.RegionShop.Interface.ServerShop", ShopCommand.class);

        for(Object command : loadedCommands) {
            ShopCommand shopCommand = (ShopCommand) command;

            help = Misc.concat(help, shopCommand.getHelpText());
        }
    }

    @Override
    public int getHelpPage() {
        return 1;
    }

    @Override
    public String[] getHelpText() {
        return help;
    }

    @Override
    public String getCommand() {
        return "server";
    }

    @Override
    public String getPermissionNode() {
        return "rs.server";
    }

    @Override
    public int getNumberOfArgs() {
        return 1;
    }

    @Override
    public void execute(Player player, String[] args) {
        for(ShopCommand command : loadedCommands) {
            if(command.getCommand().equalsIgnoreCase(args[0])) {
                if(command.getPermissionNode() == null || player.hasPermission(command.getPermissionNode())) {
                    if(args.length + 1 > command.getNumberOfArgs()) {
                        command.execute(player, Arrays.copyOfRange(args, 1, args.length));
                        return;
                    } else {
                        player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Not enough arguments given. Type " + ChatColor.DARK_RED + "/shop help" + ChatColor.RED + " for more informations.");
                        return;
                    }
                } else {
                    player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You don't have the permission " + ChatColor.DARK_RED + command.getPermissionNode());
                    return;
                }
            }
        }
    }
}
