package sample;

//********************************************************************//
//                          AlertData class                           //
//                                                                    //
//     This file's job is to:                                         //
//         - To hold all current alerts while the                     //
//           program is running                                       //
//         - Check alert prices and notify when an alert's            //
//           parameters have been met                                 //
//         - Remove any inactive alerts                               //
//                                                                    //
//                                                                    //
//********************************************************************//


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class AlertDataAndDirectory {


    private ArrayList<AlertItem> alertItems;
    private File dir = new File("CryptoAlerts");
    private File file = new File(dir,"Alert.txt");


    public AlertDataAndDirectory()
    {
        alertItems = new ArrayList<>();
    }


    protected void findAlerts() throws FileNotFoundException {

        Scanner scan = new Scanner(file);

        while(scan.hasNextLine())
        {
            String alertLine = scan.nextLine();
            alertItems.add(parseAlertData(alertLine));
        }

        scan.close();
    }

    protected ArrayList<AlertItem> checkAlertSignals()
    {
        ArrayList<AlertItem> trashedAlerts = new ArrayList<>();

        for(int i = 0; i < alertItems.size(); i++)
        {
            //convert these into doubles to compare
            double modifiedCurrentPrice = alertItems.get(i).getModifiedCurrentPrice();
            double modifiedAlertPrice = alertItems.get(i).getModifiedAlertPrice();

            //checkAlertComparison do the work
            boolean trashedAlert = checkAlertComparison(alertItems.get(i), modifiedCurrentPrice, modifiedAlertPrice);

            System.out.println(trashedAlert);

            if(trashedAlert)
            {
                trashedAlerts.add(alertItems.get(i));
            }
        }

        return trashedAlerts;
    }

    //Parses through a line of data, where '|' is the regex
    protected AlertItem parseAlertData(String alertLine){

        // Example:
        // wowBtcgood|>=|BTC/USDT|23456.0|C:\Users\Cade\Desktop\music\AlertSoundEffect.mp3
        // SHOULD return 5 items.

        String[] split = alertLine.split("\\|");

        AlertItem alertItem = new AlertItem(split[0], split[1], split[2], Double.parseDouble(split[3]), split[4]);

        return alertItem;
    }


    protected boolean checkAlertComparison(AlertItem alertItem, double currentPrice, double alertPrice)
    {
        SendSMS sms = new SendSMS();
        AlertDialog alertDialog = new AlertDialog();

        if (alertItem.getComparison().equals("<="))
        {
            if(currentPrice <= alertPrice)
            {
                sms.sendTextAlert(alertItem, "below");
                alertDialog.showAlertDialog(alertItem);
                return true;
            }
        }
        else {
            if(currentPrice >= alertPrice)
            {
                sms.sendTextAlert(alertItem, "above");
                alertDialog.showAlertDialog(alertItem);
                return true;
            }
        }
        return false;
    }

    protected void removeTrashedAlerts(ArrayList<AlertItem> trashAlertItems) {

        for(int i = 0; i < trashAlertItems.size(); i++)
        {
            removeAlert(trashAlertItems.get(i));
        }

    }


    protected ArrayList<AlertItem> getAlertItems()
    {return alertItems;}

    protected void setAlertItems(AlertItem createdAlertItem)
    {
        alertItems.add(createdAlertItem);}

    protected void removeAlert(AlertItem alertItem)
    {
        alertItems.remove(alertItem);
    }


}
