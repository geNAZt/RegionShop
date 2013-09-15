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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class Set implements CLICommand {
    @Command(command="shop set", arguments=4, permission="rs.command.set", helpKey="Command_Set_HelpText", helpPage="owner")
    public void execute(Player player, String[] args) {
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
                    eq("owner", player.getName()).
                findUnique();

        if (item == null) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Set_InvalidItem);
        } else {
            item.setBuy(buy);
            item.setSell(sell);
            item.setUnitAmount(amount);

            Database.getServer().update(item);

            ItemStack itemStack = Item.fromDBItem(item);

            String itemName;
            if (itemStack.getItemMeta().hasDisplayName()) {
                itemName = ItemName.getDataName(itemStack) + itemStack.getItemMeta().getDisplayName();
            } else {
                itemName = ItemName.getDataName(itemStack) + itemStack.getType().toString();
            }

            Region region = item.getItemStorage().getRegions().iterator().next();

            Transaction.generateTransaction(player, com.geNAZt.RegionShop.Database.Table.Transaction.TransactionType.ADD, region.getName(), region.getWorld(), player.getName(), item.getMeta().getId().getItemID(), item.getUnitAmount(), item.getSell().doubleValue(), item.getBuy().doubleValue(), item.getUnitAmount());
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Set_Success.replace("%itemname", ItemName.nicer(itemName)).replace("%sell", item.getSell().toString()).replace("%buy", item.getBuy().toString()).replace("%amount", item.getUnitAmount().toString()));
        }
    }
}
