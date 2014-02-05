package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Database.Database;
import com.geNAZt.RegionShop.Database.Model.Item;
import com.geNAZt.RegionShop.Database.Table.Items;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;
import com.geNAZt.RegionShop.Util.ItemName;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.Map;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 06.06.13
 */
public class Detail implements CLICommand {
    @Command(command="shop detail", arguments=1, helpKey="Command_Detail_HelpText", helpPage="consumer", permission="rs.command.detail")
    public static void detail(CommandSender sender, String[] args) {
        //Check if sender is a player
        if(!(sender instanceof Player)) {
            sender.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_OnlyForPlayers);
            return;
        }

        Player player = (Player) sender;

        //Convert args
        Integer itemId;

        try {
            itemId = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Detail_InvalidArguments);
            return;
        }

        Items item = Database.getServer().find(Items.class).
                    where().
                        eq("id", itemId).
                    findUnique();

        if (item != null && (((item.getSell() != 0 || item.getBuy() != 0) && item.getUnitAmount() > 0) || item.getOwner().equalsIgnoreCase(player.getName()))) {
            ItemStack iStack = Item.fromDBItem(item);

            String dataName = ItemName.getDataName(iStack);
            String niceItemName;
            if(dataName.endsWith(" ")) {
                niceItemName = dataName + ItemName.nicer(iStack.getType().toString());
            } else if(!dataName.equals("")) {
                niceItemName = dataName;
            } else {
                niceItemName = ItemName.nicer(iStack.getType().toString());
            }

            Integer dmg = 0;

            if (iStack.getDurability() > 0 && item.getMeta().getId().getItemID() != 373 && item.getMeta().getMaxStackSize() == 1) {
                Float divide = ((float)iStack.getDurability() / (float)iStack.getType().getMaxDurability());
                dmg = Math.round(divide * 100);
            }

            for(String headerLine : ConfigManager.language.Command_Detail_Header) {
                player.sendMessage(ConfigManager.main.Chat_prefix + headerLine.
                        replace("%owner", item.getOwner()).
                        replace("%item", niceItemName).
                        replace("%id", item.getId().toString()));
            }

            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Detail_Sell.replace("%sell", item.getSell().toString()));
            player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Detail_Buy.replace("%buy", item.getBuy().toString()));

            if(dmg > 0) {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Detail_Damage.replace("%dmg", dmg.toString()));
            }

            if (item.getCustomName() != null) {
                player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Detail_CustomName.replace("%name", item.getCustomName()));
            }

            if(!iStack.getEnchantments().isEmpty()) {
                for(String headerLine : ConfigManager.language.Command_Detail_Ench_Header) {
                    player.sendMessage(ConfigManager.main.Chat_prefix + headerLine);
                }

                for(Map.Entry<Enchantment, Integer> ench : iStack.getEnchantments().entrySet()) {
                    player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Detail_Ench_Main.replace("%ench", ItemName.nicer(ench.getKey().getName())).replace("%lvl", ench.getValue().toString()));
                }
            }

            if (item.getMeta().getId().getItemID() == 373) {
                Potion ptn = Potion.fromItemStack(iStack);
                Collection<PotionEffect> ptnEffects = ptn.getEffects();

                if (ptnEffects.size() > 0) {
                    for(String headerLine : ConfigManager.language.Command_Detail_Potion_Header) {
                        player.sendMessage(ConfigManager.main.Chat_prefix + headerLine);
                    }

                    for(PotionEffect ptnEffect : ptnEffects) {
                        Integer duration = 0;

                        if (ptnEffect.getDuration() >= 20) {
                            Float divide = ((float)ptnEffect.getDuration() / (float)20);
                            duration = Math.round(divide);
                        }

                        player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Detail_Potion_Main.replace("%potion", ItemName.nicer(ptnEffect.getType().getName())).replace("%amplifier",  ((Integer)ptnEffect.getAmplifier()).toString()).replace("%duration", duration.toString()) );
                    }
                }
            }

            return;
        }

        player.sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Command_Detail_NotFound);
    }
}
