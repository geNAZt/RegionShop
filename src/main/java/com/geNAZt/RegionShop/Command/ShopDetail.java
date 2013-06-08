package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.Model.ShopItemEnchantmens;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemName;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class ShopDetail {
    private RegionShopPlugin plugin;

    public ShopDetail(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public boolean execute(Player p, Integer itemID) {
        ShopItems item = plugin.getDatabase().
                find(ShopItems.class).
                where().
                eq("id", itemID).
                findUnique();

        if (item != null && (((item.getSell() != 0 || item.getBuy() != 0) && item.getUnitAmount() > 0) || item.getOwner().equalsIgnoreCase(p.getName()))) {
            ItemStack iStack = new ItemStack(Material.getMaterial(item.getItemID()), 1);
            iStack.getData().setData(item.getDataID());
            iStack.setDurability(item.getDurability());

            String niceItemName = ItemName.nicer(iStack.getType().toString());
            String itemName = ItemName.getDataName(iStack) + niceItemName;

            Integer dmg = 0;

            if (iStack.getDurability() > 0 && item.getItemID() != 373 && !item.isStackable()) {
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

            List<ShopItemEnchantmens> enchants = plugin.getDatabase().find(ShopItemEnchantmens.class).
                    where().
                        eq("shop_item_id", item.getId()).
                    findList();

            if(enchants.size() > 0) {
                p.sendMessage(Chat.getPrefix() + " ");
                p.sendMessage(Chat.getPrefix() + ChatColor.GREEN + "Enchantments:");

                for(ShopItemEnchantmens ench : enchants) {
                    Enchantment enchObj = new EnchantmentWrapper(ench.getEnchId()).getEnchantment();

                    p.sendMessage(Chat.getPrefix() + "    " + ChatColor.DARK_GREEN + ItemName.nicer(enchObj.getName()) + " Level " + ChatColor.GREEN + ench.getEnchLvl());
                }
            }

            if (item.getItemID() == 373) {
                Potion ptn = Potion.fromItemStack(iStack);
                Collection<PotionEffect> ptnEffects = ptn.getEffects();

                if (ptnEffects.size() > 0) {
                    p.sendMessage(Chat.getPrefix() + " ");
                    p.sendMessage(Chat.getPrefix() + ChatColor.GREEN + "Potion Effects:");

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

            return true;
        }

        p.sendMessage(Chat.getPrefix() + ChatColor.RED + "This Shopitem couldn't be found");
        return false;
    }
}
