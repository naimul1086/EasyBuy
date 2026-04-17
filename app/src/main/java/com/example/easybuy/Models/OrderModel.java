package com.example.easybuy.Models;

import java.util.List;

public class OrderModel {
    private String orderId;
    private String userId;
    private List<CartModel> items;
    private double subtotal;
    private double shippingCost;
    private double discount;
    private double totalPrice;
    private String status;
    private long orderTimestamp;
    private String promoCode;

    public OrderModel() {}

    public OrderModel(String orderId, String userId, List<CartModel> items,
                      double subtotal, double shippingCost, double discount,
                      double totalPrice, String status, long orderTimestamp,
                      String promoCode) {
        this.orderId        = orderId;
        this.userId         = userId;
        this.items          = items;
        this.subtotal       = subtotal;
        this.shippingCost   = shippingCost;
        this.discount       = discount;
        this.totalPrice     = totalPrice;
        this.status         = status;
        this.orderTimestamp = orderTimestamp;
        this.promoCode      = promoCode;
    }

    public String getOrderId()        { return orderId; }
    public String getUserId()         { return userId; }
    public List<CartModel> getItems() { return items; }
    public double getSubtotal()       { return subtotal; }
    public double getShippingCost()   { return shippingCost; }
    public double getDiscount()       { return discount; }
    public double getTotalPrice()     { return totalPrice; }
    public String getStatus()         { return status; }
    public long getOrderTimestamp()   { return orderTimestamp; }
    public String getPromoCode()      { return promoCode; }

    public void setOrderId(String orderId)               { this.orderId = orderId; }
    public void setUserId(String userId)                 { this.userId = userId; }
    public void setItems(List<CartModel> items)          { this.items = items; }
    public void setSubtotal(double subtotal)             { this.subtotal = subtotal; }
    public void setShippingCost(double shippingCost)     { this.shippingCost = shippingCost; }
    public void setDiscount(double discount)             { this.discount = discount; }
    public void setTotalPrice(double totalPrice)         { this.totalPrice = totalPrice; }
    public void setStatus(String status)                 { this.status = status; }
    public void setOrderTimestamp(long orderTimestamp)   { this.orderTimestamp = orderTimestamp; }
    public void setPromoCode(String promoCode)           { this.promoCode = promoCode; }
}
