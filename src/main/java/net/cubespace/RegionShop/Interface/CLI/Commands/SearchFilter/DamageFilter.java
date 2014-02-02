package net.cubespace.RegionShop.Interface.CLI.Commands.SearchFilter;

import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Table.Items;
import org.bukkit.inventory.ItemStack;

public class DamageFilter extends Filter {
    private String mode;
    private Double dmg;

    @Override
    public boolean checkItem(Items shopItem, ItemStack item) {
        boolean in = true;

        Integer dmgItem = 0;

        if (item.getDurability() > 0 && shopItem.getMeta().getItemID() != 373 && shopItem.getMeta().getMaxStackSize() == 1) {
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
            return ConfigManager.language.Command_Filter_Damage_InvalidArguments;
        }

        return null;
    }
}
