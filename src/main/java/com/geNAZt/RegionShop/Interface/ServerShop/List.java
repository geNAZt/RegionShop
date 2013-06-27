package com.geNAZt.RegionShop.Interface.ServerShop;

import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.ServerShop.Price;
import com.geNAZt.RegionShop.ServerShop.PriceStorage;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemName;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 22.06.13
 */
public class List extends ShopCommand {
    public List(Plugin plugin) {

    }

    @Override
    public int getHelpPage() {
        return 0;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop server list" + ChatColor.RESET + ": List items in the Servershop"};
    }

    @Override
    public String getCommand() {
        return "list";
    }

    @Override
    public String getPermissionNode() {
        return "rs.server.list";
    }

    @Override
    public int getNumberOfArgs() {
        return 0;
    }

    @Override
    public void execute(Player player, String[] args) {
        ConcurrentHashMap<ItemStack, Price> itemPrices = PriceStorage.getAll();

        Float max = (float)itemPrices.size() / (float)7;
        Integer maxPage = (int)Math.ceil(max);
        Integer page = 1;
        Integer current = 0;
        Integer skip = (page - 1) * 7;

        if(args.length > 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED +  "Only numbers as page value allowed");
                return;
            }
        }

        player.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "List of items inside the Servershop -- " + ChatColor.GOLD + "Page " + ChatColor.RED + page + ChatColor.GOLD + "/" + ChatColor.RED + maxPage + ChatColor.YELLOW + " --");
        player.sendMessage(Chat.getPrefix() + " ");

        for(Map.Entry<ItemStack, Price> itemPrice : itemPrices.entrySet()) {
            current++;

            if(skip > current) {
                continue;
            }

            if(current - skip > 7) {
                break;
            }

            ItemStack iStack = itemPrice.getKey();
            Price price = itemPrice.getValue();

            String niceItemName = ItemName.nicer(iStack.getType().toString());
            String itemName = ItemName.getDataName(iStack) + niceItemName;
            String message = Chat.getPrefix() + ChatColor.DARK_GREEN + "1x" + " " + ChatColor.GREEN + itemName + ChatColor.DARK_GREEN + " for (S)" + ChatColor.GREEN + price.getCurrentSell() + "$" + ChatColor.DARK_GREEN + " (B)" + ChatColor.GREEN + price.getCurrentBuy() + "$";
            player.sendMessage(message);
        }

        if(page < maxPage) {
            player.sendMessage(Chat.getPrefix() + ChatColor.GREEN +"/shop server list "+ (page+1) + ChatColor.GOLD + " for the next page");
        }
    }
}
