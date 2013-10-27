package net.cubespace.RegionShop.Util;

import net.cubespace.RegionShop.Bukkit.Plugin;
import org.apache.commons.lang3.Validate;

import java.util.ArrayList;

/**
 * @author geNAZt (fabian.fassbender42@googlemail.com)
 * @date Last changed: 27.10.13 12:24
 */
public class Version {
    /**
     * This function checks if a Version is newer as the compareVersion. Both Versions need to be in format like "3.1.2" (major.minor.bugfix)
     *
     * @warning This would give false if the Versions are equal
     * @param curVersion The current Version which should be newer as
     * @param compareVersion this Version
     * @return True if newer, false if not
     */
    public static boolean isNewer(String curVersion, String compareVersion) {
        //Convert both Versions
        ArrayList<Integer> curVersionList = convertVersion(curVersion);
        ArrayList<Integer> compareVersionList = convertVersion(compareVersion);

        //Go through all Numbers from front (major) to end (bugfix)
        for(Integer i = 0; i < curVersionList.size(); i++) {
            if(curVersionList.get(i) > compareVersionList.get(i)) {
                return true;
            }
        }

        return false;
    }

    /**
     * This function checks if a Version is newer or equal as the compareVersion. Both Versions need to be in format like "3.1.2" (major.minor.bugfix)
     *
     * @param curVersion The current Version which should be newer or equal as
     * @param compareVersion this Version
     * @return True if newer or equal, false if not
     */
    public static boolean isNewerOrEqual(String curVersion, String compareVersion) {
        //Convert both Versions
        ArrayList<Integer> curVersionList = convertVersion(curVersion);
        ArrayList<Integer> compareVersionList = convertVersion(compareVersion);

        //Go through all Numbers from front (major) to end (bugfix)
        for(Integer i = 0; i < curVersionList.size(); i++) {
            if(curVersionList.get(i) >= compareVersionList.get(i)) {
                return true;
            }
        }

        return false;
    }

    /**
     * This function converts a Version String into a ArrayList of Integers. The first entry is the major, second minor and so on
     *
     * @param version The Version in major.minor.bugfix format
     * @return The ArrayList of Integers
     */
    private static ArrayList<Integer> convertVersion(String version) {
        //Validate the Input
        Validate.matchesPattern(version, "[0-9]+\\.[0-9]+\\.[0-9]+");

        //Split the string
        String[] versionSplit = version.split("\\.");

        //Create a new ArrayList and try to parse all integers into it
        ArrayList<Integer> versionList = new ArrayList<Integer>();
        for(String versionSplitItem : versionSplit) {
            try {
                versionList.add(Integer.parseInt(versionSplitItem));
            } catch(NumberFormatException e) {
                Logger.warn("Version could not be converted", e);
                return null;
            }
        }

        return versionList;
    }

    /**
     * Gives you the current Version of RegionShop running
     *
     * @return The RegionShop Version
     */
    public static String getRegionShopVersion() {
        return Plugin.getInstance().getDescription().getVersion();
    }
}
