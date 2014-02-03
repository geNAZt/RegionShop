package net.cubespace.RegionShop.Interface.Sign;

import net.cubespace.RegionShop.Bukkit.Plugin;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Interface.Sign.Commands.Customer;
import net.cubespace.RegionShop.Interface.Sign.Commands.Shop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

public class CommandExecutor implements Listener {
    private HashMap<String, net.cubespace.RegionShop.Data.Struct.SignCommand> commandMap = new HashMap<String, net.cubespace.RegionShop.Data.Struct.SignCommand>();

    public CommandExecutor() {
        //Load all commands
        ArrayList<SignCommand> commands = new ArrayList<SignCommand>();

        commands.add(new Customer());
        commands.add(new Shop());

        //Map all given commands
        for (SignCommand cmd : commands) {
            for (Method method : cmd.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    Annotation[] annotations = method.getAnnotations();

                    for (Annotation annotation : annotations) {
                        if (annotation instanceof Command) {
                            Command aCmd = (Command) annotation;

                            //Save the command
                            net.cubespace.RegionShop.Data.Struct.SignCommand command = new net.cubespace.RegionShop.Data.Struct.SignCommand();
                            command.annotation = aCmd;
                            command.command = method;

                            commandMap.put(aCmd.command(), command);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onSignChange(final SignChangeEvent event) {
        //Check if Event is in an enabled world
        if (!ConfigManager.main.World_enabledWorlds.contains(event.getPlayer().getWorld().getName())) {
            return;
        }

        //Check if Sign is a Shop creation
        net.cubespace.RegionShop.Data.Struct.SignCommand command1;

        if (Shop.checkForShop(event)) {
            command1 = commandMap.get("thisisimpossibletowriteonasign");
        } else {
            //Check if RegionShop is part of the Sign
            if (!event.getLine(0).equals("[RegionShop]")) return;

            if (commandMap.containsKey(event.getLine(1))) {
                command1 = commandMap.get(event.getLine(1));
            } else {
                return;
            }
        }

        if (!event.getPlayer().hasPermission(command1.annotation.permission())) {
            event.getPlayer().sendMessage("Insufficient permission.");
            event.getBlock().breakNaturally();
            return;
        }


        final net.cubespace.RegionShop.Data.Struct.SignCommand command = command1;

        Plugin.getInstance().getServer().getScheduler().runTaskAsynchronously(Plugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                try {
                    command.command.invoke(null, event);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
