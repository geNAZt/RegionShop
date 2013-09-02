package com.geNAZt.RegionShop.Bukkit.Util;

import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Dye;
import org.bukkit.material.FlowerPot;
import org.bukkit.material.MaterialData;
import org.bukkit.material.Wool;
import org.bukkit.potion.Potion;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 07.06.13
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

        //Saplings / Wood / Leaves
        if (itemId == 6 || itemId == 17 || itemId == 18) {
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

        //Sandstone
        if (itemId == 24) {
            MaterialData data = item.getData();

            if(data.getData() == 1) {
                return "Chiseled ";
            }

            if(data.getData() == 2) {
                return "Smooth ";
            }
        }

        //Wool
        if (itemId == 35) {
            Wool wool = (Wool) item.getData();

            return nicer(wool.getColor().toString()) + " ";
        }

        //Double / Normal Slabs
        if (itemId == 43 || itemId == 44) {
            MaterialData data = item.getData();

            if(data.getData() == 0) {
                return "Stone ";
            }

            if(data.getData() == 1) {
                return "Sandstone ";
            }

            if(data.getData() == 2) {
                return "Wooden ";
            }

            if(data.getData() == 3) {
                return "Cobblestone ";
            }

            if(data.getData() == 4) {
                return "Brick ";
            }

            if(data.getData() == 5) {
                return "Stone Brick ";
            }

            if(data.getData() == 6) {
                return "Nether Brick ";
            }

            if(data.getData() == 7) {
                return "Quartz ";
            }
        }

        //Stone Bricks
        if (itemId == 98) {
            MaterialData data = item.getData();

            if(data.getData() == 1) {
                return "Mossy ";
            }

            if(data.getData() == 2) {
                return "Cracked ";
            }

            if(data.getData() == 3) {
                return "Chiseled ";
            }
        }

        //Cobblestone Wall
        if (itemId == 139) {
            MaterialData data = item.getData();

            if(data.getData() == 1) {
                return "Mossy ";
            }
        }

        //Flower Pot
        if (itemId == 140) {
            FlowerPot data = (FlowerPot) item.getData();

            return nicer(data.getContents().getItemType().name()) + " ";
        }

        //Quartz
        if (itemId == 155) {
            MaterialData data = item.getData();

            if (data.getData() == 1) {
                return "Chiseled ";
            }

            if (data.getData() == 2) {
                return "Pillar (vert) ";
            }

            if (data.getData() == 3) {
                return "Pillar (ns) ";
            }

            if (data.getData() == 4) {
                return "Pillar (ew) ";
            }
        }

        //Dye
        if (itemId == 351) {
            Dye dye = (Dye) item.getData();

            return nicer(dye.getColor().toString());
        }

        //Potions
        if (itemId == 373) {
            Potion ptn = Potion.fromItemStack(item);

            return ((ptn.hasExtendedDuration()) ? "Extended " : "") + ((ptn.isSplash()) ? "Splash " : "") + nicer(ptn.getType().getEffectType().getName()) + " Lvl " + ptn.getLevel() + " ";
        }

        //Mob head
        if (itemId == 397) {
            MaterialData data = item.getData();

            if(data.getData() == 0) {
                return "Skeleton ";
            }

            if(data.getData() == 1) {
                return "Wither Skeleton ";
            }

            if(data.getData() == 2) {
                return "Zombie ";
            }

            if(data.getData() == 3) {
                return "Human ";
            }

            if(data.getData() == 4) {
                return "Creeper ";
            }
        }

        return "";
    }
}
