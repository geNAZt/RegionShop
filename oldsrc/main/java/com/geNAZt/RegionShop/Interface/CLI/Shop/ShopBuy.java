package com.geNAZt.RegionShop.Interface.CLI.Shop;

import com.geNAZt.RegionShop.Bukkit.Bridges.EssentialBridge;
import com.geNAZt.RegionShop.Bukkit.Bridges.VaultBridge;
import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.ItemName;
import com.geNAZt.RegionShop.Data.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Data.Storages.PriceStorage;
import com.geNAZt.RegionShop.Data.Struct.Price;
import com.geNAZt.RegionShop.Data.Struct.Region;
import com.geNAZt.RegionShop.Database.Database.ItemConverter;
import com.geNAZt.RegionShop.Database.Database.Model.ShopItems;
import com.geNAZt.RegionShop.Database.Database.Model.ShopTransaction;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Transaction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopBuy extends ShopCommand {
    private final RegionShopPlugin plugin;

    public ShopBuy(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 1;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop buy " + ChatColor.RED + "shopItemID " +  ChatColor.GREEN + "amount" + ChatColor.RESET + ": Buy (" + ChatColor.GREEN + "amount" + ChatColor.RESET + " pcs. of) " + ChatColor.RED + "shopItemID " + ChatColor.RESET + "from the shop"};
    }

    @Override
    public String getCommand() {
        return "buy";
    }

    @Override
    public String getPermissionNode() {
        return "rs.buy";
    }

    @Override
    public int getNumberOfArgs() {
        return 1;
    }

    private void executePlayerShop(Player player, String[] args, Region region, String amountStr) {
        Integer shopItemId, wishAmount;

        try {
            shopItemId = Integer.parseInt(args[0]);
            wishAmount = Integer.parseInt(amountStr);
        } catch (NumberFormatException e) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "Only numbers as shopItemId and amount values allowed");
            return;
        }

        ShopItems item = plugin.getDatabase().find(ShopItems.class).
            where().
                conjunction().
                    eq("world", player.getWorld().getName()).
                    eq("region", region.getItemStorage()).
                    eq("id", shopItemId).
                endJunction().
            findUnique();

        if (item == null) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "This shopItem could not be found");
            return;
        }

        if (item.getSell() > 0) {
            if (wishAmount > item.getCurrentAmount() && wishAmount != -1) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "This shop has not enough items in stock");
                return;
            }

            if (wishAmount < 1) {
                wishAmount = 1;
            }

            ItemStack iStack = ItemConverter.fromDBItem(item);
            iStack.setAmount(wishAmount);

            Economy eco = VaultBridge.economy;

            if (eco.has(player.getName(), ((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell())) {
                HashMap<Integer, ItemStack> notFitItems = player.getInventory().addItem(iStack);
                if (!notFitItems.isEmpty()) {
                    for(Map.Entry<Integer, ItemStack> notFitItem : notFitItems.entrySet()) {
                        wishAmount -= notFitItem.getValue().getAmount();
                    }
                }

                OfflinePlayer owner = plugin.getServer().getOfflinePlayer(item.getOwner());
                Player onOwner = null;

                if(owner.isOnline()) {
                    onOwner = (Player) owner;
                }

                if (onOwner != null) {
                    onOwner.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "Player " + ChatColor.GREEN + player.getDisplayName() + ChatColor.DARK_GREEN + " bought " + ChatColor.GREEN + wishAmount + " " + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_GREEN + " from your shop (" + ChatColor.GREEN + region.getName() + ChatColor.DARK_GREEN + ") for " + ChatColor.GREEN + (((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell()) + "$");
                }

                eco.withdrawPlayer(player.getName(), ((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell());
                eco.depositPlayer(item.getOwner(), ((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell());
                player.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "You have bought " + ChatColor.GREEN + wishAmount + " " + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + (((float) wishAmount / (float) item.getUnitAmount()) * (float) item.getSell()) + "$" + ChatColor.DARK_GREEN + " from shop");

                item.setCurrentAmount(item.getCurrentAmount() - wishAmount);

                if (item.getCurrentAmount() > 0) {
                    plugin.getDatabase().update(item);
                } else {
                    if (onOwner != null) {
                        onOwner.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "ShopItem " + ChatColor.GREEN + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_GREEN + " is empty. It has been removed from your shop (" + ChatColor.GREEN + region.getName() + ChatColor.DARK_GREEN + ")");
                    } else {
                        EssentialBridge.sendMail(Chat.getPrefix(), owner, ChatColor.DARK_GREEN + "ShopItem " + ChatColor.GREEN + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_GREEN + " is empty. It has been removed from your shop (" + ChatColor.GREEN + region.getName() + ChatColor.DARK_GREEN + ")");
                    }

                    plugin.getDatabase().delete(item);
                }

                Transaction.generateTransaction(player, ShopTransaction.TransactionType.BUY, region.getName(), player.getWorld().getName(), owner.getName(), item.getItemID(), wishAmount, item.getSell().doubleValue(), 0.0, item.getUnitAmount());
                Transaction.generateTransaction(owner, ShopTransaction.TransactionType.SELL, region.getName(), player.getWorld().getName(), player.getName(), item.getItemID(), wishAmount, 0.0, item.getSell().doubleValue(), item.getUnitAmount());
            } else {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You have not enough money for this. You need "+ (((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell()) + "$");
            }
        } else {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "This Shop doesn't sell this Item");
        }
    }

    private void executeServerShop(Player player, String[] args, ConcurrentHashMap<ItemStack, Price> items, Region region, String amountStr) {
        String itemId = args[0];
        String dataValue = "0";

        if(args[0].contains(":")) {
            String[] temp = args[0].split(":");
            itemId = temp[0];
            dataValue = temp[1];
        }

        Integer itemID, wishAmount;
        byte dataVal;
        try {
            dataVal = Byte.parseByte(dataValue);
            itemID = Integer.parseInt(itemId);
            wishAmount = Integer.parseInt(amountStr);
        } catch(NumberFormatException e) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid ItemID or amount");
            return;
        }

        if (wishAmount < 1) {
            wishAmount = 1;
        }

        for(Map.Entry<ItemStack, Price> item : items.entrySet()) {
            if(item.getKey().getTypeId() == itemID && item.getKey().getData().getData() == dataVal) {
                if(item.getValue().getCurrentSell() > 0.0) {
                    Economy eco = VaultBridge.economy;

                    if (eco.has(player.getName(), ((float)wishAmount) * (float)item.getValue().getCurrentSell())) {
                        ItemStack iStack = new ItemStack(itemID, 1);
                        iStack.setAmount(wishAmount);

                        if(dataVal > 0) {
                            iStack.getData().setData(dataVal);
                        }

                        HashMap<Integer, ItemStack> notFitItems = player.getInventory().addItem(iStack);
                        if (!notFitItems.isEmpty()) {
                            for(Map.Entry<Integer, ItemStack> notFitItem : notFitItems.entrySet()) {
                                wishAmount -= notFitItem.getValue().getAmount();
                            }
                        }

                        eco.withdrawPlayer(player.getName(), ((float)wishAmount) * (float)item.getValue().getCurrentSell());
                        player.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "You have bought " + ChatColor.GREEN + wishAmount + " " + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + (((float)wishAmount) * (float)item.getValue().getCurrentSell()) + "$" + ChatColor.DARK_GREEN + " from shop");

                        Transaction.generateTransaction(player, ShopTransaction.TransactionType.BUY, region.getName(), player.getWorld().getName(), "Server", itemID, wishAmount, item.getValue().getCurrentSell(), 0.0, 1);

                        item.getValue().setSold(item.getValue().getSold() + wishAmount);
                        PriceStorage.add(region.getRegion().getId(), item.getKey(), item.getValue());
                    } else {
                        player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You have not enough money for this. You need "+ (((float)wishAmount) * (float)item.getValue().getCurrentSell()) + "$");
                    }

                    return;
                }
            }
        }

        player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "This Shop doesn't sell this Item");
    }

    @Override
    public void execute(Player player, String[] args) {
        //Convert args
        String amountStr = "1";
        if(args.length > 1) {
            amountStr = args[1];
        }

        if (PlayerStorage.has(player)) {
            Region region = PlayerStorage.get(player);
            ConcurrentHashMap<ItemStack, Price> serverShop = PriceStorage.getRegion(region.getRegion().getId());

            if(serverShop != null) {
                executeServerShop(player, args, serverShop, region, amountStr);
            } else {
                executePlayerShop(player, args, region, amountStr);
            }
        } else {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You are not inside a shop");
        }
    }
}
