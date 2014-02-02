package net.cubespace.RegionShop.Interface.CLI;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Interface.CLI.Commands.Add;
import net.cubespace.RegionShop.Interface.CLI.Commands.Buy;
import net.cubespace.RegionShop.Interface.CLI.Commands.Detail;
import net.cubespace.RegionShop.Interface.CLI.Commands.Equip;
import net.cubespace.RegionShop.Interface.CLI.Commands.Filter;
import net.cubespace.RegionShop.Interface.CLI.Commands.Help;
import net.cubespace.RegionShop.Interface.CLI.Commands.List;
import net.cubespace.RegionShop.Interface.CLI.Commands.Name;
import net.cubespace.RegionShop.Interface.CLI.Commands.Reload;
import net.cubespace.RegionShop.Interface.CLI.Commands.Remove;
import net.cubespace.RegionShop.Interface.CLI.Commands.Result;
import net.cubespace.RegionShop.Interface.CLI.Commands.Search;
import net.cubespace.RegionShop.Interface.CLI.Commands.Sell;
import net.cubespace.RegionShop.Interface.CLI.Commands.Set;
import net.cubespace.RegionShop.Interface.CLI.Commands.Shop;
import net.cubespace.RegionShop.Interface.CLI.Commands.Transaction;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 */
public class CommandExecutor implements org.bukkit.command.CommandExecutor {
    private HashMap<String, net.cubespace.RegionShop.Data.Struct.Command> commandMap = new HashMap<String, net.cubespace.RegionShop.Data.Struct.Command>();
    private static HashMap<String, ArrayList<String>> help = new HashMap<String, ArrayList<String>>();

    public CommandExecutor() {
        //Load all commands
        ArrayList<CLICommand> commands = new ArrayList<CLICommand>();

        commands.add(new Shop());
        commands.add(new Add());
        commands.add(new Help());
        commands.add(new Set());
        commands.add(new List());
        commands.add(new Buy());
        commands.add(new Sell());
        commands.add(new Search());
        commands.add(new Result());
        commands.add(new Reload());
        commands.add(new Filter());
        commands.add(new Detail());
        commands.add(new Name());
        commands.add(new Remove());
        commands.add(new Equip());
        commands.add(new Transaction());

        //Map all given commands
        for (CLICommand cmd : commands) {
            for (Method method : cmd.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    Annotation[] annotations = method.getAnnotations();

                    for (Annotation annotation : annotations) {
                        if (annotation instanceof Command) {
                            Command aCmd = (Command) annotation;

                            //Save the help to the command
                            if (!help.containsKey(aCmd.helpPage())) {
                                help.put(aCmd.helpPage(), new ArrayList<String>());
                            }

                            ArrayList<String> helpPage = help.get(aCmd.helpPage());

                            for (Field field : ConfigManager.language.getClass().getDeclaredFields()) {
                                if (field.getName().equals(aCmd.helpKey())) {
                                    try {
                                        Object value = field.get(ConfigManager.language);
                                        if (value instanceof String) {
                                            helpPage.add((String) value);
                                        }

                                        if (value instanceof ArrayList) {
                                            ArrayList<String> strings = (ArrayList<String>) value;

                                            for (String helpText : strings) {
                                                helpPage.add(helpText);
                                            }
                                        }
                                    } catch (IllegalAccessException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }

                            help.put(aCmd.helpPage(), helpPage);

                            //Save the command
                            net.cubespace.RegionShop.Data.Struct.Command command = new net.cubespace.RegionShop.Data.Struct.Command();
                            command.annotation = aCmd;
                            command.command = method;

                            commandMap.put(aCmd.command(), command);
                        }
                    }
                }
            }
        }
    }

    @Override
    public boolean onCommand(final CommandSender commandSender, final org.bukkit.command.Command command, String s, final String[] args) {
        for (int argsIncluded = args.length; argsIncluded >= -1; argsIncluded--) {
            StringBuilder identifierBuilder = new StringBuilder(command.getName());
            for (int i = 0; i < argsIncluded; i++) {
                identifierBuilder.append(' ').append(args[i]);
            }

            String identifier = identifierBuilder.toString();

            if (commandMap.containsKey(identifier)) {
                final String[] realArgs = Arrays.copyOfRange(args, argsIncluded, args.length);
                final net.cubespace.RegionShop.Data.Struct.Command command1 = commandMap.get(identifier);

                if (!commandSender.hasPermission(command1.annotation.permission())) {
                    commandSender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_NoPermission);
                    return true;
                }

                if (realArgs.length < command1.annotation.arguments()) {
                    commandSender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_NotEnoughArguments);
                    return true;
                }

                Plugin.getInstance().getServer().getScheduler().runTaskAsynchronously(Plugin.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        try {
                            command1.command.invoke(null, commandSender, realArgs);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                });

                return true;
            }
        }

        if (commandMap.containsKey(command.getName())) {
            Plugin.getInstance().getServer().getScheduler().runTaskAsynchronously(Plugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    try {
                        commandMap.get(command.getName()).command.invoke(null, commandSender, args);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        commandSender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_NotKnown);
        return true;
    }

    public static ArrayList<String> getHelpPage() {
        return ConfigManager.language.Command_Help_Default;
    }

    public static ArrayList<String> getHelpPage(String helpPage) {
        return help.get(helpPage);
    }
}
