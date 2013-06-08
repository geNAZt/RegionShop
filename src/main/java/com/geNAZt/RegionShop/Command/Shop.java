package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;
import com.geNAZt.RegionShop.Util.ItemName;
import com.geNAZt.RegionShop.Util.PlayerStorage;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

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
    private ShopName shopName;
    private ShopSearch shopSearch;
    private ShopResult shopResult;

    public Shop(RegionShopPlugin pl) {
        this.shopWarp = new ShopWarp(pl);
        this.shopList = new ShopList(pl);
        this.shopAdd = new ShopAdd(pl);
        this.shopDetail = new ShopDetail(pl);
        this.shopEquip = new ShopEquip(pl);
        this.shopSet = new ShopSet(pl);
        this.shopSell = new ShopSell(pl);
        this.shopBuy = new ShopBuy(pl);
        this.shopName = new ShopName(pl);
        this.shopSearch = new ShopSearch(pl);
        this.shopResult = new ShopResult(pl);
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
                                Integer page;

                                try {
                                    page = Integer.parseInt(args[1]);
                                } catch (NumberFormatException e) {
                                    p.sendMessage(Chat.getPrefix() + "Only Numbers as page value");
                                    return true;
                                }

                                shopList.execute(p, regString, page);
                                return true;
                            } else {
                                shopList.execute(p, regString, 1);
                                return true;
                            }
                        } else {
                            shopList.execute(p, null, 1);
                            return true;
                        }
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.list");
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("warp")) {
                    if (p.hasPermission("rs.warp")) {
                        if (args.length > 1) {
                            String[] nameParts = Arrays.copyOfRange(args, 1, args.length);
                            shopWarp.execute(p, StringUtils.join(nameParts, " "));
                            return true;
                        } else {
                            p.sendMessage(Chat.getPrefix() + "Not enough Arguments given. Type /shop help for help.");
                            return true;
                        }
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.warp");
                        return true;
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
                            return true;
                        } else {
                            p.sendMessage(Chat.getPrefix() + "Not enough Arguments given. Type /shop help for help.");
                            return true;
                        }
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.stock.add");
                        return true;
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
                            return true;
                        } else {
                            p.sendMessage(Chat.getPrefix() + "Not enough Arguments given. Type /shop help for help.");
                            return true;
                        }
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.detail");
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("equip")) {
                    if (p.hasPermission("rs.stock.equip")) {
                        if (args.length > 1) {
                            String[] nameParts = Arrays.copyOfRange(args, 1, args.length);
                            shopEquip.execute(p, StringUtils.join(nameParts, " "));
                            return true;
                        } else {
                            shopEquip.execute(p, null);
                            return true;
                        }
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.stock.equip");
                        return true;
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
                            return true;
                        } else {
                            p.sendMessage(Chat.getPrefix() + "Not enough Arguments given. Type /shop help for help.");
                            return true;
                        }
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.stock.set");
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("sell")) {
                    if (p.hasPermission("rs.sell")) {
                        shopSell.execute(p);
                        return true;
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.sell");
                        return true;
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
                            return true;
                        } else if (args.length > 1) {
                            Integer shopItemId;

                            try {
                                shopItemId = Integer.parseInt(args[1]);
                            } catch (NumberFormatException e) {
                                p.sendMessage(Chat.getPrefix() + "Only Numbers as shopItemId value");
                                return true;
                            }

                            shopBuy.execute(p, shopItemId, -1);
                            return true;
                        } else {
                            p.sendMessage(Chat.getPrefix() + "Not enough Arguments given. Type /shop help for help.");
                            return true;
                        }
                    } else {
                        p.sendMessage(Chat.getPrefix() + "You don't have the permission " + ChatColor.RED + "rs.buy");
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("name")) {
                    if (args.length > 1) {
                        String[] nameParts = Arrays.copyOfRange(args, 1, args.length);

                        shopName.execute(p, StringUtils.join(nameParts, " "));
                        return true;
                    } else {
                        p.sendMessage(Chat.getPrefix() + "Not enough Arguments given. Type /shop help for help.");
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("search")) {
                    if (args.length > 1) {
                        String[] nameParts = Arrays.copyOfRange(args, 1, args.length);

                        shopSearch.execute(p, ItemName.nicer(StringUtils.join(nameParts, "_")));
                        return true;
                    } else {
                        p.sendMessage(Chat.getPrefix() + "Not enough Arguments given. Type /shop help for help.");
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("result")) {
                    if (args.length > 1) {
                        Integer page;

                        try {
                            page = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            p.sendMessage(Chat.getPrefix() + "Only Numbers as page value");
                            return true;
                        }

                        shopResult.execute(p, page);
                        return true;
                    } else {
                        p.sendMessage(Chat.getPrefix() + "Not enough Arguments given. Type /shop help for help.");
                        return true;
                    }
                } else if(args[0].equalsIgnoreCase("help")) {
                    if (args.length > 1) {
                        Integer page;

                        try {
                            page = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            p.sendMessage(Chat.getPrefix() + "Only Numbers as page value");
                            return true;
                        }

                        showHelp(p, page);
                        return true;
                    } else {
                        showHelp(p, 1);
                        return true;
                    }
                } else {
                    showHelp(p, 1);
                    return true;
                }
            } else {
                showHelp(p, 1);
                return true;
            }
        }

        return false;
    }

    private void showHelp(Player sender, Integer page) {
        if (page == 1) {
            sender.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Help: RegionShop " + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Page " + ChatColor.RED + "1" + ChatColor.GOLD + "/" + ChatColor.RED + "2 " + ChatColor.YELLOW + "--");
            sender.sendMessage(Chat.getPrefix() + ChatColor.RED + "Necessary arguments");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GREEN + "Optional arguments");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop list" + ChatColor.RESET + ": List items in the shop (inside a shopregion)");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop list" + ChatColor.RESET + ": List all available shops (outside a shopregion)");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop search " + ChatColor.RED + "ItemID/ItemName" + ChatColor.RESET + ": Search for " + ChatColor.RED + "ItemID/ItemName");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop warp " + ChatColor.RED + "owner" + ChatColor.RESET + ": Warp to " + ChatColor.RED + "owner" + ChatColor.RESET + "'s shop");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop warp " + ChatColor.RED + "shopname" + ChatColor.RESET + ": Warp to the shop called " + ChatColor.RED + "shopname");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop detail " + ChatColor.RED + "shopItemID" + ChatColor.RESET + ": Display details of " + ChatColor.RED + "shopItemID");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop add " + ChatColor.RED + "sellprice buyprice amount" + ChatColor.RESET + ": Add current item in hand to the shop stock");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop buy " + ChatColor.RED + "shopItemID" +  ChatColor.GREEN + "amount" + ChatColor.RESET + ": Buy (" + ChatColor.GREEN + "amount" + ChatColor.RESET + " pcs. of) " + ChatColor.RED + "shopItemID " + ChatColor.RESET + "from the shop");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop sell " + ChatColor.RED + "shopItemID " + ChatColor.GREEN + "amount" + ChatColor.RESET + ": sell (" + ChatColor.GREEN + "amount" + ChatColor.RESET + " pcs. of) " + ChatColor.RED + "shopItemID " + ChatColor.RESET + "to the shop");
        }

        if (page == 2) {
            sender.sendMessage(Chat.getPrefix() +ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Help: RegionShop " + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Page " + ChatColor.RED + "2" + ChatColor.GOLD + "/" + ChatColor.RED + "2 " + ChatColor.YELLOW + "--");
            sender.sendMessage(Chat.getPrefix() +ChatColor.RED + "Necessary arguments");
            sender.sendMessage(Chat.getPrefix() +ChatColor.GREEN + "Optional arguments");
        }
    }
}
