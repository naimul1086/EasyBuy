package com.example.easybuy.Models;

public class WishlistModel {
    private String wishlistId;
    private String productId;
    private String userId;
    private String image;
    private String title;
    private double price;
    private double originalPrice;
    private String category;

    public WishlistModel() {}

    public WishlistModel(String wishlistId, String productId, String userId,
                         String image, String title, double price,
                         double originalPrice, String category) {
        this.wishlistId    = wishlistId;
        this.productId     = productId;
        this.userId        = userId;
        this.image         = image;
        this.title         = title;
        this.price         = price;
        this.originalPrice = originalPrice;
        this.category      = category;
    }

    public String getWishlistId()    { return wishlistId; }
    public String getProductId()     { return productId; }
    public String getUserId()        { return userId; }
    public String getImage()         { return image; }
    public String getTitle()         { return title; }
    public double getPrice()         { return price; }
    public double getOriginalPrice() { return originalPrice; }
    public String getCategory()      { return category; }

    public void setWishlistId(String wishlistId)       { this.wishlistId = wishlistId; }
    public void setProductId(String productId)         { this.productId = productId; }
    public void setUserId(String userId)               { this.userId = userId; }
    public void setImage(String image)                 { this.image = image; }
    public void setTitle(String title)                 { this.title = title; }
    public void setPrice(double price)                 { this.price = price; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }
    public void setCategory(String category)           { this.category = category; }
}
