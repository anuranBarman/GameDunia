package com.anuranbarman.gamedunia;

/**
 * Created by anuran on 26/10/16.
 */
public class MyData {
    private String gameTitle,gamePrice,gameDesc,gameImage,gameID;

    public MyData(String gameTitle, String gamePrice, String gameDesc, String gameImage,String gameID) {
        this.gameTitle = gameTitle;
        this.gamePrice = gamePrice;
        this.gameDesc = gameDesc;
        this.gameImage = gameImage;
        this.gameID=gameID;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public String getGameID() {
        return gameID;
    }

    public void setGameID(String gameID) {
        this.gameID = gameID;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public String getGamePrice() {
        return gamePrice;
    }

    public void setGamePrice(String gamePrice) {
        this.gamePrice = gamePrice;
    }

    public String getGameDesc() {
        return gameDesc;
    }

    public void setGameDesc(String gameDesc) {
        this.gameDesc = gameDesc;
    }

    public String getGameImage() {
        return gameImage;
    }

    public void setGameImage(String gameImage) {
        this.gameImage = gameImage;
    }
}
