package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import com.geNAZt.RegionShop.Util.ItemName;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class Result implements CLICommand {
    @Command(command="shop result", arguments=0, permission="rs.command.result", helpKey="Command_Result_HelpText", helpPage="consumer")
    public static void result(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
            return;
        }

        Player player = (Player) sender;

        Integer page = 1;

        if(args.length > 1) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Result_InvalidArguments);
                return;
            }
        }

        if (com.geNAZt.RegionShop.Data.Storage.Search.has(player)) {
            printResultPage(player, com.geNAZt.RegionShop.Data.Storage.Search.getSearchQuery(player), com.geNAZt.RegionShop.Data.Storage.Search.getSearchResult(player), page);
        } else {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Result_NoResults);
        }
    }

    public static void printResultPage(Player p, String searchQry, ConcurrentHashMap<Items, ItemStack> result, Integer page) {
        Float max = (float)result.size() / (float)7;
        Integer maxPage = (int)Math.ceil(max);
        Integer skip = (page - 1) * 7;
        Integer current = 0;

        String ench = Character.toString((char)0x2692);
        String dmg = Character.toString((char)0x26A0);
        String name = Character.toString((char)0x270E);

        if (skip > result.size()) {
            p.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Result_InvalidPage);
            return;
        }

        for(String headerLine : ConfigManager.language.Command_Result_Header) {
            p.sendMessage(ConfigManager.main.Chat_prefix + headerLine.
                    replace("%ench", ench).
                    replace("%dmg", dmg).
                    replace("%name", name).
                    replace("%page", page.toString()).
                    replace("%maxpage", maxPage.toString()).
                    replace("%search", searchQry));
        }

        for(Map.Entry<Items, ItemStack> entry : result.entrySet()) {
            current++;

            if(skip > current) {
                continue;
            }

            if(current - skip > 7) {
                return;
            }

            Items item = entry.getKey();
            ItemStack iStack = entry.getValue();

            String amount = item.getCurrentAmount().toString();
            String dataName = ItemName.getDataName(iStack);
            String niceItemName;
            if(dataName.endsWith(" ")) {
                niceItemName = dataName + ItemName.nicer(iStack.getType().toString());
            } else if(!dataName.equals("")) {
                niceItemName = dataName;
            } else {
                niceItemName = ItemName.nicer(iStack.getType().toString());
            }

            String shop;
            if(!item.getItemStorage().getRegions().isEmpty()) {
                shop = item.getItemStorage().getRegions().iterator().next().getName();
            } else {
                shop = item.getItemStorage().getChests().iterator().next().getName();
            }

            String message = ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Result_Item_Main.
                    replace("%amount", amount).
                    replace("%item", niceItemName).
                    replace("%sell", item.getSell().toString()).
                    replace("%buy", item.getBuy().toString()).
                    replace("%unitamount", item.getUnitAmount().toString()).
                    replace("%shop", shop).
                    replace("%id", (!item.getItemStorage().getRegions().isEmpty()) ? item.getId().toString() : item.getItemStorage().getChests().iterator().next().getId().toString());

            Integer perDmg = 0;
            if (iStack.getDurability() > 0 && item.getMeta().getId().getItemID() != 373 && item.getMeta().getMaxStackSize() == 1) {
                Float divide = ((float)iStack.getDurability() / (float)iStack.getType().getMaxDurability());
                perDmg = Math.round(divide * 100);
            }

            if (item.getMeta().getMaxStackSize() == 1 && perDmg > 0) {
                message += ConfigManager.language.Command_Result_Item_Dmg.replace("%dmg", dmg);
            }

            if (!iStack.getEnchantments().isEmpty()) {
                message += ConfigManager.language.Command_Result_Item_Ench.replace("%ench", ench);
            }

            if(item.getCustomName() != null) {
                message += ConfigManager.language.Command_Result_Item_Name.replace("%name", name);
            }

            p.sendMessage(message);
        }
    }
}
