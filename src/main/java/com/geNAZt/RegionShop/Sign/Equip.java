package com.geNAZt.RegionShop.Sign;

import com.geNAZt.RegionShop.RegionShopPlugin;

import com.geNAZt.RegionShop.Storages.SignStorage;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 10.06.13
 */
public class Equip {
    private RegionShopPlugin plugin;

    public Equip(RegionShopPlugin pl) {
        plugin = pl;
    }

    public void execute(Player player, Block sign, String[] lines) {
        for(Integer y = -1; y<2; y++) {
            for(Integer x = -1; x<2; x++) {
                for(Integer z = -1; z<2; z++) {
                    Block rl = sign.getRelative(x, y, z);

                    if (rl.getType().equals(Material.CHEST)) {
                        SignStorage.addSign(sign);
                    }
                }
            }
        }
    }
}
