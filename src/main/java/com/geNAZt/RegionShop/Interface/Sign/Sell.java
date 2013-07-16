package com.geNAZt.RegionShop.Interface.Sign;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Data.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Data.Storages.PriceStorage;
import com.geNAZt.RegionShop.Data.Struct.Region;
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
    private static final Pattern SELL_REG = Pattern.compile("([0-9]{1,2}):([0-9]+)");
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
        Matcher matcher = SELL_REG.matcher(lines[2]);
        if(matcher.matches()) {
            Integer amount = 0, sell = 0;

            while (matcher.find()) {
                try {
                    amount = Integer.parseInt(matcher.group(0));
                    sell = Integer.parseInt(matcher.group(1));
                } catch (NumberFormatException e) {
                    player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid Number");
                    sign.breakNaturally();
                    return;
                }
            }
        } else {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid Sell Pattern. Must be <amount>:<sellprice>");
            sign.breakNaturally();
        }
    }
}
