package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.awt.datatransfer.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main extends JFrame {

    private JPanel FrontEnd;
    private JLabel txtPingTime;
    private JLabel txtLocation;
    private JLabel txtPingTimeMin;
    private JLabel txtPingTimeMax;
    private JButton btnClear;

    public static void main(String[] args) throws IOException, InterruptedException {
        Main main = new Main();

        main.setVisible(true);
        main.setTitle("Blyat Ping Getter");
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setContentPane(main.FrontEnd);
        main.setLocationRelativeTo(null);
        main.pack();

        String IpAddress;

        try {
            IpAddress = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (UnsupportedFlavorException e) {
            IpAddress = "85.114.146.82";
        }

        String IpExample = "(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)";
        String WebExample = "[a-zA-Z0-9@:%._\\+~#?&//=]" + "{2,256}\\.[a-z]" + "{2,6}\\b";

        Pattern IpPattern = Pattern.compile(IpExample);
        Pattern WebPattern = Pattern.compile(WebExample);

        Matcher IpMatcher = IpPattern.matcher(IpAddress);
        Matcher WebMatcher = WebPattern.matcher(IpAddress);

        if(WebMatcher.find()) {
            IpAddress = WebMatcher.group();
        } else if(IpMatcher.find()) {
            IpAddress = IpMatcher.group();
        } else {
            IpAddress = "85.114.146.82";
        }

        URL LocationURL = null;
        BufferedReader LocationIn;
        String Location,Country = null,City = null;
        try {
            LocationURL = new URL("http://ip-api.com/line/" + IpAddress);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            LocationIn = new BufferedReader(
                    new InputStreamReader(LocationURL.openStream()));
            try {
                Location = LocationIn.readLine();
                for(int i=0; i < 5 ; i++) {
                    Location = LocationIn.readLine();

                    if(i == 0) Country = Location;
                    if(i == 4) City = Location;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                LocationIn.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        main.txtLocation.setText("( " + Country + " / " + City + " )");

        //int pingTime[] = new int[150];
        ArrayList<Integer> pingTime = new ArrayList<>();

        main.btnClear.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pingTime.clear();
                main.txtPingTimeMin.setText("MIN");
                main.txtPingTimeMax.setText("MAX");
                main.txtPingTime.setText("PINGTIME");
            }
        });

        while(true) {
            long currentTime = System.currentTimeMillis();
            boolean isPinged = InetAddress.getByName(IpAddress).isReachable(1000);
            currentTime = System.currentTimeMillis() - currentTime + (1000/128);
            if(isPinged) {
                pingTime.add((int) currentTime);
                main.txtPingTime.setText(currentTime + "ms");

            } else {
                main.txtPingTime.setText("PingTime > 1000ms");
                continue;
            }
            main.txtPingTimeMax.setText(getMax(pingTime) + "ms");
            main.txtPingTimeMin.setText(getMin(pingTime) + "ms");
            Thread.sleep(500);
        }
    }
    //Get minimum of an Array
    public static int getMin(ArrayList<Integer> inputArray) {
        int minValue = inputArray.get(0);
        for(int i=1;i<inputArray.size();i++){
            if(inputArray.get(i) > 1 && inputArray.get(i) < minValue){
                minValue = inputArray.get(i);
            }
        }
        return minValue;
    }
    //Get minimum of an Array
    public static int getMax(ArrayList<Integer> inputArray){
        int maxValue = inputArray.get(0);
        for(int i=1;i < inputArray.size();i++){
            if(inputArray.get(i) > maxValue){
                maxValue = inputArray.get(i);
            }
        }
        return maxValue;
    }
}