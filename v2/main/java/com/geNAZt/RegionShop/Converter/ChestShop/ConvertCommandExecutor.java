package com.geNAZt.RegionShop.Converter.ChestShop;

import com.Acrobot.Breeze.Utils.MaterialUtil;
import com.Acrobot.Breeze.Utils.PriceUtil;
import com.Acrobot.ChestShop.Containers.AdminInventory;
import com.Acrobot.ChestShop.Signs.ChestShopSign;
import com.Acrobot.ChestShop.Utils.uBlock;
import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Converter.ChestShopConverter;
import com.geNAZt.RegionShop.Data.Storage.InRegion;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.sk89q.worldedit.Vector;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static com.Acrobot.Breeze.Utils.BlockUtil.isSign;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 15.06.13
 */
public class ConvertCommandExecutor implements CommandExecutor {
    @SuppressWarnings("ConstantConditions")
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean isPlayer = (sender instanceof Player);
        Player p = (isPlayer) ? (Player) sender : null;

        if(!isPlayer) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + "No shop for you Console!");
            return true;
        }

        if (p.hasPermission("rs.command.convert")) {
            if(InRegion.has(p)) {
                Region region = InRegion.get(p);

                Vector minPoint = new Vector(region.getMinX(), region.getMinY(), region.getMinZ());
                Vector maxPoint = new Vector(region.getMaxX(), region.getMaxY(), region.getMaxZ());

                p.sendMessage(minPoint.toString());
                p.sendMessage(maxPoint.toString());

                for(Integer x = (int)minPoint.getX(); x < maxPoint.getX() + 1; x++) {
                    for(Integer y = (int)minPoint.getY(); y < maxPoint.getY() + 1; y++) {
                        for(Integer z = (int)minPoint.getZ(); z < maxPoint.getZ() + 1; z++) {
                            Block block = p.getWorld().getBlockAt(x, y, z);

                            if (block == null) {
                                continue;
                            }

                            if (!isSign(block)) { // Blocking accidental sign edition
                                continue;
                            }

                            Sign sign = (Sign) block.getState();

                            if (!ChestShopSign.isValid(sign)) {
                                continue;
                            }

                            ItemStack item = MaterialUtil.getItem(sign.getLine(3));

                            Chest chest = uBlock.findConnectedChest(sign);
                            Inventory ownerInventory = (ChestShopSign.isAdminShop(sign) ? new AdminInventory() : chest != null ? chest.getInventory() : null);

                            if(ownerInventory != null) {
                                Float buy = ((Double)PriceUtil.getBuyPrice(sign.getLine(2))).floatValue();
                                Float sell = ((Double)PriceUtil.getSellPrice(sign.getLine(2))).floatValue();

                                if(buy < 0) {
                                    buy = 0.0F;
                                }

                                if(sell < 0) {
                                    sell = 0.0F;
                                }

                                int amount = Integer.parseInt(sign.getLine(1));

                                if (amount < 1) {
                                    amount = 1;
                                }

                                ChestShopConverter.convertInventory(ownerInventory, item, p, sign.getLine(0), sell, buy, amount);
                                block.breakNaturally();
                            }
                        }
                    }
                }
            } else {
                p.sendMessage(ConfigManager.main.Chat_prefix + ChatColor.RED + "You are not inside a Shop Region.");
            }
        } else {
            p.sendMessage(ConfigManager.main.Chat_prefix + ChatColor.RED + "You haven't enough permissions for this.");
        }

        return true;
    }
}
