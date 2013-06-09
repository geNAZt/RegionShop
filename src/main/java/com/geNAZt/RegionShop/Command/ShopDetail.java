package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemConverter;
import com.geNAZt.RegionShop.Util.ItemName;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
class ShopDetail {
    private final RegionShopPlugin plugin;

    public ShopDetail(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public void execute(Player p, Integer itemID) {
        ShopItems item = plugin.getDatabase().
                find(ShopItems.class).
                where().
                    eq("id", itemID).
                findUnique();

        if (item != null && (((item.getSell() != 0 || item.getBuy() != 0) && item.getUnitAmount() > 0) || item.getOwner().equalsIgnoreCase(p.getName()))) {
            ItemStack iStack = ItemConverter.fromDBItem(item);

            String niceItemName = ItemName.nicer(iStack.getType().toString());
            String itemName = ItemName.getDataName(iStack) + niceItemName;

            Integer dmg = 0;

            if (iStack.getDurability() > 0 && item.getItemID() != 373 && item.isStackable()) {
                Float divide = ((float)iStack.getDurability() / (float)iStack.getType().getMaxDurability());
                dmg = Math.round(divide * 100);
            }

            p.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Detail view " + ChatColor.YELLOW + "-- " + ChatColor.RED + item.getOwner() + "'s " + ChatColor.GREEN + itemName + ChatColor.GRAY + "#" + item.getId());
            p.sendMessage(Chat.getPrefix() + " ");
            p.sendMessage(Chat.getPrefix() + ChatColor.GREEN + item.getSell() + "$ " + ChatColor.GOLD + "selling price");
            p.sendMessage(Chat.getPrefix() + ChatColor.GREEN + item.getBuy() + "$ " + ChatColor.GOLD + "buying price");
            p.sendMessage(Chat.getPrefix() + ChatColor.RED + dmg + "% " + ChatColor.GOLD + "damaged");

            if (item.getCustomName() != null) {
                p.sendMessage(Chat.getPrefix() + "     " + ChatColor.GOLD + "Custom name: " + ChatColor.GRAY + item.getCustomName());
            }

            if(!iStack.getEnchantments().isEmpty()) {
                p.sendMessage(Chat.getPrefix() + " ");
                p.sendMessage(Chat.getPrefix() + ChatColor.GREEN + "Enchantments:");

                for(Map.Entry<Enchantment, Integer> ench : iStack.getEnchantments().entrySet()) {
                   p.sendMessage(Chat.getPrefix() + "    " + ChatColor.DARK_GREEN + ItemName.nicer(ench.getKey().getName()) + " Level " + ChatColor.GREEN + ench.getValue());
                }
            }

            if (item.getItemID() == 373) {
                Potion ptn = Potion.fromItemStack(iStack);
                Collection<PotionEffect> ptnEffects = ptn.getEffects();

                if (ptnEffects.size() > 0) {
                    p.sendMessage(Chat.getPrefix() + " ");
                    p.sendMessage(Chat.getPrefix() + ChatColor.GREEN + "Potion effects:");

                    for(PotionEffect ptnEffect : ptnEffects) {
                        Integer duration = 0;

                        if (ptnEffect.getDuration() >= 20) {
                            Float divide = ((float)ptnEffect.getDuration() / (float)20);
                            duration = Math.round(divide);
                        }

                        p.sendMessage(Chat.getPrefix() + "    " + ChatColor.DARK_GREEN + ItemName.nicer(ptnEffect.getType().getName()) + ": Amplifier " + ChatColor.GREEN + ptnEffect.getAmplifier() + ChatColor.DARK_GREEN + ", Duration " + ChatColor.GREEN + duration + "s" );
                    }
                }
            }

            return;
        }

        p.sendMessage(Chat.getPrefix() + ChatColor.RED + "This Shopitem could not be found");
    }
}
