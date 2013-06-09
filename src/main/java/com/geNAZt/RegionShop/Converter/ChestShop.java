package com.geNAZt.RegionShop.Converter;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;

import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemConverter;
import com.geNAZt.RegionShop.Util.ItemName;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 09.06.13
 */
public class ChestShop implements Listener {
    private static class ConvertStorage {
        private static final HashMap<Player, HashMap<Integer, Integer>> playersInConvert = new HashMap<Player, HashMap<Integer, Integer>>();

        public static boolean hasPlayer(Player plyr) {
            if (!playersInConvert.containsKey(plyr)) {
                return false;
            }

            return true;
        }

        public static void setPlayer(Player plyr, HashMap<Integer, Integer> map) {
            playersInConvert.put(plyr, map);
        }

        public static HashMap<Integer, Integer> getPlayer(Player player) {
            return playersInConvert.get(player);
        }

        public static void removerPlayer(Player plyr) {
            playersInConvert.remove(plyr);
        }
    }

    private class ConvertCommandExecutor implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            boolean isPlayer = (sender instanceof Player);
            Player p = (isPlayer) ? (Player) sender : null;

            if(!isPlayer) {
                sender.sendMessage(Chat.getPrefix() + "No shop for you Console!");
                return true;
            }

            if (p.hasPermission("rs.convert")) {

                if (ConvertStorage.hasPlayer(p)) {
                    ConvertStorage.removerPlayer(p);

                    p.sendMessage(Chat.getPrefix() + "You don't convert anymore");
                    return true;
                }

                if (PlayerStorage.getPlayer(p) != null) {
                    HashMap<Integer, Integer> hMap = new HashMap<Integer, Integer>();
                    hMap.put(-1, -1);

                    ConvertStorage.setPlayer(p, hMap);
                    p.sendMessage(Chat.getPrefix() + "Hit the ChestSign to convert. (Left click).");
                } else {
                    p.sendMessage(Chat.getPrefix() + "Can't convert. You aren't in a Shop Region.");
                }
            } else {
                p.sendMessage(Chat.getPrefix() + "You haven't enough permissions for this.");
            }

            return true;
        }
    }

    private RegionShopPlugin plugin;

    public ChestShop(RegionShopPlugin pl) {
        plugin = pl;

        plugin.getCommand("convert").setExecutor(new ConvertCommandExecutor());
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignClick(PreTransactionEvent event) {
        Player p = event.getClient();

        if (ConvertStorage.hasPlayer(p)) {
            event.setCancelled(PreTransactionEvent.TransactionOutcome.OTHER);

            HashMap<Integer, Integer> buySell = ConvertStorage.getPlayer(p);
            if(event.getTransactionType() == TransactionEvent.TransactionType.BUY) {
                if (buySell.entrySet().iterator().next().getKey() < 0) {
                    ConvertStorage.removerPlayer(p);

                    HashMap<Integer, Integer> hMap = new HashMap<Integer, Integer>();
                    hMap.put((int)event.getPrice(), buySell.entrySet().iterator().next().getValue());

                    ConvertStorage.setPlayer(p, hMap);
                }
            } else {
                if (buySell.entrySet().iterator().next().getValue() < 0) {
                    ConvertStorage.removerPlayer(p);

                    HashMap<Integer, Integer> hMap = new HashMap<Integer, Integer>();
                    hMap.put(buySell.entrySet().iterator().next().getKey(), (int)event.getPrice());

                    ConvertStorage.setPlayer(p, hMap);
                }
            }

            buySell = ConvertStorage.getPlayer(p);
            if(PlayerStorage.getPlayer(p) != null && buySell.entrySet().iterator().next().getValue() > -1 && buySell.entrySet().iterator().next().getKey() > -1) {
                Inventory chestInv = event.getOwnerInventory();

                if(chestInv != null) {
                    convertInventory(chestInv, event.getStock()[0], p, event.getOwner().getName(), buySell.entrySet().iterator().next().getValue(), buySell.entrySet().iterator().next().getKey(), event.getStock()[0].getAmount());

                    ConvertStorage.removerPlayer(p);

                    HashMap<Integer, Integer> hMap = new HashMap<Integer, Integer>();
                    hMap.put(-1, -1);

                    ConvertStorage.setPlayer(p, hMap);
                } else {
                    p.sendMessage(Chat.getPrefix() + "Invalid ChestShop");
                    return;
                }
            }
        }
    }

    private void convertInventory(Inventory inv, ItemStack find, Player p, String owner, Integer buy, Integer sell, Integer amount) {
        ItemStack[] invItems = inv.getContents();
        boolean first = true;
        Integer converted = 0;

        ShopItems sItem = null;

        for(ItemStack item: invItems) {
            if(item == null) {
                continue;
            }

            if (item.getType().toString().contains(find.getType().toString())) {
                converted += item.getAmount();

                if(first) {
                    first = false;

                    sItem = plugin.getDatabase().find(ShopItems.class).
                            where().
                                conjunction().
                                    eq("world", p.getWorld().getName()).
                                    eq("region", PlayerStorage.getPlayer(p)).
                                    eq("item_id", item.getType().getId()).
                                    eq("data_id", item.getData().getData()).
                                    eq("durability", item.getDurability()).
                                    eq("owner", owner).
                                    eq("custom_name", (item.getItemMeta().hasDisplayName()) ? item.getItemMeta().getDisplayName() : null).
                                endJunction().
                            findUnique();

                    if(sItem == null) {
                        sItem = ItemConverter.toDBItem(item, p.getWorld(), owner, PlayerStorage.getPlayer(p), buy, sell, amount);
                        inv.remove(item);
                    } else {
                        sItem.setCurrentAmount(sItem.getCurrentAmount() + item.getAmount());
                        plugin.getDatabase().update(sItem);

                        inv.remove(item);
                    }
                } else {
                    sItem.setCurrentAmount(sItem.getCurrentAmount() + item.getAmount());
                    plugin.getDatabase().update(sItem);

                    inv.remove(item);
                }
            }
        }

        p.sendMessage(Chat.getPrefix() + "Converted " + converted + " of " + ItemName.nicer(find.toString()));
    }
}
