package com.geNAZt.RegionShop.Util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.DyeColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 07.06.13
 * Time: 14:35
 * To change this template use File | Settings | File Templates.
 */
public class ItemName {
    public static String nicer(String itemName) {
        String [] itemPieces = itemName.split("_");

        for(Integer i = 0;i<itemPieces.length;i++) {
            itemPieces[i] = StringUtils.capitalize(itemPieces[i].toLowerCase());
        }

        return StringUtils.join(itemPieces, " ");
    }

    public static String getDataName(ItemStack item) {
        Integer itemId = item.getType().getId();

        //Wood
        if (itemId == 17) {
            MaterialData data = item.getData();

            if(data.getData() == 0) {
                return "Oak ";
            }

            if(data.getData() == 1) {
                return "Spruce ";
            }

            if(data.getData() == 2) {
                return "Birch ";
            }

            if(data.getData() == 3) {
                return "Jungle ";
            }
        }

        //Wool
        if (itemId == 35) {
            Wool wool = (Wool) item.getData();
            DyeColor color = wool.getColor();

            return nicer(color.toString()) + " ";
        }

        return "";
    }
}
