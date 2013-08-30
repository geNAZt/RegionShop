package com.geNAZt.RegionShop.Interface.CLI.Shop;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.ItemName;
import com.geNAZt.RegionShop.Bukkit.Util.Parser;
import com.geNAZt.RegionShop.Data.Storages.PriceStorage;
import com.geNAZt.RegionShop.Data.Storages.SearchStorage;
import com.geNAZt.RegionShop.Data.Struct.ParsedItem;
import com.geNAZt.RegionShop.Data.Struct.Price;
import com.geNAZt.RegionShop.Database.ItemConverter;
import com.geNAZt.RegionShop.Database.Model.ShopItems;
import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopSearch extends ShopCommand {
    private final RegionShopPlugin plugin;

    public ShopSearch(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 1;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop search " + ChatColor.RED + "ItemID/ItemName" + ChatColor.RESET + ": Search for " + ChatColor.RED + "ItemID/ItemName"};
    }

    @Override
    public String getCommand() {
        return "search";
    }

    @Override
    public String getPermissionNode() {
        return "rs.search";
    }

    @Override
    public int getNumberOfArgs() {
        return 1;
    }

    @Override
    public void execute(Player player, String[] args) {
        String search = StringUtils.join(args, "_");

        ConcurrentHashMap<ShopItems, ItemStack> result = new ConcurrentHashMap<ShopItems, ItemStack>();
        Pattern r = Pattern.compile("(.*)" + search + "(.*)", Pattern.CASE_INSENSITIVE);
        ParsedItem parsedItem = Parser.parseItemID(search);

        List<ShopItems> items = plugin.getDatabase().find(ShopItems.class).findList();
        if(items != null) {
            for(ShopItems item : items) {
                ItemStack iStack = ItemConverter.fromDBItem(item);

                String searchString = ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString());

                Matcher m = r.matcher(searchString);

                if((parsedItem != null && item.getItemID().equals(parsedItem.itemID) && item.getDataID().equals(parsedItem.dataValue)) && ((item.getSell() != 0 || item.getBuy() != 0) && item.getUnitAmount() > 0)) {
                    result.put(item, iStack);
                    continue;
                }

                if (m.matches() && ((item.getSell() != 0 || item.getBuy() != 0) && item.getUnitAmount() > 0)) {
                    result.put(item, iStack);
                }
            }
        }

        if(plugin.getConfig().getBoolean("include_servershops_into_search", true)) {
            ConcurrentHashMap<String, ConcurrentHashMap<ItemStack, Price>> itemsInServershops = PriceStorage.getAll();

            for(Map.Entry<String, ConcurrentHashMap<ItemStack, Price>> regionInServershop : itemsInServershops.entrySet()) {
                for(Map.Entry<ItemStack, Price> itemsInServershop : regionInServershop.getValue().entrySet()) {
                    String searchString = ItemName.getDataName(itemsInServershop.getKey()) + ItemName.nicer(itemsInServershop.getKey().getType().toString());
                    Matcher m = r.matcher(searchString);

                    if((parsedItem != null && itemsInServershop.getKey().getTypeId() == parsedItem.itemID && itemsInServershop.getKey().getData().getData() == parsedItem.dataValue && ((itemsInServershop.getValue().getCurrentSell() != 0 || itemsInServershop.getValue().getCurrentBuy() != 0)))) {
                        ShopItems items1 = new ShopItems();
                        items1.setOwner("server");
                        items1.setRegion(regionInServershop.getKey());
                        items1.setWorld(player.getWorld().getName());
                        items1.setUnitAmount(1);
                        items1.setBuy(((Double) itemsInServershop.getValue().getCurrentBuy()).intValue());
                        items1.setSell(((Double) itemsInServershop.getValue().getCurrentSell()).intValue());
                        items1.setCurrentAmount(-1);

                        result.put(items1, itemsInServershop.getKey());
                        continue;
                    }

                    if (m.matches() && ((itemsInServershop.getValue().getCurrentSell() != 0 || itemsInServershop.getValue().getCurrentBuy() != 0))) {
                        ShopItems items1 = new ShopItems();
                        items1.setOwner("server");
                        items1.setRegion(regionInServershop.getKey());
                        items1.setWorld(player.getWorld().getName());
                        items1.setUnitAmount(1);
                        items1.setBuy(((Double) itemsInServershop.getValue().getCurrentBuy()).intValue());
                        items1.setSell(((Double) itemsInServershop.getValue().getCurrentSell()).intValue());
                        items1.setCurrentAmount(-1);

                        result.put(items1, itemsInServershop.getKey());
                    }
                }
            }
        }

        if(!result.isEmpty()) {
            if(SearchStorage.hasPlayer(player)) {
                SearchStorage.removeAllPlayer(player);
            }

            SearchStorage.putSearchResults(player, search, result);
            ShopResult.printResultPage(player, search, result, 1);

        } else {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "No items found for your search");
        }
    }
}
