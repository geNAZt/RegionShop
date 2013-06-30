package com.geNAZt.RegionShop.Interface.Shop;

import com.geNAZt.RegionShop.Bridges.VaultBridge;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.Model.ShopTransaction;
import com.geNAZt.RegionShop.Region.Region;
import com.geNAZt.RegionShop.ServerShop.Price;
import com.geNAZt.RegionShop.ServerShop.PriceStorage;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Transaction.Transaction;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemName;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopSell extends ShopCommand {
    private final Plugin plugin;

    public ShopSell(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 1;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop sell "+ ChatColor.RESET + ": Sell the current Item in Hand to the shop"};
    }

    @Override
    public String getCommand() {
        return "sell";
    }

    @Override
    public String getPermissionNode() {
        return "rs.sell";
    }

    @Override
    public int getNumberOfArgs() {
        return 0;
    }

    private void executeServerShop(Player player, ItemStack itemInHand, ConcurrentHashMap<ItemStack, Price> items, Region region) {
        for(Map.Entry<ItemStack, Price> item : items.entrySet()) {
            if(item.getKey().getTypeId() == itemInHand.getTypeId() && item.getKey().getData().getData() == itemInHand.getData().getData()) {
                if(item.getValue().getCurrentBuy() > 0.0) {
                    Economy eco = VaultBridge.economy;
                    eco.depositPlayer(player.getName(), itemInHand.getAmount() * item.getValue().getCurrentBuy());
                    player.getInventory().remove(itemInHand);
                    player.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "You have sold " + ChatColor.GREEN + itemInHand.getAmount() + " " + ItemName.getDataName(itemInHand) + ItemName.nicer(itemInHand.getType().toString()) + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + (itemInHand.getAmount() * item.getValue().getCurrentBuy()) + "$" + ChatColor.DARK_GREEN + " to shop");

                    Transaction.generateTransaction(player, ShopTransaction.TransactionType.SELL, region.getName(), player.getWorld().getName(), "Server", itemInHand.getTypeId(), itemInHand.getAmount(), 0.0, item.getValue().getCurrentBuy(), 1);

                    item.getValue().setBought(item.getValue().getBought() + itemInHand.getAmount());
                    PriceStorage.add(region.getRegion().getId(), item.getKey(), item.getValue());

                    return;
                }
            }
        }

        player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "This Shop doesn't buy this Item");
    }

    private void executePlayerShop(Player player, ItemStack itemInHand, Region region) {
        List<ShopItems> items = plugin.getDatabase().find(ShopItems.class).
                where().
                    conjunction().
                        eq("world", player.getWorld().getName()).
                        eq("region", region.getItemStorage()).
                        eq("item_id", itemInHand.getType().getId()).
                        eq("data_id", itemInHand.getData().getData()).
                        eq("durability", itemInHand.getDurability()).
                        eq("custom_name", (itemInHand.getItemMeta().hasDisplayName()) ? itemInHand.getItemMeta().getDisplayName() : null).
                    endJunction().
                findList();

        if(items.isEmpty()) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "This shop does not buy this item");
            return;
        }

        for(ShopItems item : items) {
            if (item != null && item.getBuy() > 0) {
                Economy eco = VaultBridge.economy;

                if (eco.has(item.getOwner(), itemInHand.getAmount() * item.getBuy())) {
                    Player owner = plugin.getServer().getPlayer(item.getOwner());
                    if (owner != null) {
                        owner.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "Player " + ChatColor.GREEN + player.getDisplayName() + ChatColor.DARK_GREEN + " has sold " + ChatColor.GREEN + itemInHand.getAmount() + " " + ItemName.getDataName(itemInHand) + ItemName.nicer(itemInHand.getType().toString()) + ChatColor.DARK_GREEN + " to your shop (" + ChatColor.GREEN + region.getName() + ChatColor.DARK_GREEN + ") for " + ChatColor.GREEN + (itemInHand.getAmount() * item.getBuy()) + "$");
                    }

                    eco.withdrawPlayer(item.getOwner(), itemInHand.getAmount() * item.getBuy());
                    eco.depositPlayer(player.getName(), itemInHand.getAmount() * item.getBuy());
                    player.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "You have sold " + ChatColor.GREEN + itemInHand.getAmount() + " " + ItemName.getDataName(itemInHand) + ItemName.nicer(itemInHand.getType().toString()) + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + (itemInHand.getAmount() * item.getBuy()) + "$" + ChatColor.DARK_GREEN + " to shop");

                    player.getInventory().remove(itemInHand);
                    item.setCurrentAmount(item.getCurrentAmount() + itemInHand.getAmount());
                    plugin.getDatabase().update(item);

                    //noinspection ConstantConditions
                    Transaction.generateTransaction(player, ShopTransaction.TransactionType.SELL, region.getName(), player.getWorld().getName(), owner.getName(), item.getItemID(), itemInHand.getAmount(), 0.0, item.getBuy().doubleValue(), item.getUnitAmount());
                    Transaction.generateTransaction(owner, ShopTransaction.TransactionType.BUY, region.getName(), player.getWorld().getName(), player.getName(), item.getItemID(), itemInHand.getAmount(), item.getBuy().doubleValue(), 0.0, item.getUnitAmount());

                    return;
                }
            }
        }

        player.sendMessage(Chat.getPrefix() + ChatColor.RED + "None of the Item Owners has enough Money");
    }

    @Override
    public void execute(Player player, String[] args) {
        if (PlayerStorage.has(player)) {
            Region region = PlayerStorage.get(player);

            ItemStack itemInHand = player.getItemInHand();

            if(itemInHand == null || itemInHand.getType().getId() == 0) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You have no item in the hand");
                return;
            }

            if(!itemInHand.getEnchantments().isEmpty() || itemInHand.getItemMeta().hasDisplayName()) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You can't sell enchanted / custom renamed Items into a shop");
            }

            ConcurrentHashMap<ItemStack, Price> serverShop = PriceStorage.getRegion(region.getRegion().getId());

            if(serverShop != null) {
                executeServerShop(player, itemInHand, serverShop, region);
            } else {
                executePlayerShop(player, itemInHand, region);
            }

        } else {
            //Nothing of all
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not inside a shop");
        }
    }
}
