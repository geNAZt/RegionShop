package com.geNAZt.RegionShop.Interface.CLI.Shop.SearchFilter;

import com.geNAZt.RegionShop.Database.Database.Model.ShopItems;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 29.06.13
 */
public class PriceFilter extends Filter {
    private class PriceFilterContainer {
        public String mode;
        public Double price;
    }

    private final ArrayList<PriceFilterContainer> priceFilter = new ArrayList<PriceFilterContainer>();

    @Override
    public boolean checkItem(ShopItems shopItem, ItemStack item) {
        boolean in = true;

        for(PriceFilterContainer filter : priceFilter) {
            if(filter.mode.equals(">") && shopItem.getSell() < filter.price) {
                in = false;
                break;
            }

            if(filter.mode.equals("<") && shopItem.getSell() > filter.price) {
                in = false;
                break;
            }

            if(filter.mode.equals("=") && shopItem.getSell().doubleValue() != filter.price) {
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
                String number;
                String mode;

                if(pat.contains(">")) {
                    number = pat.substring(1);
                    mode = ">";
                } else if (pat.contains("<")) {
                    number = pat.substring(1);
                    mode = "<";
                } else if (pat.contains("=")) {
                    number = pat.substring(1);
                    mode = "=";
                } else {
                    number = pat;
                    mode = "=";
                }

                try {
                    Double dbl = Double.parseDouble(number);
                    PriceFilterContainer priceFilterContainer = new PriceFilterContainer();
                    priceFilterContainer.mode = mode;
                    priceFilterContainer.price = dbl;

                    priceFilter.add(priceFilterContainer);
                } catch(NumberFormatException e) {
                    return "Pricefilter has a non numeric argument";
                }
            }
        } else {
            String number;
            String mode;

            if(pattern.contains(">")) {
                number = pattern.substring(1);
                mode = ">";
            } else if (pattern.contains("<")) {
                number = pattern.substring(1);
                mode = "<";
            } else if (pattern.contains("=")) {
                number = pattern.substring(1);
                mode = "=";
            } else {
                number = pattern;
                mode = "=";
            }

            try {
                Double dbl = Double.parseDouble(number);
                PriceFilterContainer priceFilterContainer = new PriceFilterContainer();
                priceFilterContainer.mode = mode;
                priceFilterContainer.price = dbl;

                priceFilter.add(priceFilterContainer);
            } catch(NumberFormatException e) {
                return "Pricefilter has a non numeric argument";
            }
        }

        return null;
    }
}
