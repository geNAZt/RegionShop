package net.cubespace.RegionShop.Interface.CLI.Commands;

import com.j256.ormlite.dao.ForeignCollection;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Data.Storage.InRegion;
import net.cubespace.RegionShop.Database.Table.PlayerOwnsRegion;
import net.cubespace.RegionShop.Interface.CLI.CLICommand;
import net.cubespace.RegionShop.Interface.CLI.Command;
import net.cubespace.RegionShop.Util.ItemName;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Add implements CLICommand {
    @Command(command="shop add", arguments=3, permission="rs.command.add", helpKey="Command_Add_HelpText", helpPage="owner")
    public static void add(CommandSender sender, String[] args) {
        //This command is not enabled for console
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
            return;
        }

        //Cast to player
        Player player = (Player) sender;

        //Convert arguments
        Float buy, sell;
        Integer amount;

        try {
            buy = Float.parseFloat(args[1]);
            sell = Float.parseFloat(args[0]);
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Add_InvalidArguments);
            return;
        }

        //Check if User is inside Region
        if(!InRegion.has(player)) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Add_NotInRegion);
            return;
        }

        //Check if User is owner in this region
        ForeignCollection<PlayerOwnsRegion> playerList = InRegion.get(player).getOwners();
        boolean isOwner = false;

        for(PlayerOwnsRegion player1 : playerList) {
            if(player1.getPlayer().getName().equals(player.getName().toLowerCase())) {
                isOwner = true;
            }
        }

        if(player.hasPermission("rs.bypass.add")) {
            isOwner = true;
        }

        if(!isOwner) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Add_NotOwnerInThisRegion);
            return;
        }

        ItemStack itemInHand = player.getItemInHand();

        //Check if the User has something in his hand
        if(itemInHand == null || itemInHand.getType().getId() == 0) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Add_NoItemInHand);
            return;
        }

        //Store the item
        Integer itemID;
        if((itemID = net.cubespace.RegionShop.Core.Add.add(itemInHand, player, InRegion.get(player), sell, buy, amount)) == 0) {
            //Remove the item from the Player
            player.getInventory().removeItem(itemInHand);

            //Get the nice name
            String dataName = ItemName.getDataName(itemInHand);
            String niceItemName;
            if(dataName.endsWith(" ")) {
                niceItemName = dataName + ItemName.nicer(itemInHand.getType().toString());
            } else if(!dataName.equals("")) {
                niceItemName = dataName;
            } else {
                niceItemName = ItemName.nicer(itemInHand.getType().toString());
            }

            if (itemInHand.getItemMeta().hasDisplayName()) {
                niceItemName = "(" + itemInHand.getItemMeta().getDisplayName() + ")";
            }

            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Add_AddedItem.replace("%item", ItemName.nicer(niceItemName)));
        } else {
            if(itemID != -1) {
                player.sendMessage(ConfigManager.main.Chat_prefix  + ConfigManager.language.Command_Add_ChangeItem.replace("%itemid", itemID.toString()));
            }
        }
    }
}
