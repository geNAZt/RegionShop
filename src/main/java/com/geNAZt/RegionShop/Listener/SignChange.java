package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Sign.Equip;
import com.geNAZt.RegionShop.Util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

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
    private final Equip signEquip;

    public SignChange(RegionShopPlugin pl) {
        plugin = pl;

        signEquip = new Equip(pl);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onSignChange(SignChangeEvent e) {
        Player p = e.getPlayer();

        Block signBlock = e.getBlock();
        if (signBlock == null) {
            plugin.getLogger().warning("Player " + p.getName() + " tried to generate a fake sign.");
            return;
        }

        if(e.getLine(0).contains("[RegionShop]")) {
            if(e.getLine(1).contains("equip")) {
                signEquip.execute(p, signBlock, e.getLines());
            } else {
                p.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid RegionShop Sign");
                e.getBlock().breakNaturally();
            }
        }
    }


}
