package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Model.Item;
import com.geNAZt.RegionShop.Database.Model.Transaction;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import com.geNAZt.RegionShop.Util.ItemName;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class Set implements CLICommand {
    @Command(command="shop set", arguments=4, permission="rs.command.set", helpKey="Command_Set_HelpText", helpPage="owner")
    public static void set(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
            return;
        }

        Player player = (Player) sender;

        Float buy, sell;
        Integer amount, shopItemId;

        try {
            shopItemId = Integer.parseInt(args[0]);
            buy = Float.parseFloat(args[2]);
            sell = Float.parseFloat(args[1]);
            amount = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Set_InvalidArguments);
            return;
        }

        Items item = Database.getServer().
                find(Items.class).
                where().
                    eq("id", shopItemId).
                findUnique();

        if (item == null) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Set_InvalidItem);
        } else {
            if(!item.getOwner().equals(player.getName()) && !player.hasPermission("rs.bypass.set")) {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Set_InvalidItem);
                return;
            }

            item.setBuy(buy);
            item.setSell(sell);
            item.setUnitAmount(amount);

            Database.getServer().update(item);

            ItemStack itemStack = Item.fromDBItem(item);

            String dataName = ItemName.getDataName(itemStack);
            String niceItemName;
            if(dataName.endsWith(" ")) {
                niceItemName = dataName + ItemName.nicer(itemStack.getType().toString());
            } else {
                niceItemName = dataName;
            }

            if (itemStack.getItemMeta().hasDisplayName()) {
                niceItemName += "(" + itemStack.getItemMeta().getDisplayName() + ")";
            }

            Region region = item.getItemStorage().getRegions().iterator().next();

            Transaction.generateTransaction(player, com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.ADD, region.getName(), region.getWorld(), player.getName(), item.getMeta().getId().getItemID(), item.getUnitAmount(), item.getSell().doubleValue(), item.getBuy().doubleValue(), item.getUnitAmount());
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Set_Success.replace("%itemname", ItemName.nicer(niceItemName)).replace("%sell", item.getSell().toString()).replace("%buy", item.getBuy().toString()).replace("%amount", item.getUnitAmount().toString()));
        }
    }
}
