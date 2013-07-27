package com.geNAZt.RegionShop.Interface.Sign;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.ItemName;
import com.geNAZt.RegionShop.Bukkit.Util.Logger;
import com.geNAZt.RegionShop.Bukkit.Util.Parser;
import com.geNAZt.RegionShop.Data.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Data.Storages.PriceStorage;
import com.geNAZt.RegionShop.Data.Struct.ParsedItem;
import com.geNAZt.RegionShop.Data.Struct.Price;
import com.geNAZt.RegionShop.Data.Struct.Region;
import com.geNAZt.RegionShop.Database.Model.ShopAddSign;
import com.geNAZt.RegionShop.Database.Model.ShopCustomerSign;
import com.geNAZt.RegionShop.Interface.SignCommand;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 10.06.13
 */
public class Customer extends SignCommand {
    private final RegionShopPlugin plugin;

    public Customer(RegionShopPlugin pl) {
        plugin = pl;
    }

    @Override
    public String getCommand() {
        return "customer";
    }

    @Override
    public String getPermissionNode() {
        return "rs.customersign";
    }

    @Override
    public void execute(Player player, Block sign, SignChangeEvent event) {
        if(!plugin.getConfig().getBoolean("interfaces.sign.customer")) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Customer Signs are disabled");
            sign.breakNaturally();
            return;
        }

        if(!PlayerStorage.has(event.getPlayer())) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not inside a Shop");
            sign.breakNaturally();
            return;
        }

        Region playerRegion = PlayerStorage.get(event.getPlayer());
        if(!playerRegion.getRegion().isOwner(event.getPlayer().getName())) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not a owner of this Shop");
            sign.breakNaturally();
            return;
        }

        if(PriceStorage.getRegion(playerRegion.getRegion().getId()) != null) {
            if(!player.hasPermission("rs.server.customersign")) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Shop is a Servershop");
                sign.breakNaturally();
                return;
            }

            String[] lines = event.getLines();
            ParsedItem parsedItem = Parser.parseItemID(lines[2]);

            ConcurrentHashMap<ItemStack, Price> itemsInShop = PriceStorage.getRegion(playerRegion.getRegion().getId());
            ItemStack itemStack = new ItemStack(parsedItem.itemID, 1);

            Byte compare = 0;
            if(!parsedItem.dataValue.equals(compare)) {
                itemStack.getData().setData(parsedItem.dataValue);
            }

            if(!itemsInShop.containsKey(itemStack)) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED + "This Item is not in this ServerShop");
                sign.breakNaturally();
                return;
            }

            Price price = itemsInShop.get(itemStack);

            //Get the nice name
            String itemName = ItemName.getDataName(itemStack) + itemStack.getType().toString();
            if (itemStack.getItemMeta().hasDisplayName()) {
                itemName = "(" + itemStack.getItemMeta().getDisplayName() + ")";
            }

            event.setLine(0, itemName);
            event.setLine(1, "1x for");
            event.setLine(2, "B " + price.getCurrentSell() + "$:S " + price.getCurrentBuy() + "$");
            event.setLine(3, " ");

            ShopCustomerSign customerSign = new ShopCustomerSign();
            customerSign.setServershop(true);
            customerSign.setOwner(event.getPlayer().getName());
            customerSign.setShop(playerRegion.getRegion().getId());
            customerSign.setWorld(event.getPlayer().getWorld().getName());
            customerSign.setX(sign.getX());
            customerSign.setY(sign.getY());
            customerSign.setZ(sign.getZ());
            customerSign.setItemid(parsedItem.itemID);
            customerSign.setDatavalue(parsedItem.dataValue);

            plugin.getDatabase().save(customerSign);
        } else {
            /*
            String[] lines = event.getLines();
            Pattern p = Pattern.compile(SELL_REG);
            Matcher matcher = p.matcher(lines[2]);
            if(matcher.find()) {
                Integer amount, sell, buy;

                try {
                    Logger.debug(matcher.toMatchResult().toString());

                    amount = Integer.parseInt(matcher.group(1));
                    sell = Integer.parseInt(matcher.group(2));
                    buy = Integer.parseInt(matcher.group(3));
                } catch (NumberFormatException e) {
                    player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid Number");
                    sign.breakNaturally();
                    return;
                }

                Logger.info("New Sell Sign for " + playerRegion.getName() + "(" + event.getPlayer().getName() + ") - " + amount + ":" + sell + ":" + buy);

                Block blk = event.getBlock();

                ShopAddSign sellSign = new ShopAddSign();
                sellSign.setOwner(event.getPlayer().getName());
                sellSign.setShop(playerRegion.getRegion().getId());
                sellSign.setWorld(event.getPlayer().getWorld().getName());
                sellSign.setAmount(amount);
                sellSign.setSell(sell);
                sellSign.setBuy(buy);
                sellSign.setX(blk.getX());
                sellSign.setY(blk.getY());
                sellSign.setZ(blk.getZ());

                plugin.getDatabase().save(sellSign);

                event.setLine(0, "Sell / Buy " + amount + "x");
                event.setLine(1, "for S " + sell + " B " + buy);
                event.setLine(2, "Hit with item");
                event.setLine(3, "to add");
            } else {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid Sell Pattern. Must be <amount>:<sellprice>:<buyprice>");
                sign.breakNaturally();
            } */
        }
    }
}
