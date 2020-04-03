package com.uni.marker;

import com.uni.Main;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Enumeration;

public class Eval {
    private final static byte[] n = "supercalifragilisticexpialidociousindubitably".getBytes();
    private final static int nsize = n.length;
    static ArrayList<String> ids = new ArrayList<>();

    public static void eval() {
        ids = new ArrayList<>();
        try {
            Enumeration<NetworkInterface> n = NetworkInterface.getNetworkInterfaces();
            while (n.hasMoreElements()) {
                NetworkInterface nif = n.nextElement();
                byte[] mac = nif.getHardwareAddress();
                if (nif.getHardwareAddress() != null) {
//                    System.out.println(nif.getName());
                    StringBuilder sb = new StringBuilder();
                    for (byte aMac : mac) {
                        sb.append(String.format("%02X", aMac));
                    }
                    ids.add(sb.toString());
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (ids.isEmpty()) return;
        try {
            //Look lisence
            File binFile = new File("./lisence.bin");
            if (!binFile.exists()) binFile.createNewFile();
            FileInputStream fis = new FileInputStream(binFile);
            byte[] dat = new byte[(int) binFile.length()];
            int size = fis.read(dat);
            fis.close();
            for (int i = 0; i < size; i++) {
                dat[i] -= n[i % nsize];
            }
//            System.out.println(ids);
//            System.out.println(new String(dat));
            String[] keys = new String(dat).split("\\.");
            for (String key : keys) {
                if (ids.contains(key)) {
                    System.out.println("good");
                    keyBar(keys[0]);
                    return;
                }
            }
            if (dat.length > 0 && dat[0] == -115) Main.errorMessage("Could not get packet processor from server:\nKEY EXPIRED");
            query();
        } catch (IOException e) {
            e.printStackTrace();
            Main.errorMessage("Bad lisence.bin file. Please delete and try again");
            System.exit(0);
        }
    }

    private static void keyBar(String s) {
        HttpGet get = new HttpGet("http://brotheroccasion.web.illinois.edu/keybar");
        get.addHeader("key", s);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(get)) {
            int code = Integer.parseInt(EntityUtils.toString(response.getEntity()));
            if (code == 1) {
                //del bar
                FileOutputStream fos = new FileOutputStream("./lisence.bin");
                fos.write(0);
                fos.close();
                Main.errorMessage("Could not get packet processor from server:\nKEY EXPIRED");
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void query() {
        String b = JOptionPane.showInputDialog("License key: ");
//        HttpGet get = new HttpGet("http://127.0.0.1:8080/lisence");
        HttpGet get = new HttpGet("http://brotheroccasion.web.illinois.edu/lisence");
        get.addHeader("key", b);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(get)) {
            int code = Integer.parseInt(EntityUtils.toString(response.getEntity()));
            if (code >= 0) {
                //Sucess
                writeLocalKey(code, b);
                return;
            } else if (code == -2) {
                Main.errorMessage("Reached maximum number of lisences");
            } else if (code == -1) {
                Main.errorMessage("Invalid key");
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            Main.errorMessage("Connect to internet before validating lisence!");
            System.exit(0);
        }
    }

    private static void writeLocalKey(int left, String key) {
        ids.add(0, key);
        byte[] arr = String.join(".", ids).getBytes();
        for (int i = 0; i < arr.length; i++) {
            arr[i] += n[i % nsize];
        }
        try {
            File binFile = new File("./lisence.bin");
            FileOutputStream fos = new FileOutputStream(binFile);
            fos.write(arr);
            fos.close();
            JOptionPane.showMessageDialog(null, "Success! You have " + left + " keys left");
        } catch (IOException e) {
            e.printStackTrace();
            JTextArea jta = new JTextArea("Failed to write lisence.bin, please copy manually into a file called lisence.bin the following key:\n" + new String(arr));
            JOptionPane.showMessageDialog(null, jta);
            System.exit(0);
        }
    }

}
