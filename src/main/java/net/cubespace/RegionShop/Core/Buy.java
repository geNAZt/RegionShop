package net.cubespace.RegionShop.Core;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.ItemStorageHolder;
import net.cubespace.RegionShop.Database.Repository.ItemRepository;
import net.cubespace.RegionShop.Database.Repository.TransactionRepository;
import net.cubespace.RegionShop.Database.Table.ItemStorage;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Database.Table.Transaction;
import net.cubespace.RegionShop.Util.ItemName;
import net.cubespace.RegionShop.Util.Logger;
import net.cubespace.RegionShop.Util.MailBridge;
import net.cubespace.RegionShop.Util.VaultBridge;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Buy {
    public static void buy(final Items item, final Player player, final ItemStorageHolder region, Integer wishAmount) {
        if (item.getOwner().toLowerCase().equals(player.getName().toLowerCase())) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Buy_NotYourItems);
            return;
        }

        if (item.getSell() <= 0) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Buy_NoSell);
            return;
        }

        if (wishAmount > item.getCurrentAmount()) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Buy_NotEnoughItems);
            return;
        }

        if (wishAmount < 1) {
            wishAmount = 1;
        }

        final int amountWished = wishAmount;

        Float price = (((float) wishAmount / (float) item.getUnitAmount()) * item.getSell());

        if (VaultBridge.has(player.getName(), price)) {
            Plugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(Plugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    final ItemStack iStack = ItemRepository.fromDBItem(item);
                    iStack.setAmount(amountWished);

                    int internalAmountWished = amountWished;

                    HashMap<Integer, ItemStack> notFitItems = player.getInventory().addItem(iStack);
                    if (!notFitItems.isEmpty()) {
                        for (Map.Entry<Integer, ItemStack> notFitItem : notFitItems.entrySet()) {
                            internalAmountWished -= notFitItem.getValue().getAmount();
                        }
                    }

                    player.updateInventory();

                    final int internalAmountWishedAsync = internalAmountWished;

                    Plugin.getInstance().getServer().getScheduler().runTaskAsynchronously(Plugin.getInstance(), new Runnable() {
                        @Override
                        public void run() {
                            float newPrice = (((float) internalAmountWishedAsync / (float) item.getUnitAmount()) * item.getSell());

                            String dataName = ItemName.getDataName(iStack);
                            String niceItemName;
                            if (dataName.endsWith(" ")) {
                                niceItemName = dataName + ItemName.nicer(iStack.getType().toString());
                            } else if (!dataName.equals("")) {
                                niceItemName = dataName;
                            } else {
                                niceItemName = ItemName.nicer(iStack.getType().toString());
                            }

                            item.setCurrentAmount(item.getCurrentAmount() - internalAmountWishedAsync);

                            if (!item.getItemStorage().isServershop()) {
                                OfflinePlayer owner = Plugin.getInstance().getServer().getOfflinePlayer(item.getOwner());
                                Player onOwner = null;

                                if (owner.isOnline()) {
                                    onOwner = (Player) owner;
                                }

                                if (onOwner != null) {
                                    onOwner.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Buy_OwnerHint.
                                            replace("%player", player.getDisplayName()).
                                            replace("%amount", String.valueOf(internalAmountWishedAsync)).
                                            replace("%item", niceItemName).
                                            replace("%shop", region.getName()).
                                            replace("%price", String.valueOf(newPrice)));
                                }

                                VaultBridge.depositPlayer(item.getOwner(), newPrice);

                                if (item.getCurrentAmount() == 0) {
                                    if (onOwner != null) {
                                        onOwner.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Buy_OwnerHintEmptyShop.replace("%item", niceItemName).replace("%shop", region.getName()));
                                    } else {
                                        MailBridge.sendMail(ConfigManager.main.Chat_prefix, owner, ConfigManager.language.Buy_OwnerHintEmptyShop.replace("%item", niceItemName).replace("%shop", region.getName()));
                                    }
                                }

                                TransactionRepository.generateTransaction(owner, Transaction.TransactionType.SELL, region.getName(), player.getWorld().getName(), player.getName(), item.getMeta().getItemID(), internalAmountWishedAsync, 0.0, item.getSell().doubleValue(), item.getUnitAmount());
                            }

                            VaultBridge.withdrawPlayer(player.getName(), newPrice);
                            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Buy_PlayerHint.
                                    replace("%player", player.getDisplayName()).
                                    replace("%amount", String.valueOf(internalAmountWishedAsync)).
                                    replace("%item", niceItemName).
                                    replace("%shop", region.getName()).
                                    replace("%price", String.valueOf(newPrice)).
                                    replace("%owner", item.getOwner()));

                            item.setSold(item.getSold() + internalAmountWishedAsync);

                            ItemStorage itemStorage = region.getItemStorage();
                            itemStorage.setItemAmount(itemStorage.getItemAmount() - internalAmountWishedAsync);

                            try {
                                Database.getDAO(ItemStorage.class).update(itemStorage);
                                Database.getDAO(Items.class).update(item);
                            } catch (SQLException e) {
                                Logger.error("Could not update Items/ItemStorage", e);
                            }

                            TransactionRepository.generateTransaction(player, Transaction.TransactionType.BUY, region.getName(), player.getWorld().getName(), item.getOwner(), item.getMeta().getItemID(), internalAmountWishedAsync, item.getSell().doubleValue(), 0.0, item.getUnitAmount());

                        }
                    });
                }
            });
        } else {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Buy_NotEnoughMoney.replace("%price", price.toString()));
        }
    }
}
