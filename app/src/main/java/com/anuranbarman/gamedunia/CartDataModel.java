package com.anuranbarman.gamedunia;

/**
 * Created by anuran on 28/10/16.
 */

public class CartDataModel {
    private String gameName,image;
    private int quantity,price,product_id;

    public CartDataModel(int product_id,String gameName, String image, int price, int quantity) {
        this.product_id=product_id;
        this.gameName = gameName;
        this.image = image;
        this.price = price;
        this.quantity = quantity;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }
}
