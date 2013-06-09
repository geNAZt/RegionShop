package com.geNAZt.RegionShop.Converter;

import com.geNAZt.RegionShop.Model.ShopItems;
import com.geNAZt.RegionShop.RegionShopPlugin;

import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ChestUtil;
import com.geNAZt.RegionShop.Util.ItemConverter;
import com.geNAZt.RegionShop.Util.ItemName;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.block.Sign;
import org.bukkit.inventory.DoubleChestInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 09.06.13
 */
public class ChestShop implements Listener {
    private static class ConvertStorage {
        private static final HashSet<Player> playersInConvert = new HashSet<Player>();

        public static boolean hasPlayer(Player plyr) {
            if (!playersInConvert.contains(plyr)) {
                return false;
            }

            return true;
        }

        public static void setPlayer(Player plyr) {
            playersInConvert.add(plyr);
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
                    ConvertStorage.setPlayer(p);
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
    public void onSignClick(PlayerInteractEvent event) {
        Player p = event.getPlayer();

        if (ConvertStorage.hasPlayer(p)) {

            if(PlayerStorage.getPlayer(p) != null) {

                if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
                    Block b = event.getClickedBlock();

                    if (b == null) {
                        return;
                    }

                    if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
                        Sign s = (Sign) b.getState();

                        String owner = s.getLine(0);

                        if (plugin.getServer().getPlayer(owner) == null) {
                            p.sendMessage(Chat.getPrefix() + "Invalid ChestShop sign. Owner failure");

                            return;
                        }

                        String amount = s.getLine(1);
                        Integer amt = 0;

                        try {
                            amt = Integer.parseInt(amount);
                        } catch(NumberFormatException e) {
                            p.sendMessage(Chat.getPrefix() + "Invalid ChestShop sign. Amount is not a number");

                            return;
                        }

                        if (amt < 0) {
                            p.sendMessage(Chat.getPrefix() + "Invalid ChestShop sign. Amount is a negative number");

                            return;
                        }

                        String buySell = s.getLine(2);
                        Pattern regex = Pattern.compile("B (\\d+): S (\\d+)");
                        Integer buy, sell;
                        Matcher regexMatcher = regex.matcher(buySell);

                        if(regexMatcher.matches()) {
                            String sellStr = regexMatcher.group(1);
                            String buyStr = regexMatcher.group(2);

                            try {
                                buy = Integer.parseInt(buyStr);
                                sell = Integer.parseInt(sellStr);
                            } catch(NumberFormatException e) {
                                p.sendMessage(Chat.getPrefix() + "Invalid ChestShop sign. 3rd line Buy/Sell isn't a number");

                                return;
                            }
                        } else {
                            regex = Pattern.compile("B (\\d+):(\\d+) S");
                            regexMatcher = regex.matcher(buySell);

                            if(regexMatcher.matches()) {
                                String sellStr = regexMatcher.group(1);
                                String buyStr = regexMatcher.group(2);

                                try {
                                    buy = Integer.parseInt(buyStr);
                                    sell = Integer.parseInt(sellStr);
                                } catch(NumberFormatException e) {
                                    p.sendMessage(Chat.getPrefix() + "Invalid ChestShop sign. 3rd line Buy/Sell isn't a number");

                                    return;
                                }
                            } else {
                                p.sendMessage(Chat.getPrefix() + "Invalid ChestShop sign. 3rd line Format is wrong");
                                return;
                            }
                        }

                        if (buy < 0 || sell < 0) {
                            p.sendMessage(Chat.getPrefix() + "Invalid ChestShop sign. 3rd line Buy or Sell under 0");
                            return;
                        }

                        String item = s.getLine(3);
                        Material mat = Material.matchMaterial(item);

                        if(mat == null) {
                            p.sendMessage(Chat.getPrefix() + "Invalid ChestShop sign. Item is invalid");
                            return;
                        }

                        for(Integer y = -1; y<2; y++) {
                            for(Integer x = -1; x<2; x++) {
                                for(Integer z = -1; z<2; z++) {
                                    Block rl = b.getRelative(x, y, z);

                                    if (rl.getType().equals(Material.CHEST)) {
                                        if (ChestUtil.checkForDblChest(rl)) {
                                            convertInventory(((Chest) rl.getState()).getInventory(), mat, p, owner, buy, sell, amt);
                                        } else {
                                            convertInventory(((Chest) rl.getState()).getInventory(), mat, p, owner, buy, sell, amt);
                                        }

                                        b.breakNaturally();
                                        return;
                                    }
                                }
                            }
                        }

                        p.sendMessage(Chat.getPrefix() + "Invalid ChestShop sign. No Chest found.");
                        return;
                    }
                }
            }
        }
    }

    private void convertInventory(Inventory inv, Material find, Player p, String owner, Integer buy, Integer sell, Integer amount) {
        ItemStack[] invItems = inv.getContents();
        boolean first = true;
        Integer converted = 0;

        ShopItems sItem = null;

        for(ItemStack item: invItems) {
            if(item == null) {
                continue;
            }

            if (item.getType() == find) {
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
