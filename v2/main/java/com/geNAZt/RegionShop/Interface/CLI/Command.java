package com.geNAZt.RegionShop.Interface.CLI;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created for YEAHWH.AT
 * User: fabian
 * Date: 03.09.13
 */

@Retention(RetentionPolicy.RUNTIME)
public @interface Command {
    String command();
    int arguments();
    String permission();
    String helpKey();
    String helpPage();
}
