package com.geNAZt.RegionShop.Storages;

import com.geNAZt.RegionShop.Bridges.EssentialBridge;
import com.geNAZt.RegionShop.Model.ShopEquipSign;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemConverter;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 14.06.13
 */
public class SignStorage {
    private static HashMap<Block, BukkitTask> signWorkers = new HashMap<Block, BukkitTask>();
    protected static RegionShopPlugin plugin;

    private static class EquipTask extends BukkitRunnable {
        private Block sign;
        private String owner;
        private String region;
        private String world;

        public EquipTask(Block sign, String owner, String region, String world) {
            this.sign = sign;
            this.owner = owner;
            this.region = region;
            this.world = world;
        }

        @Override
        public void run() {
            for(Integer y = -1; y<2; y++) {
                for(Integer x = -1; x<2; x++) {
                    for(Integer z = -1; z<2; z++) {
                        Block rl = sign.getRelative(x, y, z);

                        if (rl.getType().equals(Material.CHEST)) {
                            Chest chst = (Chest)rl.getState();
                            Inventory inv = chst.getInventory();

                            ItemStack[] chstContent = inv.getContents();

                            for(ItemStack iStack : chstContent) {
                                if(iStack == null) continue;

                                ShopItems item = plugin.getDatabase().find(ShopItems.class).
                                        where().
                                            conjunction().
                                                eq("world", world).
                                                eq("region", region).
                                                eq("item_id", iStack.getType().getId()).
                                                eq("data_id", iStack.getData().getData()).
                                                eq("durability", iStack.getDurability()).
                                                eq("owner", owner).
                                                eq("custom_name", (iStack.getItemMeta().hasDisplayName()) ? iStack.getItemMeta().getDisplayName() : null).
                                            endJunction().
                                        findUnique();

                                if(item != null) {
                                    item.setCurrentAmount(item.getCurrentAmount() + iStack.getAmount());
                                    plugin.getDatabase().update(item);
                                } else {
                                    ItemConverter.toDBItem(iStack, plugin.getServer().getWorld(world), owner, region, 0, 0, 0);

                                    OfflinePlayer player = plugin.getServer().getOfflinePlayer(owner);

                                    if(player.isOnline()) {
                                        Player onlinePlayer = (Player) player;
                                        onlinePlayer.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "A Equip Sign has added a new Item to your Shop");
                                    } else {

                                        EssentialBridge.sendMail(Chat.getPrefix(), player, ChatColor.GOLD + "A Equip Sign has added a new Item to your Shop");
                                    }
                                }

                                inv.remove(iStack);
                            }

                            return;
                        }
                    }
                }
            }

            Location loc = sign.getLocation();

            plugin.getLogger().warning("No Chest found for Sign: (" + sign.getWorld().getName() + ") X: " + loc.getX() + " Y: " + loc.getY() + " Z: " + loc.getZ());
            SignStorage.removeSign(sign);
            sign.breakNaturally();

            ShopEquipSign equipSign = plugin.getDatabase().find(ShopEquipSign.class).
                    where().
                        conjunction().
                            eq("world", sign.getWorld().getName()).
                            eq("x", sign.getX()).
                            eq("y", sign.getY()).
                            eq("z", sign.getZ()).
                        endJunction().
                    findUnique();

            if(equipSign != null) {
                plugin.getDatabase().delete(equipSign);
            }

            OfflinePlayer player = plugin.getServer().getOfflinePlayer(owner);

            if(player.isOnline()) {
                Player onlinePlayer = (Player) player;
                onlinePlayer.sendMessage(Chat.getPrefix() + ChatColor.RED + "A Equip Sign has been destroyed: (" + sign.getWorld().getName() + ") X: " + loc.getX() + " Y: " + loc.getY() + " Z: " + loc.getZ());
            } else {

                EssentialBridge.sendMail(Chat.getPrefix(), player, ChatColor.RED + "A Equip Sign has been destroyed: (" + sign.getWorld().getName() + ") X: " + loc.getX() + " Y: " + loc.getY() + " Z: " + loc.getZ());
            }
        }
    }

    public static void init(RegionShopPlugin pl) {
        plugin = pl;

        List<ShopEquipSign> equipSigns = plugin.getDatabase().find(ShopEquipSign.class).findList();

        for(ShopEquipSign equipSign:equipSigns) {
            Block blk = plugin.getServer().getWorld(equipSign.getWorld()).getBlockAt(equipSign.getX(), equipSign.getY(), equipSign.getZ());
            SignStorage.addSign(blk, equipSign.getOwner(), equipSign.getShop(), equipSign.getWorld());
        }
    }

    public static void addSign(Block sign, String owner, String region, String world) {
        signWorkers.put(sign, new EquipTask(sign, owner, region, world).runTaskTimer(plugin, 20, 20));
    }

    public static void removeSign(Block sign) {
        BukkitTask task = signWorkers.get(sign);
        task.cancel();

        signWorkers.remove(sign);
    }
}
