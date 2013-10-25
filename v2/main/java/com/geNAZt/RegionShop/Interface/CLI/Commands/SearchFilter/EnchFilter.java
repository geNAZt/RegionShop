package com.geNAZt.RegionShop.Interface.CLI.Commands.SearchFilter;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Database.Table.Items;
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
    public boolean checkItem(Items shopItem, ItemStack item) {
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
                    return ConfigManager.language.Command_Filter_Ench_InvalidOperator;
                }

                try {
                    Integer ench = Integer.parseInt(enchId);

                    if(!Arrays.asList(validEnchIDs).contains(ench)) {
                        return ConfigManager.language.Command_Filter_Ench_InvalidEnchID;
                    }

                    Integer enchLvl = Integer.parseInt(enchLevel);

                    EnchFilterContainer enchFilterContainer = new EnchFilterContainer();
                    enchFilterContainer.mode = mode;
                    enchFilterContainer.enchID = ench;
                    enchFilterContainer.enchLvl = enchLvl;

                    enchFilter.add(enchFilterContainer);
                } catch(NumberFormatException e) {
                    return ConfigManager.language.Command_Filter_Ench_InvalidArguments;
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
                return ConfigManager.language.Command_Filter_Ench_InvalidOperator;
            }

            try {
                Integer ench = Integer.parseInt(enchId);

                if(!Arrays.asList(validEnchIDs).contains(ench)) {
                    return ConfigManager.language.Command_Filter_Ench_InvalidEnchID;
                }

                Integer enchLvl = Integer.parseInt(enchLevel);

                EnchFilterContainer enchFilterContainer = new EnchFilterContainer();
                enchFilterContainer.mode = mode;
                enchFilterContainer.enchID = ench;
                enchFilterContainer.enchLvl = enchLvl;

                enchFilter.add(enchFilterContainer);
            } catch(NumberFormatException e) {
                return ConfigManager.language.Command_Filter_Ench_InvalidArguments;
            }
        }

        return null;
    }
}
