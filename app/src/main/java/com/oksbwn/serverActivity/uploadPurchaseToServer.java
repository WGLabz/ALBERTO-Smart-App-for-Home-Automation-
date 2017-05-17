package com.oksbwn.serverActivity;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by OKSBWN on 4/18/2015.
 */
public class uploadPurchaseToServer extends Thread {
    String fileNam;
    uploadPurchaseToServer(String fileName){
       this.fileNam=fileName;
        start();
    }
    public void run() {
        }
}
