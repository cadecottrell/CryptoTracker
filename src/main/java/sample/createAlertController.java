package sample;

import com.webcerebrium.binance.api.BinanceApiException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.text.Text;
import javafx.stage.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//COMMENT THIS TO MAKE LOOK PRETTY PLEZ

public class createAlertController implements Initializable{

    private String selectedComboBoxPairing;
    private boolean running;
    private ScheduledExecutorService scheduledExecutorService;
    private File audioFile;

    private double xOffset = 0;
    private double yOffset = 0;

    //Stage
    Stage stage;

    //ComboBoxes
    @FXML
    private ComboBox btcPairings , ethPairings, usdtPairings;

    //Text
    @FXML
    private Text pairingSelected, currentPrice, moreThanOnePairing, priceMustBeNumber, mustBeAudio = new Text();

    //Buttons
    @FXML
    private Button exitButton, confirmButton, chooseAudioFile = new Button();

    //RadioButtons
    @FXML
    private RadioButton above, below = new RadioButton();

    //TextFields
    @FXML
    private TextField alertNameField, alertPriceField = new TextField();

    //APIs
    private final CryptoApi api = new CryptoApi();
    private final AlertDataAndDirectory alertDataAndDirectory = new AlertDataAndDirectory();
    private final FileManager fileManager = new FileManager();

    //The list from which a user can choose from only these specified pairings
    private final ObservableList<String> btcList = FXCollections.observableArrayList("N/A", "ETH/BTC", "XRP/BTC", "BCH/BTC", "LTC/BTC", "LINK/BTC", "EOS/BTC", "XMR/BTC", "BNB/BTC");
    private final ObservableList<String> ethList = FXCollections.observableArrayList("N/A", "XRP/ETH", "LINK/ETH", "EOS/ETH", "BNB/ETH", "LTC/ETH", "VET/ETH", "XMR/ETH");
    private final ObservableList<String> usdtList = FXCollections.observableArrayList("N/A", "BTC/USDT", "ETH/USDT", "BCH/USDT", "LTC/USDT", "XRP/USDT", "LINK/USDT", "BNB/USDT", "EOS/USDT", "XMR/USDT");


