package com.geNAZt.RegionShop.Data.Tasks;

import com.geNAZt.RegionShop.Bukkit.Bridges.EssentialBridge;
import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Data.Storages.SignEquipStorage;
import com.geNAZt.RegionShop.Database.ItemConverter;
import com.geNAZt.RegionShop.Database.Model.ShopEquipSign;
import com.geNAZt.RegionShop.Database.Model.ShopItems;
import com.geNAZt.RegionShop.Database.Model.ShopTransaction;
import com.geNAZt.RegionShop.Util.Transaction;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 03.07.13
 */
public class SignEquip extends BukkitRunnable{
    private final JavaPlugin plugin;

    public SignEquip(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        HashMap<Block, com.geNAZt.RegionShop.Data.Struct.SignEquip> signEquipHashMap = SignEquipStorage.getAll();

        for(Map.Entry<Block, com.geNAZt.RegionShop.Data.Struct.SignEquip> signEquip : signEquipHashMap.entrySet()) {
            boolean chestFound = false;

            for(Integer y = -1; y<2; y++) {
                for(Integer x = -1; x<2; x++) {
                    for(Integer z = -1; z<2; z++) {
                        Block rl = signEquip.getKey().getRelative(x, y, z);

                        if (rl.getType().equals(Material.CHEST)) {
                            chestFound = true;

                            Chest chst = (Chest)rl.getState();
                            Inventory inv = chst.getInventory();

                            ItemStack[] chstContent = inv.getContents();

                            for(ItemStack iStack : chstContent) {
                                if(iStack == null) continue;

                                ShopItems item = plugin.getDatabase().find(ShopItems.class).
                                    where().
                                        conjunction().
                                            eq("world", signEquip.getValue().world).
                                            eq("region", signEquip.getValue().region).
                                            eq("item_id", iStack.getType().getId()).
                                            eq("data_id", iStack.getData().getData()).
                                            eq("durability", iStack.getDurability()).
                                            eq("owner", signEquip.getValue().owner).
                                            eq("custom_name", (iStack.getItemMeta().hasDisplayName()) ? iStack.getItemMeta().getDisplayName() : null).
                                        endJunction().
                                    findUnique();

                                OfflinePlayer player = plugin.getServer().getOfflinePlayer(signEquip.getValue().owner);

                                if(item != null) {
                                    item.setCurrentAmount(item.getCurrentAmount() + iStack.getAmount());
                                    plugin.getDatabase().update(item);

                                    Transaction.generateTransaction(player, ShopTransaction.TransactionType.EQUIP, signEquip.getValue().region, signEquip.getValue().world, signEquip.getValue().owner, iStack.getTypeId(), iStack.getAmount(), item.getSell().doubleValue(), item.getBuy().doubleValue(), item.getUnitAmount());
                                } else {
                                    ItemConverter.toDBItem(iStack, plugin.getServer().getWorld(signEquip.getValue().world), signEquip.getValue().owner, signEquip.getValue().region, 0, 0, 0);

                                    if(player.isOnline()) {
                                        Player onlinePlayer = (Player) player;
                                        onlinePlayer.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "A Equip Sign has added a new Item to your Shop");
                                    } else {
                                        EssentialBridge.sendMail(Chat.getPrefix(), player, ChatColor.GOLD + "A Equip Sign has added a new Item to your Shop");
                                    }

                                    Transaction.generateTransaction(player, ShopTransaction.TransactionType.EQUIP, signEquip.getValue().region, signEquip.getValue().world, signEquip.getValue().owner, iStack.getTypeId(), iStack.getAmount(), 0.0, 0.0, 0);
                                }

                                inv.remove(iStack);
                            }

                            break;
                        }

                        if(chestFound) break;
                    }

                    if(chestFound) break;
                }
            }

            if(!chestFound) {
                Location loc = signEquip.getKey().getLocation();

                plugin.getLogger().warning("No Chest found for Sign: (" + signEquip.getKey().getWorld().getName() + ") X: " + loc.getX() + " Y: " + loc.getY() + " Z: " + loc.getZ());
                SignEquipStorage.removeSign(signEquip.getKey());
                signEquip.getKey().breakNaturally();

                ShopEquipSign equipSign = plugin.getDatabase().find(ShopEquipSign.class).
                    where().
                        conjunction().
                            eq("world", signEquip.getKey().getWorld().getName()).
                            eq("x", signEquip.getKey().getX()).
                            eq("y", signEquip.getKey().getY()).
                            eq("z", signEquip.getKey().getZ()).
                        endJunction().
                    findUnique();

                if(equipSign != null) {
                    plugin.getDatabase().delete(equipSign);
                }

                OfflinePlayer player = plugin.getServer().getOfflinePlayer(signEquip.getValue().owner);

                if(player.isOnline()) {
                    Player onlinePlayer = (Player) player;
                    onlinePlayer.sendMessage(Chat.getPrefix() + ChatColor.RED + "A Equip Sign has been destroyed: (" + signEquip.getKey().getWorld().getName() + ") X: " + loc.getX() + " Y: " + loc.getY() + " Z: " + loc.getZ());
                } else {
                    EssentialBridge.sendMail(Chat.getPrefix(), player, ChatColor.RED + "A Equip Sign has been destroyed: (" + signEquip.getKey().getWorld().getName() + ") X: " + loc.getX() + " Y: " + loc.getY() + " Z: " + loc.getZ());
                }
            }
        }
    }
}
