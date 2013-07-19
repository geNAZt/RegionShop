package com.geNAZt.RegionShop.Interface;

import com.geNAZt.RegionShop.Bukkit.Events.RegionShopConfigReload;
import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Data.Storages.Profiler;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.geNAZt.RegionShop.Util.Loader.loadFromJAR;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class ShopExecutor implements CommandExecutor, Listener {
    private CopyOnWriteArrayList<ShopCommand> loadedCommands = new CopyOnWriteArrayList<ShopCommand>();
    private CopyOnWriteArrayList<ShopCommand> adminCommands = new CopyOnWriteArrayList<ShopCommand>();

    private final RegionShopPlugin plugin;

    public ShopExecutor(RegionShopPlugin pl) {
        pl.getServer().getPluginManager().registerEvents(this, pl);
        this.plugin = pl;

        loadAllCommands();
    }

    private void loadAllCommands() {
        loadedCommands = new CopyOnWriteArrayList<ShopCommand>();
        adminCommands = new CopyOnWriteArrayList<ShopCommand>();

        loadedCommands = loadFromJAR("com.geNAZt.RegionShop.Interface.CLI.Shop", ShopCommand.class, new Object[]{plugin});
        adminCommands  = loadFromJAR("com.geNAZt.RegionShop.Interface.CLI.Admin", ShopCommand.class, new Object[]{plugin});

        for(Object command : loadedCommands) {
            ShopCommand shopCommand = (ShopCommand) command;

            if(!plugin.getConfig().getBoolean("interfaces.command." + shopCommand.getCommand(), true)) {
                loadedCommands.remove(loadedCommands.indexOf(shopCommand));
            }
        }

        plugin.getLogger().info("Loaded ShopCommands: " + loadedCommands.toString());
        plugin.getLogger().info("Loaded AdminCommands: " + adminCommands.toString());
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
                Profiler.start("ShopCommand:" + args[0]);

                if(args[0].equalsIgnoreCase("help")) {
                    if (args.length > 1) {
                        Integer page;

                        try {
                            page = Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            p.sendMessage(Chat.getPrefix() + ChatColor.RED +  "Only numbers as page value allowed");
                            Profiler.end("ShopCommand:" + args[0]);
                            return true;
                        }

                        showHelp(p, page);
                        Profiler.end("ShopCommand:" + args[0]);
                        return true;
                    } else {
                        showHelp(p, 1);
                        Profiler.end("ShopCommand:" + args[0]);
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
                                        Profiler.end("ShopCommand:" + args[0]);
                                        return true;
                                    } else {
                                        p.sendMessage(Chat.getPrefix() + ChatColor.RED + "Not enough arguments given. Type " + ChatColor.DARK_RED + "/shop help 3" + ChatColor.RED + " for more informations.");
                                        Profiler.end("ShopCommand:" + args[0]);
                                        return true;
                                    }
                                } else {
                                    p.sendMessage(Chat.getPrefix() + ChatColor.RED + "You don't have the permission " + ChatColor.DARK_RED + command.getPermissionNode());
                                    Profiler.end("ShopCommand:" + args[0]);
                                    return true;
                                }
                            }
                        }

                        showHelp(p, 4);
                        Profiler.end("ShopCommand:" + args[0]);
                        return true;
                    } else {
                        showHelp(p, 4);
                        Profiler.end("ShopCommand:" + args[0]);
                        return true;
                    }
                } else {
                    for(ShopCommand command : loadedCommands) {
                        if(command.getCommand().equalsIgnoreCase(args[0])) {
                            if(command.getPermissionNode() == null || p.hasPermission(command.getPermissionNode())) {
                                if(args.length > command.getNumberOfArgs()) {
                                    command.execute(p, Arrays.copyOfRange(args, 1, args.length));
                                    Profiler.end("ShopCommand:" + args[0]);
                                    return true;
                                } else {
                                    p.sendMessage(Chat.getPrefix() + ChatColor.RED + "Not enough arguments given. Type " + ChatColor.DARK_RED + "/shop help" + ChatColor.RED + " for more informations.");
                                    Profiler.end("ShopCommand:" + args[0]);
                                    return true;
                                }
                            } else {
                                p.sendMessage(Chat.getPrefix() + ChatColor.RED + "You don't have the permission " + ChatColor.DARK_RED + command.getPermissionNode());
                                Profiler.end("ShopCommand:" + args[0]);
                                return true;
                            }
                        }
                    }
                }

                showHelp(p, 1);

                Profiler.end("ShopCommand:" + args[0]);
                return true;
            } else {
                showHelp(p, 1);
                return true;
            }
        }

        return false;
    }

    private void showHelp(Player sender, Integer page) {
        Integer maxPage = sender.hasPermission("rs.admin") ? 4 : 3;

        if (page == 1) {
            sender.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "RegionShop: Help" + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Page " + ChatColor.RED + "1" + ChatColor.GOLD + "/" + ChatColor.RED + maxPage + ChatColor.GREEN + " (Consumers)" + ChatColor.YELLOW + "--");
            sender.sendMessage(Chat.getPrefix() + ChatColor.RED + "Necessary arguments");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GREEN + "Optional arguments");
            showHelpPage(sender, page);
            return;
        }

        if (page == 2) {
            sender.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "RegionShop: Help" + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Page " + ChatColor.RED + "2" + ChatColor.GOLD + "/" + ChatColor.RED + maxPage + ChatColor.GREEN + " (ShopOwner)" + ChatColor.YELLOW + "--");
            sender.sendMessage(Chat.getPrefix() + ChatColor.RED + "Necessary arguments");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GREEN + "Optional arguments");
            showHelpPage(sender, page);
            return;
        }

        if (page == 3) {
            sender.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "RegionShop: Help" + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Page " + ChatColor.RED + "3" + ChatColor.GOLD + "/" + ChatColor.RED + maxPage + ChatColor.GREEN + " (Signs)" + ChatColor.YELLOW + "--");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "Please visit " + ChatColor.GREEN + "<linkhere>" + ChatColor.GOLD + " for the Sign Help");

            return;
        }

        if (page == 4 && sender.hasPermission("rs.admin")) {
            sender.sendMessage(Chat.getPrefix() + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "RegionShop: Help" + ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Page " + ChatColor.RED + "4" + ChatColor.GOLD + "/" + ChatColor.RED + maxPage + ChatColor.GREEN + " (Admin)" + ChatColor.YELLOW + "--");
            sender.sendMessage(Chat.getPrefix() + ChatColor.RED + "Necessary arguments");
            sender.sendMessage(Chat.getPrefix() + ChatColor.GREEN + "Optional arguments");
            showHelpPage(sender, page);
            sender.sendMessage(Chat.getPrefix() + ChatColor.GOLD + "/convert " + ChatColor.RESET + ": Starts the loaded Converter if any is loaded");
            return;
        }

        sender.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid Help page");
    }

    private void showHelpPage(Player player, Integer page) {
        if(page == 4) {
            for(Object command : adminCommands) {
                ShopCommand shopCommand = (ShopCommand) command;

                if(!player.hasPermission(shopCommand.getPermissionNode())) continue;

                if(shopCommand.getHelpPage() == page) {
                    String[] helpTexts = shopCommand.getHelpText();
                    for(String helpText : helpTexts) {
                        player.sendMessage(Chat.getPrefix() + helpText);
                    }
                }
            }
        } else {
            for(Object command : loadedCommands) {
                ShopCommand shopCommand = (ShopCommand) command;

                if(!player.hasPermission(shopCommand.getPermissionNode())) continue;

                if(shopCommand.getHelpPage() == page) {
                    String[] helpTexts = shopCommand.getHelpText();
                    for(String helpText : helpTexts) {
                        player.sendMessage(Chat.getPrefix() + helpText);
                    }
                }
            }
        }
    }
}
