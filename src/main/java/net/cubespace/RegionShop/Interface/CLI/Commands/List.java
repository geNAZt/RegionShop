package net.cubespace.RegionShop.Interface.CLI.Commands;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.stmt.Where;
import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Data.Storage.InRegion;
import net.cubespace.RegionShop.Database.Database;
import net.cubespace.RegionShop.Database.Repository.ItemRepository;
import net.cubespace.RegionShop.Database.Table.Items;
import net.cubespace.RegionShop.Database.Table.PlayerOwnsRegion;
import net.cubespace.RegionShop.Database.Table.Region;
import net.cubespace.RegionShop.Interface.CLI.CLICommand;
import net.cubespace.RegionShop.Interface.CLI.Command;
import net.cubespace.RegionShop.Util.ItemName;
import net.cubespace.RegionShop.Util.Logger;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.util.ArrayList;

public class List implements CLICommand {
    @Command(command = "shop list", permission = "rs.command.list", helpKey = "Command_List_HelpText", helpPage = "consumer", arguments = 0)
    public static void list(CommandSender sender, String[] args) {
        //Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
            return;
        }

        Player player = (Player) sender;

        //Check for optional Args
        Integer page = 1;

        if (args.length > 0) {
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
        java.util.List<Region> regionList = null;
        try {
            regionList = Database.getDAO(Region.class).queryBuilder().
                    where().
                    eq("world", player.getWorld().getName()).
                    query();

            Integer curPage = page - 1;

            //Check if Valid page
            if (curPage < 0 || (curPage * 7 > 7 + regionList.size() - 7 && regionList.size() != 0)) {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_InvalidPage);
                return;
            }

            for (String headerLine : ConfigManager.language.Command_List_Header_OutsideRegion) {
                player.sendMessage(ConfigManager.main.Chat_prefix + headerLine.replace("%page", ((Integer) (curPage + 1)).toString()).replace("%maxpage", ((Integer) (regionList.size() / 7)).toString()));
            }

            if (regionList.size() == 0) {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_NoShops);
                return;
            }

