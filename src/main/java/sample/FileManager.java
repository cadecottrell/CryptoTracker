package sample;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager
{

    private File dir = new File("CryptoAlerts");
    private File file = new File(dir,"Alert.txt");
    private File temporaryFile = new File(dir, "tempAlert.txt");

    protected void checkAlertDirectoryExists()
    {
        URL location = getClass().getProtectionDomain().getCodeSource().getLocation();
        File file = new File(location.getPath());
        Path path = Paths.get("CryptoAlerts");

        if(!Files.exists(path))
        {
            createAlertDirectory(Paths.get(file.getAbsolutePath()));
        }
    }

    protected void createAlertDirectory(Path path)
    {
        new File("CryptoAlerts").mkdirs();
    }

    protected void addAlert(String alertName, String comparison, String selectedComboBoxPairing, double price, File audioFile ) throws IOException
    {

        PrintWriter printWriter = null;

        if(file.exists())
        {
            printWriter = new PrintWriter(new FileWriter(new File(String.valueOf(file)), true));
        }
        else
        {
            printWriter = new PrintWriter(file);
        }

        //PrintWriter writes information down with "|" notation
        printWriter.println(alertName + "|" + comparison + "|" + selectedComboBoxPairing + "|" + price + "|" + audioFile);
        printWriter.close();

    }

    protected int alertFileLines() throws FileNotFoundException {

        Scanner scan = new Scanner(file);
        int i = 0;
        while(scan.hasNextLine())
        {
            if(scan.nextLine().equals(""))
            {
                System.out.println("ignore");
            }
            else
            {
                i++;
            }
        }

        scan.close();
        return i;
    }

    protected String findLastAlert() throws IOException {

        BufferedReader buffer = new BufferedReader(new FileReader(file));
        String currentLine = "";
        String lastLine = "";

        while((currentLine = buffer.readLine()) != null)
        {
            lastLine = currentLine;
        }

        buffer.close();
        return lastLine;
    }

    protected void deleteAlertLine(ArrayList<AlertItem> trashedAlertItems) throws IOException {
        ArrayList<String> lines = new ArrayList<>();

        //for rewriting the file
        PrintWriter printWriter = new PrintWriter(temporaryFile);

        //for reading the old file
        BufferedReader buffer = new BufferedReader(new FileReader(file));
        String currentLine = "";

        while((currentLine = buffer.readLine()) != null)
        {
            for(int i = 0; i < trashedAlertItems.size(); i++)
            {
                String alertLine = trashedAlertItems.get(i).getAlertLine();
                System.out.println(alertLine);

                if(!currentLine.equals(alertLine))
                {
                    lines.add(currentLine);
                }
            }
        }

        buffer.close();

        for(int i = 0; i < lines.size(); i++)
        {
            printWriter.println(lines.get(i));
        }

        printWriter.close();;

        file.delete();

        if(temporaryFile.renameTo(file));
        {
            System.out.println("true");
        }


    }



}
