package com.geNAZt.RegionShop.Interface.CLI;

import com.geNAZt.RegionShop.Interface.CLI.Commands.Admin.setTeleport;
import org.bukkit.command.CommandSender;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class CommandExecutor implements org.bukkit.command.CommandExecutor{
    private HashMap<String, com.geNAZt.RegionShop.Data.Struct.Command> commandMap = new HashMap<String, com.geNAZt.RegionShop.Data.Struct.Command>();

    public CommandExecutor() {
        //Load all commands
        ArrayList<CLICommand> commands = new ArrayList<CLICommand>();

        commands.add(new setTeleport());

        //Map all given commands
        for(CLICommand cmd : commands) {
            for (Method method : cmd.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    Annotation[] annotations = method.getAnnotations();

                    for(Annotation annotation : annotations) {
                        if(annotation instanceof Command) {
                            Command aCmd = (Command)annotation;

                            com.geNAZt.RegionShop.Data.Struct.Command command = new com.geNAZt.RegionShop.Data.Struct.Command();
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
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] args) {
        StringBuilder identifierBuilder = new StringBuilder(command.getName());

        if(commandMap.containsKey(identifierBuilder.toString())) {
            try {
                commandMap.get(identifierBuilder.toString()).command.invoke(commandSender, args);
                return true;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            for (int argsIncluded = args.length; argsIncluded >= -1; argsIncluded--) {
                identifierBuilder = new StringBuilder(command.getName());
                for(int i = 0; i < argsIncluded; i++) {
                    identifierBuilder.append(' ').append(args[i]);
                }

                String identifier = identifierBuilder.toString();

                if (commandMap.containsKey(identifier)) {
                    String[] realArgs = Arrays.copyOfRange(args, argsIncluded, args.length);
                    com.geNAZt.RegionShop.Data.Struct.Command command1 = commandMap.get(identifier);

                    if (!commandSender.hasPermission(command1.annotation.permission())) {
                        commandSender.sendMessage("Insufficient permission.");
                        return true;
                    }

                    try {
                        command1.command.invoke(null, commandSender, realArgs);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        }

        return false;
    }
}
