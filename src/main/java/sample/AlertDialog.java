package sample;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.*;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Optional;

public class AlertDialog
{
    protected void showAlertDialog(AlertItem alert)
    {
        Platform.runLater(()-> {
            Alert alertDialog = new Alert(AlertType.INFORMATION);
            Stage stage = (Stage) alertDialog.getDialogPane().getScene().getWindow();
            Image icon = new Image("BC_Logo_.png");
            stage.getIcons().add(icon);

            alertDialog.setTitle(alert.getAlertName().getText() + " Notification!");
            alertDialog.setHeaderText(null);
            alertDialog.setContentText(alert.getAlertName().getText() + " has just crossed $" + alert.getModifiedAlertPrice() + "!");

            alertDialog.show();
       });
    }

    protected boolean showDeleteConfirmationDialog(AlertItem alertItem)
    {
            Alert alertConfirmation = new Alert(AlertType.CONFIRMATION);

            Stage stage = (Stage) alertConfirmation.getDialogPane().getScene().getWindow();
            Image icon = new Image("BC_Logo_.png");
            stage.getIcons().add(icon);

            alertConfirmation.setTitle("Delete " + alertItem.getAlertName().getText() + " Confirmation.");
            alertConfirmation.setHeaderText(null);
            alertConfirmation.setContentText("Are you sure you want to delete " + alertItem.getAlertName().getText() +"?");

            Optional<ButtonType> result = alertConfirmation.showAndWait();

            if(result.get() == ButtonType.OK)
            { return true; }
            else
            { return false; }
    }

}
