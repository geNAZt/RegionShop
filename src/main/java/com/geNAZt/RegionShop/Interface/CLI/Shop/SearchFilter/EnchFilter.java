package com.geNAZt.RegionShop.Interface.CLI.Shop.SearchFilter;

import com.geNAZt.RegionShop.Database.Model.ShopItems;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 29.06.13
 */
public class EnchFilter extends Filter {
    private final Integer[] validEnchIDs = {0,1,2,3,4,5,6,7,16,17,18,19,20,21,32,33,34,35,48,49,50,51};

    private class EnchFilterContainer {
        public String mode;
        public Integer enchID;
        public Integer enchLvl;
    }

    private final ArrayList<EnchFilterContainer> enchFilter = new ArrayList<EnchFilterContainer>();

    @Override
    public boolean checkItem(ShopItems shopItem, ItemStack item) {
        boolean in = true;

        if(item.getEnchantments() == null || item.getEnchantments().isEmpty()) {
            return false;
        }

        for(EnchFilterContainer filter : enchFilter) {
            if(filter.mode.equals(">") && item.getEnchantmentLevel(Enchantment.getById(filter.enchID)) < filter.enchLvl) {
                in = false;
                break;
            }

            if(filter.mode.equals("<") && item.getEnchantmentLevel(Enchantment.getById(filter.enchID)) > filter.enchLvl) {
                in = false;
                break;
            }

            if(filter.mode.equals("=") && item.getEnchantmentLevel(Enchantment.getById(filter.enchID)) != filter.enchLvl) {
                in = false;
                break;
            }
        }

        return in;
    }

    @Override
    public String parse(String pattern) {
        if(pattern.contains("&")) {
            String[] tempPattern = pattern.split("&");

            for(String pat : tempPattern) {
                String enchLevel;
                String enchId;
                String mode;

                if(pat.contains(">")) {
                    enchId = pat.substring(0, pat.indexOf(">"));
                    enchLevel = pat.substring(pat.indexOf(">")+1);
                    mode = ">";
                } else if (pat.contains("<")) {
                    enchId = pat.substring(0, pat.indexOf("<"));
                    enchLevel = pat.substring(pat.indexOf("<")+1);
                    mode = "<";
                } else if (pat.contains("=")) {
                    enchId = pat.substring(0, pat.indexOf("="));
                    enchLevel = pat.substring(pat.indexOf("=")+1);
                    mode = "=";
                } else {
                    return "Invalid Ench selector";
                }

                try {
                    Integer ench = Integer.parseInt(enchId);

                    if(!Arrays.asList(validEnchIDs).contains(ench)) {
                        return "Invalid EnchID";
                    }

                    Integer enchLvl = Integer.parseInt(enchLevel);

                    EnchFilterContainer enchFilterContainer = new EnchFilterContainer();
                    enchFilterContainer.mode = mode;
                    enchFilterContainer.enchID = ench;
                    enchFilterContainer.enchLvl = enchLvl;

                    enchFilter.add(enchFilterContainer);
                } catch(NumberFormatException e) {
                    return "Ench selector has a non numeric argument";
                }
            }
        } else {
            String enchLevel;
            String enchId;
            String mode;

            if(pattern.contains(">")) {
                enchId = pattern.substring(0, pattern.indexOf(">"));
                enchLevel = pattern.substring(pattern.indexOf(">")+1);
                mode = ">";
            } else if (pattern.contains("<")) {
                enchId = pattern.substring(0, pattern.indexOf("<"));
                enchLevel = pattern.substring(pattern.indexOf("<")+1);
                mode = "<";
            } else if (pattern.contains("=")) {
                enchId = pattern.substring(0, pattern.indexOf("="));
                enchLevel = pattern.substring(pattern.indexOf("=")+1);
                mode = "=";
            } else {
                return "Invalid Ench selector";
            }

            try {
                Integer ench = Integer.parseInt(enchId);

                if(!Arrays.asList(validEnchIDs).contains(ench)) {
                    return "Invalid EnchID";
                }

                Integer enchLvl = Integer.parseInt(enchLevel);

                EnchFilterContainer enchFilterContainer = new EnchFilterContainer();
                enchFilterContainer.mode = mode;
                enchFilterContainer.enchID = ench;
                enchFilterContainer.enchLvl = enchLvl;

                enchFilter.add(enchFilterContainer);
            } catch(NumberFormatException e) {
                return "Ench selector has a non numeric argument";
            }
        }

        return null;
    }
}
