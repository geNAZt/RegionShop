package com.geNAZt.RegionShop.Interface.Sign.Commands;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Data.Storage.InRegion;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Model.Item;
import com.geNAZt.RegionShop.Database.Table.CustomerSign;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.Interface.Sign.Command;
import com.geNAZt.RegionShop.Interface.Sign.SignCommand;
import com.geNAZt.RegionShop.Util.ItemName;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 10.06.13
 */
public class Customer implements SignCommand {
    @Command(command="customer", permission="rs.sign.customer")
    public static void customer(SignChangeEvent event) {
        if(!InRegion.has(event.getPlayer())) {
            event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sign_Customer_NotInRegion);
            event.getBlock().breakNaturally();
            return;
        }

        //Check if User is owner in this region
        Region region = InRegion.get(event.getPlayer());
        java.util.List<com.geNAZt.RegionShop.Database.Table.Player> playerList = region.getOwners();
        boolean isOwner = false;

        for(com.geNAZt.RegionShop.Database.Table.Player player1 : playerList) {
            if(player1.getName().equals(event.getPlayer().getName().toLowerCase())) {
                isOwner = true;
            }
        }

        if(!isOwner) {
            event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sign_Customer_NotOwner);
            event.getBlock().breakNaturally();
            return;
        }

        String[] lines = event.getLines();
        Integer itemID;

        try {
            itemID = Integer.parseInt(lines[2]);
        } catch(NumberFormatException e) {
            event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sign_Customer_InvalidItemID);
            event.getBlock().breakNaturally();
            return;
        }

        Items item = Database.getServer().find(Items.class).
            where().
                eq("id", itemID).
            findUnique();

        if(item == null) {
            event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sign_Customer_NotFoundItemID);
            event.getBlock().breakNaturally();
            return;
        }

        if(!item.getOwner().equals(event.getPlayer().getName()) && !item.getItemStorage().isServershop()) {
            event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sign_Customer_NotYourItem);
            event.getBlock().breakNaturally();
            return;
        }

        //Get the nice name
        ItemStack itemStack = Item.fromDBItem(item);
        String itemName = ItemName.getDataName(itemStack) + itemStack.getType().toString();
        if (itemStack.getItemMeta().hasDisplayName()) {
            itemName = "(" + itemStack.getItemMeta().getDisplayName() + ")";
        }

        for(Integer line = 0; line < 4; line++) {
            event.setLine(line, ConfigManager.language.Sign_Customer_SignText.get(line).
                    replace("%id", item.getId().toString()).
                    replace("%itemname", ItemName.nicer(itemName)).
                    replace("%amount", item.getUnitAmount().toString()).
                    replace("%sell", item.getSell().toString()).
                    replace("%buy", item.getBuy().toString()));
        }

        CustomerSign customerSign = new CustomerSign();
        customerSign.setOwner(event.getPlayer().getName());
        customerSign.setRegion(region);
        customerSign.setX(event.getBlock().getX());
        customerSign.setY(event.getBlock().getY());
        customerSign.setZ(event.getBlock().getZ());
        customerSign.setItem(item);

        Database.getServer().save(customerSign);
    }
}
