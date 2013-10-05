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

        Command_List_Header_OutsideRegion.add(ChatColor.YELLOW + "-- " + ChatColor.GOLD + "List of shops " + ChatColor.YELLOW + " -- " + ChatColor.GOLD + "Page " + ChatColor.RED + "%page" + ChatColor.GOLD + "/" + ChatColor.RED + "%maxpage" + ChatColor.YELLOW + " --");
        Command_List_Header_OutsideRegion.add(" ");

        Command_List_Header_InsideRegion.add(ChatColor.YELLOW + "-- " + ChatColor.GOLD + "List of items in " + ChatColor.GREEN + "%name" + ChatColor.YELLOW + " -- " + ChatColor.GOLD + "Page " + ChatColor.RED + "%page" + ChatColor.GOLD + "/" + ChatColor.RED + "%maxpage" + ChatColor.YELLOW + " --");
        Command_List_Header_InsideRegion.add(" ");

        Command_Result_Header.add(ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Result for search: " + ChatColor.GREEN + "%search" + ChatColor.YELLOW + " -- " + ChatColor.GOLD + "Page " + ChatColor.RED + "%page" + ChatColor.GOLD + "/" + ChatColor.RED + "%maxpage" + ChatColor.YELLOW + " --");
        Command_Result_Header.add(ChatColor.YELLOW + "Legend: " + ChatColor.RED + "%dmg" + ChatColor.YELLOW + " damaged, " + ChatColor.RED + "%ench" + ChatColor.YELLOW + " enchanted, " + ChatColor.RED + "%name" + ChatColor.YELLOW + " renamed");
        Command_Result_Header.add(" ");

        Command_Detail_Header.add(ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Detail view " + ChatColor.YELLOW + "-- " + ChatColor.RED + "%owner's " + ChatColor.GREEN + "%item" + ChatColor.GRAY + " #%id");
        Command_Detail_Header.add(" ");

        Command_Detail_Ench_Header.add(" ");
        Command_Detail_Ench_Header.add(ChatColor.GREEN + "Enchantments:");

        Command_Detail_Potion_Header.add(" ");
        Command_Detail_Potion_Header.add(ChatColor.GREEN + "Potion effects:");

        Command_Equip_MultipleShops.add(ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Shop Selector" + ChatColor.YELLOW + " -- To select a shop: " + ChatColor.GOLD +"/shop equip " + ChatColor.RED + "shopname");
        Command_Equip_MultipleShops.add(" ");

        Command_Transcation_Header.add(ChatColor.YELLOW + "-- " + ChatColor.GOLD + "Transactionlog -- " + ChatColor.GOLD + "Page " + ChatColor.RED + "%page" + ChatColor.GOLD + "/" + ChatColor.RED + "%maxpage" + ChatColor.YELLOW + " --");
        Command_Transcation_Header.add(ChatColor.YELLOW + "Legend: " + ChatColor.DARK_GREEN + "You have bought" + ChatColor.RESET + " - " + ChatColor.DARK_RED + "You have sold" + ChatColor.RESET + " - " + ChatColor.GOLD + "You have equipped" + ChatColor.RESET + " - " + ChatColor.YELLOW + "You added/set Item" + ChatColor.RESET + " - " + ChatColor.DARK_AQUA + "You have removed");

        Sign_Customer_SignText.add("ID: %id");
        Sign_Customer_SignText.add("%itemname");
        Sign_Customer_SignText.add("%amountx for");
        Sign_Customer_SignText.add("S %sell$:B %buy$");

        Sign_Shop_SignText.add("%player");
        Sign_Shop_SignText.add("%itemname");
        Sign_Shop_SignText.add("%amountx for");
        Sign_Shop_SignText.add("S %sell$:B %buy$");
    }

    //Command Executer Messages
    public String Command_NoPermission = ChatColor.RED + "Insufficient permission.";
    public String Command_NotEnoughArguments = ChatColor.RED + "You have not given all arguments needed";
    public String Command_NotKnown = ChatColor.RED + "Command is not known. Maybe try /shop help ?";
    public String Command_OnlyForPlayers = ChatColor.RED + "Command only for Players";

    //Enter and Leave Message
    public String Shop_Enter = ChatColor.GOLD + "You have entered " + ChatColor.DARK_GREEN + "%name" +  ChatColor.GOLD + ". Type " + ChatColor.GREEN + "/shop list " + ChatColor.GOLD + "to list the items";
    public String Shop_Leave = ChatColor.GOLD + "You have left " + ChatColor.DARK_GREEN + "%name" +  ChatColor.GOLD + ". Bye!";

    //Add Core
    public String Add_FullStorage = ChatColor.RED + "Your Shop is full. Please remove/sell some Items or upgrade your Shop";

    //Equip Core
    public String Equip_Add_Item = ChatColor.GOLD + "Added "+ ChatColor.GREEN + "%item" + ChatColor.GOLD + " to the shop.";

    //Sell Core
    public String Sell_DoesNotBuy = ChatColor.RED + "This shop does not buy this item";
    public String Sell_OwnerHint = ChatColor.DARK_GREEN + "Player " + ChatColor.GREEN + "%player" + ChatColor.DARK_GREEN + " has sold " + ChatColor.GREEN + "%amount %item" + ChatColor.DARK_GREEN + " to your shop (" + ChatColor.GREEN + "%shop" + ChatColor.DARK_GREEN + ") for " + ChatColor.GREEN + "%price" + "$";
    public String Sell_PlayerHint = ChatColor.DARK_GREEN + "You have sold " + ChatColor.GREEN + "%amount" + " " + "%item" + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + "%price" + "$" + ChatColor.DARK_GREEN + " to shop";
    public String Sell_OwnerHasNotEnoughMoney = ChatColor.RED + "The Item Owner has not enough Money";
    public String Sell_FullStorage = ChatColor.RED + "You can not sell to this Shop. It is full";
    public String Sell_NotYourItem = ChatColor.RED + "You can't sell into Shops you own. Please use /shop equip or /shop add";

    //Buy Core
    public String Buy_NoSell = ChatColor.RED +  "This Shop doesn't sell this Item";
    public String Buy_NotEnoughItems = ChatColor.RED +  "This shop has not enough items in stock";
    public String Buy_PlayerHint = ChatColor.DARK_GREEN + "You have bought " + ChatColor.GREEN + "%amount %item" + ChatColor.DARK_GREEN + " for " + ChatColor.GREEN + "%price" + "$" + ChatColor.DARK_GREEN + " from shop";
    public String Buy_NotEnoughMoney = ChatColor.RED +  "You have not enough money for this. You need %price$";
    public String Buy_OwnerHintEmptyShop = ChatColor.DARK_GREEN + "ShopItem " + ChatColor.GREEN + "%item" + ChatColor.DARK_GREEN + " is empty. It has been removed from your shop (" + ChatColor.GREEN + "%shop" + ChatColor.DARK_GREEN + ")";
    public String Buy_OwnerHint = ChatColor.DARK_GREEN + "Player " + ChatColor.GREEN + "%player" + ChatColor.DARK_GREEN + " bought " + ChatColor.GREEN + "%amount %item" + ChatColor.DARK_GREEN + " from your shop (" + ChatColor.GREEN + "%shop" + ChatColor.DARK_GREEN + ") for " + ChatColor.GREEN + "%price" + "$";
    public String Buy_NotYourItems = ChatColor.RED + "You can't buy the items you own. Please use /shop remove";

    //List Command
    public ArrayList<String> Command_List_HelpText = new ArrayList<String>();
    public String Command_List_InvalidArguments = ChatColor.RED + "Only numbers as page value";
    public String Command_List_InvalidPage = ChatColor.RED + "Invalid page";
    public ArrayList<String> Command_List_Header_OutsideRegion = new ArrayList<String>();
    public ArrayList<String> Command_List_Header_InsideRegion = new ArrayList<String>();
    public String Command_List_NoShops = ChatColor.RED + "No shops available";
    public String Command_List_PrintShop = ChatColor.GREEN + "%name" + ChatColor.GOLD + " - Owners: " + ChatColor.GRAY + "%owners";
    public String Command_List_NextPage = ChatColor.GOLD + "Type " + ChatColor.GREEN + "/shop list %page " + ChatColor.GOLD + "for the next page";
    public String Command_List_Legend_Owner = ChatColor.YELLOW + "Legend: " + ChatColor.RED + "%dmg" + ChatColor.YELLOW + " damaged, " + ChatColor.RED + "%ench" + ChatColor.YELLOW + " enchanted, " + ChatColor.RED + "%name" + ChatColor.YELLOW + " renamed, " + ChatColor.RED + "%notrdy" + ChatColor.YELLOW + " not ready";
    public String Command_List_Legend_Consumer = ChatColor.YELLOW + "Legend: " + ChatColor.RED + "%dmg" + ChatColor.YELLOW + " damaged, " + ChatColor.RED + "%ench" + ChatColor.YELLOW + " enchanted, " + ChatColor.RED + "%name" + ChatColor.YELLOW + " renamed";
    public String Command_List_Item_Main = ChatColor.DARK_GREEN + "%amount" + " " + ChatColor.GREEN + "%name" + ChatColor.DARK_GREEN + " for (S)" + ChatColor.GREEN + "%sell" + "$" + ChatColor.DARK_GREEN + " (B)" + ChatColor.GREEN + "%buy" + "$/" + "%unitamount" + ChatColor.DARK_GREEN + " Unit(s) from " + ChatColor.GREEN + "%owner" + ChatColor.DARK_GREEN + " (" + ChatColor.GRAY + "#" + "%id" + ChatColor.DARK_GREEN + ")";
    public String Command_List_Item_Dmg = " " + ChatColor.RED + "%dmg";
    public String Command_List_Item_Ench = " " + ChatColor.GREEN + "%ench";
    public String Command_List_Item_NotRDY = " " + ChatColor.LIGHT_PURPLE + "%notrdy";
    public String Command_List_Item_Name = " " + ChatColor.YELLOW + "%name";
    public String Command_List_ShopEmpty = ChatColor.RED + "This shop has no items";

    //Setteleport (Admin)
    public String Command_Admin_SetTeleport_HelpText = ChatColor.GOLD + "/shop admin setteleport" + ChatColor.RESET + ": Set the Teleportion point for /shop to where you stand";
    public String Command_Admin_SetTeleport_Success = ChatColor.GOLD + "Successfully set Teleportion point in World " + ChatColor.GREEN + "%world";
    public String Command_Admin_SetTeleport_WorldNotEnabled = ChatColor.RED + "Can not set a Teleportion Point for " + ChatColor.DARK_RED + " %world " + ChatColor.RED + ". The World is not enabled";

    //Shop command (Teleport)
    public String Command_Shop_HelpText = ChatColor.GOLD + "/shop " + ChatColor.GREEN + "name" + ChatColor.RESET + ": Use this Command to get to the global Shop Teleport Point or to the Shop under "+ ChatColor.GREEN + "name";
    public String Command_Shop_NoTPPoint = ChatColor.RED + "This World has no Teleportion Point setup";
    public String Command_Shop_NotEnabled = ChatColor.RED + "You can not be teleported because the World is not enabled for Shopping";

    //Add command
    public String Command_Add_HelpText = ChatColor.GOLD + "/shop add " + ChatColor.RED + "sellprice buyprice amount" + ChatColor.RESET + ": Add current item in hand to the shop stock";
    public String Command_Add_NotInRegion = ChatColor.RED + "You can not add Items if you are not in a Shop";
    public String Command_Add_NotOwnerInThisRegion = ChatColor.RED + "You are not an Owner of this Shop";
    public String Command_Add_NoItemInHand = ChatColor.RED +  "You have no item in the hand";
    public String Command_Add_InvalidArguments = ChatColor.RED + "Only numbers as sell, buy and amount values allowed";
    public String Command_Add_AddedItem = ChatColor.GOLD + "Added "+ ChatColor.GREEN + "%item" + ChatColor.GOLD + " to the shop.";
    public String Command_Add_ChangeItem = ChatColor.RED + "Item already added. " + ChatColor.DARK_RED + "/shop set %itemid sellprice buyprice amount" + ChatColor.RED + " to change it.";

    //Set command
    public String Command_Set_HelpText = ChatColor.GOLD + "/shop set " + ChatColor.RED + "shopItemID sellprice buyprice amount" + ChatColor.RESET + ": Set/adjust the price for " + ChatColor.RED + "shopItemID";
    public String Command_Set_InvalidArguments = ChatColor.RED + "Only numbers as shopItemId, buy, sell and amount values allowed";
    public String Command_Set_InvalidItem = ChatColor.RED + "This item could not be found or you aren't the owner of it.";
    public String Command_Set_Success = ChatColor.GOLD + "Item " + ChatColor.GREEN + "%itemname" + ChatColor.GOLD + " now sells for " + ChatColor.GREEN + "%sell" + ChatColor.GOLD + ", buys for " + ChatColor.GREEN + "%buy" + ChatColor.GOLD + ", per " + ChatColor.GREEN + "%amount" + ChatColor.GOLD + " unit(s)";

    //Help Command
    public String Command_Help_HelpText = ChatColor.GOLD + "If you are here.... You are even to stupid to use the Help :X";
    public ArrayList<String> Command_Help_Default = new ArrayList<String>();
    public String Command_Help_NoHelp = ChatColor.RED + "No HelpPage found";
    public ArrayList<String> Command_Help_Header = new ArrayList<String>();
    public String Command_Help_NoPermission = ChatColor.RED + "You have no permission to view this Help";

    //Buy Command
    public String Command_Buy_HelpText = ChatColor.GOLD + "/shop buy " + ChatColor.RED + "shopItemID " +  ChatColor.GREEN + "amount" + ChatColor.RESET + ": Buy (" + ChatColor.GREEN + "amount" + ChatColor.RESET + " pcs. of) " + ChatColor.RED + "shopItemID " + ChatColor.RESET + "from the shop";
    public String Command_Buy_NotInRegion = ChatColor.RED +  "You are not inside a shop";
    public String Command_Buy_InvalidArguments = ChatColor.RED +  "Only numbers as shopItemId and amount values allowed";
    public String Command_Buy_ItemNotFound = ChatColor.RED +  "This shopItem could not be found";

    //Sell Command
    public String Command_Sell_HelpText = ChatColor.GOLD + "/shop sell "+ ChatColor.RESET + ": Sell the current Item in Hand to the shop";
    public String Command_Sell_NotInRegion = ChatColor.RED + "You are not inside a shop";
    public String Command_Sell_NoItemInHand = ChatColor.RED +  "You have no item in the hand";
    public String Command_Sell_NoEnchantedOrRenamed = ChatColor.RED + "You can't sell enchanted / custom renamed Items into a shop";
    public String Command_Sell_NoBuy = ChatColor.RED + "This shop does not buy this item";

    //Search Command
    public String Command_Search_HelpText = ChatColor.GOLD + "/shop search " + ChatColor.RED + "ItemID/ItemName" + ChatColor.RESET + ": Search for " + ChatColor.RED + "ItemID/ItemName";
    public String Command_Search_NoHit = ChatColor.RED + "No items found for your search";

    //Result Command
    public String Command_Result_HelpText = ChatColor.GOLD + "/shop result " + ChatColor.RED + "page" + ChatColor.RESET + ": Browse to page " + ChatColor.RED + "page";
    public String Command_Result_InvalidArguments = ChatColor.RED + "Only numbers as page value allowed";
    public String Command_Result_NoResults = ChatColor.RED + "You have no results";
    public String Command_Result_InvalidPage = ChatColor.RED + "Invalid page";
    public ArrayList<String> Command_Result_Header = new ArrayList<String>();
    public String Command_Result_Item_Main = ChatColor.DARK_GREEN + "%amount " + ChatColor.GREEN + "%item" + ChatColor.DARK_GREEN + " for (S)" + ChatColor.GREEN + "%sell$" + ChatColor.DARK_GREEN + " (B)" + ChatColor.GREEN + "%buy$/%unitamount Unit(s)" + ChatColor.DARK_GREEN + " at " + ChatColor.GREEN + "%shop " + ChatColor.GRAY + "#%id";
    public String Command_Result_Item_Dmg = " " + ChatColor.RED + "%dmg";
    public String Command_Result_Item_Ench = " " + ChatColor.GREEN + "%ench";
    public String Command_Result_Item_Name = " " + ChatColor.YELLOW + "%name";

    //Reload Command
    public String Command_Reload_HelpText = ChatColor.GOLD + "/shop admin reload " + ChatColor.RESET + ": Reloads Language, parts of the Config. THIS IS NOT RECOMMENDED !";
    public String Command_Reload_Success = ChatColor.YELLOW + "Reloaded " + ChatColor.GOLD + "Config/Language";

    //Filter Command
    public String Command_Filter_HelpText = ChatColor.GOLD + "/shop filter " + ChatColor.RED + "filter" + ChatColor.RESET + ": Filter the result";
    public String Command_Filter_InvalidSyntax = ChatColor.RED + "Error in Filtersyntax. %filter has no :";
    public String Command_Filter_Damage_InvalidArguments = ChatColor.RED + "Damagefilter has a non numeric argument";
    public String Command_Filter_Ench_InvalidOperator = ChatColor.RED + "Invalid Ench operator";
    public String Command_Filter_Ench_InvalidEnchID = ChatColor.RED + "Invalid EnchID";
    public String Command_Filter_Ench_InvalidArguments = ChatColor.RED + "Ench selector has a non numeric argument";
    public String Command_Filter_Price_InvalidArguments = ChatColor.RED + "Pricefilter has a non numeric argument";
    public String Command_Filter_NoResults = ChatColor.RED + "You have no results";

    //Detail Command
    public String Command_Detail_HelpText = ChatColor.GOLD + "/shop detail " + ChatColor.RED + "shopItemID" + ChatColor.RESET + ": Display details of " + ChatColor.RED + "shopItemID";
    public String Command_Detail_InvalidArguments = ChatColor.RED + "Only numbers as argument allowed";
    public ArrayList<String> Command_Detail_Header = new ArrayList<String>();
    public String Command_Detail_Sell = ChatColor.GREEN + "%sell$ " + ChatColor.GOLD + "selling price";
    public String Command_Detail_Buy = ChatColor.GREEN + "%buy$ " + ChatColor.GOLD + "buying price";
    public String Command_Detail_Damage = ChatColor.RED + "%dmg% " + ChatColor.GOLD + "damaged";
    public String Command_Detail_CustomName = "     " + ChatColor.GOLD + "Custom name: " + ChatColor.GRAY + "%name";
    public ArrayList<String> Command_Detail_Ench_Header = new ArrayList<String>();
    public String Command_Detail_Ench_Main = "    " + ChatColor.DARK_GREEN + "%ench Level " + ChatColor.GREEN + "%lvl";
    public ArrayList<String> Command_Detail_Potion_Header = new ArrayList<String>();
    public String Command_Detail_Potion_Main = "    " + ChatColor.DARK_GREEN + "%potion: Amplifier " + ChatColor.GREEN +"%amplifier" + ChatColor.DARK_GREEN + ", Duration " + ChatColor.GREEN + "%durations";
    public String Command_Detail_NotFound = ChatColor.RED + "This Shopitem could not be found";

    //Name Command
    public String Command_Name_HelpText = ChatColor.GOLD + "/shop name " + ChatColor.RED + "shopname" + ChatColor.RESET + ": Rename your shop to " + ChatColor.RED + "shopname";
    public String Command_Name_AlreadyTaken = ChatColor.RED + "This name is already given to another shop";
    public String Command_Name_OnlyASCII = ChatColor.RED + "You can only use ASCII characters for the name";
    public String Command_Name_TooLong = ChatColor.RED + "Name length must be under %amount chars";
    public String Command_Name_NotOwner = ChatColor.RED + "You are not an owner in this shop";
    public String Command_Name_NotInRegion = ChatColor.RED + "You are not inside a shop";

    //Remove Command
    public String Command_Remove_HelpText = ChatColor.GOLD + "/shop remove " + ChatColor.RED + "shopItemID" + ChatColor.RESET + ": Remove the "+ ChatColor.RED + "shopItemID" + ChatColor.RESET + " out of the Shop";
    public String Command_Remove_InvalidArguments = ChatColor.RED +  "Only numbers as shopItemID value allowed";
    public String Command_Remove_NotYourItem = ChatColor.RED + "This is not your Item";
    public String Command_Remove_NotAllItemsFit = ChatColor.RED + "Not all Items has fit into your Inventory. Please remove again if you have more Place";
    public String Command_Remove_Success = ChatColor.GOLD + "Item has been removed from your Shop";
    public String Command_Remove_NotFound = ChatColor.RED + "No Item found for this shopItemID";

    //Equip Command
    public String Command_Equip_HelpText = ChatColor.GOLD + "/shop equip" + ChatColor.RESET + ": Toggle " + ChatColor.GRAY + "quick add";
    public String Command_Equip_Disabled = ChatColor.GOLD + "Quick add mode for " + ChatColor.GREEN + "%shop" + ChatColor.GOLD + " disabled.";
    public String Command_Equip_NoShopFound = ChatColor.RED +  "No Shops found.";
    public ArrayList<String> Command_Equip_MultipleShops = new ArrayList<String>();
    public String Command_Equip_ShopName = ChatColor.GREEN + "%name";
    public String Command_Equip_Enabled = ChatColor.GOLD + "Quick add mode for " + ChatColor.GREEN + "%name" + ChatColor.GOLD + " enabled. Drop items to add them to your shop stock";
    public String Command_Equip_NotOwner = ChatColor.RED + "You are not an owner of this shop";
    public String Command_Equip_Selected = ChatColor.GOLD + "Shop " + ChatColor.GREEN + "%name" + ChatColor.GOLD + " selected";
    public String Command_Equip_ShopNotFound = ChatColor.RED + "This region could not be found";

    //Customer Sign
    public String Sign_Customer_NotInRegion = ChatColor.RED + "You are not inside a Shop";
    public String Sign_Customer_NotOwner = ChatColor.RED + "You are not a owner of this Shop";
    public String Sign_Customer_InvalidItemID = ChatColor.RED + "ItemID is not a number";
    public String Sign_Customer_NotFoundItemID = ChatColor.RED + "ItemID could not be found";
    public String Sign_Customer_NotYourItem = ChatColor.RED + "You do not own this Item";
    public ArrayList<String> Sign_Customer_SignText = new ArrayList<String>();

    //Customer Sign Interact
    public String Interact_Customer_NotInRegion = ChatColor.RED + "You are not inside a shop region.";
    public String Interact_Customer_Sell_NoItemInHand = ChatColor.RED +  "You have no item in the hand";
    public String Interact_Customer_Sell_WrongItem = ChatColor.RED + "You can't sell this here";

    //Shop Sign
    public String Sign_Shop_NoChest = ChatColor.RED + "There is no Chest around the Sign";
    public String Sign_Shop_InvalidAmount = ChatColor.RED + "The amount (2nd Line) is not a Number";
    public String Sign_Shop_NoValidBuySellLine = ChatColor.RED + "The Buy/Sell Line is invalid. It must be <sell>:<buy>";
    public String Sign_Shop_InvalidBuySell = ChatColor.RED + "The Buy/Sell value must be a floating Number. For example 1.0 or 1.7";
    public String Sign_Shop_ChestIsEmpty = ChatColor.RED + "The Chest is empty. First add items into the Chest and then make the Sign.";
    public ArrayList<String> Sign_Shop_SignText = new ArrayList<String>();

    //Shop Sign Interact
    public String Interact_Shop_Sell_NoItemInHand = ChatColor.RED +  "You have no item in the hand";
    public String Interact_Shop_Sell_WrongItem = ChatColor.RED + "You can't sell this here";

    //Chest Protection
    public String Protection_Chest_NotOwner = ChatColor.RED + "You are not the owner of this Chest";

    //Transaction Command
    public String Command_Transaction_HelpText = ChatColor.GOLD + "/shop transaction " + ChatColor.GREEN + "page" + ChatColor.RESET + ": See your Transaction log";
    public String Command_Transaction_InvalidArguments = ChatColor.RED + "Only numbers as page value";
    public String Command_Transaction_InvalidPage = ChatColor.RED + "Invalid page";
    public ArrayList<String> Command_Transcation_Header = new ArrayList<String>();
    public String Command_Transaction_DateFormat = "dd.MM.yy HH:mm:ss";
}
