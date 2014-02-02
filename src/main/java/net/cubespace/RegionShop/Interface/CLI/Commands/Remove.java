package net.cubespace.RegionShop.Interface.CLI.Commands;

import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Repository.ItemRepository;
import net.cubespace.RegionShop.Database.Repository.TransactionRepository;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Interface.CLI.CLICommand;
import net.cubespace.RegionShop.Interface.CLI.Command;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Remove implements CLICommand {
    @Command(command="shop remove", arguments=1, helpKey="Command_Remove_HelpText", helpPage="owner", permission="rs.command.remove")
    public static void remove(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
            return;
        }

        Player player = (Player) sender;

        Integer shopItemId = 0;

        try {
            shopItemId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Remove_InvalidArguments);
            return;
        }

        Items item = null;
        try {
            item = Database.getDAO(Items.class).queryBuilder().
                        where().
                            eq("id", shopItemId).
                        queryForFirst();
        } catch (SQLException e) {
            Logger.warn("Could not get Item", e);
        }

        if(item != null) {
            if(!item.getOwner().equals(player.getName()) && !player.hasPermission("rs.bypass.remove")) {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Remove_NotYourItem);
                return;
            }

            ItemStack iStack = ItemRepository.fromDBItem(item);
            iStack.setAmount(item.getCurrentAmount());

            Region region = item.getItemStorage().getRegions().iterator().next();

            HashMap<Integer, ItemStack> notFitItems = player.getInventory().addItem(iStack);
            if (!notFitItems.isEmpty()) {
                for(Map.Entry<Integer, ItemStack> notFitItem : notFitItems.entrySet()) {
                    item.setCurrentAmount(item.getCurrentAmount() - notFitItem.getValue().getAmount());
                }

                try {
                    Database.getDAO(Items.class).update(item);
                } catch (SQLException e) {
                    Logger.error("Could not update Item", e);
                }

                TransactionRepository.generateTransaction(player, net.cubespace.RegionShop.Database.Table.Transaction.TransactionType.REMOVE, region.getRegion(), region.getWorld(), item.getOwner(), iStack.getTypeId(), iStack.getAmount(), item.getSell().doubleValue(), item.getBuy().doubleValue(), item.getUnitAmount());

                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Remove_NotAllItemsFit);
                return;
            }

            try {
                Database.getDAO(Items.class).delete(item);
            } catch (SQLException e) {
                Logger.error("Could not delete Item", e);
            }

            TransactionRepository.generateTransaction(player, net.cubespace.RegionShop.Database.Table.Transaction.TransactionType.REMOVE, region.getRegion(), region.getWorld(), item.getOwner(), iStack.getTypeId(), iStack.getAmount(), item.getSell().doubleValue(), item.getBuy().doubleValue(), item.getUnitAmount());
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Remove_Success);
        } else {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Remove_NotFound);
        }
    }
}
