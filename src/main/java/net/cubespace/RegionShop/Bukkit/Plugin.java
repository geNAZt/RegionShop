package net.cubespace.RegionShop.Bukkit;

import net.cubespace.RegionShop.Bukkit.Listener.CheckChestProtection;
import net.cubespace.RegionShop.Bukkit.Listener.CheckForNewPlayer;
import net.cubespace.RegionShop.Bukkit.Listener.DropEquip;
import net.cubespace.RegionShop.Bukkit.Listener.EquipOnChestClose;
import net.cubespace.RegionShop.Bukkit.Listener.PlayerMove;
import net.cubespace.RegionShop.Bukkit.Listener.PlayerQuit;
import net.cubespace.RegionShop.Bukkit.Listener.PretendDisplaysToPickup;
import net.cubespace.RegionShop.Bukkit.Listener.RestoreOnChestOpen;
import net.cubespace.RegionShop.Bukkit.Listener.SignDestroy;
import net.cubespace.RegionShop.Bukkit.Listener.SignInteractPrepare;
import net.cubespace.RegionShop.Bukkit.Listener.WGChanges;
import net.cubespace.RegionShop.Data.Parser.ItemDB;
import net.cubespace.RegionShop.Data.Tasks.DetectWGChanges;
import net.cubespace.RegionShop.Data.Tasks.IntegrateServershop;
import net.cubespace.RegionShop.Data.Tasks.PriceRecalculateManagerTask;
import net.cubespace.RegionShop.Data.Tasks.ShowcaseRefresh;
import net.cubespace.RegionShop.Data.Tasks.SignOnChest;
import net.cubespace.RegionShop.Interface.CLI.CommandExecutor;
import net.cubespace.RegionShop.Interface.Sign.Interact.Customer;
import net.cubespace.RegionShop.Interface.Sign.Interact.Shop;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 *
 * This class gets loaded by the Bukkit PluginManager
 */
public class Plugin extends JavaPlugin {
    /* This variable holds the Instance of the RegionShop which has been initialized */
    private static Plugin instance;

    public Plugin() { }

    /**
     * This function gets called when the Bukkit Plugin Manager wants to init the Plugin
     */
    public void onEnable() {
        instance = this;

        //Start the ItemDB
        new ItemDB();

        //Append the Listeners
        getServer().getPluginManager().registerEvents(new CheckForNewPlayer(), this);
        getServer().getPluginManager().registerEvents(new WGChanges(), this);
        getServer().getPluginManager().registerEvents(new PlayerMove(), this);
        getServer().getPluginManager().registerEvents(new SignInteractPrepare(), this);
        getServer().getPluginManager().registerEvents(new net.cubespace.RegionShop.Interface.Sign.CommandExecutor(), this);
        getServer().getPluginManager().registerEvents(new CheckChestProtection(), this);
        getServer().getPluginManager().registerEvents(new DropEquip(), this);
        getServer().getPluginManager().registerEvents(new EquipOnChestClose(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new PretendDisplaysToPickup(), this);
        getServer().getPluginManager().registerEvents(new RestoreOnChestOpen(), this);
        getServer().getPluginManager().registerEvents(new SignDestroy(), this);
        getServer().getPluginManager().registerEvents(new Customer(), this);
        getServer().getPluginManager().registerEvents(new Shop(), this);

        //Start the needed Tasks
        getServer().getScheduler().runTaskTimerAsynchronously(this, new DetectWGChanges(), 5 * 20, 5 * 20);
        getServer().getScheduler().runTaskLaterAsynchronously(this, new IntegrateServershop(), 10 * 20);
        getServer().getScheduler().runTaskLaterAsynchronously(this, new PriceRecalculateManagerTask(), 2 * 20);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new ShowcaseRefresh(), 5 * 20, 5 * 20);
        getServer().getScheduler().runTaskTimerAsynchronously(this, new SignOnChest(), 5 * 20, 5 * 20);

        //Command
        getCommand("shop").setExecutor(new CommandExecutor());
    }

    /**
     * This function tells Bukkit to shut the Plugin down
     */
    public void shutdown() {
        setEnabled(false);
    }

    /**
     * @return Get the instance from the Bukkit Pluginloader
     */
    public static Plugin getInstance() {
        return instance;
    }
}
