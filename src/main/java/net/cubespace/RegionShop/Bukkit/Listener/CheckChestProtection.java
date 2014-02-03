package net.cubespace.RegionShop.Bukkit.Listener;

import net.cubespace.RegionShop.Config.ConfigManager;
import net.cubespace.RegionShop.Database.Repository.ChestRepository;
import net.cubespace.RegionShop.Database.Table.Chest;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class CheckChestProtection implements Listener {
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        //Check if Event is in an enabled world
        if(!ConfigManager.main.World_enabledWorlds.contains(event.getPlayer().getWorld().getName())) {
            return;
        }

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block blk = event.getClickedBlock();

            if(blk.getType().equals(Material.CHEST)) {
                Chest chest;
                if((chest = isRegionShopChest(event.getPlayer().getWorld(), blk)) != null) {


                    if(!event.getPlayer().getName().toLowerCase().equals(chest.getOwners().iterator().next().getPlayer().getName()) && !event.getPlayer().hasPermission("rs.bypass.chestshop")) {
                        event.setCancelled(true);
                        event.getPlayer().sendMessage(ConfigManager.main.Chat_prefix + ConfigManager.language.Protection_Chest_NotOwner);
                    }
                }
            }
        }
    }

    private Chest isRegionShopChest(World world, Block blk) {
        return ChestRepository.get(blk, world);
    }
}
