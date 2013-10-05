package com.geNAZt.RegionShop.Util;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

/**
 * Created for ME :D
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 29.09.13
 */
public class ChestFinder {
    public static Inventory findChest(Block block) {
        BlockFace[] blockFaces = new BlockFace[]{BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST,BlockFace.NORTH, BlockFace.DOWN, BlockFace.UP};

        for(BlockFace blockFace : blockFaces) {
            Block block1 = block.getRelative(blockFace);

            if(block1.getState() instanceof Chest) {
                Chest chest = (Chest) block1.getState();
                InventoryHolder ih = chest.getInventory().getHolder();
                if (ih instanceof DoubleChest){
                    DoubleChest dc = (DoubleChest) ih;
                    return dc.getInventory();
                }

                return chest.getBlockInventory();
            }
        }

        return null;
    }

    public static Block findChestBlock(Block block) {
        BlockFace[] blockFaces = new BlockFace[]{BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST,BlockFace.NORTH, BlockFace.DOWN, BlockFace.UP};

        for(BlockFace blockFace : blockFaces) {
            Block block1 = block.getRelative(blockFace);

            if(block1.getState() instanceof Chest) {
                return block1;
            }
        }

        return null;
    }
}
