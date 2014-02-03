package net.cubespace.RegionShop.Interface.Sign.Commands;

import com.j256.ormlite.dao.ForeignCollection;
import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Data.Storage.InRegion;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Repository.ItemRepository;
import net.cubespace.RegionShop.Database.Table.CustomerSign;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Database.Table.PlayerOwnsRegion;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Interface.Sign.Command;
import net.cubespace.RegionShop.Interface.Sign.SignCommand;
import net.cubespace.RegionShop.Util.ItemName;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class Customer implements SignCommand {
    @Command(command="customer", permission="rs.sign.customer")
    public static void customer(final SignChangeEvent event) {
        if(!InRegion.has(event.getPlayer())) {
            event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sign_Customer_NotInRegion);
            event.getBlock().breakNaturally();
            return;
        }

        //Check if User is owner in this region
        Region region = InRegion.get(event.getPlayer());
        ForeignCollection<PlayerOwnsRegion> playerList = region.getOwners();
        boolean isOwner = false;

        for(PlayerOwnsRegion player1 : playerList) {
            if(player1.getPlayer().getName().equals(event.getPlayer().getName().toLowerCase())) {
                isOwner = true;
            }
        }

        if(!isOwner) {
            event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sign_Customer_NotOwner);
            Plugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    event.getBlock().breakNaturally();             }
            });

            return;
        }

        String[] lines = event.getLines();
        Integer itemID;

        try {
            itemID = Integer.parseInt(lines[2]);
        } catch(NumberFormatException e) {
            event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sign_Customer_InvalidItemID);
            Plugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    event.getBlock().breakNaturally();    }
            });
            return;
        }

        Items item = null;
        try {
            item = Database.getDAO(Items.class).queryForId(itemID);
        } catch (SQLException e) {
            Logger.error("Could not get Item", e);
        }

        if(item == null) {
            event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sign_Customer_NotFoundItemID);
            Plugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    event.getBlock().breakNaturally();    }
            });
            return;
        }

        if(!item.getOwner().equals(event.getPlayer().getName()) && !item.getItemStorage().isServershop()) {
            event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Sign_Customer_NotYourItem);
            Plugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    event.getBlock().breakNaturally();
                }
            });
            return;
        }

        //Get the nice name
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

        for(Integer line = 0; line < 4; line++) {
            event.setLine(line, ConfigManager.language.Sign_Customer_SignText.get(line).
                    replace("%id", item.getId().toString()).
                    replace("%itemname", ItemName.nicer(niceItemName)).
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

        try {
            Database.getDAO(CustomerSign.class).create(customerSign);
        } catch (SQLException e) {
            Logger.error("Could not save Customer Sign", e);
            Plugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    event.getBlock().breakNaturally();
                }
            });
        }
    }
}
