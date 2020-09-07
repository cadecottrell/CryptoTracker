//********************************************************************//
//                      Main Controller class                         //
//                                                                    //
//     This file's job is to:                                         //
//         - Update user alert prices                                 //
//         - Update the three main pairings (BTC, ETH, LTC)           //
//         - Checking to see if alert requirements have been reached  //
//         - Dynamically display new alerts                           //
//         - Dynamically delete displayed alerts                      //
//         - Dynamically display updated modified alerts              //
//                                                                    //
//                                                                    //
//********************************************************************//

package sample;

import com.webcerebrium.binance.api.BinanceApiException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class createMainController implements Initializable {

    //Price Texts
    @FXML
    private Text btcPrice, ethPrice, ltcPrice = new Text();

    //Menu items
    @FXML
    private MenuItem alertCreationTab, manageAlertsTab = new MenuItem();

    //Alert VBox
    @FXML
    private VBox alertVBox = new VBox();


    //APIS
    private final createAlertController alertController = new createAlertController();
    private final createManageAlertController manageAlertController = new createManageAlertController();
    private final FileManager fileManager = new FileManager();
    private final AlertDataAndDirectory alertDataAndDirectory = new AlertDataAndDirectory();
    private final CryptoApi cryptoApi = new CryptoApi();


    private ScheduledExecutorService topThreePriceThread = null;

    //Keeping track of alerts to update size
    private int lastAlertArraySize;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {}

    @FXML
    protected void startUp() throws FileNotFoundException {

        mainPagePriceUpdate();
        alertDataAndDirectory.findAlerts();
        displayAlertsOnScreen(alertDataAndDirectory.getAlertItems());
        lastAlertArraySize = alertDataAndDirectory.getAlertItems().size();

        alertCreationTab.setOnAction(actionEvent -> {
            try {
                alertController.display("Crypto Alerts");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        manageAlertsTab.setOnAction(actionEvent -> {
            try {
                manageAlertController.display("Crypto Alerts");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }


    @FXML
    private void mainPagePriceUpdate()
    {
        //Thread for updating prices of Bitcoin, Ether, and Litecoin on main page
        topThreePriceThread = Executors.newSingleThreadScheduledExecutor();


        topThreePriceThread.scheduleAtFixedRate(() ->
        {
            try
            { updateAlertPrices(alertDataAndDirectory.getAlertItems());}
            catch (BinanceApiException e)
            {e.printStackTrace();}

        }, 0, 3, TimeUnit.SECONDS);


        topThreePriceThread.scheduleAtFixedRate(() ->
        {
            try
            {

                ArrayList<AlertItem> trashedAlertItems = alertDataAndDirectory.checkAlertSignals();


                int lines = fileManager.alertFileLines();

                // create a trashed alert object to get trashed alerts from manageTab
                // instead of this array calling alertdata.checksignals() make it call the checksignals from trash.
                // this way there is no need to use another pool from the thread

                if(trashedAlertItems.size() != 0)
                {
                    removeTrashedAlerts(trashedAlertItems);
                    fileManager.deleteAlertLine(trashedAlertItems);
                }


                if(lastAlertArraySize < lines)
                {
                  String alertLine = fileManager.findLastAlert();
                  lastAlertArraySize = lines;
                  AlertItem alertItem = alertDataAndDirectory.parseAlertData(alertLine);
                  alertDataAndDirectory.setAlertItems(alertItem);
                  addNewAlertVisual(alertItem);
                }

                lastAlertArraySize = alertDataAndDirectory.getAlertItems().size();

            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }, 6, 4, TimeUnit.SECONDS);


        topThreePriceThread.scheduleAtFixedRate(() ->
        {
            try {
               btcPrice.setText("$" + cryptoApi.getPrice("BTC/USDT"));
               ethPrice.setText("$" + cryptoApi.getPrice("ETH/USDT"));
               ltcPrice.setText("$" + cryptoApi.getPrice("LTC/USDT"));
            }
            catch (BinanceApiException e) {
                e.printStackTrace();
            }
        }, 0, 3, TimeUnit.SECONDS);
    }

    @FXML
    private void displayAlertsOnScreen(ArrayList<AlertItem> alertItems)
    {
        for(int i = 0; i < alertItems.size(); i++)
        {
            alertVBox.getChildren().add(alertItems.get(i).getShell());
        }
    }


    protected void closeThreads()
    {
        topThreePriceThread.shutdownNow();
    }


    @FXML
    private void addNewAlertVisual(AlertItem alertItem)
    {
        Platform.runLater(()->{
            alertVBox.getChildren().add(alertItem.getShell());
        });
    }

    @FXML
    private void removeAlertVisual(AlertItem alertItem)
    {
        Platform.runLater(() ->{
            alertVBox.getChildren().remove(alertItem.getShell());
        });
    }

    private void removeTrashedAlerts(ArrayList<AlertItem> trashedAlertItems) throws FileNotFoundException //Make it just accept 1 alert so that it can do it individually
    {
        boolean musicPlaying = true;
        int index = 0;
        int counter = 0;

        for(int i = 0; i < trashedAlertItems.size(); i++)
        {
            trashedAlertItems.get(i).playAlert();
            removeAlertVisual(trashedAlertItems.get(i));
        }

        while(musicPlaying)
        {
            if(trashedAlertItems.get(index).getMediaPlayer().getStatus() == MediaPlayer.Status.PLAYING)
            {index++;}
            else
            {counter++;}

            if(counter == trashedAlertItems.size())
            {musicPlaying = false;}

            if(index > trashedAlertItems.size())
            {index = 0;}

        }

        alertDataAndDirectory.removeTrashedAlerts(trashedAlertItems);
    }

    @FXML
    private void updateAlertPrices(ArrayList<AlertItem> alertItems) throws BinanceApiException
    {

        for(int i = 0; i < alertItems.size(); i++)
        {
            alertItems.get(i).setCurrentPrice();}

    }

}
