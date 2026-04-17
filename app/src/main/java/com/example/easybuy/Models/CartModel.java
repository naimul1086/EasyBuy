package com.example.easybuy.Models;

public class CartModel {
    private String cartId, productId, userId, image, title;
    private double price;
    private int quantity;

    public CartModel() {}

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public CartModel(String cartId, String productId, String userId,
                     String image, String title, double price, int quantity) {
        this.cartId    = cartId;
        this.productId = productId;
        this.userId    = userId;
        this.image     = image;
        this.title     = title;
        this.price     = price;
        this.quantity  = quantity;
    }

    public String getCartId()    { return cartId; }
    public String getProductId() { return productId; }
    public String getUserId()    { return userId; }
    public String getImage()     { return image; }
    public String getTitle()     { return title; }
    public double getPrice()     { return price; }
    public int    getQuantity()  { return quantity; }
}
