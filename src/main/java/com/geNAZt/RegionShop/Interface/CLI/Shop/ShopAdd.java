package com.geNAZt.RegionShop.Interface.CLI.Shop;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.ItemName;
import com.geNAZt.RegionShop.Core.Add;
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

                Integer itemID;
                if((itemID = Add.add(itemInHand, player, region, sell, buy, amount)) == 0) {
                    //Remove the item from the Player
                    player.getInventory().remove(itemInHand);

                    //Get the nice name
                    String itemName = ItemName.getDataName(itemInHand) + itemInHand.getType().toString();
                    if (itemInHand.getItemMeta().hasDisplayName()) {
                        itemName = "(" + itemInHand.getItemMeta().getDisplayName() + ")";
                    }

                    player.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Added "+ ChatColor.GREEN + ItemName.nicer(itemName) + ChatColor.GOLD + " to the shop.");
                    return;
                } else {
                    player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Item already added. " + ChatColor.DARK_RED + "/shop set "+ itemID + " sellprice buyprice amount" + ChatColor.RED + " to change it.");
                    return;
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
