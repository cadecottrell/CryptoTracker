package sample;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class createManageAlertController implements Initializable {

    //API
    AlertDataAndDirectory alertDataAndDirectory = new AlertDataAndDirectory();
    AlertDialog alertDialog = new AlertDialog();

    private ArrayList<AlertItem> alertItems;
    private ArrayList<AlertItem> trashedItems = new ArrayList<>();


    Stage stage;

    @FXML
    ListView list = new ListView();


    @FXML
    TextField alertNameTextField, alertPriceTextField, phoneNumberTextField;

    @FXML
    RadioButton aboveRadioButton, belowRadioButton;

    @FXML
    Button audioFileButton, confirmButton, deleteAlertButton;


    //Creates the window
    protected void display(String title) throws IOException {
        stage = new Stage();


        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("manageController.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root);
        stage.setTitle(title);
        stage.setScene(scene);


        Image icon = new Image("BC_Logo_.png");
        stage.getIcons().add(icon);

        stage.show();

    }

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {

        try {
            populateListView();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        //Radio Buttons are now in a toggle group so we do not have to manually detect if one is clicked
        ToggleGroup toggleGroup = new ToggleGroup();
        aboveRadioButton.setToggleGroup(toggleGroup);
        belowRadioButton.setToggleGroup(toggleGroup);


        list.getSelectionModel().selectedItemProperty().addListener(event -> {
            resetToDefault();
            System.out.println("clicked on: " + list.getSelectionModel().getSelectedItem());
        });

        deleteAlertButton.setOnAction(event -> {

            String selected = (String) list.getSelectionModel().getSelectedItem();

            AlertItem alertItem = getSelectedAlertItem(selected);

           boolean result = alertDialog.showDeleteConfirmationDialog(alertItem);

           if(result == true)
           {
               trashedItems.add(alertItem);
               list.getItems().remove(list.getSelectionModel().getSelectedItem());
           }

        });

    }



    private AlertItem getSelectedAlertItem(String selected) {

        if(alertItems.size() != 0)
        {
            for(int i = 0; i < alertItems.size(); i++)
            {
                if(alertItems.get(i).getAlertName().getText().equals(selected))
                {
                    return alertItems.get(i);
                }
            }
        }
        return null;
    }

    @FXML
    private void populateListView() throws FileNotFoundException {

        alertDataAndDirectory.findAlerts();
        alertItems = alertDataAndDirectory.getAlertItems();


        for(int i = 0; i < alertItems.size(); i++)
        {
            list.getItems().add(alertItems.get(i).getAlertName().getText());
        }
    }

    @FXML
    private void resetToDefault()
    {
        alertNameTextField.clear();
        alertPriceTextField.clear();
        phoneNumberTextField.clear();

        aboveRadioButton.setSelected(false);
        belowRadioButton.setSelected(false);
    }

    protected ArrayList<AlertItem> getDeletedAlerts()
    { return trashedItems;}


}
