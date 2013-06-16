package com.geNAZt.RegionShop.Interface;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.06.13
 */
public abstract class SignCommand {
    public abstract String getCommand();
    public abstract String getPermissionNode();
    public abstract void execute(Player player, Block sign, String[] lines);
}
