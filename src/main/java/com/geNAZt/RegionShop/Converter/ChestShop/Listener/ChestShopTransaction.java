package com.geNAZt.RegionShop.Converter.ChestShop.Listener;

import com.Acrobot.ChestShop.Events.PreTransactionEvent;
import com.Acrobot.ChestShop.Events.TransactionEvent;
import com.geNAZt.RegionShop.Converter.ChestShop.ConvertStorage;
import com.geNAZt.RegionShop.Converter.ChestShopConverter;
import com.geNAZt.RegionShop.Storages.PlayerStorage;
import com.geNAZt.RegionShop.Util.Chat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 15.06.13
 */
public class ChestShopTransaction implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSignClick(PreTransactionEvent event) {
        Player p = event.getClient();

        if (ConvertStorage.hasPlayer(p)) {
            event.setCancelled(PreTransactionEvent.TransactionOutcome.OTHER);

            ArrayList<Integer> buySell = ConvertStorage.getPlayer(p);
            if(event.getTransactionType() == TransactionEvent.TransactionType.BUY) {
                //Buy Value
                if (buySell.get(1) < 0) {
                    ConvertStorage.removerPlayer(p);

                    buySell.set(1, (int)event.getPrice());

                    ConvertStorage.setPlayer(p, buySell);
                }
            } else {
                //Sell value
                if (buySell.get(0) < 0) {
                    ConvertStorage.removerPlayer(p);

                    buySell.set(0, (int)event.getPrice());

                    ConvertStorage.setPlayer(p, buySell);
                }
            }

            if(PlayerStorage.getPlayer(p) != null && buySell.get(0) > -1 && buySell.get(1) > -1) {
                Inventory chestInv = event.getOwnerInventory();

                if(chestInv != null) {
                    ChestShopConverter.convertInventory(chestInv, event.getStock()[0], p, event.getOwner().getName(), buySell.get(1), buySell.get(0), event.getStock()[0].getAmount());

                    ConvertStorage.removerPlayer(p);

                    ArrayList<Integer> aList = new ArrayList<Integer>();
                    aList.add(-1);
                    aList.add(-1);

                    ConvertStorage.setPlayer(p, aList);
                } else {
                    p.sendMessage(Chat.getPrefix() + "Invalid ChestShop");
                    return;
                }
            }
        }
    }
}
