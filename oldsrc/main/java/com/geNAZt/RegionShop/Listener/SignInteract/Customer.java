package com.geNAZt.RegionShop.Listener.SignInteract;

import com.geNAZt.RegionShop.Bukkit.Bridges.EssentialBridge;
import com.geNAZt.RegionShop.Bukkit.Bridges.VaultBridge;
import com.geNAZt.RegionShop.Bukkit.Events.SignInteract;
import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.ItemName;
import com.geNAZt.RegionShop.Data.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Data.Storages.PriceStorage;
import com.geNAZt.RegionShop.Data.Struct.Price;
import com.geNAZt.RegionShop.Data.Struct.Region;
import com.geNAZt.RegionShop.Database.Database.ItemConverter;
import com.geNAZt.RegionShop.Database.Database.Model.ShopCustomerSign;
import com.geNAZt.RegionShop.Database.Database.Model.ShopItems;
import com.geNAZt.RegionShop.Database.Database.Model.ShopTransaction;
import com.geNAZt.RegionShop.Listener.Listener;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Transaction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 19.07.13
 */
public class Customer extends Listener {
    private final RegionShopPlugin plugin;

    public Customer(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    public void execute(SignInteract event) {
        ShopCustomerSign customerSign = plugin.getDatabase().find(ShopCustomerSign.class).
                where().
                    conjunction().
                        eq("world", event.getParent().getPlayer().getWorld().getName()).
                        eq("x", event.getBlock().getX()).
                        eq("y", event.getBlock().getY()).
                        eq("z", event.getBlock().getZ()).
                    endJunction().
                findUnique();

        if(customerSign == null) return;

        if (!PlayerStorage.has(event.getParent().getPlayer())) {
            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not inside a shop region.");
            return;
        }

        Region region = PlayerStorage.get(event.getParent().getPlayer());

        if(event.getParent().getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if(customerSign.getServershop()) {
                executeRightClickForServershop(region, customerSign, event);
            } else {
                executeRightClickForUsershop(region, customerSign, event);
            }
        } else if(event.getParent().getAction().equals(Action.LEFT_CLICK_BLOCK)) {
            ItemStack itemStack = event.getParent().getPlayer().getItemInHand();

            if(customerSign.getServershop()) {
                executeLeftClickForServershop(region, customerSign, event, itemStack);
            } else {
                executeLeftClickForUsershop(region, customerSign, event, itemStack);
            }
        }
    }

    private void executeLeftClickForUsershop(Region region, ShopCustomerSign customerSign, SignInteract event, ItemStack itemStack) {
        ShopItems item = plugin.getDatabase().find(ShopItems.class).
            where().
                conjunction().
                    eq("id", customerSign.getItemid()).
                endJunction().
            findUnique();

        if(item == null || item.getBuy() <= 0.0) {
            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED + "This shop does not buy this item");
            return;
        }

        Economy eco = VaultBridge.economy;

        if (eco.has(item.getOwner(), itemStack.getAmount() * item.getBuy())) {
            Player owner = plugin.getServer().getPlayer(item.getOwner());
            if (owner != null) {
                owner.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "Player " + ChatColor.GREEN + event.getParent().getPlayer().getDisplayName() + ChatColor.DARK_GREEN + " has sold " + ChatColor.GREEN + itemStack.getAmount() + " " + ItemName.getDataName(itemStack) + ItemName.nicer(itemStack.getType().toString()) + ChatColor.DARK_GREEN + " to your shop (" + ChatColor.GREEN + region.getName() + ChatColor.DARK_GREEN + ") for " + ChatColor.GREEN + (itemStack.getAmount() * item.getBuy()) + "$");
            }

            eco.withdrawPlayer(item.getOwner(), itemStack.getAmount() * item.getBuy());
            eco.depositPlayer(event.getParent().getPlayer().getName(), itemStack.getAmount() * item.getBuy());
            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "You have sold " + ChatColor.GREEN + itemStack.getAmount() + " " + ItemName.getDataName(itemStack) + ItemName.nicer(itemStack.getType().toString()) + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + (itemStack.getAmount() * item.getBuy()) + "$" + ChatColor.DARK_GREEN + " to shop");

