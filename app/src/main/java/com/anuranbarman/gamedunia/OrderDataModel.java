package com.anuranbarman.gamedunia;

/**
 * Created by anuran on 6/11/16.
 */

public class OrderDataModel {
    private int orderID,product_id,user_id,product_price,quantity;
    private String product_title,product_image,status;

    public OrderDataModel(int orderID, int product_id, String product_image, int product_price, String product_title, int quantity, int user_id,String status) {
        this.orderID = orderID;
        this.product_id = product_id;
        this.product_image = product_image;
        this.product_price = product_price;
        this.product_title = product_title;
        this.quantity = quantity;
        this.user_id = user_id;
        this.status=status;
    }

    public int getOrderID() {
        return orderID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
