package com.geNAZt.RegionShop.Interface.CLI.Commands;

import com.geNAZt.RegionShop.Config.ConfigManager;
import com.geNAZt.RegionShop.Interface.CLI.CLICommand;
import com.geNAZt.RegionShop.Interface.CLI.Command;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 03.09.13
 */
@Command(command="list", arguments=0, permission="rs.list")
public class List implements CLICommand {
    @Override
    public String[] getHelp() {
        return new String[]{ConfigManager.language.List_HelpText_InsideRegion, ConfigManager.language.List_HelpText_OutSideRegion};
    }
}
