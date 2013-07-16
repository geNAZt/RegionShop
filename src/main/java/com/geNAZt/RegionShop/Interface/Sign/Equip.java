package com.geNAZt.RegionShop.Interface.Sign;

import com.geNAZt.RegionShop.Bukkit.Bridges.WorldGuardBridge;
import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.Logger;
import com.geNAZt.RegionShop.Data.Storages.PriceStorage;
import com.geNAZt.RegionShop.Data.Storages.SignEquipStorage;
import com.geNAZt.RegionShop.Database.Model.ShopEquipSign;
import com.geNAZt.RegionShop.Interface.SignCommand;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import java.util.Arrays;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 10.06.13
 */
public class Equip extends SignCommand {
    private final RegionShopPlugin plugin;

    public Equip(RegionShopPlugin pl) {
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
    public void execute(Player player, Block sign, SignChangeEvent event) {
        Logger.debug("New Shop equip Sign");

        if(!plugin.getConfig().getBoolean("interfaces.sign.equip")) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Quick Add via Signs is disabled");
            sign.breakNaturally();
            return;
        }

        String[] lines = event.getLines();

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

        if(PriceStorage.getRegion(rgnObj.getId()) != null) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Shop is a Servershop");
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

                        event.setLine(0, "Automatic Equip");
                        event.setLine(1, "to the Shop:");

                        return;
                    }
                }
            }
        }

        player.sendMessage(Chat.getPrefix() + ChatColor.RED + "No Chest in near of the Sign found");
        sign.breakNaturally();
    }
}
