package com.geNAZt.RegionShop.Interface.Shop;

import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemName;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopTransaction extends ShopCommand {
    private final Plugin plugin;

    public ShopTransaction(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 1;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop transaction " + ChatColor.GREEN + "page" + ChatColor.RESET + ": See your Transaction log"};
    }

    @Override
    public String getCommand() {
        return "transaction";
    }

    @Override
    public String getPermissionNode() {
        return "rs.transaction";
    }

    @Override
    public int getNumberOfArgs() {
        return 0;
    }

    @Override
    public void execute(Player player, String[] args) {
        //Check for optional Args
        Integer page = 1;

        if(args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Only numbers as page value");
                return;
            }
        }

        PagingList<com.geNAZt.RegionShop.Model.ShopTransaction> transactionPagingList = plugin.getDatabase().find(com.geNAZt.RegionShop.Model.ShopTransaction.class).
                where().
                    eq("issuer", player.getName()).
                findPagingList(7);

        Integer curPage = page - 1;

        //Check if Valid page
        if (curPage < 0 || (curPage > transactionPagingList.getTotalPageCount() - 1 && transactionPagingList.getTotalPageCount() != 0)) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid page");
            return;
        }

        //Get the right page
        Page qryPage = transactionPagingList.getPage(curPage);
        List<com.geNAZt.RegionShop.Model.ShopTransaction> transactionList = qryPage.getList();

        player.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Transactionlog -- " + ChatColor.GOLD + "Page " + ChatColor.RED + (curPage+1) + ChatColor.GOLD + "/" + ChatColor.RED + transactionPagingList.getTotalPageCount() + ChatColor.YELLOW + " --");
        player.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "Legend: " + ChatColor.DARK_GREEN + "You have bought" + ChatColor.RESET + " - " + ChatColor.DARK_RED + "You have sold" + ChatColor.RESET + " - " + ChatColor.GOLD + "You have equipped" + ChatColor.RESET + " - " + ChatColor.YELLOW + "You added/set Item" + ChatColor.RESET + " - " + ChatColor.DARK_AQUA + "You have removed" );

        DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

        if(transactionList.size() > 0) {
            for(com.geNAZt.RegionShop.Model.ShopTransaction item : transactionList) {
                ItemStack iStack = new ItemStack(item.getItem(), 1);
                String shopName = item.getShop();

                if(shopName != "Servershop") {
                    shopName = WorldGuardBridge.convertRegionStringToShopName(shopName, plugin.getServer().getWorld(item.getWorld()));
                }

                if(item.getType() == com.geNAZt.RegionShop.Model.ShopTransaction.TransactionType.BUY) {
                    player.sendMessage(Chat.getPrefix() + item.getDate().toString() + ChatColor.DARK_GREEN + item.getAmount() + "x " + ChatColor.GREEN + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_GREEN + " from " + ChatColor.GREEN + shopName + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + (item.getSell() * (item.getAmount() / item.getUnitAmount())) + "$ " + ChatColor.DARK_GREEN + "(" + ChatColor.GRAY + "#" + item.getId() + ChatColor.DARK_GREEN + ")");
                }

                if(item.getType() == com.geNAZt.RegionShop.Model.ShopTransaction.TransactionType.SELL) {
                    player.sendMessage(Chat.getPrefix() + ChatColor.DARK_RED + item.getAmount() + "x " + ChatColor.RED + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_RED + " via " + ChatColor.RED + shopName + ChatColor.DARK_RED + " for " + ChatColor.RED + (item.getBuy() * (item.getAmount() / item.getUnitAmount())) + "$ " + ChatColor.DARK_RED + "(" + ChatColor.GRAY + "#" + item.getId() + ChatColor.DARK_RED + ")");
                }

                if(item.getType() == com.geNAZt.RegionShop.Model.ShopTransaction.TransactionType.EQUIP) {
                    player.sendMessage(Chat.getPrefix() + ChatColor.GOLD + item.getAmount() + "x " + ChatColor.YELLOW + ItemName.nicer(iStack.getType().toString()) + ChatColor.GOLD + " into " + ChatColor.YELLOW + shopName + ChatColor.GOLD + "(" + ChatColor.GRAY + "#" + item.getId() + ChatColor.GOLD + ")");
                }

                if(item.getType() == com.geNAZt.RegionShop.Model.ShopTransaction.TransactionType.ADD) {
                    player.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + item.getAmount() + "x " + ChatColor.GOLD + ItemName.nicer(iStack.getType().toString()) + ChatColor.YELLOW + " into " + ChatColor.GOLD + shopName + ChatColor.YELLOW + "(S)"+ ChatColor.GOLD + item.getSell() + ChatColor.YELLOW + "$ (B)" +ChatColor.GOLD + item.getBuy() + ChatColor.YELLOW + "$ /" + ChatColor.GOLD + item.getUnitAmount() + ChatColor.YELLOW +" Unit(s) (" + ChatColor.GRAY + "#" + item.getId() + ChatColor.YELLOW + ")");
                }

                if(item.getType() == com.geNAZt.RegionShop.Model.ShopTransaction.TransactionType.REMOVE) {
                    player.sendMessage(Chat.getPrefix() + ChatColor.DARK_AQUA + item.getAmount() + "x " + ChatColor.AQUA + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_AQUA + " from " + ChatColor.DARK_AQUA + "(" + ChatColor.GRAY + "#" + item.getId() + ChatColor.DARK_AQUA + ")");
                }
            }

            if (qryPage.hasNext()) {
                player.sendMessage(Chat.getPrefix() + ChatColor.GREEN +"/shop transaction "+ (curPage+2) + ChatColor.GOLD + " for the next page");
            }
        } else {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "No Transactionlog found");
        }
    }
}
