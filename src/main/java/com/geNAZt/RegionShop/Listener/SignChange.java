package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.ChestUtil;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 09.06.13
 */
public class SignChange implements Listener {
    private final RegionShopPlugin plugin;

    public SignChange(RegionShopPlugin pl) {
        plugin = pl;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();

        if(e.getLine(0).contains("[RegionShop]")) {
            //Check if it is a real sign
            Block signBlock= e.getBlock();
            if (signBlock == null) {
                plugin.getLogger().warning("Player " + p.getName() + " tried to generate a fake sign.");
                return;
            }

            //Check if inside a Shop


            for(Integer y = -1; y<2; y++) {
                for(Integer x = -1; x<2; x++) {
                    for(Integer z = -1; z<2; z++) {
                        Block rl = signBlock.getRelative(x, y, z);

                        if (rl.getType().equals(Material.CHEST)) {
                            if (ChestUtil.checkForDblChest(rl)) {
                                p.sendMessage("Found Dbl ChestUtil at " + rl.getLocation().toString());
                            } else {
                                p.sendMessage("Found ChestUtil at " + rl.getLocation().toString());
                            }

                            return;
                        }
                    }
                }
            }
        }
    }


}
