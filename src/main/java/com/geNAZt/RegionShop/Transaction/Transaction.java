package com.geNAZt.RegionShop.Transaction;

import com.geNAZt.RegionShop.RegionShopPlugin;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 15.06.13
 */
public class Transaction {
    public Transaction(RegionShopPlugin pl) {
        //Check if transaction logging is enabled
        if(pl.getConfig().getBoolean("transactions")) {

        }
    }
}
