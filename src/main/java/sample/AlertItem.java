package sample;


import com.webcerebrium.binance.api.BinanceApiException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.io.File;
import java.net.URI;

public class AlertItem {

    //All items that make up an alert
    //The VBox shell holds all of these
    private VBox shell;

    //The HBox holds Coin, alertName, and Pairing
    private HBox topHBox;
    private ImageView coin;
    private Text alertName, pairing;
    //////////////////////////////////////////////
    //The bottomHBox holds the prices
    private HBox bottomHBox;
    private String comparison;
    private Text alertPrice, currentPrice;
    //////////////////////////////////////////////
    //Audio for the alert
    private Media music;
    private  MediaPlayer mediaPlayer;

    private String musicString;


    public AlertItem(String alertName, String comparison, String pairing, double alertPrice, String music){

        File musicFile = new File(music);
        musicString = music;
        URI uri = musicFile.toURI();

        //User input
        this.alertName = new Text(alertName);
        this.pairing = new Text(pairing);
        this.comparison = comparison;
        this.alertPrice = new Text("Alert Price: " + getComparison() + " $" + alertPrice);
        this.music = new Media(uri.toString());
        mediaPlayer = new MediaPlayer(getMusic());

        //Window objects needed to visually represent the alert
        shell = new VBox();
        topHBox = new HBox();
        bottomHBox = new HBox();
        coin = new ImageView(determineCoin(pairing));
        currentPrice = new Text("N/A");

        //After all this, setup the structure of the visual alert
        setupVisualAlert();
    }

    private String determineCoin(String coin)
    {
       String[] pair = coin.split("/");

       if(pair[1].equals("BTC"))
       { return "BC_Logo_.png"; }

       else if(pair[1].equals("ETH"))
       { return "eth.png"; }

       else
       { return "tether.png"; }
    }

    private void setupVisualAlert()
    {
        //Setting up coin
        constructCoin();

        //Setting up Texts

            //Alert Name setup
            alertNameSetup();

            //Pairing setup
            pairingSetup();

            //Alert Price setup
            alertPriceSetup();

            //Current Price setup
            currentPriceSetup();

        //End of Text setup

        //Setting up HBox structures
        hBoxSetup();

        //Adding HBoxes to VBox shell
        getShell().getChildren().add(getTopHBox());
        getShell().getChildren().add(getBottomHBox());


    }

    protected void playAlert()
    {
        mediaPlayer.play();
        mediaPlayer.setStopTime(Duration.seconds(10));

        System.out.println(mediaPlayer.getStatus());

    }

    protected boolean isPlayingAlert()
    {
       return mediaPlayer.getStatus().equals(MediaPlayer.Status.PLAYING);
    }

    protected MediaPlayer getMediaPlayer()
    {
        return mediaPlayer;
    }

    private void constructCoin()
    {
        //Sizing for Coin
        getCoin().setFitHeight(50);
        getCoin().setFitWidth(90);
        getCoin().setPreserveRatio(true);
        getCoin().setSmooth(true);
        HBox.setMargin(getCoin(), new Insets(10, 5, 5, 10));
    }

    private void alertNameSetup()
    {
        getAlertName().setFont(Font.font("Arial Narrow", 16));
        getAlertName().setTextAlignment(TextAlignment.RIGHT);
        HBox.setMargin(getAlertName(), new Insets(5,10,0,10));
        getAlertName().setTextOrigin(VPos.BASELINE);
    }

    private void pairingSetup()
    {
        getPairing().setFont(Font.font("Arial Narrow", 16));
        getPairing().setTextAlignment(TextAlignment.RIGHT);
        HBox.setMargin(getPairing(), new Insets(5, 5, 0, 10));
        getPairing().setTextOrigin(VPos.BASELINE);
    }

    private void currentPriceSetup()
    {
        getCurrentPrice().setWrappingWidth(100);
        getCurrentPrice().setFont(Font.font("Arial Narrow", 16));
        getCurrentPrice().setTextAlignment(TextAlignment.CENTER);
        HBox.setMargin(getCurrentPrice(), new Insets(0, 10, 5, 10));
        getCurrentPrice().setTextOrigin(VPos.BASELINE);
    }

    private void alertPriceSetup()
    {
        getAlertPrice().setWrappingWidth(100);
        getAlertPrice().setFont(Font.font("Arial Narrow", 16));
        getAlertPrice().setTextAlignment(TextAlignment.CENTER);
        HBox.setMargin(getAlertPrice(), new Insets(0, 10, 5, 10));
        getAlertPrice().setTextOrigin(VPos.BASELINE);
    }

    private void hBoxSetup()
    {
        getTopHBox().setAlignment(Pos.CENTER_LEFT);
        getBottomHBox().setAlignment(Pos.TOP_LEFT);
        getBottomHBox().setMaxWidth(301);
        getBottomHBox().setStyle("-fx-border-color: transparent transparent black transparent;"
                + "-fx-padding: 9");

        getTopHBox().getChildren().addAll(getCoin(), getAlertName(), getPairing());
        getBottomHBox().getChildren().addAll(getAlertPrice(), getCurrentPrice());
    }

    //Getters
    protected VBox getShell() { return shell; }

    protected HBox getTopHBox() { return topHBox; }

    protected HBox getBottomHBox() {return bottomHBox; }

    protected ImageView getCoin() { return coin; }

    protected Text getAlertName() { return alertName; }

    protected Text getPairing() { return pairing; }

    protected String getComparison() { return comparison; }

    protected Text getAlertPrice() { return alertPrice; }

    protected Text getCurrentPrice() { return currentPrice; }

    protected String getMusicString() { return musicString; }

    protected double getModifiedCurrentPrice()
    {
        String modifiedCurrentPrice = getCurrentPrice().getText();

        String[] split = modifiedCurrentPrice.split("\\$");

        //Turn it into a double now
        double modified = Double.parseDouble(split[1]);

        return modified;

    }

    protected double getModifiedAlertPrice()
    {
        String modifiedCurrentPrice = getAlertPrice().getText();

        String[] split = modifiedCurrentPrice.split("\\$");


        //Turn it into a double now
        double modified = Double.parseDouble(split[1]);

        return modified;

    }

    protected String getAlertLine()
    {
        return getAlertName().getText() + "|" + getComparison() + "|" + getPairing().getText() + "|" + getModifiedAlertPrice() + "|" + getMusicString();
    }

    protected Media getMusic() { return music; }

    //Setters
    protected void setCurrentPrice() throws BinanceApiException {

        CryptoApi cryptoApi = new CryptoApi();

        currentPrice.setText("Current Price: $" + cryptoApi.getPrice(getPairing().getText()));
    }

    protected void setAlertName(String name)
    {alertName.setText(name);}

    protected void setAlertPrice(String price)
    {alertPrice.setText(price);}

}
