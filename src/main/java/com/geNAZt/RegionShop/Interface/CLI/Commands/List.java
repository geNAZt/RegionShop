package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.avaje.ebean.Page;
import com.avaje.ebean.PagingList;
import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Data.Storage.InRegion;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Model.Item;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Database.Table.Region;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import com.geNAZt.RegionShop.Util.ItemName;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class List implements CLICommand {
    @Command(command="shop list", permission="rs.command.list", helpKey="Command_List_HelpText", helpPage="consumer", arguments=0)
    public static void list(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only for Players");
            return;
        }

        Player player = (Player) sender;

        //Check for optional Args
        Integer page = 1;

        if(args.length > 0) {
            try {
                page = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_InvalidArguments);
                return;
            }
        }

        //Check if Player is inside a Region
        if (InRegion.has(player)) {
            executeInsideRegion(player, page);
        } else {
            executeOutsideRegion(player, page);
        }
    }

    private static void executeOutsideRegion(Player player, Integer page) {
        PagingList<Region> regionList = Database.getServer().find(Region.class).
                where().
                    eq("world", player.getWorld().getName()).
                findPagingList(7);

        Integer curPage = page - 1;

        //Check if Valid page
        if (curPage < 0 || (curPage > regionList.getTotalPageCount() - 1 && regionList.getTotalPageCount() != 0)) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_InvalidPage);
            return;
        }

        for(String headerLine : ConfigManager.language.Command_List_Header_OutsideRegion) {
            player.sendMessage(ConfigManager.main.Chat_prefix + headerLine.replace("%page", ((Integer) (curPage + 1)).toString()).replace("%maxpage", ((Integer) regionList.getTotalPageCount()).toString()));
        }

        if(regionList.getTotalPageCount() == 0) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_NoShops);
            return;
        }

        Page qryPage = regionList.getPage(curPage);
        java.util.List<Region> qryPageList = qryPage.getList();

        for( Region region : qryPageList) {
            ArrayList<String> owners = new ArrayList<String>();
            java.util.List<com.geNAZt.RegionShop.Database.Table.Player> ownersDB = region.getOwners();

            for(com.geNAZt.RegionShop.Database.Table.Player player1 : ownersDB) {
                owners.add(player1.getName());
            }

            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_PrintShop.replace("%name", region.getName()).replace("%owners", StringUtils.join(owners, ", ")));
        }

        if(qryPage.hasNext()) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_NextPage.replace("%page", ((Integer) (page+1)).toString()));
        }

    }

    @SuppressWarnings("ConstantConditions")
    private static void executeInsideRegion(Player player, Integer page) {
        PagingList<Items> shopItems;

        //Check if User is owner in this region
        Region region = InRegion.get(player);
        java.util.List<com.geNAZt.RegionShop.Database.Table.Player> playerList = region.getOwners();
        boolean isOwner = false;

        for(com.geNAZt.RegionShop.Database.Table.Player player1 : playerList) {
            if(player1.getName().equals(player.getName().toLowerCase())) {
                isOwner = true;
            }
        }

        if (isOwner) {
            //Player is owner of this shop. he can see not ready items
            shopItems = Database.getServer().find(Items.class).
                    where().
                        conjunction().
                            eq("itemStorage", region.getItemStorage()).
                            disjunction().
                                conjunction().
                                    gt("unitAmount", 0).
                                        disjunction().
                                            gt("sell", 0).
                                            gt("buy", 0).
                                        endJunction().
                                endJunction().
                                eq("owner", player.getName()).
                            endJunction().
                        endJunction().
                    findPagingList(7);
        } else {
            //Is normal player. Can only see ready items
            shopItems = Database.getServer().find(Items.class).
                    where().
                        conjunction().
                            eq("itemStorage", region.getItemStorage()).
                            gt("unitAmount", 0).
                            disjunction().
                                gt("sell", 0).
                                gt("buy", 0).
                            endJunction().
                        endJunction().
                    findPagingList(7);
        }

        Integer curPage = page - 1;

        //Check if Valid page
        if (curPage < 0 || (curPage > shopItems.getTotalPageCount() - 1 && shopItems.getTotalPageCount() != 0)) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_InvalidPage);
            return;
        }

        //Get the right page
        Page qryPage = shopItems.getPage(curPage);
        java.util.List<Items> itemList = qryPage.getList();

        //Send the Header
        String ench = Character.toString((char)0x2692);
        String dmg = Character.toString((char)0x26A0);
        String name = Character.toString((char)0x270E);
        String notrdy = Character.toString((char)0x2716);

        for(String headerLine : ConfigManager.language.Command_List_Header_InsideRegion) {
            player.sendMessage(ConfigManager.main.Chat_prefix + headerLine.
                    replace("%name", region.getName()).
                    replace("%page", ((Integer) (curPage + 1)).toString()).
                    replace("%maxpage", ((Integer) shopItems.getTotalPageCount()).toString()));
        }

        //Define the legend
        String legend;
        if(isOwner) {
            legend = ConfigManager.language.Command_List_Legend_Owner.
                    replace("%ench", ench).
                    replace("%dmg", dmg).
                    replace("%name", name).
                    replace("%notrdy", notrdy);
        } else {
            legend = ConfigManager.language.Command_List_Legend_Consumer.
                    replace("%ench", ench).
                    replace("%dmg", dmg).
                    replace("%name", name).
                    replace("%notrdy", notrdy);
        }

        player.sendMessage(ConfigManager.main.Chat_prefix + legend);
        player.sendMessage(ConfigManager.main.Chat_prefix + " ");

        //List all Items
        if(itemList.size() > 0) {
            for(Items item : itemList) {
                if(((item.getSell() == 0 && item.getBuy() == 0) || item.getUnitAmount() == 0) && !isOwner) {
                    continue;
                }

                ItemStack iStack = Item.fromDBItem(item);

                String amount = item.getCurrentAmount().toString();
                String niceItemName = ItemName.nicer(iStack.getType().toString());
                String itemName = ItemName.getDataName(iStack) + niceItemName;

                String message = ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_Item_Main.
                        replace("%amount", (region.getItemStorage().isServershop()) ? Character.toString((char)0x221E) : amount).
                        replace("%name", itemName).
                        replace("%sell", item.getSell().toString()).
                        replace("%buy", item.getBuy().toString()).
                        replace("%unitamount", item.getUnitAmount().toString()).
                        replace("%owner", item.getOwner()).
                        replace("%id", item.getId().toString());

                Integer perDmg = 0;

                if (iStack.getDurability() > 0 && item.getMeta().getId().getItemID() != 373 && item.getMeta().getMaxStackSize() == 1) {
                    Float divide = ((float)iStack.getDurability() / (float)iStack.getType().getMaxDurability());
                    perDmg = Math.round(divide * 100);
                }

                if (item.getMeta().getMaxStackSize() == 1 && perDmg > 0) {
                    message += ConfigManager.language.Command_List_Item_Dmg.replace("%dmg", dmg);
                }

                if (!iStack.getEnchantments().isEmpty()) {
                    message += ConfigManager.language.Command_List_Item_Ench.replace("%ench", ench);
                }

                if((item.getSell() == 0 && item.getBuy() == 0) || item.getUnitAmount() == 0) {
                    message += ConfigManager.language.Command_List_Item_NotRDY.replace("%notrdy", notrdy);
                }

                if(item.getCustomName() != null) {
                    message += ConfigManager.language.Command_List_Item_Name.replace("%name", name);
                }

                player.sendMessage(message);
            }

            if (qryPage.hasNext()) {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_NextPage.replace("%page", ((Integer) (page+1)).toString()));
            }
        } else {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_ShopEmpty);
        }
    }
}
