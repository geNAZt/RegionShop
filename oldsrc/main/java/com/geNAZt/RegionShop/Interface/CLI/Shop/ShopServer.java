package com.geNAZt.RegionShop.Interface.CLI.Shop;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.Arrays;
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

    public ShopServer(RegionShopPlugin plugin) {
        loadedCommands = loadFromJAR("com.geNAZt.RegionShop.Interface.CLI.ServerShop", ShopCommand.class);

        for(Object command : loadedCommands) {
            ShopCommand shopCommand = (ShopCommand) command;

            help = concat(help, shopCommand.getHelpText());
        }
    }

    private String[] concat(String[] A, String[] B) {
        int aLen = A.length;
        int bLen = B.length;
        String[] C= new String[aLen+bLen];
        System.arraycopy(A, 0, C, 0, aLen);
        System.arraycopy(B, 0, C, aLen, bLen);
        return C;
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