            int skip = 0;
            for (Region region : regionList) {
                if (skip < curPage * 7) {
                    skip++;
                    continue;
                }

                ArrayList<String> owners = new ArrayList<String>();
                ForeignCollection<PlayerOwnsRegion> ownersDB = region.getOwners();

                for (PlayerOwnsRegion player1 : ownersDB) {
                    owners.add(player1.getPlayer().getName());
                }

                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_PrintShop.replace("%name", region.getName()).replace("%owners", StringUtils.join(owners, ", ")));
            }

            if (regionList.size() > curPage * 7) {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_NextPage.replace("%page", ((Integer) (page + 1)).toString()));
            }
        } catch (SQLException e) {
            Logger.error("Could not get RegionList", e);
        }


    }

    @SuppressWarnings("ConstantConditions")
    private static void executeInsideRegion(Player player, Integer page) {
        java.util.List<Items> shopItems = new ArrayList<Items>();

        //Check if User is owner in this region
        Region region = InRegion.get(player);
        ForeignCollection<PlayerOwnsRegion> playerList = region.getOwners();
        boolean isOwner = false;

        for (PlayerOwnsRegion player1 : playerList) {
            if (player1.getPlayer().getName().equals(player.getName().toLowerCase())) {
                isOwner = true;
            }
        }

        if (isOwner) {
            //Player is owner of this shop. he can see not ready items
            try {
                Where<Items, Integer> where = Database.getDAO(Items.class).queryBuilder().where();
                shopItems = where.and(where.eq("itemstorage_id", region.getItemStorage().getId()),
                        where.or(
                            where.and(where.gt("unitAmount", 0), where.or(where.gt("sell", 0), where.gt("buy", 0))),
                            where.eq("owner", player.getName().toLowerCase())
                        ))
                        .query();
            } catch (SQLException e) {
                Logger.error("Could not find Items", e);
            }
        } else {
            //Is normal player. Can only see ready items
            Where<Items, Integer> where = Database.getDAO(Items.class).queryBuilder().where();
            try {
                shopItems = where.and(where.eq("itemstorage_id", region.getItemStorage().getId()).
                        and().
                        gt("unitAmount", 0), where.or(where.gt("sell", 0), where.gt("buy", 0))).query();
            } catch (SQLException e) {
                Logger.error("Could not find Items", e);
            }
        }

        if (shopItems.size() == 0) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_ShopEmpty);
            return;
        }

        Integer curPage = page - 1;

        //Check if Valid page
        if (curPage < 0 || (curPage * 7 > 7 + shopItems.size() - 7 && shopItems.size() != 0)) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_InvalidPage);
            return;
        }

        //Send the Header
        String ench = Character.toString((char) 0x2692);
        String dmg = Character.toString((char) 0x26A0);
        String name = Character.toString((char) 0x270E);
        String notrdy = Character.toString((char) 0x2716);

        for (String headerLine : ConfigManager.language.Command_List_Header_InsideRegion) {
            player.sendMessage(ConfigManager.main.Chat_prefix + headerLine.
                    replace("%name", region.getName()).
                    replace("%page", ((Integer) (curPage + 1)).toString()).
                    replace("%maxpage", String.valueOf(((Double) Math.ceil(shopItems.size() / (double) 7)).intValue())));
        }

        //Define the legend
        String legend;
        if (isOwner) {
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

        int skip = 0;

        for (Items item : shopItems) {
            if (skip < curPage * 7) {
                continue;
            }

            skip++;

            if (skip > 7 + curPage * 7) {
                continue;
            }

            if (((item.getSell() == 0 && item.getBuy() == 0) || item.getUnitAmount() == 0) && !isOwner) {
                continue;
            }

            ItemStack iStack = ItemRepository.fromDBItem(item);

            String amount = item.getCurrentAmount().toString();
            String dataName = ItemName.getDataName(iStack);
            String niceItemName;
            if (dataName.endsWith(" ")) {
                niceItemName = dataName + ItemName.nicer(iStack.getType().toString());
            } else if (!dataName.equals("")) {
                niceItemName = dataName;
            } else {
                niceItemName = ItemName.nicer(iStack.getType().toString());
            }

            String message = ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_Item_Main.
                    replace("%amount", (region.getItemStorage().isServershop()) ? Character.toString((char) 0x221E) : amount).
                    replace("%name", niceItemName).
                    replace("%sell", item.getSell().toString()).
                    replace("%buy", item.getBuy().toString()).
                    replace("%unitamount", item.getUnitAmount().toString()).
                    replace("%owner", item.getOwner()).
                    replace("%id", item.getId().toString());

            Integer perDmg = 0;

            if (iStack.getDurability() > 0 && item.getMeta().getItemID() != 373 && item.getMeta().getMaxStackSize() == 1) {
                Float divide = ((float) iStack.getDurability() / (float) iStack.getType().getMaxDurability());
                perDmg = Math.round(divide * 100);
            }

            if (item.getMeta().getMaxStackSize() == 1 && perDmg > 0) {
                message += ConfigManager.language.Command_List_Item_Dmg.replace("%dmg", dmg);
            }

            if (!iStack.getEnchantments().isEmpty()) {
                message += ConfigManager.language.Command_List_Item_Ench.replace("%ench", ench);
            }

            if ((item.getSell() == 0 && item.getBuy() == 0) || item.getUnitAmount() == 0) {
                message += ConfigManager.language.Command_List_Item_NotRDY.replace("%notrdy", notrdy);
            }

            if (item.getCustomName() != null) {
                message += ConfigManager.language.Command_List_Item_Name.replace("%name", name);
            }

            player.sendMessage(message);
        }

        if (shopItems.size() > 7 + (curPage * 7)) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_List_NextPage.replace("%page", ((Integer) (page + 1)).toString()));
        }

    }
}
