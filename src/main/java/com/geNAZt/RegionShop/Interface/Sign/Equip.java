package com.geNAZt.RegionShop.Interface.Sign;

import com.geNAZt.RegionShop.Bridges.WorldGuardBridge;
import com.geNAZt.RegionShop.Interface.SignCommand;
import com.geNAZt.RegionShop.Model.ShopEquipSign;
import com.geNAZt.RegionShop.Storages.SignEquipStorage;
import com.geNAZt.RegionShop.Util.Chat;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import org.apache.commons.lang.StringUtils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.block.Sign;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 10.06.13
 */
public class Equip extends SignCommand {
    private Plugin plugin;

    public Equip(Plugin pl) {
        plugin = pl;
    }

    @Override
    public String getCommand() {
        return "equip";
    }

    @Override
    public String getPermissionNode() {
        return "rs.equip";
    }

    @Override
    public void execute(Player player, Block sign, String[] lines) {
        if(!plugin.getConfig().getBoolean("interfaces.sign.equip")) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Quick Add via Signs is disabled");
            sign.breakNaturally();
            return;
        }

        String region = StringUtils.join(Arrays.copyOfRange(lines, 2, 4), "");
        ProtectedRegion rgnObj = WorldGuardBridge.convertShopNameToRegion(region);

        if(rgnObj == null) {
            rgnObj = WorldGuardBridge.getRegionByString(region, player.getWorld());
        }

        if(rgnObj == null) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid Shopname or Region: " + region);
            sign.breakNaturally();
            return;
        }

        if(!rgnObj.isOwner(player.getName())) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not an owner of this Shop");
            sign.breakNaturally();
            return;
        }

        for(Integer y = -1; y<2; y++) {
            for(Integer x = -1; x<2; x++) {
                for(Integer z = -1; z<2; z++) {
                    Block rl = sign.getRelative(x, y, z);

                    if (rl.getType().equals(Material.CHEST)) {
                        ShopEquipSign equipSign = new ShopEquipSign();
                        equipSign.setOwner(player.getName());
                        equipSign.setShop(rgnObj.getId());
                        equipSign.setWorld(player.getWorld().getName());
                        equipSign.setX(sign.getX());
                        equipSign.setY(sign.getY());
                        equipSign.setZ(sign.getZ());

                        plugin.getDatabase().save(equipSign);

                        SignEquipStorage.addSign(sign, player.getName(), rgnObj.getId(), player.getWorld().getName());
                        player.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "All Items in this Chest will go to " + ChatColor.GREEN + region);

                        Sign sgn = (Sign) sign.getState();
                        sgn.setLine(0, "Automatic Equip");
                        sgn.setLine(1, "to a Shop of");
                        sgn.setLine(2, player.getDisplayName());
                        sgn.setLine(3, region);
                        sgn.update(true);

                        return;
                    }
                }
            }
        }

        player.sendMessage(Chat.getPrefix() + ChatColor.RED + "No Chest in near of the Sign found");
        sign.breakNaturally();
        return;
    }
}
