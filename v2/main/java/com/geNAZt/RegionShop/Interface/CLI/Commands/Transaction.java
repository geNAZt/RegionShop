package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import com.geNAZt.RegionShop.Util.ItemName;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class Transaction implements CLICommand {
    @Command(command="shop transaction", arguments=0, permission="rs.command.transaction", helpKey="Command_Transaction_HelpText", helpPage="consumer")
    public static void transaction(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
            return;
        }

        Player player = (Player) sender;

        //Check for optional Args
        Integer page = 1;

        if(args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Transaction_InvalidArguments);
                return;
            }
        }

        PagingList<com.geNAZt.RegionShop.Database.Table.Transaction> transactionPagingList = Database.getServer().find(com.geNAZt.RegionShop.Database.Table.Transaction.class).
                where().
                    eq("issuer", player.getName()).
                order().desc("date").
                findPagingList(7);

        Integer curPage = page - 1;

        //Check if Valid page
        if (curPage < 0 || (curPage > transactionPagingList.getTotalPageCount() - 1 && transactionPagingList.getTotalPageCount() != 0)) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Transaction_InvalidPage);
            return;
        }

        //Get the right page
        Page qryPage = transactionPagingList.getPage(curPage);
        List<com.geNAZt.RegionShop.Database.Table.Transaction> transactionList = qryPage.getList();

        for(String headerLine : ConfigManager.language.Command_Transcation_Header) {
            player.sendMessage(ConfigManager.main.Chat_prefix + headerLine.replace("%page", ((Integer)(curPage + 1)).toString()).replace("%maxpage", ((Integer)transactionPagingList.getTotalPageCount()).toString()));
        }

        DateFormat df = new SimpleDateFormat(ConfigManager.language.Command_Transaction_DateFormat);

        if(transactionList.size() > 0) {
            for(com.geNAZt.RegionShop.Database.Table.Transaction item : transactionList) {
                ItemStack iStack = new ItemStack(item.getItem(), 1);
                String shopName = item.getShop();

                if(item.getType() == com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.BUY) {
                    player.sendMessage(ConfigManager.main.Chat_prefix + df.format(item.getDate()) + ": " + ChatColor.DARK_GREEN + item.getAmount() + "x " + ChatColor.GREEN + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_GREEN + " from " + ChatColor.GREEN + shopName + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + (item.getSell() * (item.getAmount() / item.getUnitAmount())) + "$ " + ChatColor.DARK_GREEN + "(" + ChatColor.GRAY + "#" + item.getId() + ChatColor.DARK_GREEN + ")");
                }

                if(item.getType() == com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.SELL) {
                    player.sendMessage(ConfigManager.main.Chat_prefix + df.format(item.getDate()) + ": " + ChatColor.DARK_RED + item.getAmount() + "x " + ChatColor.RED + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_RED + " via " + ChatColor.RED + shopName + ChatColor.DARK_RED + " for " + ChatColor.RED + (item.getBuy() * (item.getAmount() / item.getUnitAmount())) + "$ " + ChatColor.DARK_RED + "(" + ChatColor.GRAY + "#" + item.getId() + ChatColor.DARK_RED + ")");
                }

                if(item.getType() == com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.EQUIP) {
                    player.sendMessage(ConfigManager.main.Chat_prefix + df.format(item.getDate()) + ": " + ChatColor.GOLD + item.getAmount() + "x " + ChatColor.YELLOW + ItemName.nicer(iStack.getType().toString()) + ChatColor.GOLD + " into " + ChatColor.YELLOW + shopName + ChatColor.GOLD + " (" + ChatColor.GRAY + "#" + item.getId() + ChatColor.GOLD + ")");
                }

                if(item.getType() == com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.ADD) {
                    player.sendMessage(ConfigManager.main.Chat_prefix + df.format(item.getDate()) + ": " + ChatColor.YELLOW + item.getAmount() + "x " + ChatColor.GOLD + ItemName.nicer(iStack.getType().toString()) + ChatColor.YELLOW + " into " + ChatColor.GOLD + shopName + ChatColor.YELLOW + " (S)"+ ChatColor.GOLD + item.getSell() + ChatColor.YELLOW + "$ (B)" +ChatColor.GOLD + item.getBuy() + ChatColor.YELLOW + "$ /" + ChatColor.GOLD + item.getUnitAmount() + ChatColor.YELLOW +" Unit(s) (" + ChatColor.GRAY + "#" + item.getId() + ChatColor.YELLOW + ")");
                }

                if(item.getType() == com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.REMOVE) {
                    player.sendMessage(ConfigManager.main.Chat_prefix + df.format(item.getDate()) + ": " + ChatColor.DARK_AQUA + item.getAmount() + "x " + ChatColor.AQUA + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_AQUA + " from " + ChatColor.DARK_AQUA + " (" + ChatColor.GRAY + "#" + item.getId() + ChatColor.DARK_AQUA + ")");
                }
            }

            if (qryPage.hasNext()) {
                player.sendMessage(ConfigManager.main.Chat_prefix + ChatColor.GREEN +"/shop transaction "+ (curPage+2) + ChatColor.GOLD + " for the next page");
            }
        } else {
            player.sendMessage(ConfigManager.main.Chat_prefix + ChatColor.RED + "No Transactionlog found");
        }
    }
}
