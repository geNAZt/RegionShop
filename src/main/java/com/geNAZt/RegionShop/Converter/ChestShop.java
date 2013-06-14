package com.geNAZt.RegionShop.Converter;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;

import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemConverter;
import com.geNAZt.RegionShop.Util.ItemName;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
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
 * Date: 09.06.13
 */
public class ChestShop implements Listener {
    protected static RegionShopPlugin plugin;

    private static class ConvertStorage {
        private static class PlayerResetTask extends BukkitRunnable {
            private Player plyr = null;

            public PlayerResetTask(Player player) {
                plyr = player;
            }

            public void run() {
                ArrayList<Integer> aList = new ArrayList<Integer>();
                aList.add(-1);
                aList.add(-1);

                if(ConvertStorage.hasPlayer(plyr)) {
                    ConvertStorage.setPlayer(plyr, aList);
                    plyr.sendMessage(Chat.getPrefix() + "Your convert status has been reseted");
                }
            }
        }

        private static final HashMap<Player, ArrayList<Integer>> playersInConvert = new HashMap<Player, ArrayList<Integer>>();
        private static final HashMap<Player, BukkitTask> playerReset = new HashMap<Player, BukkitTask>();

        public static boolean hasPlayer(Player plyr) {
            if (!playersInConvert.containsKey(plyr)) {
                return false;
            }

            return true;
        }

        public static void setPlayer(Player plyr, ArrayList<Integer> map) {
            if(map.get(0) != -1 || map.get(1) != -1) {
                if(!playerReset.containsKey(plyr)) {
                    playerReset.put(plyr, new PlayerResetTask(plyr).runTaskLater(plugin, 40));
                }
            }

            playersInConvert.put(plyr, map);
        }

        public static ArrayList<Integer> getPlayer(Player player) {
            return playersInConvert.get(player);
        }

        public static void removerPlayer(Player plyr) {
            if(playerReset.containsKey(plyr)) {
                BukkitTask task = playerReset.get(plyr);
                task.cancel();

                playerReset.remove(plyr);
            }

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
                    ArrayList<Integer> aList = new ArrayList<Integer>();
                    aList.add(-1);
                    aList.add(-1);

                    ConvertStorage.setPlayer(p, aList);
                    p.sendMessage(Chat.getPrefix() + "You must click the ChestShop twice (once with the right and once with the left key)");
                } else {
                    p.sendMessage(Chat.getPrefix() + "Can't convert. You aren't in a Shop Region.");
                }
            } else {
                p.sendMessage(Chat.getPrefix() + "You haven't enough permissions for this.");
            }

            return true;
        }
    }

    public ChestShop(RegionShopPlugin pl) {
        plugin = pl;

        plugin.getCommand("convert").setExecutor(new ConvertCommandExecutor());
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerQuit(PlayerQuitEvent e) {
        if(ConvertStorage.hasPlayer(e.getPlayer())) {
            ConvertStorage.removerPlayer(e.getPlayer());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignClick(PreTransactionEvent event) {
        Player p = event.getClient();

        if (ConvertStorage.hasPlayer(p)) {
            event.setCancelled(PreTransactionEvent.TransactionOutcome.OTHER);

            ArrayList<Integer> buySell = ConvertStorage.getPlayer(p);
            if(event.getTransactionType() == TransactionEvent.TransactionType.BUY) {
                //Buy Value
                if (buySell.get(1) < 0) {
                    ConvertStorage.removerPlayer(p);

                    buySell.set(1, (int)event.getPrice());

                    ConvertStorage.setPlayer(p, buySell);
                }
            } else {
                //Sell value
                if (buySell.get(0) < 0) {
                    ConvertStorage.removerPlayer(p);

                    buySell.set(0, (int)event.getPrice());

                    ConvertStorage.setPlayer(p, buySell);
                }
            }

            if(PlayerStorage.getPlayer(p) != null && buySell.get(0) > -1 && buySell.get(1) > -1) {
                Inventory chestInv = event.getOwnerInventory();

                if(chestInv != null) {
                    convertInventory(chestInv, event.getStock()[0], p, event.getOwner().getName(), buySell.get(1), buySell.get(0), event.getStock()[0].getAmount());

                    ConvertStorage.removerPlayer(p);

                    ArrayList<Integer> aList = new ArrayList<Integer>();
                    aList.add(-1);
                    aList.add(-1);

                    ConvertStorage.setPlayer(p, aList);
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

        p.sendMessage(Chat.getPrefix() + "Converted " + converted + " of " + ItemName.nicer(find.getType().toString()));
    }
}
