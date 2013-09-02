package com.geNAZt.RegionShop.Listener;

import com.geNAZt.RegionShop.Bukkit.Util.Chat;
import com.geNAZt.RegionShop.Interface.SignCommand;
import com.geNAZt.RegionShop.RegionShopPlugin;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;

import java.util.concurrent.CopyOnWriteArrayList;

import static com.geNAZt.RegionShop.Util.Loader.loadFromJAR;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 09.06.13
 */
public class SignChange extends Listener {
    private final RegionShopPlugin plugin;
    private CopyOnWriteArrayList<SignCommand> signCommands = new CopyOnWriteArrayList<SignCommand>();

    public SignChange(RegionShopPlugin pl) {
        plugin = pl;

        signCommands = loadFromJAR("com.geNAZt.RegionShop.Interface.Sign", SignCommand.class, new Object[]{plugin});

        for(Object command : signCommands) {
            SignCommand signCommand = (SignCommand) command;

            if(!pl.getConfig().getBoolean("interfaces.sign." + signCommand.getCommand(), true)) {
                signCommands.remove(signCommands.indexOf(signCommand));
            }
        }

        pl.getLogger().info("Loaded SignCommands: " + signCommands.toString());
    }

    public void execute(SignChangeEvent event) {
        Player p = event.getPlayer();

        Block signBlock = event.getBlock();
        if (signBlock == null) {
            plugin.getLogger().warning("Player " + p.getName() + " tried to generate a fake sign.");
            return;
        }

        if(event.getLine(0).contains("[RegionShop]")) {
            for(SignCommand command : signCommands) {
                if(command.getCommand().equalsIgnoreCase(event.getLine(1))) {
                    if(command.getPermissionNode() == null || p.hasPermission(command.getPermissionNode())) {
                        command.execute(p, signBlock, event);
                        return;
                    } else {
                        p.sendMessage(Chat.getPrefix() + ChatColor.RED + "You don't have the permission " + ChatColor.DARK_RED + command.getPermissionNode());
                        signBlock.breakNaturally();
                        return;
                    }
                }
            }


            p.sendMessage(Chat.getPrefix() + ChatColor.RED + "Invalid RegionShop Sign");
            signBlock.breakNaturally();
        }
    }


}
