package com.geNAZt.RegionShop.Database.Table;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
import com.avaje.ebean.annotation.CacheStrategy;
import com.avaje.ebean.annotation.CacheTuning;
import com.avaje.ebean.annotation.EnumMapping;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
@Entity()
@Table(name = "rs_transaction")
public class Transaction {
    @Id
    private Integer id;
    private String issuer;
    private String shop;
    private String world;
    private Date date;
    private String owner;
    private Integer item;
    private Integer amount;
    private Double sell;
    private Double buy;
    private Integer unitAmount;

    @EnumMapping(nameValuePairs="BUY=B, SELL=S, ADD=A, EQUIP=E, REMOVE=R")
    public enum TransactionType {
        BUY,
        SELL,
        ADD,
        EQUIP,
        REMOVE
    }

    private TransactionType type;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getItem() {
        return item;
    }

    public void setItem(Integer item) {
        this.item = item;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Double getBuy() {
        return buy;
    }

    public void setBuy(Double buy) {
        this.buy = buy;
    }

    public Double getSell() {
        return sell;
    }

    public void setSell(Double sell) {
        this.sell = sell;
    }

    public String getWorld() {
        return world;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public Integer getUnitAmount() {
        return unitAmount;
    }

    public void setUnitAmount(Integer unitAmount) {
        this.unitAmount = unitAmount;
    }
}