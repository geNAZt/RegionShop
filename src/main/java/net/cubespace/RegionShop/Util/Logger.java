package net.cubespace.RegionShop.Util;

import net.cubespace.RegionShop.Bukkit.Plugin;

import java.util.logging.Level;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 25.10.13 21:59
 * @warning You should use your own Logger since this is backed with the RegionShop Plugin
 */
public class Logger {
    /**
     * This function tells Bukkit to warn the message out
     *
     * @param message The message which gets warned to Bukkit
     */
    public static void warn(String message) {
        Plugin.getInstance().getLogger().log(Level.WARNING, message);
    }

    /**
     * This function warns out and also prints the Stacktrace of the Exception given
     *
     * @param message The message which gets warned to Bukkit
     * @param ex The exception which stacktrace will be printed
     */
    public static void warn(String message, Exception ex) {
        warn(message);
        ex.printStackTrace();
    }

    /**
     * This function tells Bukkit to error the message out
     *
     * @param message The message which gets errord to Bukkit
     */
    public static void error(String message) {
        Plugin.getInstance().getLogger().log(Level.SEVERE, message);
    }

    /**
     * This function errors out and also prints the Stacktrace of the Exception given
     *
     * @param message The message which gets errord to Bukkit
     * @param ex The exception which stacktrace will be printed
     */
    public static void error(String message, Exception ex) {
        error(message);
        ex.printStackTrace();
    }

    /**
     * This function tells Bukkit to error the message out but it also takes the Plugin down
     *
     * @param message The message which gets errord to Bukkit before the Plugin shutdown
     */
    public static void fatal(String message) {
        Plugin.getInstance().getLogger().log(Level.SEVERE, message);
        Plugin.getInstance().shutdown();
    }

    /**
     * This function fatals out and also prints the Stacktrace of the Exception given
     *
     * @param message The message which gets fataled to Bukkit
     * @param ex The exception which stacktrace will be printed
     */
    public static void fatal(String message, Exception ex) {
        fatal(message);
        ex.printStackTrace();
    }

    /**
     * This function tells the Bukkit Logger to info the string out
     *
     * @param message The message which will be printed
     */
    public static void info(String message) {
        Plugin.getInstance().getLogger().info(message);
    }

    /**
     * This function tells Bukkit to log debug messages, but only if System Property "net.cubespace.RegionShop.Util.Logger.debug" is true
     *
     * @param message The message which will be printed
     */
    public static void debug(String message) {
        if(System.getProperty("net.cubespace.RegionShop.Util.Logger.debug", "false").equals("true")) {
            Plugin.getInstance().getLogger().log(Level.FINEST, message);
        }
    }
}
