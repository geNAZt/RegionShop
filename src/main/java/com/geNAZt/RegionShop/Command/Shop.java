package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.PlayerStorage;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created with IntelliJ IDEA.
 * User: geNAZt
 * Date: 06.06.13
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class Shop implements CommandExecutor {
    private ShopWarp shopWarp;
    private ShopList shopList;
    private ShopAdd shopAdd;
    private ShopDetail shopDetail;
    private ShopEquip shopEquip;
    private ShopSet shopSet;
    private ShopSell shopSell;
    private ShopBuy shopBuy;

    public Shop(RegionShopPlugin pl) {
        this.shopWarp = new ShopWarp(pl);
        this.shopList = new ShopList(pl);
        this.shopAdd = new ShopAdd(pl);
        this.shopDetail = new ShopDetail(pl);
        this.shopEquip = new ShopEquip(pl);
        this.shopSet = new ShopSet(pl);
        this.shopSell = new ShopSell(pl);
        this.shopBuy = new ShopBuy(pl);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean isPlayer = (sender instanceof Player);
        Player p = (isPlayer) ? (Player) sender : null;

        if(!isPlayer) {
            sender.sendMessage(Chat.getPrefix() + "No shop for you Console!");
            return true;
        }

        if (cmd.getName().equalsIgnoreCase("shop")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("list")) {
                    if (p.hasPermission("rs.list")) {
                        if (PlayerStorage.getPlayer(p) != null) {
                            String regString = PlayerStorage.getPlayer(p);

                            if (args.length > 1) {
                                shopList.execute(p, regString, args[1]);
                            } else {
                                shopList.execute(p, regString, "1");
                            }
                        }
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.list");
                    }
                } else if(args[0].equalsIgnoreCase("warp")) {
                    if (p.hasPermission("rs.warp")) {
                        if (args.length > 1) {
                            shopWarp.execute(p, args[1]);
                        } else {
                            p.sendMessage(Chat.getPrefix() + "Not enough Arguments given");
                            showHelp(p);

                            return true;
                        }
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.warp");
                    }
                } else if(args[0].equalsIgnoreCase("add")) {
                    if (p.hasPermission("rs.stock.add")) {
                        if (args.length > 3) {
                            Integer buy, sell, amount;

                            try {
                                buy = Integer.parseInt(args[2]);
                                sell = Integer.parseInt(args[1]);
                                amount = Integer.parseInt(args[3]);
                            } catch (NumberFormatException e) {
                                p.sendMessage(Chat.getPrefix() + "Only Numbers as sell, buy and amount values");
                                return true;
                            }

                            shopAdd.execute(p, buy, sell, amount);
                        } else {
                            p.sendMessage(Chat.getPrefix() + "Not enough Arguments given");
                            showHelp(p);

                            return true;
                        }
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.stock.add");
                    }
                } else if(args[0].equalsIgnoreCase("detail")) {
                    if (p.hasPermission("rs.detail")) {
                        if (args.length > 1) {
                            Integer itemId;

                            try {
                                itemId = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                p.sendMessage(Chat.getPrefix() + "Only Numbers as detail ItemID");
                                return true;
                            }

                            shopDetail.execute(p, itemId);
                        } else {
                            p.sendMessage(Chat.getPrefix() + "Not enough Arguments given");
                            showHelp(p);

                            return true;
                        }
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.detail");
                    }
                } else if(args[0].equalsIgnoreCase("equip")) {
                    if (p.hasPermission("rs.stock.equip")) {
                        if (args.length > 1) {
                            shopEquip.execute(p, args[1]);
                        } else {
                            shopEquip.execute(p, null);
                        }
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.stock.equip");
                    }
                } else if(args[0].equalsIgnoreCase("set")) {
                    if (p.hasPermission("rs.stock.set")) {
                        if (args.length > 4) {
                            Integer buy, sell, amount, shopItemId;

                            try {
                                shopItemId = Integer.parseInt(args[1]);
                                buy = Integer.parseInt(args[3]);
                                sell = Integer.parseInt(args[2]);
                                amount = Integer.parseInt(args[4]);
                            } catch (NumberFormatException e) {
                                p.sendMessage(Chat.getPrefix() + "Only Numbers as shopItemId, buy, sell and amount values");
                                return true;
                            }

                            shopSet.execute(p, shopItemId, sell, buy, amount);
                        } else {
                            p.sendMessage(Chat.getPrefix() + "Not enough Arguments given");
                            showHelp(p);

                            return true;
                        }
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.stock.set");
                    }
                } else if(args[0].equalsIgnoreCase("sell")) {
                    if (p.hasPermission("rs.sell")) {
                        shopSell.execute(p);
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.sell");
                    }
                } else if(args[0].equalsIgnoreCase("buy")) {
                    if (p.hasPermission("rs.buy")) {
                        if (args.length > 2) {
                            Integer shopItemId, amount;

                            try {
                                shopItemId = Integer.parseInt(args[1]);
                                amount = Integer.parseInt(args[2]);
                            } catch (NumberFormatException e) {
                                p.sendMessage(Chat.getPrefix() + "Only Numbers as shopItemId and amount values");
                                return true;
                            }

                            shopBuy.execute(p, shopItemId, amount);
                        } else if (args.length > 1) {
                            Integer shopItemId;

                            try {
                                shopItemId = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                p.sendMessage(Chat.getPrefix() + "Only Numbers as shopItemId value");
                                return true;
                            }

                            shopBuy.execute(p, shopItemId, -1);
                        } else {
                            p.sendMessage(Chat.getPrefix() + "Not enough Arguments given");
                            showHelp(p);

                            return true;
                        }
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.buy");
                    }
                } else {
                    showHelp(p);
                }
            } else {
                showHelp(p);
            }

            return true;
        }

        return false;
    }

    private void showHelp(Player sender) {
        sender.sendMessage(Chat.getPrefix() + "RegionShop help");
        sender.sendMessage(Chat.getPrefix() + ChatColor.RED + "Things in red must be given    " + ChatColor.GREEN + "Things in green are optional");
        sender.sendMessage(Chat.getPrefix() + "----------------------------------------");
        sender.sendMessage(Chat.getPrefix() + "Consumer Commands: ");
        sender.sendMessage(Chat.getPrefix() + ChatColor.AQUA + "/shop list -> " + ChatColor.RESET + " Show all Items in a Shop");
        sender.sendMessage(Chat.getPrefix() + ChatColor.AQUA + "/shop detail "+ ChatColor.RED +"shopItemID"+ ChatColor.AQUA +"-> " + ChatColor.RESET + " Show details of an Item");
        sender.sendMessage(Chat.getPrefix() + ChatColor.AQUA + "/shop warp "+ ChatColor.RED +"owner"+ ChatColor.AQUA +" -> " + ChatColor.RESET + " Warp to the Shop of Player <owner>");
        sender.sendMessage(Chat.getPrefix() + ChatColor.AQUA + "/shop warp "+ ChatColor.RED +"region"+ ChatColor.AQUA +" -> " + ChatColor.RESET + " Warp to the <region> of a Shop");
        sender.sendMessage(Chat.getPrefix() + " ");
        sender.sendMessage(Chat.getPrefix() + "Shop Owner Commands: ");
        sender.sendMessage(Chat.getPrefix() + ChatColor.AQUA + "/shop add "+ ChatColor.RED +"sell buy amount"+ ChatColor.AQUA +"-> " + ChatColor.RESET + " Adds the current in Hand Item to the Shop. <sell> - Amount of $ which you want to sell this Item for; <buy> - Amount of $ you want to buy this Item for; <amount> - Amount of Items which will be dealt");
        sender.sendMessage(Chat.getPrefix() + ChatColor.AQUA + "/shop equip "+ ChatColor.GREEN +"region"+ ChatColor.AQUA +" -> " + ChatColor.RESET + " Toggle if dropped items will be added into the Shop. If you have multiple shop select via <region>");
    }
}
