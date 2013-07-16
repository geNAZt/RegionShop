package com.geNAZt.RegionShop.Interface.CLI.Shop;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.ItemName;
import com.geNAZt.RegionShop.Data.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Data.Struct.Region;
import com.geNAZt.RegionShop.Database.ItemConverter;
import com.geNAZt.RegionShop.Database.Model.ShopItemEnchantments;
import com.geNAZt.RegionShop.Database.Model.ShopItems;
import com.geNAZt.RegionShop.Database.Model.ShopTransaction;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Transaction;
import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopAdd extends ShopCommand {
    private final RegionShopPlugin plugin;

    public ShopAdd(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 2;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop add " + ChatColor.RED + "sellprice buyprice amount" + ChatColor.RESET + ": Add current item in hand to the shop stock"};
    }

    @Override
    public String getCommand() {
        return "add";
    }

    @Override
    public String getPermissionNode() {
        return "rs.stock.add";
    }

    @Override
    public int getNumberOfArgs() {
        return 3;
    }

    @Override
    public void execute(Player player, String[] args) {
        //Convert arguments
        Integer buy, sell, amount;

        try {
            buy = Integer.parseInt(args[1]);
            sell = Integer.parseInt(args[0]);
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Only numbers as sell, buy and amount values allowed");
            return;
        }

        //Check if the Player is inside a Region
        if (PlayerStorage.has(player)) {
            Region region = PlayerStorage.get(player);

            //Check if User is Owner of this shop
            if (region.getRegion().isOwner(player.getName())) {
                ItemStack itemInHand = player.getItemInHand();

                //Check if the User has something in his hand
                if(itemInHand == null || itemInHand.getType().getId() == 0) {
                    player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You have no item in the hand");
                    return;
                }

                //Ask Database for this Item
                List<ShopItems> item = plugin.getDatabase().find(ShopItems.class).
                        where().
                            conjunction().
                                eq("world", player.getWorld().getName()).
                                eq("region", region.getItemStorage()).
                                eq("item_id", itemInHand.getType().getId()).
                                eq("data_id", itemInHand.getData().getData()).
                                eq("durability", itemInHand.getDurability()).
                                eq("owner", player.getName()).
                                eq("custom_name", (itemInHand.getItemMeta().hasDisplayName()) ? itemInHand.getItemMeta().getDisplayName() : null).
                            endJunction().
                        findList();

                //Check if item is already in the Database
                if (item == null || item.isEmpty()) {
                    //It is new. Convert it into the Database
                    ItemConverter.toDBItem(itemInHand, player.getWorld(), player.getName(), region.getItemStorage(), buy, sell, amount);

                    //Remove it from the Player
                    player.getInventory().remove(itemInHand);

                    //Get the nice name
                    String itemName = ItemName.getDataName(itemInHand) + itemInHand.getType().toString();
                    if (itemInHand.getItemMeta().hasDisplayName()) {
                        itemName = "(" + itemInHand.getItemMeta().getDisplayName() + ")";
                    }

                    Transaction.generateTransaction(player, ShopTransaction.TransactionType.ADD, region.getName(), player.getWorld().getName(), player.getName(), itemInHand.getTypeId(), itemInHand.getAmount(), sell.doubleValue(), buy.doubleValue(), amount);

                    player.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Added "+ ChatColor.GREEN + ItemName.nicer(itemName) + ChatColor.GOLD + " to the shop.");
                    return;
                } else {
                    boolean found = false;
                    Integer itemID = 0;

                    for(ShopItems it : item) {
                        //Check if enchantments are the same
                        List<ShopItemEnchantments> enchantments = plugin.getDatabase().find(ShopItemEnchantments.class).
                                where().
                                    eq("shop_item_id", it.getId()).
                                findList();

                        Map<Enchantment, Integer> enchOnItem = itemInHand.getEnchantments();

                        if((enchantments == null || enchantments.isEmpty()) && (enchOnItem == null || enchOnItem.isEmpty())) {
                            found = true;
                            itemID = it.getId();
                            break;
                        } else {
                            if(enchantments == null || enchantments.isEmpty()) {
                                continue;
                            }

                            if (enchOnItem == null || enchOnItem.isEmpty()) {
                                continue;
                            }

                            Integer foundEnchs = 0;
                            for(Map.Entry<Enchantment, Integer> ench : enchOnItem.entrySet()) {
                                for(ShopItemEnchantments enchI : enchantments) {
                                    if(enchI.getEnchId().equals(ench.getKey().getId()) && enchI.getEnchLvl().equals(ench.getValue())) {
                                        foundEnchs++;
                                    }
                                }
                            }

                            if(foundEnchs.equals(enchOnItem.size()) && enchantments.size() == enchOnItem.size()) {
                                itemID = it.getId();
                                found = true;
                                break;
                            }
                        }
                    }

                    if(found) {
                        //Item is already added
                        player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Item already added. " + ChatColor.DARK_RED + "/shop set "+ itemID + " sellprice buyprice amount" + ChatColor.RED + " to change it.");
                        return;
                    } else {
                        //It is new. Convert it into the Database
                        ItemConverter.toDBItem(itemInHand, player.getWorld(), player.getName(), region.getItemStorage(), buy, sell, amount);

                        //Remove it from the Player
                        player.getInventory().remove(itemInHand);

                        //Get the nice name
                        String itemName = ItemName.getDataName(itemInHand) + itemInHand.getType().toString();
                        if (itemInHand.getItemMeta().hasDisplayName()) {
                            itemName = "(" + itemInHand.getItemMeta().getDisplayName() + ")";
                        }

                        Transaction.generateTransaction(player, ShopTransaction.TransactionType.ADD, region.getName(), player.getWorld().getName(), player.getName(), itemInHand.getTypeId(), itemInHand.getAmount(), sell.doubleValue(), buy.doubleValue(), amount);

                        player.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Added "+ ChatColor.GREEN + ItemName.nicer(itemName) + ChatColor.GOLD + " to the shop.");
                        return;
                    }
                }
            } else {
                //Player is not owner in this shop
                player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You are not a owner in this shop. You can not add items to it.");
                return;
            }
        }

        //Nothing of all
        player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You are not inside a shop region.");
    }
}
