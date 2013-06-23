package com.geNAZt.RegionShop.ServerShop;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 21.06.13
 */
public class Price {
    private double sell;
    private double buy;
    private double currentSell;
    private double currentBuy;
    private Integer maxItemRecalc;
    private double limitSellPriceFactor;
    private double limitBuyPriceFactor;
    private double limitSellPriceUnderFactor;
    private double limitBuyPriceUnderFactor;
    private Integer sold = 0;
    private Integer bought = 0;

    public double getSell() {
        return sell;
    }

    public void setSell(double sell) {
        this.sell = sell;
    }

    public double getBuy() {
        return buy;
    }

    public void setBuy(double buy) {
        this.buy = buy;
    }

    public Integer getMaxItemRecalc() {
        return maxItemRecalc;
    }

    public void setMaxItemRecalc(Integer maxItemRecalc) {
        this.maxItemRecalc = maxItemRecalc;
    }

    public double getLimitSellPriceFactor() {
        return limitSellPriceFactor;
    }

    public void setLimitSellPriceFactor(double limitSellPriceFactor) {
        this.limitSellPriceFactor = limitSellPriceFactor;
    }

    public double getLimitBuyPriceFactor() {
        return limitBuyPriceFactor;
    }

    public void setLimitBuyPriceFactor(double limitBuyPriceFactor) {
        this.limitBuyPriceFactor = limitBuyPriceFactor;
    }

    public Integer getSold() {
        return sold;
    }

    public void setSold(Integer sold) {
        this.sold = sold;
    }

    public Integer getBought() {
        return bought;
    }

    public void setBought(Integer bought) {
        this.bought = bought;
    }

    public double getCurrentSell() {
        return currentSell;
    }

    public void setCurrentSell(double currentSell) {
        this.currentSell = currentSell;
    }

    public double getCurrentBuy() {
        return currentBuy;
    }

    public void setCurrentBuy(double currentBuy) {
        this.currentBuy = currentBuy;
    }

    public double getLimitSellPriceUnderFactor() {
        return limitSellPriceUnderFactor;
    }

    public void setLimitSellPriceUnderFactor(double limitSellPriceUnderFactor) {
        this.limitSellPriceUnderFactor = limitSellPriceUnderFactor;
    }

    public double getLimitBuyPriceUnderFactor() {
        return limitBuyPriceUnderFactor;
    }

    public void setLimitBuyPriceUnderFactor(double limitBuyPriceUnderFactor) {
        this.limitBuyPriceUnderFactor = limitBuyPriceUnderFactor;
    }
}
