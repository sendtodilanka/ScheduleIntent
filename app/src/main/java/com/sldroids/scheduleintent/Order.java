package com.sldroids.scheduleintent;

/**
 * Copyright(c) SLDroid Creations (Pvt) Ltd.  All Rights Reserved.
 * This software is the proprietary information of SLDroid Creations (Pvt) Ltd.
 * .
 * Created by dilanka on 5/9/17.
 */

public class Order {

    private int number, status;
    private double qty, rate, srcLat, srcLon, desLat, desLon;
    private String cname, contact, item, descript;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(double qty) {
        this.qty = qty;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getSrcLat() {
        return srcLat;
    }

    public void setSrcLat(double srcLat) {
        this.srcLat = srcLat;
    }

    public double getSrcLon() {
        return srcLon;
    }

    public void setSrcLon(double srcLon) {
        this.srcLon = srcLon;
    }

    public double getDesLat() {
        return desLat;
    }

    public void setDesLat(double desLat) {
        this.desLat = desLat;
    }

    public double getDesLon() {
        return desLon;
    }

    public void setDesLon(double desLon) {
        this.desLon = desLon;
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String name) {
        this.cname = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getDescript() {
        return descript;
    }

    public void setDescript(String descript) {
        this.descript = descript;
    }
}
