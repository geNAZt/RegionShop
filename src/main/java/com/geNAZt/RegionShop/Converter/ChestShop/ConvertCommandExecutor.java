package com.geNAZt.RegionShop.Converter.ChestShop;

import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Util.Chat;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 15.06.13
 */
public class ConvertCommandExecutor implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        boolean isPlayer = (sender instanceof Player);
        Player p = (isPlayer) ? (Player) sender : null;

        if(!isPlayer) {
            sender.sendMessage(Chat.getPrefix() + "No shop for you Console!");
            return true;
        }

        if (p.hasPermission("rs.convert")) {

            if (ConvertStorage.hasPlayer(p)) {
                ConvertStorage.removerPlayer(p);

                p.sendMessage(Chat.getPrefix() + "You don't convert anymore");
                return true;
            }

            if (PlayerStorage.getPlayer(p) != null) {
                ArrayList<Integer> aList = new ArrayList<Integer>();
                aList.add(-1);
                aList.add(-1);

                ConvertStorage.setPlayer(p, aList);
                p.sendMessage(Chat.getPrefix() + "You must click the ChestShop twice (once with the right and once with the left key)");
            } else {
                p.sendMessage(Chat.getPrefix() + "Can't convert. You aren't in a Shop Region.");
            }
        } else {
            p.sendMessage(Chat.getPrefix() + "You haven't enough permissions for this.");
        }

        return true;
    }
}
