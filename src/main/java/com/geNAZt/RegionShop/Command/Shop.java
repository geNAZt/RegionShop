package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.PlayerStorage;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class Shop implements CommandExecutor {
    private RegionShopPlugin plugin;
    private ShopWarp shopWarp;
    private ShopList shopList;
    private ShopSelect shopSelect;
    private ShopAdd shopAdd;
    private ShopDetail shopDetail;

    public Shop(RegionShopPlugin pl) {
        this.plugin = pl;
        this.shopWarp = new ShopWarp(pl);
        this.shopList = new ShopList(pl);
        this.shopSelect = new ShopSelect(pl);
        this.shopAdd = new ShopAdd(pl);
        this.shopDetail = new ShopDetail(pl);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean isPlayer = (sender instanceof Player);
        Player p = (isPlayer) ? (Player) sender : null;

        if(!isPlayer) {
            sender.sendMessage(Chat.getPrefix() + "No shop for you Console!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("shop")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("list")) {
                    if (PlayerStorage.getPlayer(p) != null) {
                        String regString = PlayerStorage.getPlayer(p);

                        if (args.length > 1) {
                            shopList.execute(p, regString, args[1]);
                        } else {
                            shopList.execute(p, regString, "1");
                        }
                    }
                } else if(args[0].equalsIgnoreCase("warp")) {
                    if (args.length > 1) {
                        shopWarp.execute(p, args[1]);
                    } else {
                        p.sendMessage(Chat.getPrefix() + "No second argument given");
                        showHelp(p);
                    }
                } else if(args[0].equalsIgnoreCase("select")) {
                    if (args.length > 1) {
                        shopSelect.execute(p, args[1]);
                    } else {
                        p.sendMessage(Chat.getPrefix() + "No second argument given");
                        showHelp(p);
                    }
                } else if(args[0].equalsIgnoreCase("add")) {
                    if (args.length > 3) {
                        Integer buy, sell, amount;

                        try {
                            buy = Integer.parseInt(args[2]);
                            sell = Integer.parseInt(args[1]);
                            amount = Integer.parseInt(args[3]);
                        } catch (NumberFormatException e) {
                            p.sendMessage(Chat.getPrefix() + "Only Numbers as sell, buy and amount values");
                            return true;
                        }

                        shopAdd.execute(p, buy, sell, amount);
                    }
                } else if(args[0].equalsIgnoreCase("detail")) {
                    if (args.length > 1) {
                        Integer itemId;

                        try {
                            itemId = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            p.sendMessage(Chat.getPrefix() + "Only Numbers as detail ItemID");
                            return true;
                        }

                        shopDetail.execute(p, itemId);
                    }
                } else {
                    showHelp(p);
                }
            } else {
                showHelp(p);
            }

            return true;
        }

        return false;
    }

    private void showHelp(Player sender) {
        sender.sendMessage(Chat.getPrefix() + "RegionShop help");
        sender.sendMessage(Chat.getPrefix() + "----------------------------------------");
        sender.sendMessage(Chat.getPrefix() + ChatColor.AQUA + "/shop list -> " + ChatColor.RESET + " Show all Items in a Shop");
        sender.sendMessage(Chat.getPrefix() + ChatColor.AQUA + "/shop warp <owner> -> " + ChatColor.RESET + " Warp to the Shop of Player <owner>");
        sender.sendMessage(Chat.getPrefix() + ChatColor.AQUA + "/shop warp <region> -> " + ChatColor.RESET + " Warp to the <region> of a Shop");
        sender.sendMessage(Chat.getPrefix() + ChatColor.AQUA + "/shop select <owner/region> -> " + ChatColor.RESET + " Select the Shop <owner/region> as current Shop");
        sender.sendMessage(Chat.getPrefix() + ChatColor.AQUA + "/shop add <sell> <buy> <amount> -> " + ChatColor.RESET + " Adds the current in Hand Item to the Shop.");
        sender.sendMessage(Chat.getPrefix() + "<sell> - Amount of $ which you want to sell; <buy> - Amount of $ you want to buy; <amount> - Amount of Items which will be dealt");
    }
}
