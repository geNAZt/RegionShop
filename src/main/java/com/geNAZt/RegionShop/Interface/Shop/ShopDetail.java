package com.geNAZt.RegionShop.Interface.Shop;

import com.geNAZt.RegionShop.Interface.ShopCommand;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemConverter;
import com.geNAZt.RegionShop.Util.ItemName;

import org.bukkit.ChatColor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopDetail extends ShopCommand {
    private final Plugin plugin;

    public ShopDetail(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public int getHelpPage() {
        return 1;
    }

    @Override
    public String[] getHelpText() {
        return new String[]{ChatColor.GOLD + "/shop detail " + ChatColor.RED + "shopItemID" + ChatColor.RESET + ": Display details of " + ChatColor.RED + "shopItemID"};
    }

    @Override
    public String getCommand() {
        return "detail";
    }

    @Override
    public String getPermissionNode() {
        return "rs.detail";
    }

    @Override
    public int getNumberOfArgs() {
        return 1;
    }

    @Override
    public void execute(Player player, String[] args) {
        //Convert args
        Integer itemId;

        try {
            itemId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + "Only numbers as argument allowed");
            return;
        }

        ShopItems item = plugin.getDatabase().find(ShopItems.class).
                    where().
                        eq("id", itemId).
                    findUnique();

        if (item != null && (((item.getSell() != 0 || item.getBuy() != 0) && item.getUnitAmount() > 0) || item.getOwner().equalsIgnoreCase(player.getName()))) {
            ItemStack iStack = ItemConverter.fromDBItem(item);

            String niceItemName = ItemName.nicer(iStack.getType().toString());
            String itemName = ItemName.getDataName(iStack) + niceItemName;

            Integer dmg = 0;

            if (iStack.getDurability() > 0 && item.getItemID() != 373 && !item.isStackable()) {
                Float divide = ((float)iStack.getDurability() / (float)iStack.getType().getMaxDurability());
                dmg = Math.round(divide * 100);
            }

            player.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Detail view " + ChatColor.YELLOW + "-- " + ChatColor.RED + item.getOwner() + "'s " + ChatColor.GREEN + itemName + ChatColor.GRAY + " #" + item.getId());
            player.sendMessage(Chat.getPrefix() + " ");
            player.sendMessage(Chat.getPrefix() + ChatColor.GREEN + item.getSell() + "$ " + ChatColor.GOLD + "selling price");
            player.sendMessage(Chat.getPrefix() + ChatColor.GREEN + item.getBuy() + "$ " + ChatColor.GOLD + "buying price");
            player.sendMessage(Chat.getPrefix() + ChatColor.RED + dmg + "% " + ChatColor.GOLD + "damaged");

            if (item.getCustomName() != null) {
                player.sendMessage(Chat.getPrefix() + "     " + ChatColor.GOLD + "Custom name: " + ChatColor.GRAY + item.getCustomName());
            }

            if(!iStack.getEnchantments().isEmpty()) {
                player.sendMessage(Chat.getPrefix() + " ");
                player.sendMessage(Chat.getPrefix() + ChatColor.GREEN + "Enchantments:");

                for(Map.Entry<Enchantment, Integer> ench : iStack.getEnchantments().entrySet()) {
                    player.sendMessage(Chat.getPrefix() + "    " + ChatColor.DARK_GREEN + ItemName.nicer(ench.getKey().getName()) + " Level " + ChatColor.GREEN + ench.getValue());
                }
            }

            if (item.getItemID() == 373) {
                Potion ptn = Potion.fromItemStack(iStack);
                Collection<PotionEffect> ptnEffects = ptn.getEffects();

                if (ptnEffects.size() > 0) {
                    player.sendMessage(Chat.getPrefix() + " ");
                    player.sendMessage(Chat.getPrefix() + ChatColor.GREEN + "Potion effects:");

                    for(PotionEffect ptnEffect : ptnEffects) {
                        Integer duration = 0;

                        if (ptnEffect.getDuration() >= 20) {
                            Float divide = ((float)ptnEffect.getDuration() / (float)20);
                            duration = Math.round(divide);
                        }

                        player.sendMessage(Chat.getPrefix() + "    " + ChatColor.DARK_GREEN + ItemName.nicer(ptnEffect.getType().getName()) + ": Amplifier " + ChatColor.GREEN + ptnEffect.getAmplifier() + ChatColor.DARK_GREEN + ", Duration " + ChatColor.GREEN + duration + "s" );
                    }
                }
            }

            return;
        }

        player.sendMessage(Chat.getPrefix() + ChatColor.RED + "This Shopitem could not be found");
    }
}
