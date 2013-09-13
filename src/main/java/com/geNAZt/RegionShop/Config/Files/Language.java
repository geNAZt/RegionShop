package com.geNAZt.RegionShop.Config.Files;

import com.geNAZt.RegionShop.Config.Config;
import com.geNAZt.RegionShop.RegionShopPlugin;

import org.bukkit.ChatColor;

import java.io.File;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 02.09.13
 */
public class Language extends Config {
    public Language(RegionShopPlugin plugin) {
        CONFIG_FILE = new File(plugin.getDataFolder() + File.separator + "config" + File.separator + "language.yml");
        CONFIG_HEADER = new String[]{
            "This file holds all Language Strings. If you need to install another language",
            "You can find them at http://cube-sapce.net/RegionShop/Language"
        };
    }

    public String Add_FullStorage = ChatColor.RED + "Your Shop is full. Please remove/sell some Items or upgrade your Shop";
    public String Shop_Enter = ChatColor.GOLD + "You have entered " + ChatColor.DARK_GREEN + "%name" +  ChatColor.GOLD + ". Type " + ChatColor.GREEN + "/shop list " + ChatColor.GOLD + "to list the items";
    public String Shop_Leave = ChatColor.GOLD + "You have left " + ChatColor.DARK_GREEN + "%name" +  ChatColor.GOLD + ". Bye!";
    public String Command_NotEnoughArguments = ChatColor.RED + "You have not given all arguments needed";
    public String[] Command_List_HelpText = new String[] {ChatColor.GOLD + "/shop list" + ChatColor.RESET + ": List items in the shop (inside a shopregion)", ChatColor.GOLD + "/shop list" + ChatColor.RESET + ": List all shops (outside a shopregion)"};
    public String Command_Admin_SetTeleport_HelpText = ChatColor.GOLD + "/shop admin setteleport" + ChatColor.RESET + ": Set the Teleportion point for /shop to where you stand";
    public String Command_Admin_SetTeleport_Success = ChatColor.GOLD + "Successfully set Teleportion point in World " + ChatColor.GREEN + "%world";
    public String Command_Admin_SetTeleport_WorldNotEnabled = ChatColor.RED + "Can not set a Teleportion Point for " + ChatColor.DARK_RED + " %world " + ChatColor.RED + ". The World is not enabled";
    public String Command_Shop_NotEnabled = ChatColor.RED + "You can not be teleported because the World is not enabled for Shopping";
    public String Command_Shop_NoTPPoint = ChatColor.RED + "This World has no Teleportion Point setup";
    public String Command_Add_NotInRegion = ChatColor.RED + "You can not add Items if you are not in a Shop";
    public String Command_Add_NotOwnerInThisRegion = ChatColor.RED + "You are not an Owner of this Shop";
    public String Command_Add_NoItemInHand = ChatColor.RED +  "You have no item in the hand";
    public String Command_Add_InvalidArguments = ChatColor.RED + "Only numbers as sell, buy and amount values allowed";
    public String Command_Add_AddedItem = ChatColor.GOLD + "Added "+ ChatColor.GREEN + "%item" + ChatColor.GOLD + " to the shop.";
    public String Command_Add_ChangeItem = ChatColor.RED + "Item already added. " + ChatColor.DARK_RED + "/shop set %itemid sellprice buyprice amount" + ChatColor.RED + " to change it.";
    public String Command_Set_HelpText = ChatColor.GOLD + "/shop set " + ChatColor.RED + "shopItemID sellprice buyprice amount" + ChatColor.RESET + ": Set/adjust the price for " + ChatColor.RED + "shopItemID";
    public String Command_Set_InvalidArguments = ChatColor.RED + "Only numbers as shopItemId, buy, sell and amount values allowed";
    public String Command_Set_InvalidItem = ChatColor.RED + "This item could not be found or you aren't the owner of it.";
    public String Command_Set_Success = ChatColor.GOLD + "Item " + ChatColor.GREEN + "%itemname" + ChatColor.GOLD + " now sells for " + ChatColor.GREEN + "%sell" + ChatColor.GOLD + ", buys for " + ChatColor.GREEN + "%buy" + ChatColor.GOLD + ", per " + ChatColor.GREEN + "%amount" + ChatColor.GOLD + " unit(s)";
}
