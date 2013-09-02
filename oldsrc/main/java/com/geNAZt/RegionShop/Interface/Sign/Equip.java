package com.geNAZt.RegionShop.Interface.Sign;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.Logger;
import com.geNAZt.RegionShop.Data.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Data.Storages.PriceStorage;
import com.geNAZt.RegionShop.Data.Struct.Region;
import com.geNAZt.RegionShop.Database.Database.Model.ShopEquipSign;
import com.geNAZt.RegionShop.Interface.SignCommand;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 10.06.13
 */
public class Equip extends SignCommand {
    private static final String SELL_REG = "([0-9]{1,2}):([0-9]+):([0-9]+)";
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
        if(!plugin.getConfig().getBoolean("interfaces.sign.equip")) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Equiping via Signs is disabled");
            sign.breakNaturally();
            return;
        }

        if(!PlayerStorage.has(event.getPlayer())) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not inside a Shop");
            sign.breakNaturally();
            return;
        }

        Region playerRegion = PlayerStorage.get(event.getPlayer());
        if(!playerRegion.getRegion().isOwner(event.getPlayer().getName())) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "You are not a owner of this Shop");
            sign.breakNaturally();
            return;
        }

        if(PriceStorage.getRegion(playerRegion.getRegion().getId()) != null) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Shop is a Servershop");
            sign.breakNaturally();
            return;
        }

        Logger.info("New Equip Sign for " + playerRegion.getName() + "(" + event.getPlayer().getName() + ")");

        Block blk = event.getBlock();

        ShopEquipSign equipSign = new ShopEquipSign();
        equipSign.setOwner(event.getPlayer().getName());
        equipSign.setShop(playerRegion.getRegion().getId());
        equipSign.setWorld(event.getPlayer().getWorld().getName());
        equipSign.setX(blk.getX());
        equipSign.setY(blk.getY());
        equipSign.setZ(blk.getZ());

        plugin.getDatabase().save(equipSign);

        event.setLine(0, "Equip to Shop");
        event.setLine(1, " ");
        event.setLine(2, "Hit with item");
        event.setLine(3, "to equip");
    }
}