    protected void display(String title) throws IOException {

        btcPairings = new ComboBox();
        ethPairings = new ComboBox();
        usdtPairings = new ComboBox();
        stage = new Stage();

        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("createAlert.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);

        stage.setScene(scene);

        Image icon = new Image("BC_Logo_.png");
        stage.getIcons().add(icon);

        //Stage Settings
        stage.initStyle(StageStyle.UNDECORATED); //No Minimize, Maximize, or Exit

        stage.setTitle(title);
        stage.setMinWidth(716);  //
        stage.setMinHeight(639); /// Windows maximum and minimum size, cannot adjust it
        stage.setMaxWidth(716);  ///
        stage.setMaxHeight(639); //

        stage.initModality(Modality.APPLICATION_MODAL); // Cannot leave this window, it must be dealt with before going back to main window
        stage.show();


        //This moves the Window
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });

    }


    //Initializes the items
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //Puts pairings in respective places
        btcPairings.setItems(btcList);
        ethPairings.setItems(ethList);
        usdtPairings.setItems(usdtList);

        //Radio Buttons are now in a toggle group so we do not have to manually detect if one is clicked
        ToggleGroup toggleGroup = new ToggleGroup();
        above.setToggleGroup(toggleGroup);
        below.setToggleGroup(toggleGroup);

        //If BTC is picked and chooses an option other than N/A disable other comboboxes
        btcPairings.setOnAction(event -> {
            if(!btcPairings.getValue().equals("N/A"))
            {
                selectedComboBoxPairing = btcPairings.getValue().toString();
                updateSelectedPairing(selectedComboBoxPairing);
                disableComboBoxListener(ethPairings, usdtPairings);

                if(running)
                {
                    stop(); //Determine if the thread is running, if it is make the thread stop
                }

                updatePrice(); //Create thread
            }
            else //Else if it IS N/A enable the other comboboxes and reset price.
            {
                stop();
                priceNA();
                reEnableComboBoxListener(ethPairings, usdtPairings);
                updateSelectedPairing(selectedComboBoxPairing);
            }
        });

        //Functions exactly like btcPairings, except it is ETH
        ethPairings.setOnAction(event -> {
            if(!ethPairings.getValue().equals("N/A"))
            {
                selectedComboBoxPairing = ethPairings.getValue().toString();
                updateSelectedPairing(selectedComboBoxPairing);
                disableComboBoxListener(btcPairings, usdtPairings);
                if(running)
                {
                    stop();
                }
                updatePrice();
            }
            else
            {
                stop();
                priceNA();
                reEnableComboBoxListener(btcPairings, usdtPairings);
                updateSelectedPairing(selectedComboBoxPairing);
            }
        });

        //Functions exactly like btcPairings, except it is USDT
        usdtPairings.setOnAction(event -> {
            if(!usdtPairings.getValue().equals("N/A"))
            {
                selectedComboBoxPairing = usdtPairings.getValue().toString();
                updateSelectedPairing(selectedComboBoxPairing);
                disableComboBoxListener(btcPairings, ethPairings);
                if(running)
                {
                    stop();
                }
                updatePrice();
            }
            else
            {
                stop();
                priceNA();
                reEnableComboBoxListener(btcPairings, ethPairings);
                updateSelectedPairing(selectedComboBoxPairing);
            }
        });

        //Button Actions

        //If user clicks on exit button, stop the thread if it exists and close the stage.
        exitButton.setOnAction(event -> {
            stop();
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
        });

        //Finds audiofile to play for alert sound
        chooseAudioFile.setOnAction(event -> {
            String fileName;
            FileChooser fileChosen = new FileChooser();
            File file = fileChosen.showOpenDialog(null);

            if(file != null)
            {
                fileName = file.getName();
                if(fileName.endsWith(".mp3")) //file must be MP3
                {
                    mustBeAudio.setVisible(false);
                    audioFile = file;
                }
                else
                {
                    mustBeAudio.setVisible(true);
                }
            }
        });

        confirmButton.setOnAction(event -> {
            String alertName = alertNameField.getText();
            String alertPrice = alertPriceField.getText();
            Double price;

            try
            {
               if(!alertName.equals(""))
               {
                   if (above.isSelected() || below.isSelected())
                   {
                       if (audioFile != null)
                       {
                           if (selectedComboBoxPairing != null && !selectedComboBoxPairing.equals("N/A"))
                           {
                               //Parsing Price
                               price = Double.parseDouble(alertPrice);

                               //Finding comparison
                               String compare = determineComparison();

                               //Created alert, close the page.
                               fileManager.addAlert(alertName, compare, selectedComboBoxPairing, price, audioFile);
                               String alertCreated = fileManager.findLastAlert();
                               AlertItem newestAlertItem = alertDataAndDirectory.parseAlertData(alertCreated);
                               alertDataAndDirectory.setAlertItems(newestAlertItem);
                               stop();
                               Stage stage = (Stage) confirmButton.getScene().getWindow();
                               stage.close();
                           }
                           //alert no pairing chosen
                       }
                       //alert no file chosen
                   }
                   //alert no name given
               }
            }
            catch (Exception e)
            {
                priceMustBeNumber.setVisible(true);
                System.out.println(e);
            }
        });

    }

    //Greys out other combo listeners
    @FXML
    private void disableComboBoxListener(ComboBox pairing1, ComboBox pairing2)
    {
        pairing1.setDisable(true);
        pairing2.setDisable(true);
    }

    //Re-enables combo listeners
    @FXML
    private void reEnableComboBoxListener(ComboBox pairing1, ComboBox pairing2)
    {
        selectedComboBoxPairing = "N/A";
        pairing1.setDisable(false);
        pairing2.setDisable(false);
    }

    //Changes text to pairing you chose.
    @FXML
    private void updateSelectedPairing(String pairing)
    {pairingSelected.setText(pairing);}

    //Changes price back to N/A
    @FXML
    private void priceNA()
    {
        currentPrice.setText("N/A");
    }

    //Creates a thread, and that thread updates the price every 4 seconds
    @FXML
    private void updatePrice()
    {
        running = true;
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

        scheduledExecutorService.scheduleAtFixedRate(() ->
        {
            try {
                currentPrice.setText(api.getPrice(selectedComboBoxPairing));
            } catch (BinanceApiException e) {
                e.printStackTrace();
            }

        }, 0, 4, TimeUnit.SECONDS);
    }

    //Stops the thread if it is running.
    private void stop()
    {
        if(running)
        {
            scheduledExecutorService.shutdownNow();
            running = false;
        }
    }

    private String determineComparison()
    {
        if(above.isSelected())
        {return ">=";}
        else
        {return "<=";}
    }

}
