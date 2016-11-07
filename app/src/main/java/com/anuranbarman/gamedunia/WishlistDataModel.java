package com.anuranbarman.gamedunia;

/**
 * Created by anuran on 7/11/16.
 */

public class WishlistDataModel {
    private String product_title,product_image;
    private int wishlist_id,product_id,user_id,product_price,product_quantity;



    public WishlistDataModel(int product_id, String product_image, int product_price, int product_quantity, String product_title, int user_id, int wishlist_id) {
        this.product_id = product_id;
        this.product_image = product_image;
        this.product_price = product_price;
        this.product_title = product_title;
        this.user_id = user_id;
        this.wishlist_id = wishlist_id;
        this.product_quantity=product_quantity;
    }

    public int getProduct_quantity() {
        return product_quantity;
    }

    public void setProduct_quantity(int product_quantity) {
        this.product_quantity = product_quantity;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public String getProduct_image() {
        return product_image;
    }

    public void setProduct_image(String product_image) {
        this.product_image = product_image;
    }

    public int getProduct_price() {
        return product_price;
    }

    public void setProduct_price(int product_price) {
        this.product_price = product_price;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getWishlist_id() {
        return wishlist_id;
    }

    public void setWishlist_id(int wishlist_id) {
        this.wishlist_id = wishlist_id;
    }
}
