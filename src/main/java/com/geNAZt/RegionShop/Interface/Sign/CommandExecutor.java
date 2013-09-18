package com.geNAZt.RegionShop.Interface.Sign;

import com.geNAZt.RegionShop.Interface.Sign.Commands.Customer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class CommandExecutor implements Listener {
    private HashMap<String, com.geNAZt.RegionShop.Data.Struct.SignCommand> commandMap = new HashMap<String, com.geNAZt.RegionShop.Data.Struct.SignCommand>();

    public CommandExecutor() {
        //Load all commands
        ArrayList<SignCommand> commands = new ArrayList<SignCommand>();

        commands.add(new Customer());

        //Map all given commands
        for(SignCommand cmd : commands) {
            for (Method method : cmd.getClass().getDeclaredMethods()) {
                if (method.isAnnotationPresent(Command.class)) {
                    Annotation[] annotations = method.getAnnotations();

                    for(Annotation annotation : annotations) {
                        if(annotation instanceof Command) {
                            Command aCmd = (Command)annotation;

                            //Save the command
                            com.geNAZt.RegionShop.Data.Struct.SignCommand command = new com.geNAZt.RegionShop.Data.Struct.SignCommand();
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
    public void onSignChange(SignChangeEvent event) {
        if(!event.getLine(0).equals("[RegionShop]")) return;

        if(commandMap.containsKey(event.getLine(1))) {
            com.geNAZt.RegionShop.Data.Struct.SignCommand command1 = commandMap.get(event.getLine(1));

            if (!event.getPlayer().hasPermission(command1.annotation.permission())) {
                event.getPlayer().sendMessage("Insufficient permission.");
                event.getBlock().breakNaturally();
                return;
            }

            try {
                command1.command.invoke(null, event);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }
}
