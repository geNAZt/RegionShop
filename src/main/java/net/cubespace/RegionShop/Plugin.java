package net.cubespace.RegionShop;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

/**
 * User: frdmn
 * Date: 26/10/13
 * Time: 23:09
 */
public class Plugin extends JavaPlugin {
    public static Integer itemId = 0;
    public static Short damageValue = 0;
    public static ArrayList<String> foundItems = new ArrayList<String>();

    public void onEnable() {
        getServer().getScheduler().runTaskTimer(this, new Runnable() {
            @Override
            public void run() {
                for(Integer i = 0; i < 5000; i++) {
                    try {
                        ItemStack itemStack = new ItemStack(itemId, 1);
                        itemStack.setDurability(damageValue);

                        Item item = Bukkit.getWorld("survival").dropItem(new Location(Bukkit.getWorld("survival"), 1519D, 79D, 1742D), itemStack);

                        if(!foundItems.contains(item.getItemStack().getTypeId() + ":" + item.getItemStack().getDurability())) {
                            getLogger().info("Found maybe valid Item - " + item.getItemStack().getTypeId() + ":" + item.getItemStack().getDurability());
                            foundItems.add(item.getItemStack().getTypeId() + ":" + item.getItemStack().getDurability());
                        }

                        item.remove();
                    } catch(Exception e) {

                    }

                    damageValue++;

                    if(damageValue == Short.MAX_VALUE) {
                        itemId++;
                        damageValue = 0;
                    }

                    if(itemId == 10000) {
                        setEnabled(false);
                    }
                }
            }
        }, 1, 1);
    }
}