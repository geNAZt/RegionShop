package com.geNAZt.RegionShop.Command;

import org.bukkit.entity.Player;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 16.06.13
 */
public interface ShopCommand {
    public String getCommand();
    public String getPermissionNode();
    public int getNumberOfArgs();
    public void execute(Player player, String[] args);

}
