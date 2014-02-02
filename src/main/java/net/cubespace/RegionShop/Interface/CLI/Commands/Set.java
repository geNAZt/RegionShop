package net.cubespace.RegionShop.Interface.CLI.Commands;

import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Repository.ItemRepository;
import net.cubespace.RegionShop.Database.Repository.TransactionRepository;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Interface.CLI.CLICommand;
import net.cubespace.RegionShop.Interface.CLI.Command;
import net.cubespace.RegionShop.Util.ItemName;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

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

        Items item = null;
        try {
            item = Database.getDAO(Items.class).queryBuilder().
                    where().
                        eq("id", shopItemId).
                    queryForFirst();
        } catch (SQLException e) {
            Logger.error("Could not get item", e);
        }

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

            try {
                Database.getDAO(Items.class).update(item);
            } catch (SQLException e) {
                Logger.warn("Could not update item", e);
            }

            ItemStack itemStack = ItemRepository.fromDBItem(item);

            String dataName = ItemName.getDataName(itemStack);
            String niceItemName;
            if(dataName.endsWith(" ")) {
                niceItemName = dataName + ItemName.nicer(itemStack.getType().toString());
            } else if(!dataName.equals("")) {
                niceItemName = dataName;
            } else {
                niceItemName = ItemName.nicer(itemStack.getType().toString());
            }

            if (itemStack.getItemMeta().hasDisplayName()) {
                niceItemName += "(" + itemStack.getItemMeta().getDisplayName() + ")";
            }

            Region region = item.getItemStorage().getRegions().iterator().next();

            TransactionRepository.generateTransaction(player, net.cubespace.RegionShop.Database.Table.Transaction.TransactionType.ADD, region.getName(), region.getWorld(), player.getName(), item.getMeta().getItemID(), item.getUnitAmount(), item.getSell().doubleValue(), item.getBuy().doubleValue(), item.getUnitAmount());
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Set_Success.replace("%itemname", ItemName.nicer(niceItemName)).replace("%sell", item.getSell().toString()).replace("%buy", item.getBuy().toString()).replace("%amount", item.getUnitAmount().toString()));
        }
    }
}
