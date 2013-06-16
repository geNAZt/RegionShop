package com.geNAZt.RegionShop.Model;

/**
 * Created for YEAHWH.AT
 * User: geNAZt (fabian.fassbender42@googlemail.com)
 * Date: 05.06.13
 */
import com.avaje.ebean.annotation.EnumMapping;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@SuppressWarnings("UnusedDeclaration")
@Entity()
@Table(name = "ShopTransaction")
public class ShopTransaction {
    @Id
    private Integer id;
    private String issuer;
    private String shop;
    private Date date;
    private String owner;
    private Integer item;
    private Integer amount;
    private Integer sell;
    private Integer buy;

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

    public Integer getBuy() {
        return buy;
    }

    public void setBuy(Integer buy) {
        this.buy = buy;
    }

    public Integer getSell() {
        return sell;
    }

    public void setSell(Integer sell) {
        this.sell = sell;
    }
}