            event.getParent().getPlayer().getInventory().removeItem(itemStack);
            item.setCurrentAmount(item.getCurrentAmount() + itemStack.getAmount());
            plugin.getDatabase().update(item);

            Transaction.generateTransaction(event.getParent().getPlayer(), ShopTransaction.TransactionType.SELL, region.getName(), event.getParent().getPlayer().getWorld().getName(), owner.getName(), item.getItemID(), itemStack.getAmount(), 0.0, item.getBuy().doubleValue(), item.getUnitAmount());
            Transaction.generateTransaction(owner, ShopTransaction.TransactionType.BUY, region.getName(), event.getParent().getPlayer().getWorld().getName(), event.getParent().getPlayer().getName(), item.getItemID(), itemStack.getAmount(), item.getBuy().doubleValue(), 0.0, item.getUnitAmount());

            return;
        }

        event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED + "The Item Owner has not enough Money");
    }

    private void executeLeftClickForServershop(Region region, ShopCustomerSign customerSign, SignInteract event, ItemStack itemStack) {
        ConcurrentHashMap<ItemStack, Price> itemsInShop = PriceStorage.getRegion(region.getRegion().getId());
        ItemStack compareStack = new ItemStack(itemStack.getType(), 1);

        if(itemStack.getData().getData() != 0) {
            compareStack.getData().setData(itemStack.getData().getData());
        }

        if(!itemsInShop.containsKey(compareStack)) {
            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED + "This Item is not in this ServerShop");
            return;
        }

        if(!itemStack.getEnchantments().isEmpty() || itemStack.getItemMeta().hasDisplayName()) {
            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED + "You can't sell enchanted / custom renamed Items into a shop");
            return;
        }

        Price price = itemsInShop.get(compareStack);

        if(price.getCurrentBuy() <= 0.0) {
            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED +  "This Shop doesn't buy this Item");
            return;
        }

        Economy eco = VaultBridge.economy;
        Double buyPrice = itemStack.getAmount() * price.getCurrentBuy();

        eco.depositPlayer(event.getParent().getPlayer().getName(), buyPrice);
        event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "You have sold " + ChatColor.GREEN + itemStack.getAmount() + " " + ItemName.getDataName(itemStack) + ItemName.nicer(itemStack.getType().toString()) + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + buyPrice + "$" + ChatColor.DARK_GREEN + " to Servershop");
        Transaction.generateTransaction(event.getParent().getPlayer(), ShopTransaction.TransactionType.SELL, "Servershop", event.getParent().getPlayer().getWorld().getName(), "server", itemStack.getTypeId(), itemStack.getAmount(), 0.0, price.getCurrentBuy(), 1);
        event.getParent().getPlayer().getInventory().removeItem(itemStack);

        price.setBought(price.getBought() + itemStack.getAmount());
        PriceStorage.add(region.getRegion().getId(), compareStack, price);
    }

    private void executeRightClickForUsershop(Region region, ShopCustomerSign customerSign, SignInteract event) {
        ShopItems item = plugin.getDatabase().find(ShopItems.class).
            where().
                eq("id", customerSign.getItemid()).
            findUnique();

        if(item == null) {
            event.getBlock().setType(Material.AIR);
            plugin.getDatabase().delete(customerSign);

            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED +  "Item is out of stock");
        } else {
            if (item.getSell() > 0) {
                if (item.getUnitAmount() > item.getCurrentAmount()) {
                    event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED +  "This shop has not enough items in stock");
                    return;
                }

                ItemStack iStack = ItemConverter.fromDBItem(item);
                iStack.setAmount(item.getUnitAmount());

                Economy eco = VaultBridge.economy;
                Integer wishAmount = item.getUnitAmount();

                if (eco.has(event.getParent().getPlayer().getName(), ((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell())) {
                    HashMap<Integer, ItemStack> notFitItems = event.getParent().getPlayer().getInventory().addItem(iStack);
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
                        onOwner.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "Player " + ChatColor.GREEN + event.getParent().getPlayer().getDisplayName() + ChatColor.DARK_GREEN + " bought " + ChatColor.GREEN + wishAmount + " " + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_GREEN + " from your shop (" + ChatColor.GREEN + region.getName() + ChatColor.DARK_GREEN + ") for " + ChatColor.GREEN + (((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell()) + "$");
                    }

                    eco.withdrawPlayer(event.getParent().getPlayer().getName(), ((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell());
                    eco.depositPlayer(item.getOwner(), ((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell());
                    event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "You have bought " + ChatColor.GREEN + wishAmount + " " + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + (((float) wishAmount / (float) item.getUnitAmount()) * (float) item.getSell()) + "$" + ChatColor.DARK_GREEN + " from shop");

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

                        event.getBlock().setType(Material.AIR);
                        plugin.getDatabase().delete(customerSign);
                    }

                    Transaction.generateTransaction(event.getParent().getPlayer(), ShopTransaction.TransactionType.BUY, region.getName(), event.getParent().getPlayer().getWorld().getName(), owner.getName(), item.getItemID(), wishAmount, item.getSell().doubleValue(), 0.0, item.getUnitAmount());
                    Transaction.generateTransaction(owner, ShopTransaction.TransactionType.SELL, region.getName(), event.getParent().getPlayer().getWorld().getName(), event.getParent().getPlayer().getName(), item.getItemID(), wishAmount, 0.0, item.getSell().doubleValue(), item.getUnitAmount());
                } else {
                    event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED +  "You have not enough money for this. You need "+ (((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell()) + "$");
                }
            } else {
                event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED +  "This Shop doesn't sell this Item");
            }
        }
    }

    private void executeRightClickForServershop(Region region, ShopCustomerSign customerSign, SignInteract event) {
        ConcurrentHashMap<ItemStack, Price> itemsInShop = PriceStorage.getRegion(region.getRegion().getId());
        ItemStack itemStack = new ItemStack(customerSign.getItemid(), 1);

        Byte compare = 0;
        if(!customerSign.getDatavalue().equals(compare)) {
            itemStack.getData().setData(customerSign.getDatavalue());
        }

        if(!itemsInShop.containsKey(itemStack)) {
            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED + "This Item is not in this ServerShop");
            return;
        }

        Price price = itemsInShop.get(itemStack);

        if(price.getCurrentSell() <= 0.0) {
            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED +  "This Shop doesn't sell this Item");
            return;
        }

        Economy eco = VaultBridge.economy;

        if(eco.has(event.getParent().getPlayer().getName(), price.getCurrentSell())) {
            eco.withdrawPlayer(event.getParent().getPlayer().getName(), price.getCurrentSell());
            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "You have bought " + ChatColor.GREEN + "1x " + ItemName.getDataName(itemStack) + ItemName.nicer(itemStack.getType().toString()) + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + (price.getCurrentSell()) + "$" + ChatColor.DARK_GREEN + " from shop");

            Transaction.generateTransaction(event.getParent().getPlayer(), ShopTransaction.TransactionType.BUY, region.getName(), event.getParent().getPlayer().getWorld().getName(), "Server", customerSign.getItemid(), 1, price.getCurrentSell(), 0.0, 1);

            price.setSold(price.getSold() + 1);
            PriceStorage.add(region.getRegion().getId(), itemStack, price);

            event.getParent().getPlayer().getInventory().addItem(itemStack);
        } else {
            event.getParent().getPlayer().sendMessage(Chat.getPrefix() + ChatColor.RED +  "You have not enough money for this. You need "+ price.getCurrentSell() + "$");
        }
    }
}
