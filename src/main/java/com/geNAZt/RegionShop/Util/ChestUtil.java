package com.geNAZt.RegionShop.Util;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 09.06.13
 */
public class ChestUtil {
    public static boolean checkForDblChest(Block chest) {
        BlockFace[] aside = new BlockFace[]{BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST};
        for(BlockFace bf : aside) {
            if(chest.getRelative(bf, 1).getType() == Material.CHEST) {
                return true;
            }
        }

        return false;
    }
}
