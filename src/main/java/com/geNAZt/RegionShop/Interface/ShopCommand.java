package com.geNAZt.RegionShop.Interface;

import org.bukkit.entity.Player;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.06.13
 */
public abstract class ShopCommand {
    public abstract String getCommand();
    public abstract String getPermissionNode();
    public abstract int getNumberOfArgs();
    public abstract void execute(Player player, String[] args);
}
