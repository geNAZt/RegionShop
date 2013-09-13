package com.geNAZt.RegionShop.Config.Files;

import com.geNAZt.RegionShop.Config.Config;
import com.geNAZt.RegionShop.RegionShopPlugin;

import org.bukkit.ChatColor;

import java.io.File;
import java.util.ArrayList;

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

        Command_List_HelpText.add(ChatColor.GOLD + "/shop list" + ChatColor.RESET + ": List items in the shop (inside a shopregion)");
        Command_List_HelpText.add(ChatColor.GOLD + "/shop list" + ChatColor.RESET + ": List all shops (outside a shopregion)");

        Command_Help_Default.add(ChatColor.GOLD + "/shop help consumer" + ChatColor.RESET + ": List all Helps for normal commands");
        Command_Help_Default.add(ChatColor.GOLD + "/shop help owner" + ChatColor.RESET + ": List all Helps for Shopowners");
        Command_Help_Default.add(ChatColor.GOLD + "/shop help admin" + ChatColor.RESET + ": List all Helps for Admins");

        Command_Help_Header.add(ChatColor.YELLOW + "-- " + ChatColor.GOLD + "RegionShop: Help" + ChatColor.YELLOW + " -- " + ChatColor.GOLD + "Page " + ChatColor.RED + "%page" + ChatColor.YELLOW + " --");
        Command_Help_Header.add(ChatColor.RED + "Necessary arguments");
        Command_Help_Header.add(ChatColor.GREEN + "Optional arguments");
    }

    public String Add_FullStorage = ChatColor.RED + "Your Shop is full. Please remove/sell some Items or upgrade your Shop";
    public String Shop_Enter = ChatColor.GOLD + "You have entered " + ChatColor.DARK_GREEN + "%name" +  ChatColor.GOLD + ". Type " + ChatColor.GREEN + "/shop list " + ChatColor.GOLD + "to list the items";
    public String Shop_Leave = ChatColor.GOLD + "You have left " + ChatColor.DARK_GREEN + "%name" +  ChatColor.GOLD + ". Bye!";
    public String Command_NotEnoughArguments = ChatColor.RED + "You have not given all arguments needed";
    public ArrayList<String> Command_List_HelpText =new ArrayList<String>();
    public String Command_List_InvalidArguments = ChatColor.RED + "Only numbers as page value";
    public String Command_Admin_SetTeleport_HelpText = ChatColor.GOLD + "/shop admin setteleport" + ChatColor.RESET + ": Set the Teleportion point for /shop to where you stand";
    public String Command_Admin_SetTeleport_Success = ChatColor.GOLD + "Successfully set Teleportion point in World " + ChatColor.GREEN + "%world";
    public String Command_Admin_SetTeleport_WorldNotEnabled = ChatColor.RED + "Can not set a Teleportion Point for " + ChatColor.DARK_RED + " %world " + ChatColor.RED + ". The World is not enabled";
    public String Command_Shop_NotEnabled = ChatColor.RED + "You can not be teleported because the World is not enabled for Shopping";
    public String Command_Shop_NoTPPoint = ChatColor.RED + "This World has no Teleportion Point setup";
    public String Command_Shop_HelpText = ChatColor.GOLD + "/shop" + ChatColor.RESET + ": Use this Command to get to the global Shop Teleport Point";
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
    public ArrayList<String> Command_Help_Default = new ArrayList<String>();
    public String Command_Help_HelpText = ChatColor.GOLD + "If you are here.... You are even to stupid to use the Help :X";
    public String Command_Help_NoHelp = ChatColor.RED + "No HelpPage found";
    public ArrayList<String> Command_Help_Header = new ArrayList<String>();
    public String Command_Help_NoPermission = ChatColor.RED + "You have no permission to view this Help";
}
