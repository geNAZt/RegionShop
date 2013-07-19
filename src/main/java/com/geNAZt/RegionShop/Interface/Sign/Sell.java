package com.geNAZt.RegionShop.Interface.Sign;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Bukkit.Util.Logger;
import com.geNAZt.RegionShop.Data.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Data.Storages.PriceStorage;
import com.geNAZt.RegionShop.Data.Struct.Region;
import com.geNAZt.RegionShop.Database.Model.ShopSellSign;
import com.geNAZt.RegionShop.Interface.SignCommand;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 10.06.13
 */
public class Sell extends SignCommand {
    private static final String SELL_REG = "([0-9]{1,2}):([0-9]+):([0-9]+)";
    private final RegionShopPlugin plugin;

    public Sell(RegionShopPlugin pl) {
        plugin = pl;
    }

    @Override
    public String getCommand() {
        return "sell";
    }

    @Override
    public String getPermissionNode() {
        return "rs.sell";
    }

    @Override
    public void execute(Player player, Block sign, SignChangeEvent event) {
        if(!plugin.getConfig().getBoolean("interfaces.sign.sell")) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Selling via Signs is disabled");
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

        String[] lines = event.getLines();
        Pattern p = Pattern.compile(SELL_REG);
        Matcher matcher = p.matcher(lines[2]);
        if(matcher.find()) {
            Integer amount, sell, buy;

            try {
                Logger.debug(matcher.toMatchResult().toString());

                amount = Integer.parseInt(matcher.group(1));
                sell = Integer.parseInt(matcher.group(2));
                buy = Integer.parseInt(matcher.group(3));
            } catch (NumberFormatException e) {
                player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid Number");
                sign.breakNaturally();
                return;
            }

            Logger.info("New Sell Sign for " + playerRegion.getName() + "(" + event.getPlayer().getName() + ") - " + amount + ":" + sell + ":" + buy);

            Block blk = event.getBlock();

            ShopSellSign sellSign = new ShopSellSign();
            sellSign.setOwner(event.getPlayer().getName());
            sellSign.setShop(playerRegion.getRegion().getId());
            sellSign.setWorld(event.getPlayer().getWorld().getName());
            sellSign.setAmount(amount);
            sellSign.setSell(sell);
            sellSign.setX(blk.getX());
            sellSign.setY(blk.getY());
            sellSign.setZ(blk.getZ());

            plugin.getDatabase().save(sellSign);

            event.setLine(0, "Sell / Buy " + amount + "x");
            event.setLine(1, "for S " + sell + " B " + buy);
            event.setLine(2, "Hit with item");
            event.setLine(3, "to add");
        } else {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid Sell Pattern. Must be <amount>:<sellprice>:<buyprice>");
            sign.breakNaturally();
        }
    }
}
