package com.uni.marker;

import com.uni.Main;
import com.uni.gui.Messages;
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
    private static String s = "supercalifragilisticexpialidociousindubitably";
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
            File binFile = new File(Messages.m1);
            if (!binFile.exists()) binFile.createNewFile();
            FileInputStream fis = new FileInputStream(binFile);
            byte[] dat = new byte[(int) binFile.length()];
            int size = fis.read(dat);
            fis.close();
            for (int i = 0; i < size; i++) {
                dat[i] = (byte) (dat[i] ^ n[i % nsize]);
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
            if (dat.length > 0 && dat[0] == -115)
                Main.errorMessage(Messages.m8);
            query();
        } catch (IOException e) {
            e.printStackTrace();
            Main.errorMessage(Messages.m9);
            System.exit(0);
        }
    }

    private static void keyBar(String s) {
        HttpGet get = new HttpGet(Messages.m10);
        get.addHeader("key", s);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(get)) {
            int code = Integer.parseInt(EntityUtils.toString(response.getEntity()));
            if (code == 1) {
                //del bar
                FileOutputStream fos = new FileOutputStream(Messages.m1);
                fos.write(0);
                fos.close();
                Main.errorMessage(Messages.m8);
                System.exit(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void query() {
        String b = JOptionPane.showInputDialog(Messages.m3);
        if(b==null || b.length()<1){


        }
        HttpGet get = new HttpGet(Messages.m7);
        get.addHeader("key", b);
        try (CloseableHttpClient httpClient = HttpClients.createDefault();
             CloseableHttpResponse response = httpClient.execute(get)) {
            int code = Integer.parseInt(EntityUtils.toString(response.getEntity()));
            if (code >= 0) {
                //Sucess
                writeLocalKey(code, b);
                return;
            } else if (code == -2) {
                Main.errorMessage(Messages.m5);
            } else if (code == -1) {
                Main.errorMessage(Messages.m6);
            }
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
            Main.errorMessage(Messages.m4);
            System.exit(0);
        }
    }

    private static void writeLocalKey(int left, String key) {
        ids.add(0, key);
        byte[] arr = String.join(".", ids).getBytes();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = (byte) (arr[i] ^ n[i % nsize]);
        }
        try {
            File binFile = new File(Messages.m1);
            FileOutputStream fos = new FileOutputStream(binFile);
            fos.write(arr);
            fos.close();
            JOptionPane.showMessageDialog(null, "Success! You have " + left + " keys left");
        } catch (IOException e) {
            e.printStackTrace();
            JTextArea jta = new JTextArea(Messages.m2 + new String(arr));
            JOptionPane.showMessageDialog(null, jta);
            System.exit(0);
        }
    }

    private final static byte[] n = s.getBytes();
    private final static int nsize = n.length;

}
