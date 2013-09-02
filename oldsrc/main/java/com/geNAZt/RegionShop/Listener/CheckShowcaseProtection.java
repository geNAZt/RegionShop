package com.geNAZt.RegionShop.Listener;

import org.bukkit.entity.Item;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 30.08.13
 */
public class CheckShowcaseProtection extends Listener {
    private boolean checkProtection(Item itemDrop) {
        Integer entityID = itemDrop.getEntityId();

        return true;
    }

    public void execute(ItemDespawnEvent event) {

    }

    public void execute(PlayerPickupItemEvent event) {

    }
}
