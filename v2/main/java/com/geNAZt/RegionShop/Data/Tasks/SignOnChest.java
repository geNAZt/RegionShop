package com.geNAZt.RegionShop.Data.Tasks;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Model.Item;
import com.geNAZt.RegionShop.Database.Table.Chest;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.ItemName;
import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.List;

/**
 * Created for ME :D
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 04.10.13
 */
public class SignOnChest extends BukkitRunnable {
    @Override
    public void run() {
        final List<Chest> chestList = Database.getServer().find(Chest.class).findList();

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(RegionShopPlugin.getInstance(), new BukkitRunnable() {
            @Override
            public void run() {
                for (Chest chest : chestList) {
                    Iterator itemsIterator = chest.getItemStorage().getItems().iterator();
                    if (!itemsIterator.hasNext()) {
                        RegionShopPlugin.getInstance().getLogger().warning("Found Chest without item. Maybe wrong deletion: " + chest.getId());
                        continue;
                    }

                    final Items items = chest.getItemStorage().getItems().iterator().next();
                    final ItemStack itemStack = Item.fromDBItem(items);
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
                                replace("%player", chest.getOwners().iterator().next().getName()).
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
