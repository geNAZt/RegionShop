package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.Model.ShopItemEnchantmens;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.*;

import com.sk89q.worldguard.protection.managers.RegionManager;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentWrapper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 19:09
 * To change this template use File | Settings | File Templates.
 */
public class ShopBuy {
    private RegionShopPlugin plugin;

    public ShopBuy(RegionShopPlugin plugin) {
        this.plugin = plugin;
    }

    @SuppressWarnings("unchecked")
    public boolean execute(Player p, Integer shopItemId, Integer wishAmount) {
        if (PlayerStorage.getPlayer(p) != null) {
            String region = PlayerStorage.getPlayer(p);

            ShopItems item = plugin.getDatabase().find(ShopItems.class).
                    where().
                        conjunction().
                            eq("world", p.getWorld().getName()).
                            eq("region", region).
                            eq("id", shopItemId).
                        endJunction().
                    findUnique();

            if (item.getSell() > 0) {
                if (wishAmount > item.getCurrentAmount() && wishAmount != -1) {
                    p.sendMessage(Chat.getPrefix() + ChatColor.RED +  "This Shop hasn't so much of this Item");
                    return true;
                }

                if (wishAmount == -1) {
                    wishAmount = item.getCurrentAmount();
                }

                ItemStack iStack = new ItemStack(Material.getMaterial(item.getItemID()), wishAmount);
                iStack.getData().setData(item.getDataID());
                iStack.setDurability(item.getDurability());

                List<ShopItemEnchantmens> enchants = plugin.getDatabase().find(ShopItemEnchantmens.class).
                        where().
                            eq("shop_item_id", item.getId()).
                        findList();

                if(enchants.size() > 0) {
                    for(ShopItemEnchantmens ench : enchants) {
                        Enchantment enchObj = new EnchantmentWrapper(ench.getEnchId()).getEnchantment();
                        iStack.addEnchantment(enchObj, ench.getEnchLvl());
                    }
                }

                if(item.getCustomName() != null) {
                    ItemMeta iMeta = iStack.getItemMeta();
                    iMeta.setDisplayName(item.getCustomName());
                    iStack.setItemMeta(iMeta);
                }

                Economy eco = VaultBridge.economy;

                if (eco.has(p.getName(), ((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell())) {
                    HashMap<Integer, ItemStack> notFitItems = p.getInventory().addItem(iStack);
                    if (!notFitItems.isEmpty()) {
                        for(Map.Entry<Integer, ItemStack> notFitItem : notFitItems.entrySet()) {
                            wishAmount -= notFitItem.getValue().getAmount();
                        }
                    }

                    RegionManager rgMngr = WorldGuardBridge.getRegionManager(p.getWorld());

                    Player owner = plugin.getServer().getPlayer(item.getOwner());
                    if (owner != null) {
                        if (owner.isOnline()) {
                            owner.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "Player " + ChatColor.GREEN + p.getDisplayName() + ChatColor.DARK_GREEN + " bought " + ChatColor.GREEN + wishAmount + " " + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_GREEN + " from your Shop (" + ChatColor.GREEN + WorldGuardBridge.convertRegionToShopName(rgMngr.getRegion(region), p.getWorld()) + ChatColor.DARK_GREEN + ") for " + ChatColor.GREEN + (((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell()) + "$");
                        }
                    }

                    eco.withdrawPlayer(p.getName(), ((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell());
                    eco.depositPlayer(item.getOwner(), ((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell());
                    p.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "You have bought " + ChatColor.GREEN + wishAmount + " " + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + " for " + ChatColor.GREEN + (((float) wishAmount / (float) item.getUnitAmount()) * (float) item.getSell()) + "$" + ChatColor.DARK_GREEN + " from Shop");

                    item.setCurrentAmount(item.getCurrentAmount() - wishAmount);

                    if (item.getCurrentAmount() > 0) {
                        plugin.getDatabase().update(item);
                    } else {
                        if (owner != null) {


                            if (owner.isOnline()) {
                                owner.sendMessage(Chat.getPrefix() + ChatColor.DARK_GREEN + "ShopItem " + ChatColor.GREEN + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_GREEN + " is empty. It has been removed from your Shop (" + ChatColor.GREEN + WorldGuardBridge.convertRegionToShopName(rgMngr.getRegion(region), p.getWorld()) + ChatColor.DARK_GREEN + ")");
                            } else {
                                EssentialBridge.sendMail(Chat.getPrefix(), owner, ChatColor.DARK_GREEN + "ShopItem " + ChatColor.GREEN + ItemName.getDataName(iStack) + ItemName.nicer(iStack.getType().toString()) + ChatColor.DARK_GREEN + " is empty. It has been removed from your Shop (" + ChatColor.GREEN + WorldGuardBridge.convertRegionToShopName(rgMngr.getRegion(region), p.getWorld()) + ChatColor.DARK_GREEN + ")");
                            }
                        }

                        plugin.getDatabase().delete(item);
                    }
                } else {
                    p.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You haven't enough money for this. You need "+ (((float)wishAmount / (float)item.getUnitAmount()) * (float)item.getSell()) + "$");
                }
            } else {
                p.sendMessage(Chat.getPrefix() + ChatColor.RED +  "This Shop doesn't sell this Item");
            }

            return true;
        }

        //Nothing of all
        p.sendMessage(Chat.getPrefix() + ChatColor.RED +  "You aren't inside a Shop");
        return false;
    }
}
