package com.geNAZt.RegionShop.Command;

import com.geNAZt.RegionShop.RegionShopPlugin;
import com.geNAZt.RegionShop.Util.Chat;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;

import static com.geNAZt.RegionShop.Util.Loader.loadFromJAR;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopExecutor implements CommandExecutor {
    private ArrayList<ShopCommand> loadedCommands = new ArrayList<ShopCommand>();
    private ArrayList<ShopCommand> adminCommands = new ArrayList<ShopCommand>();

    private final RegionShopPlugin plugin;

    public ShopExecutor(RegionShopPlugin pl) {
        loadedCommands = loadFromJAR(pl, "com.geNAZt.RegionShop.Command.Shop", ShopCommand.class);
        adminCommands  = loadFromJAR(pl, "com.geNAZt.RegionShop.Command.Admin", ShopCommand.class);

        for(Object command : loadedCommands) {
            ShopCommand shopCommand = (ShopCommand) command;

            if(!pl.getConfig().getBoolean("interfaces.command." + shopCommand.getCommand(), true)) {
                loadedCommands.remove(loadedCommands.indexOf(shopCommand));
            }
        }

        pl.getLogger().info("Loaded ShopCommands: " + loadedCommands.toString());
        pl.getLogger().info("Loaded AdminCommands: " + adminCommands.toString());

        this.plugin = pl;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean isPlayer = (sender instanceof Player);
        Player p = (isPlayer) ? (Player) sender : null;

        if(!isPlayer) {
            sender.sendMessage(Chat.getPrefix() + "No shop for you Console!");
            return true;
        }

        if(plugin.getConfig().getBoolean("only-survival") && p.getGameMode() != GameMode.SURVIVAL) {
            p.sendMessage(Chat.getPrefix() + ChatColor.RED + "You must be in survival Mode to use the Shop");

            return true;
        }

        if (cmd.getName().equalsIgnoreCase("shop")) {
            if (args.length > 0) {
                if(args[0].equalsIgnoreCase("help")) {
                    if (args.length > 1) {
                        Integer page;

                        try {
                            page = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            p.sendMessage(Chat.getPrefix() + ChatColor.RED +  "Only numbers as page value allowed");
                            return true;
                        }

                        showHelp(p, page);
                        return true;
                    } else {
                        showHelp(p, 1);
                        return true;
                    }
                }


                if(args[0].equalsIgnoreCase("admin") && sender.hasPermission("rs.admin")) {
                    if(args.length > 1) {
                        for(ShopCommand command : adminCommands) {
                            if(command.getCommand().equalsIgnoreCase(args[1])) {
                                if(command.getPermissionNode() == null || p.hasPermission(command.getPermissionNode())) {
                                    if(args.length > command.getNumberOfArgs()) {
                                        command.execute(p, Arrays.copyOfRange(args, 2, args.length));
                                    } else {
                                        p.sendMessage(Chat.getPrefix() + ChatColor.RED + "Not enough arguments given. Type " + ChatColor.DARK_RED + "/shop help 3" + ChatColor.RED + " for more informations.");
                                        return true;
                                    }
                                } else {
                                    p.sendMessage(Chat.getPrefix() + ChatColor.RED + "You don't have the permission " + ChatColor.DARK_RED + command.getPermissionNode());
                                    return true;
                                }
                            }
                        }

                        showHelp(p, 3);
                        return true;
                    } else {
                        showHelp(p, 3);
                        return true;
                    }
                } else {
                    for(ShopCommand command : loadedCommands) {
                        if(command.getCommand().equalsIgnoreCase(args[0])) {
                            if(command.getPermissionNode() == null || p.hasPermission(command.getPermissionNode())) {
                                if(args.length > command.getNumberOfArgs()) {
                                    command.execute(p, Arrays.copyOfRange(args, 1, args.length));
                                } else {
                                    p.sendMessage(Chat.getPrefix() + ChatColor.RED + "Not enough arguments given. Type " + ChatColor.DARK_RED + "/shop help" + ChatColor.RED + " for more informations.");
                                    return true;
                                }
                            } else {
                                p.sendMessage(Chat.getPrefix() + ChatColor.RED + "You don't have the permission " + ChatColor.DARK_RED + command.getPermissionNode());
                                return true;
                            }
                        }
                    }
                }

                showHelp(p, 1);
                return true;
            } else {
                showHelp(p, 1);
                return true;
            }
        }

        return false;

                /*else if(args[0].equalsIgnoreCase("warp")) {
                    if (p.hasPermission("rs.warp")) {
                        if (args.length > 1) {
                            String[] nameParts = Arrays.copyOfRange(args, 1, args.length);
                            shopWarp.execute(p, StringUtils.join(nameParts, " "));
                            return true;
                        } else {
                            p.sendMessage(Chat.getPrefix() + ChatColor.RED + "Not enough arguments given. Type " + ChatColor.DARK_RED + "/shop help" + ChatColor.RED + " for more informations.");
                            return true;
                        }
                    } else {
                        p.sendMessage(Chat.getPrefix() + ChatColor.RED + "You don't have the permission " + ChatColor.DARK_RED + "rs.warp");
                        return true;
                    }
                }*/
    }

    private void showHelp(Player sender, Integer page) {
        if (page == 1) {
            sender.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "RegionShop: Help" + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Page " + ChatColor.RED + "1" + ChatColor.GOLD + "/" + ChatColor.RED + "2 " + ChatColor.YELLOW + "--");
            sender.sendMessage(Chat.getPrefix() + ChatColor.RED + "Necessary arguments");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GREEN + "Optional arguments");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop list" + ChatColor.RESET + ": List items in the shop (inside a shopregion)");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop list" + ChatColor.RESET + ": List all available shops (outside a shopregion)");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop search " + ChatColor.RED + "ItemID/ItemName" + ChatColor.RESET + ": Search for " + ChatColor.RED + "ItemID/ItemName");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop result " + ChatColor.RED + "page" + ChatColor.RESET + ": Browse to page " + ChatColor.RED + "page");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop warp " + ChatColor.RED + "owner" + ChatColor.RESET + ": Warp to " + ChatColor.RED + "owner" + ChatColor.RESET + "'s shop");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop warp " + ChatColor.RED + "shopname" + ChatColor.RESET + ": Warp to the shop called " + ChatColor.RED + "shopname");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop detail " + ChatColor.RED + "shopItemID" + ChatColor.RESET + ": Display details of " + ChatColor.RED + "shopItemID");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop buy " + ChatColor.RED + "shopItemID " +  ChatColor.GREEN + "amount" + ChatColor.RESET + ": Buy (" + ChatColor.GREEN + "amount" + ChatColor.RESET + " pcs. of) " + ChatColor.RED + "shopItemID " + ChatColor.RESET + "from the shop");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop sell " + ChatColor.RED + "shopItemID " + ChatColor.GREEN + "amount" + ChatColor.RESET + ": Sell (" + ChatColor.GREEN + "amount" + ChatColor.RESET + " pcs. of) " + ChatColor.RED + "shopItemID " + ChatColor.RESET + "to the shop");
            return;
        }

        if (page == 2) {
            sender.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "RegionShop: Help" + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Page " + ChatColor.RED + "2" + ChatColor.GOLD + "/" + ChatColor.RED + "2 " + ChatColor.YELLOW + "--");
            sender.sendMessage(Chat.getPrefix() + ChatColor.RED + "Necessary arguments");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GREEN + "Optional arguments");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop add " + ChatColor.RED + "sellprice buyprice amount" + ChatColor.RESET + ": Add current item in hand to the shop stock");
            if(plugin.getConfig().getBoolean("interfaces.command.equip")) sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop equip" + ChatColor.RESET + ": Toggle " + ChatColor.GRAY + "quick add");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop name " + ChatColor.RED + "shopname" + ChatColor.RESET + ": Rename your shop to " + ChatColor.RED + "shopname");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop set " + ChatColor.RED + "shopItemID sellprice buyprice amount" + ChatColor.RESET + ": Set/adjust the price for " + ChatColor.RED + "shopItemID");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop remove " + ChatColor.RED + "shopItemID" + ChatColor.RESET + ": Remove the "+ ChatColor.RED + "shopItemID" + ChatColor.RESET + " out of the Shop");
            return;
        }

        if (page == 3 && sender.hasPermission("rs.admin")) {
            sender.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "RegionShop: Help" + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Page " + ChatColor.RED + "2" + ChatColor.GOLD + "/" + ChatColor.RED + "2 " + ChatColor.YELLOW + "--");
            sender.sendMessage(Chat.getPrefix() + ChatColor.RED + "Necessary arguments");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GREEN + "Optional arguments");
            if(sender.hasPermission("rs.admin.reload")) sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/shop admin reload: Reload the Config from RegionShop");
            return;
        }

        sender.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid Help page");
    }
}
