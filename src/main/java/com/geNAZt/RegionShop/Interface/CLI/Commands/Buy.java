package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Data.Storage.InRegion;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Model.Item;
import com.geNAZt.RegionShop.Database.Model.Transaction;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.EssentialBridge;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Util.VaultBridge;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class Buy implements CLICommand {
    @Command(command="shop buy", arguments=1, helpKey="Command_Buy_HelpText", helpPage="consumer", permission="rs.command.buy")
    public static void buy(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only for Players");
            return;
        }

        Player player = (Player) sender;

        //Convert args
        Integer shopItemId, wishAmount = 1;

        try {
            if(args.length > 1) {
                wishAmount = Integer.parseInt(args[1]);
            }

            shopItemId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Buy_InvalidArguments);
            return;
        }

        //Is player in region ?
        if (!InRegion.has(player)) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Buy_NotInRegion);
            return;
        }

        //Get the item
        Items item = Database.getServer().find(Items.class).
                where().
                    conjunction().
                        eq("id", shopItemId).
                    endJunction().
                findUnique();

        //No luck for you => no item found
        if (item == null) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Buy_ItemNotFound);
            return;
        }

        if (item.getSell() > 0) {
            if (wishAmount > item.getCurrentAmount() && wishAmount != -1) {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Buy_NotEnoughItems);
                return;
            }

            if (wishAmount < 1) {
                wishAmount = 1;
            }

            ItemStack iStack = Item.fromDBItem(item);
            iStack.setAmount(wishAmount);

            Economy eco = VaultBridge.economy;
            Float price = (((float) wishAmount / (float) item.getUnitAmount()) * item.getSell());

            if (eco.has(player.getName(), price)) {
                HashMap<Integer, ItemStack> notFitItems = player.getInventory().addItem(iStack);
                if (!notFitItems.isEmpty()) {
                    for(Map.Entry<Integer, ItemStack> notFitItem : notFitItems.entrySet()) {
                        wishAmount -= notFitItem.getValue().getAmount();
                    }
                }

                String niceItemName = ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString());
                Region region = InRegion.get(player);

                item.setCurrentAmount(item.getCurrentAmount() - wishAmount);

                if(!item.getItemStorage().isServershop()) {
                    OfflinePlayer owner = RegionShopPlugin.getInstance().getServer().getOfflinePlayer(item.getOwner());
                    Player onOwner = null;

                    if(owner.isOnline()) {
                        onOwner = (Player) owner;
                    }

                    if (onOwner != null) {
                        onOwner.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Buy_OwnerHint.
                            replace("%player", player.getDisplayName()).
                            replace("%amount", wishAmount.toString()).
                            replace("%item", niceItemName).
                            replace("%shop", region.getName()).
                            replace("%price", price.toString()));
                    }

                    eco.depositPlayer(item.getOwner(), price);

                    if(item.getCurrentAmount() == 0) {
                        if (onOwner != null) {
                            onOwner.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Buy_OwnerHintEmptyShop.replace("%item", niceItemName).replace("%shop", region.getName()));
                        } else {
                            EssentialBridge.sendMail(ConfigManager.main.Chat_prefix, owner, ConfigManager.language.Command_Buy_OwnerHintEmptyShop.replace("%item", niceItemName).replace("%shop", region.getName()));
                        }
                    }

                    Transaction.generateTransaction(owner, com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.SELL, region.getName(), player.getWorld().getName(), player.getName(), item.getMeta().getId().getItemID(), wishAmount, 0.0, item.getSell().doubleValue(), item.getUnitAmount());
                }

                eco.withdrawPlayer(player.getName(), price);
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Buy_PlayerHint.
                        replace("%player", player.getDisplayName()).
                        replace("%amount", wishAmount.toString()).
                        replace("%item", niceItemName).
                        replace("%shop", region.getName()).
                        replace("%price", price.toString()));

                item.setSold(item.getSold() + wishAmount);

                if (item.getCurrentAmount() > 0 || item.getItemStorage().isServershop()) {
                    Database.getServer().update(item);
                } else {
                    Database.getServer().delete(item);
                }

                Transaction.generateTransaction(player, com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.BUY, region.getName(), player.getWorld().getName(), item.getOwner(), item.getMeta().getId().getItemID(), wishAmount, item.getSell().doubleValue(), 0.0, item.getUnitAmount());
            } else {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Buy_NotEnoughMoney.replace("%price", price.toString()));
            }
        } else {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Buy_NoSell);
        }
    }
}
