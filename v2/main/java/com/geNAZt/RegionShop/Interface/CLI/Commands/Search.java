package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Data.Struct.ParsedItem;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Model.Item;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Util.Parser;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class Search implements CLICommand {
    @Command(command="shop search", arguments=1, permission="rs.command.search", helpKey="Command_Search_HelpText", helpPage="consumer")
    public static void search(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
            return;
        }

        Player player = (Player) sender;

        String search = StringUtils.join(args, "_");

        ConcurrentHashMap<Items, ItemStack> result = new ConcurrentHashMap<Items, ItemStack>();
        Pattern r = Pattern.compile("(.*)" + search + "(.*)", Pattern.CASE_INSENSITIVE);
        ParsedItem parsedItem = Parser.parseItemID(search);

        List<Items> itemsRegion = Database.getServer().find(Items.class).
                where().
                    conjunction().
                        eq("itemStorage.regions.world", player.getWorld().getName()).
                        gt("unitAmount", 0).
                        disjunction().
                            gt("sell", 0).
                            gt("buy", 0).
                        endJunction().
                    endJunction().
                findList();

        List<Items> itemsChest = Database.getServer().find(Items.class).
                where().
                    conjunction().
                        eq("itemStorage.chests.world", player.getWorld().getName()).
                        gt("unitAmount", 0).
                        disjunction().
                            gt("sell", 0).
                            gt("buy", 0).
                        endJunction().
                    endJunction().
                findList();

        List<Items> items = new ArrayList<Items>(itemsRegion);
        items.addAll(itemsChest);

        if(!items.isEmpty()) {
            for(Items item : items) {
                ItemStack iStack = Item.fromDBItem(item);

                String dataName = ItemName.getDataName(iStack);
                String niceItemName;
                if(dataName.endsWith(" ")) {
                    niceItemName = dataName + ItemName.nicer(iStack.getType().toString());
                } else if(!dataName.equals("")) {
                    niceItemName = dataName;
                } else {
                    niceItemName = ItemName.nicer(iStack.getType().toString());
                }

                Matcher m = r.matcher(niceItemName);

                if((parsedItem != null && item.getMeta().getId().getItemID().equals(parsedItem.itemID) && item.getMeta().getId().getDataValue().equals(parsedItem.dataValue))) {
                    result.put(item, iStack);
                    continue;
                }

                if (m.matches()) {
                    result.put(item, iStack);
                }
            }
        }

        if(!result.isEmpty()) {
            if(com.geNAZt.RegionShop.Data.Storage.Search.has(player)) {
                com.geNAZt.RegionShop.Data.Storage.Search.remove(player);
            }

            com.geNAZt.RegionShop.Data.Storage.Search.put(player, search, result);
            Result.printResultPage(player, search, result, 1);
        } else {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Search_NoHit);
        }
    }
}
