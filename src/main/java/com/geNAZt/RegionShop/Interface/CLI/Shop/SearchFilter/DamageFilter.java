package com.geNAZt.RegionShop.Interface.CLI.Shop.SearchFilter;

import com.geNAZt.RegionShop.Database.Model.ShopItems;
import org.bukkit.inventory.ItemStack;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 29.06.13
 */
public class DamageFilter extends Filter {
    private String mode;
    private Double dmg;

    @Override
    public boolean checkItem(ShopItems shopItem, ItemStack item) {
        boolean in = true;

        Integer dmgItem = 0;

        if (item.getDurability() > 0 && shopItem.getItemID() != 373 && !shopItem.isStackable()) {
            Float divide = ((float)item.getDurability() / (float)item.getType().getMaxDurability());
            dmgItem = Math.round(divide * 100);
        }

        if(mode.equals(">") && dmgItem < dmg) {
            in = false;
        }

        if(mode.equals("<") && dmgItem > dmg) {
            in = false;
        }

        if(mode.equals("=") && dmgItem.doubleValue() != dmg) {
            in = false;
        }

        return in;
    }

    @Override
    public String parse(String pattern) {
        String number;

        if(pattern.contains("<")) {
            number = pattern.substring(1);
            mode = "<";
        } else if(pattern.contains(">")) {
            number = pattern.substring(1);
            mode = ">";
        } else if(pattern.contains("=")) {
            number = pattern.substring(1);
            mode = "=";
        } else {
            number = pattern;
            mode = "=";
        }

        try {
            dmg = Double.parseDouble(number);
            System.out.println(mode + " -  " + dmg);
        } catch(NumberFormatException e) {
            return "Damagefilter has a non numeric argument";
        }

        return null;
    }
}
