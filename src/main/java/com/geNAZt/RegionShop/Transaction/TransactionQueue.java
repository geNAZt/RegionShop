package com.geNAZt.RegionShop.Transaction;

import com.geNAZt.RegionShop.Model.ShopTransaction;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 15.06.13
 */
class TransactionQueue {
    private static final ArrayBlockingQueue<ShopTransaction> transactionPriorityQueue = new ArrayBlockingQueue<ShopTransaction>(500);

    public static void addTransaction(ShopTransaction shopTransaction) {
        transactionPriorityQueue.add(shopTransaction);
    }

    public synchronized static ShopTransaction getTransaction() {
        return transactionPriorityQueue.poll();
    }
}
