package net.cubespace.RegionShop.Data.Tasks;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Repository.ItemRepository;
import net.cubespace.RegionShop.Database.Table.Chest;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Util.ItemName;
import net.cubespace.RegionShop.Util.Logger;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class SignOnChest extends BukkitRunnable {
    @Override
    public void run() {
        final List<Chest> chestList;
        try {
            chestList = Database.getDAO(Chest.class).queryForAll();
        } catch (SQLException e) {
            Logger.error("Could not get Chest list", e);
            return;
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                for (Chest chest : chestList) {
                    Iterator itemsIterator = chest.getItemStorage().getItems().iterator();
                    if (!itemsIterator.hasNext()) {
                        Logger.warn("Found Chest without item. Maybe wrong deletion: " + chest.getId());
                        continue;
                    }

                    final Items items = chest.getItemStorage().getItems().iterator().next();
                    final ItemStack itemStack = ItemRepository.fromDBItem(items);
                    itemStack.setAmount(1);

                    Sign sign = (Sign) Bukkit.getWorld(chest.getWorld()).getBlockAt(chest.getSignX(), chest.getSignY(), chest.getSignZ()).getState();

                    //Get the nice name
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


                    for (Integer line = 0; line < 4; line++) {
                        sign.setLine(line, ConfigManager.language.Sign_Shop_SignText.get(line).
                                replace("%player", chest.getOwners().iterator().next().getPlayer().getName()).
                                replace("%itemname", ItemName.nicer(niceItemName)).
                                replace("%amount", items.getUnitAmount().toString()).
                                replace("%sell", items.getSell().toString()).
                                replace("%buy", items.getBuy().toString()));
                    }

                    sign.update();
                }

            }
        });
    }
}
